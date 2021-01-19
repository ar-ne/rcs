import React, {useCallback, useState} from "react";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogProps,
    DialogTitle,
    FormControl,
    Grid,
    InputLabel,
    MenuItem,
    Select,
    TextField
} from "@material-ui/core";
import {BaseInfo} from "../../Interfaces/VideoDeviceMR";
import {Autocomplete} from "@material-ui/lab";
import {useSnackbar} from "notistack";
import match from "autosuggest-highlight/match";
import parse from "autosuggest-highlight/parse";
import {CloseIcon} from "@material-ui/data-grid";
import styled from "styled-components";
import dateFormat from "dateformat";
import axios from "axios";


export interface AddJobFormProps extends DialogProps {
    targets: BaseInfo[],
    handleClose: () => void;
    form: Partial<{
        mode: number,
        targets: BaseInfo[],
        os_version: string,
        command: string,
        schedule: Date
    }>
}

export const AddJobForm: React.FC<AddJobFormProps> = (p: AddJobFormProps) => {
    const [form, setForm] = useState({
        mode: 0,
        targets: [] as BaseInfo[],
        os_version: "",
        command: "",
        schedule: new Date(),
        ...p.form
    });
    const {enqueueSnackbar} = useSnackbar();

    const handleConfirm = useCallback(() => {
        switch (form.mode) {
            case 0:
                break;
            case 1:
                if (form.targets.length === 0) {
                    enqueueSnackbar("请选择设备！", {variant: "warning"})
                    return;
                }
                break;
            case 2:
                if (form.os_version.trim() === "") {
                    enqueueSnackbar("请选择系统版本！", {variant: "warning"})
                    return;
                }
                break;
        }
        if (form.command.trim() === "") {
            enqueueSnackbar("请输入命令！", {variant: "warning"})
            return;
        }

        axios.post("/web/ajax/VideoDeviceMR.ashx?operation=AddJob", {
            ...form,
            schedule: dateFormat(form.schedule, "yyyy-mm-dd'T'HH:MM:ss")
        })
            .then(r => {
                enqueueSnackbar(`${r.data}条数据已成功添加`, {variant: "success"});
            });

        p.handleClose();
    }, [enqueueSnackbar, form, p])
    return (
        <Dialog style={{width: "100%"}} {...p}>
            <DialogTitle>添加任务</DialogTitle>
            <DialogContent>
                <Grid container justify={"flex-start"} alignItems={"center"} spacing={1}>
                    <Grid item xs={12} container spacing={1}>
                        <Grid item xs={4}>
                            <FormControl style={{width: "100%"}}>
                                <InputLabel>目标设备</InputLabel>
                                <Select
                                    value={form.mode}
                                    onChange={event => setForm({...form, mode: event.target.value as number})}>
                                    <MenuItem value={0}>全部设备</MenuItem>
                                    <MenuItem value={1}>指定设备</MenuItem>
                                    <MenuItem value={2}>系统版本</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} md={8}>
                            {form.mode === 1 ? (
                                <Autocomplete
                                    disableCloseOnSelect
                                    options={p.targets}
                                    getOptionLabel={(option) => `${option.deviceName} ${option.schoolName} ${option.imei} ${option.os_version}`}
                                    groupBy={(option => option.schoolName)}
                                    renderOption={(option, {inputValue}) => {
                                        const text = `${option.deviceName} ${option.schoolName} ${option.imei} ${option.os_version}`;
                                        const matches = match(text, inputValue);
                                        const parts = parse(text, matches);
                                        return (
                                            <div>
                                                {parts.map((part, index) => (
                                                    <span
                                                        key={index}
                                                        style={{fontWeight: part.highlight ? 700 : 400}}>
                                                        {part.text}
                                                    </span>
                                                ))}
                                            </div>
                                        );
                                    }}
                                    defaultValue={p.targets}
                                    renderTags={((value, getTagProps) =>
                                        value.map((a, index) => <Tag label={a.imei} {...getTagProps({index})}/>))}
                                    onChange={(event, value) =>
                                        setForm({...form, targets: value})}
                                    multiple
                                    limitTags={1}
                                    style={{width: 300}}
                                    renderInput={(params) =>
                                        <TextField {...params} label="设备"/>}
                                />
                            ) : null}
                            {form.mode === 2 ? (
                                <Autocomplete
                                    options={Array.from(new Set(p.targets.map(d => d.os_version)))}
                                    getOptionLabel={(option) => option as string}
                                    onChange={(e, v) => setForm({...form, os_version: v || ""})}
                                    style={{width: 300}}
                                    renderInput={(params) =>
                                        <TextField {...params} label="版本"/>}
                                />
                            ) : null}
                        </Grid>
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            rows={3}
                            label={"命令"}
                            multiline
                            style={{width: "100%"}}
                            onChange={event => setForm({...form, command: event.target.value as string})}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            id="datetime-local"
                            label={"时间"}
                            type="datetime-local"
                            value={dateFormat(form.schedule, "yyyy-mm-dd'T'HH:MM:ss")}
                            onChange={event => setForm({...form, schedule: new Date(event.target.value)})}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                    </Grid>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button
                    color={"secondary"}
                    variant={"contained"}
                    onClick={p.handleClose}>
                    关闭
                </Button>
                <Button
                    color={"primary"}
                    variant={"contained"}
                    onClick={handleConfirm}>
                    确定
                </Button>
            </DialogActions>
        </Dialog>
    )
}

const Tag = styled(({label, onDelete, ...props}) => (
    <div {...props}>
        <span>{label}</span>
        <CloseIcon onClick={onDelete}/>
    </div>
))`
  display: flex;
  align-items: center;
  height: 24px;
  margin: 2px;
  line-height: 22px;
  background-color: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  box-sizing: content-box;
  padding: 0 4px 0 10px;
  outline: 0;
  overflow: hidden;

  &:focus {
    border-color: #40a9ff;
    background-color: #e6f7ff;
  }

  & span {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  & svg {
    font-size: 12px;
    cursor: pointer;
    padding: 4px;
  }`;
