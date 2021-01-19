import React, {useEffect, useState} from "react";
import {CheckCircle, Error, Warning} from "@material-ui/icons";
import {Box, Grid, Tooltip} from "@material-ui/core";

export interface DeviceStatusProps {
    lastSeen: Date;
    updateInterval: number;
    disableTime?: boolean
}

export const StatusIcon: React.FC<DeviceStatusProps> = (props: DeviceStatusProps) => {
    const [now, setNow] = useState(new Date());
    const timePassed = (now.getTime() - props.lastSeen.getTime()) / 1000;
    useEffect(() => {
        const interval = setInterval(() => setNow(new Date()), 1000);
        return () => {
            clearInterval(interval);
        };
    }, []);

    return (
        <Box>
            <Grid container alignItems={"center"}>
                <Tooltip
                    title={<span style={{whiteSpace: 'pre-line'}}>
                        {`上次回报: ${Math.floor(timePassed)}秒前\n`}
                        {`回报间隔: ${props.updateInterval}秒`}
                    </span>}>
                    <div style={{display: "flex", alignItems: "center"}}>
                        {
                            timePassed <= props.updateInterval
                                ? <CheckCircle style={{paddingRight: "3px", color: "green"}}/>
                                : timePassed > props.updateInterval * 2
                                ? <Error color={"error"} style={{paddingRight: "3px"}}/>
                                : <Warning style={{color: "#ffc107", paddingRight: "3px"}}/>
                        }
                        {props.disableTime ? null : props.lastSeen.toLocaleString()}
                    </div>
                </Tooltip>
            </Grid>
        </Box>
    );
}
