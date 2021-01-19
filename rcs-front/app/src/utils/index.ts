export const secToString = (sec: number): string => {
    let hh: number, mm: number, ss: number;
    let tmp = sec;
    hh = Math.floor(tmp / 3600);
    tmp -= hh * 3600;
    mm = Math.floor(tmp / 60);
    tmp -= mm * 60;
    ss = Math.ceil(tmp);
    return `${hh < 10 ? '0' + hh : hh}:${mm < 10 ? '0' + mm : mm}:${ss < 10 ? '0' + ss : ss}`;
}
