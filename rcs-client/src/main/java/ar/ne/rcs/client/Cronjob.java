package ar.ne.rcs.client;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Cronjob {
    private Timer timer;

    public Cronjob(int interval, Runnable task) {
        run(task, interval * 1000L);
    }

    public Cronjob(long interval, Runnable task) {
        run(task, interval);
    }

    public static void runOnce(Runnable task, Date date) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                t.cancel();
                task.run();
            }
        }, date);
    }

    public void run(Runnable task, int interval) {
        this.run(task, interval * 1000L);
    }

    public void run(Runnable task, long interval) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, 0, interval);
    }
}
