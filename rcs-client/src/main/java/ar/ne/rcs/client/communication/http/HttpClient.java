package ar.ne.rcs.client.communication.http;

import ar.ne.rcs.client.utilities.shell.Executor;
import ar.ne.rcs.client.utilities.shell.Result;
import ar.ne.rcs.shared.models.RemoteCommand;
import ar.ne.rcs.shared.models.status.DeviceStatus;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static ar.ne.rcs.shared.consts.ClientConst.DEFAULT_CRON_INTERVAL;

public class HttpClient implements Runnable {


    static int interval = DEFAULT_CRON_INTERVAL;
    Function<Integer, Void> updateInterval;
    Callable<DeviceStatus> statusCollector;
    Executor executor;

    @Override
    public void run() {
        //检查任务列表
        try {
            RemoteCommand[] remoteCommands = HandyRequests.CheckRemoteCommandUpdate();
            for (RemoteCommand command : remoteCommands) {
                try {//别坏了一个剩下的不执行了
                    Result result = executor.exec(command.command);
                    command.setCode(result.getExitCode());
                    command.setResult(result.getOutput());
                    command.setStatus(2);
                    //上传已完成的任务结果
                    HandyRequests.UpdateRemoteCommandResult(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //检查interval更新
        try {
            //从api获取数据
            int newInterval = HandyRequests.GetInterval();
            //应用新的interval
            if (interval != newInterval) {
                interval = newInterval;
                updateInterval.apply(interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //上传设备状态
        try {
            HandyRequests.uploadDeviceStatus(statusCollector.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
