import {ReactElement} from "react";

export interface MergedInfo {
    deviceMonitor: DeviceMonitor;
    device: Device;
    school: School
    deviceRc: DeviceRC[]
}

export interface DeviceStatus {
    networkType: string;
    upTime: number;
    Time: Date;
}

export interface BaseInfo {
    id: number;
    imei: string;
    lastSeen: Date;
    current: string;
    updateInterval: number;
    schoolName: string;
    deviceName: string;
    os_version: string;
    upTime: string;
    icon: ReactElement;
}

export interface Device {
    id: number;
    deviceName: string;
    deviceNumber: string;
    sid: string;
    position: string;
    deviceStatus: string;
    deviceArea: string;
    imei: string;
}

export interface School {
    id: string;
    name: string;
}

export interface DeviceMonitor {
    id: number;
    imei: string;
    lastSeen: Date;
    history: string[];
    current: DeviceStatus;
    Environment: Environment;
    os_version: string;
    usageHistory: any;
    updateInterval: number;
}

export interface DeviceStatus {
    networkType: string;
    upTime: string;
}

export interface Environment {
    packages: [{
        name: string;
        version: string;
        applicationInfo: {
            packageName: string;
        }
    }]
}

export interface DeviceRC {
    id: number;
    targetIMEI: string;
    status: number;
    code: number;
    command: string;
    result: string;
    schedule: Date;
}
