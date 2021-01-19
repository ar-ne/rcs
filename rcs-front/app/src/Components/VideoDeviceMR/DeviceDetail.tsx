import React, {useCallback, useEffect, useState} from "react";
import {
    AppBar,
    Box,
    Button,
    Card,
    CardContent,
    createStyles,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
    IconButton,
    Input,
    LinearProgress,
    List,
    ListItem,
    ListItemSecondaryAction,
    ListItemText,
    makeStyles,
    Theme,
    Toolbar,
    Typography
} from "@material-ui/core";
import axios from "axios";
import {BaseInfo, DeviceStatus, Environment, MergedInfo} from "../../Interfaces/VideoDeviceMR";
import {CloseIcon} from "@material-ui/data-grid";
import {FileCopy, Refresh, Save} from "@material-ui/icons";
import copy from "copy-to-clipboard";
import {useSnackbar} from "notistack";
import {secToString} from "../../utils";
import {StatusIcon} from "./StatusIcon";
import {JobList} from "./JobList";

export interface DeviceDetailProps {
    baseInfo: BaseInfo;
    close: () => void;
}


const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        appBar: {
            position: 'relative',
        },
        title: {
            marginLeft: theme.spacing(2),
            flex: 1,
        },
    }),
);

const getSWVersion = (e: Environment, packageName: string): string => {
    return e.packages.filter(v => v.applicationInfo.packageName === packageName)[0].version;
}

export const DeviceDetail: React.FC<DeviceDetailProps> = (p: DeviceDetailProps) => {
    const {baseInfo, close} = p;
    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [info, setInfo] = useState({} as MergedInfo);
    const {enqueueSnackbar} = useSnackbar();
    const [interval, setInterval] = useState(0);
    const [envDetail, setEnvDetail] = useState({open: false, filter: ""});

    const onIntervalSave = useCallback(() => {
        axios.get("/web/ajax/VideoDeviceMR.ashx", {
                params: {
                    operation: "SetInterval",
                    imei: baseInfo.imei,
                    setInterval: interval
                }
            }
        ).then(() => {
            enqueueSnackbar("保存成功", {variant: "success"});
        });
    }, [baseInfo.imei, enqueueSnackbar, interval])
    const load = useCallback(
        () => {
            setLoading(true);
            axios.get("/web/ajax/VideoDeviceMR.ashx", {
                params: {
                    operation: "GetDeviceDetail",
                    imei: baseInfo.imei
                }
            }).then(r => {
                const mergedInfo: MergedInfo = {
                    ...r.data,
                    deviceMonitor: {
                        ...r.data.deviceMonitor,
                        Environment: JSON.parse(r.data.deviceMonitor.Environment) as Environment,
                        current: JSON.parse(r.data.deviceMonitor.current) as DeviceStatus,
                        lastSeen: new Date(r.data.deviceMonitor.lastSeen)
                    },
                }
                setInfo(mergedInfo);
                setInterval(mergedInfo.deviceMonitor.updateInterval);
                setLoading(false);
            }).catch(console.log);
        },
        [baseInfo.imei],
    );


    useEffect(() => {
        if (baseInfo.imei === "") return;
        load();
    }, [baseInfo.imei, load]);
    return (
        <Box style={{width: "100%", height: "100%", display: "flex", flexDirection: "column"}}>
            <AppBar className={classes.appBar}>
                <Toolbar>
                    {loading ? null :
                        <StatusIcon
                            updateInterval={info.deviceMonitor.updateInterval}
                            lastSeen={info.deviceMonitor.lastSeen}
                            disableTime={true}/>}
                    <Typography variant="h6" className={classes.title}>
                        {loading ? "Loading" : `${info.school.name} - ${info.device.deviceName}`}
                    </Typography>
                    <IconButton color="inherit" onClick={load}>
                        <Refresh/>
                    </IconButton>
                    <IconButton color="inherit" onClick={close}>
                        <CloseIcon/>
                    </IconButton>
                </Toolbar>
                {loading ? <LinearProgress/> : null}
            </AppBar>
            {loading ? null : (
                <Grid
                    container
                    spacing={1}
                    justify={"flex-start"}
                    alignContent={"flex-start"}
                    style={{
                        flexGrow: 0,
                        maxWidth: "100%",
                        flexBasis: "100%",
                        padding: "16px"
                    }}>
                    <Grid item xs={12}>
                        <Card variant={"outlined"}>
                            <CardContent>
                                <Grid container
                                      style={{width: "100%"}}
                                      justify={"flex-start"}
                                      alignContent={"center"}
                                      spacing={2}>
                                    <Grid item xs={12} md={6}>
                                        IMEI: {info.device.imei}
                                        <IconButton
                                            onClick={() => {
                                                copy(info.device.imei,
                                                    {
                                                        format: "text/plain"
                                                    })
                                                enqueueSnackbar("IMEI复制成功", {variant: "success"});
                                            }}><FileCopy/>
                                        </IconButton>
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        更新间隔:
                                        <Input
                                            value={interval}
                                            style={{width: "40%"}}
                                            onChange={(e) => setInterval(parseInt(e.target.value))}/>
                                        <IconButton
                                            color={"primary"}
                                            style={{marginLeft: "5px"}}
                                            onClick={() => {
                                                onIntervalSave();
                                            }}>
                                            <Save/>
                                        </IconButton>
                                    </Grid>
                                    <Grid item xs={12} md={6}>
                                        上次可见: {info.deviceMonitor.lastSeen.toLocaleString()}
                                    </Grid>
                                    <Grid item xs={6}>
                                        网络类型: {info.deviceMonitor.current.networkType}
                                    </Grid>
                                    <Grid item xs={6}>
                                        运行时间: {secToString(info.deviceMonitor.current.upTime)}
                                    </Grid>
                                    <Grid item xs={6}>
                                        系统版本: {info.deviceMonitor.os_version}
                                    </Grid>
                                    <Grid item xs={6}>
                                        软件版本: {getSWVersion(info.deviceMonitor.Environment, "com.school.klass")}
                                    </Grid>
                                    <Grid item xs={6}>
                                        <Button
                                            color={"primary"} variant={"outlined"}
                                            onClick={() => setEnvDetail({...envDetail, open: true})}>
                                            环境信息
                                        </Button>
                                        <Dialog
                                            open={envDetail.open}
                                            onClose={() => setEnvDetail({...envDetail, open: false})}
                                            fullWidth scroll={"body"}
                                            maxWidth={"lg"}>
                                            <DialogTitle>
                                                环境信息
                                            </DialogTitle>
                                            <DialogContent>
                                                <List>
                                                    {info.deviceMonitor.Environment.packages.map(pkg =>
                                                        <ListItem>
                                                            <ListItemText
                                                                primary={`${pkg.name} | ${pkg.version}`}
                                                                secondary={pkg.applicationInfo.packageName}/>
                                                            <ListItemSecondaryAction>
                                                                <IconButton
                                                                    onClick={() => copy(pkg.applicationInfo.packageName)}>
                                                                    <FileCopy/>
                                                                </IconButton>
                                                            </ListItemSecondaryAction>
                                                        </ListItem>)}
                                                </List>
                                            </DialogContent>
                                            <DialogActions>
                                                <Button variant={"contained"} color={"secondary"}
                                                        onClick={() => setEnvDetail({...envDetail, open: false})}>
                                                    关闭
                                                </Button>
                                            </DialogActions>
                                        </Dialog>
                                    </Grid>
                                </Grid>
                            </CardContent>
                        </Card>
                    </Grid>
                    <Grid container item xs={12} style={{height: "100%"}}>
                        <JobList jobs={info.deviceRc} targets={[baseInfo]} imei={baseInfo.imei} fullScreen={false}/>
                    </Grid>
                </Grid>
            )}
        </Box>
    );
}
