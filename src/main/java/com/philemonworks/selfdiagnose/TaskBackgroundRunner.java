package com.philemonworks.selfdiagnose;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * TaskBackgroundRunner will run a task on a background thread and expects it to complete within a specified amount of seconds.
 * If the task does not complete in time, it will report an error. The task is not aborted so may complete afterwards.
 * 
 * @author emicklei
 *
 */
public class TaskBackgroundRunner {
    // threads are removed from the pool if no longer used.
    static final ExecutorService SharedPool = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread runner = new Thread(r);
            runner.setName("SelfDiagnose.TaskBackgroundRunner");
            return runner;
        }
    });

    DiagnosticTaskResult result;

    /**
     * Run the task but do not wait for it to complete after the specified number of milliseconds.
     */
    public DiagnosticTaskResult runWithin(final DiagnosticTask task, final ExecutionContext ctx, int timeoutInMilliseconds) {
        final CountDownLatch latch = new CountDownLatch(1);
        SharedPool.execute(new Runnable() {
            public void run() {
                DiagnosticTaskResult actualResult = task.run(ctx);
                // do not overwrite a timeout result
                if (TaskBackgroundRunner.this.result != null) {
                    TaskBackgroundRunner.this.result = actualResult;
                }
                latch.countDown();
            }
        });
        try {
            boolean inTime = latch.await(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
            if (!inTime) {
                result = task.createResult();
                result.setFailedMessage("task execution timed out after " + timeoutInMilliseconds + " milliseconds");
                result.setExecutionTime(timeoutInMilliseconds);
            }
        } catch (InterruptedException e) {
            result = task.createResult();
            result.setFailedMessage("interrupted execution");
            result.setExecutionTime(timeoutInMilliseconds); // could be less
        }
        return result;
    }
}
