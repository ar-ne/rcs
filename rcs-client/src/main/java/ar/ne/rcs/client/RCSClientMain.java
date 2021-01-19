package ar.ne.rcs.client;


import ar.ne.rcs.client.network.Config;
import ar.ne.rcs.client.network.http.HandyRequests;
import ar.ne.rcs.shared.models.RemoteCommand;
import ar.ne.rcs.shared.models.status.DeviceStatus;
import ar.ne.rcs.shared.subprocess.SubProcess;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RCSClientMain {
    static Logger logger = Logger.getLogger(RCSClientMain.class.getName());
    private static RCSClientMain instance;
    String apiBase;
    String imei;
    Callable<DeviceStatus> deviceInfoCollector;
    Cronjob cronjob;
    AtomicInteger interval = new AtomicInteger(10);

    private RCSClientMain(String apiBase, String imei, Callable<DeviceStatus> deviceInfoCollector) {
        this.apiBase = apiBase;
        this.imei = imei;
        this.deviceInfoCollector = deviceInfoCollector;
    }

    public static void newInstance(String apiBase, String imei, Callable<DeviceStatus> deviceInfoCollector) {
        if (instance == null) {
            instance = new RCSClientMain(apiBase, imei, deviceInfoCollector);
            instance.Start();
            System.out.println("RCS Client Started!");
        }
    }

    private void task() {
        boolean updateSuccess = false;

        try {
            HandyRequests.uploadDeviceStatus(deviceInfoCollector.call());
        } catch (Exception e) {
            logger.warning(String.format("Upload device status failed with exception: \n%s", e));
        }

        //get update from server
        RemoteCommand[] remoteCommands = new RemoteCommand[0];
        int newInterval = interval.get();
        try {
            newInterval = HandyRequests.GetInterval();
            remoteCommands = HandyRequests.CheckRemoteCommandUpdate();
            updateSuccess = true;
        } catch (Exception e) {
            System.out.println("Update remote command list failed.");
        }

        if (updateSuccess) {
            try {
                if (newInterval != interval.get()) {
                    cronjob.run(this::task, newInterval);
                }

                for (RemoteCommand job : remoteCommands) {
                    Date date = job.schedule == null ? new Date() : job.schedule;
                    Cronjob.runOnce(() -> {
                        SubProcess process = new SubProcess(job.command);
                        try {
                            process.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //获取输出
                        job.setResult(process.getOutput());
                        job.setCode(process.getExitCode());
                        job.setStatus(2);

                        int retry = 1;
                        while (retry != -1) {
                            try {
                                HandyRequests.UpdateRemoteCommandResult(job);
                                retry = -1;
                            } catch (Exception ignored) {
                                try {
                                    retry++;
                                    Thread.sleep((long) retry * retry * 1000);
                                    logger.warning("Can not upload job:" + job.id + ",retrying:" + retry);
                                } catch (InterruptedException ignored1) {
                                }
                            }

                        }
                    }, date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void Start() {
        try {
            Config.initial(apiBase, imei);
            while (true) {
                try {
                    interval.set(HandyRequests.GetInterval());
                    cronjob = new Cronjob(interval.get(), this::task);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
