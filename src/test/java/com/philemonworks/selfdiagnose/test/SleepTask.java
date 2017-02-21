package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnoseException;
import com.philemonworks.selfdiagnose.DiagnosticTask;
import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;

import java.util.concurrent.CountDownLatch;

/**
 * Sample task that simulates a long-running activity.
 *
 * @author Ernest Micklei
 * @author Yaroslav Skopets
 */
public class SleepTask extends DiagnosticTask {

    private static final long serialVersionUID = -5193920249800557424L;

    private final CountDownLatch startedSignal = new CountDownLatch(1);

    private final CountDownLatch interruptedSignal = new CountDownLatch(1);

    private final long interval;

    @Override
    public String getDescription() {
        return null;
    }

    public SleepTask(long interval) {
        this.interval = interval;
    }

    @Override
    public void run(ExecutionContext ctx, DiagnosticTaskResult result) throws DiagnoseException {
        startedSignal.countDown();
        try {
            System.out.println(String.format("[SleepTask] going to sleep for %s ms", interval));
            Thread.sleep(interval);
            System.out.println(String.format("[SleepTask] awake after %s ms", interval));
        } catch (InterruptedException e) {
            interruptedSignal.countDown();
            e.printStackTrace();
        }
    }

    public CountDownLatch getStartedSignal() {
        return startedSignal;
    }

    public CountDownLatch getInterruptedSignal() {
        return interruptedSignal;
    }
}
