import {useSnackbar} from "notistack";
import {
    CardContent,
    Dialog,
    FormControl,
    Grid,
    IconButton,
    Input,
    InputAdornment,
    InputLabel,
    Slide,
    TextField
} from "@material-ui/core";
import React, {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {BaseInfo, DeviceStatus} from "../Interfaces/VideoDeviceMR";
import {secToString} from "../utils";
import {CellParams, CellValue, ColDef, DataGrid} from "@material-ui/data-grid";
import {StatusIcon} from "../Components/VideoDeviceMR/StatusIcon";
import {Clear, EventNote, Refresh} from "@material-ui/icons";
import {TransitionProps} from "@material-ui/core/transitions";
import {DeviceDetail} from "../Components/VideoDeviceMR/DeviceDetail";
import {EmptyTable} from "../Components/VideoDeviceMR/EmptyTable";
import {TablePager} from "../Components/VideoDeviceMR/TablePager";
import {JobList} from "../Components/VideoDeviceMR/JobList";
import {Autocomplete} from "@material-ui/lab";

const Transition = React.forwardRef(function Transition(
    props: TransitionProps & { children?: React.ReactElement },
    ref: React.Ref<unknown>,
) {
    return <Slide direction="up" ref={ref} {...props} />;
});


export const VideoDeviceMRC: React.FC = () => {
    const {enqueueSnackbar} = useSnackbar();
    const [loading, setLoading] = useState(true);
    const [baseInfos, setBaseInfos] = useState([] as BaseInfo[]);
    const [dialog, setDialog] = React.useState({open: false, data: {} as any, type: 0});
    const [filter, setFilter] = useState({school: "", keyword: ""});

    const handleOpen = (baseInfo: BaseInfo) => {
        setDialog({...dialog, open: true, data: baseInfo, type: 0});
    };

    const update = useCallback(
        () => {
            setLoading(true);
            axios.get("/web/ajax/VideoDeviceMR.ashx", {
                params: {
                    operation: "GetAllBaseInfo"
                }
            }).then(r => {
                if (r.status !== 200) {
                    return;
                }
                setBaseInfos(r.data.map((d: BaseInfo) => {
                    const lastSeen = new Date(d.lastSeen);
                    const current = JSON.parse(d.current) as DeviceStatus;
                    return {
                        ...d,
                        ...current,
                        upTime: secToString(current.upTime),
                        upTimeSec: current.upTime,
                        lastSeen
                    } as BaseInfo
                }));
                setLoading(false);
            }).catch(r => {
                enqueueSnackbar("网络错误，请刷新重试 " + r, {variant: "error"});
            });
        },
        [enqueueSnackbar],
    );

    const filteredTable = useCallback(
        () => (): BaseInfo[] => {
            const schoolFilter = (v: BaseInfo) => filter.school === "" ? true : v.schoolName === filter.school;
            const keywordFilter = (v: BaseInfo) => filter.keyword === "" ? true : (v.imei.includes(filter.keyword) || v.deviceName.includes(filter.keyword) || v.schoolName.includes(filter.keyword))
            return baseInfos.filter(v => (schoolFilter(v) && keywordFilter(v)))
        },
        [filter.keyword, filter.school, baseInfos],
    )();

    const handleClose = () => {
        setDialog({...dialog, open: false});
    };


    useEffect(() => {
        update();
    }, [enqueueSnackbar, update]);
    return (
        <div style={{height: "100%"}}>
            <div style={{height: "100%", display: "flex", flexDirection: "column"}}>
                <CardContent>
                    <Grid container justify={"flex-start"} alignItems={"center"} spacing={1}>
                        <Grid item xs={4} md={2}>
                            <FormControl>
                                <InputLabel htmlFor="text_input_keyword">关键字过滤</InputLabel>
                                <Input
                                    id="text_input_keyword"
                                    value={filter.keyword}
                                    onChange={(event) => {
                                        setFilter({
                                            ...filter,
                                            keyword: event.target.value as string
                                        });
                                    }}
                                    disabled={loading}
                                    endAdornment={
                                        <InputAdornment position="end">
                                            <IconButton
                                                disabled={loading}
                                                onClick={() => setFilter({
                                                    ...filter,
                                                    keyword: ""
                                                })}>
                                                <Clear/>
                                            </IconButton>
                                        </InputAdornment>
                                    }
                                />
                            </FormControl>
                        </Grid>
                        <Grid item xs={4} md={2}>
                            <Autocomplete
                                disabled={loading}
                                renderInput={params => <TextField {...params} label={"学校"}/>}
                                options={Array.from(new Set(baseInfos.map((d: BaseInfo) => d.schoolName)))}
                                onChange={(event, value) => {
                                    setFilter({...filter, school: value == null ? "" : value})
                                }}/>
                        </Grid>
                        <Grid item xs={1}>
                            <IconButton
                                color={"primary"}
                                disabled={loading}
                                onClick={() => setDialog({...dialog, open: true, type: 1})}>
                                <EventNote/>
                            </IconButton>
                        </Grid>
                        <Grid item xs={1}>
                            <IconButton
                                disabled={loading}
                                color={"primary"}
                                onClick={update}>
                                <Refresh/>
                            </IconButton>
                        </Grid>
                    </Grid>
                </CardContent>
                <Grid item xs={12}>
                    <DataGrid
                        rowHeight={52}
                        headerHeight={56}
                        autoPageSize
                        loading={loading}
                        checkboxSelection
                        disableSelectionOnClick
                        components={{
                            noRowsOverlay: EmptyTable,
                            pagination: TablePager
                        }}
                        columnBuffer={0}
                        onRowClick={param => handleOpen(param.row as BaseInfo)}
                        rows={filteredTable()}
                        columns={
                            [
                                {field: "id", width: 64},
                                {
                                    field: "icon",
                                    headerName: "状态/回报时间",
                                    width: 200,
                                    renderCell: params => {
                                        return (
                                            <StatusIcon
                                                lastSeen={params.row.lastSeen}
                                                updateInterval={params.row.updateInterval}
                                            />
                                        );
                                    },
                                    align: "center",
                                    sortComparator: (v1: CellValue, v2: CellValue, cellParams1: CellParams, cellParams2: CellParams) => {
                                        return cellParams1.row.lastSeen.getTime() - cellParams2.row.lastSeen.getTime()
                                    }
                                },
                                {field: "imei", width: 160},
                                {field: "deviceName", headerName: "设备名称", width: 256},
                                {
                                    field: "schoolName",
                                    headerName: "学校", width: 256,
                                },
                                {
                                    field: "upTime", headerName: "开机时间",
                                    sortComparator: ((v1, v2, cellParams1, cellParams2) =>
                                        cellParams1.row.upTimeSec - cellParams2.row.upTimeSec)
                                },
                                {field: "networkType", headerName: "网络类型", width: 128},
                            ] as ColDef[]
                        }
                    />
                </Grid>
            </div>
            <Dialog fullScreen open={dialog.open} TransitionComponent={Transition}>
                {dialog.type === 0 ? <DeviceDetail baseInfo={dialog.data} close={handleClose}/> : null}
                {dialog.type === 1 ? <JobList targets={baseInfos} fullScreen={true} handleClose={handleClose}/> : null}
            </Dialog>
        </div>
    );
};
