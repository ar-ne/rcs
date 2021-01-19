import {
    AppBar,
    Box,
    Button,
    Card,
    CardContent,
    CardHeader,
    createStyles,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    makeStyles,
    Theme,
    Toolbar,
    Typography
} from "@material-ui/core";
import React, {useCallback, useEffect, useState} from "react";
import {Add, ClearAll, Done, Refresh, Send, WatchLater} from "@material-ui/icons";
import {CloseIcon, ColDef, DataGrid} from "@material-ui/data-grid";
import {EmptyTable} from "./EmptyTable";
import {TablePager} from "./TablePager";
import {BaseInfo, DeviceRC} from "../../Interfaces/VideoDeviceMR";
import axios from "axios";
import {AddJobForm} from "./AddJobForm";
import {useSnackbar} from "notistack";

export interface JobListProps {
    imei?: string;
    jobs?: DeviceRC[];
    fullScreen?: boolean;
    handleClose?: () => void;
    targets: BaseInfo[]
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

export const JobList: React.FC<JobListProps> = (props: JobListProps) => {
    const classes = useStyles();
    const [dialog, setDialog] = useState({open: false, data: {} as any});
    const [jobs, setJobs] = useState([] as DeviceRC[]);
    const [loading, setLoading] = useState(false);
    const [form, setForm] = useState({open: false, imei: [] as string[]});
    const {enqueueSnackbar} = useSnackbar();

    const load = useCallback(() => {
        setLoading(true);
        axios.get("/web/ajax/VideoDeviceMR.ashx", {
            params: {
                operation: "GetJobs",
                imei: props.imei
            }
        }).then(r => {
            setJobs(r.data);
            setLoading(false);
        });
    }, [props.imei]);

    const clearAll = useCallback(() => {
        axios.get("/web/ajax/VideoDeviceMR.ashx", {
            params: {
                operation: "ClearJobs",
                imei: props.imei
            }
        }).then(() => {
            enqueueSnackbar("删除成功!", {variant: "success"})
            load();
        })
    }, [enqueueSnackbar, load, props.imei]);

    useEffect(() => {
        if (props.jobs !== undefined) {
            setJobs(props.jobs);
            return;
        } else {
            load();
        }
    }, [load, props.imei, props.jobs])
    return (
        <Box style={{width: "100%", height: "100%", display: "flex", flexDirection: "column"}}>
            {props.fullScreen ? <AppBar className={classes.appBar}>
                <Toolbar>
                    <Typography variant="h6" className={classes.title}>
                        JobList
                    </Typography>
                    <IconButton color={"inherit"} onClick={load}>
                        <Refresh/>
                    </IconButton>
                    <IconButton color="inherit" onClick={props.handleClose}>
                        <CloseIcon/>
                    </IconButton>
                </Toolbar>
            </AppBar> : null}
            <Card
                style={{
                    flexGrow: 0,
                    maxWidth: "100%",
                    flexBasis: "100%",
                    display: "flex",
                    flexDirection: "column"
                }}>
                <CardHeader
                    action={(
                        <Box>
                            <IconButton
                                title={"添加任务"}
                                onClick={() => setForm({
                                    ...form,
                                    open: true,
                                })}><Add/></IconButton>
                            <IconButton title={"清除全部"} onClick={clearAll}><ClearAll/></IconButton>
                        </Box>
                    )}
                    title={props.fullScreen ? "" : "JobList"}/>
                <CardContent style={{
                    flexGrow: 0,
                    maxWidth: "100%",
                    flexBasis: "100%",
                }}>
                    <DataGrid
                        components={{
                            noRowsOverlay: EmptyTable,
                            pagination: TablePager
                        }}
                        autoPageSize
                        rowHeight={52}
                        headerHeight={56}
                        loading={loading}
                        onCellClick={param => {
                            switch (param.field) {
                                case "command":
                                case "result": {
                                    setDialog({open: true, data: param.row[param.field]})
                                }
                            }
                        }}
                        rows={jobs}
                        columns={
                            [
                                {field: "targetIMEI", headerName: "目标设备", width: 160},
                                {
                                    field: "status",
                                    headerName: "状态",
                                    width: 128,
                                    renderCell: params => (
                                        <Box style={{display: "flex", justifyItems: "center", alignItems: "center"}}>
                                            {params.value === 0 ? <WatchLater color={"secondary"}/> : null}
                                            {params.value === 1 ? <Send color={"primary"}/> : null}
                                            {params.value === 2 ? <Done style={{color: "green"}}/> : null}
                                            {params.value === 0 ? "等待" : null}
                                            {params.value === 1 ? "正在执行" : null}
                                            {params.value === 2 ? "已完成" : null}
                                        </Box>
                                    )
                                },
                                {field: "code", headerName: "Code"},
                                {field: "command", headerName: "命令行", width: 256},
                                {field: "result", headerName: "结果", width: 256},
                                {field: "schedule", headerName: "计划", width: 256}
                            ] as ColDef[]
                        }
                    />
                </CardContent>
            </Card>
            <Dialog
                fullWidth
                maxWidth={"xl"}
                open={dialog.open}
                scroll={"body"}
                onClose={() => setDialog({...dialog, open: false})}>
                <DialogTitle>详情</DialogTitle>
                <DialogContent>
                        <pre>
                            {dialog.data}
                        </pre>
                </DialogContent>
                <DialogActions>
                    <Button
                        variant={"contained"}
                        color={"secondary"}
                        onClick={() => setDialog({...dialog, open: false})}>
                        关闭
                    </Button>
                </DialogActions>
            </Dialog>

            <AddJobForm
                open={form.open} fullWidth
                targets={props.targets}
                handleClose={() => {
                    load();
                    setForm({...form, open: false});
                }}
                form={props.fullScreen ? {} : {mode: 1, targets: props.targets}}/>
        </Box>
    )
}
