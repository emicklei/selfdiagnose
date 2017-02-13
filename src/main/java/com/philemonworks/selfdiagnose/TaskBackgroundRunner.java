package com.philemonworks.selfdiagnose;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * TaskBackgroundRunner will run a task on a background thread and expects it to complete within a specified amount of seconds.
 * If the task does not complete in time, it will report an error. The task is not aborted so may complete afterwards.
 * 
 * @author emicklei
 * @author Yaroslav Skopets
 */
public class TaskBackgroundRunner {
    private static final Logger log = Logger.getLogger(SelfDiagnose.class);

    private static ExecutorService newCachedThreadPool(int maximumPoolSize) {
        return new ThreadPoolExecutor(0, maximumPoolSize,
                // threads are removed from the pool if no longer used.
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        final Thread runner = new Thread(r);
                        runner.setDaemon(true);
                        runner.setName("SelfDiagnose.TaskBackgroundRunner");
                        return runner;
                    }
                });
    }

    private final ExecutorService executor;

    public TaskBackgroundRunner(int maximumPoolSize) {
        this(newCachedThreadPool(maximumPoolSize));
    }

    public TaskBackgroundRunner(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Run the task but do not wait for it to complete after the specified number of milliseconds.
     */
    public DiagnosticTaskResult runWithin(final DiagnosticTask task, final ExecutionContext ctx, final int timeoutInMilliseconds) {
        try {
            final Future<DiagnosticTaskResult> future = executor.submit(new Callable<DiagnosticTaskResult>() {
                @Override
                public DiagnosticTaskResult call() throws Exception {
                    return task.run(ctx);
                }
            });
            return waitForCompletion(task, timeoutInMilliseconds, future);
        } catch (RejectedExecutionException e) {
            return handleFailure(e, "task cannot be scheduled for execution (no more threads left in the pool)", task, timeoutInMilliseconds);
        }
    }

    protected DiagnosticTaskResult waitForCompletion(final DiagnosticTask task, final int timeoutInMilliseconds, final Future<DiagnosticTaskResult> future) {
        try {
            DiagnosticTaskResult result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
            // if result was not set for some reason then create it with a failure
            if (result == null) {
                result = task.createResult();
                result.setFailedMessage("unexpected empty result in background runner");
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return handleFailure(e, "task execution was interrupted", task, timeoutInMilliseconds);
        } catch (TimeoutException e) {
            return handleFailure(e, "task execution timed out after " + timeoutInMilliseconds + " milliseconds", task, timeoutInMilliseconds);
        } catch (Exception e) {
            return handleFailure(e, "task execution failed", task, timeoutInMilliseconds);
        } finally {
            if (!future.isDone()) {
                // cancel the future to release a thread back to the pool
                future.cancel(true);
            }
        }
    }

    protected DiagnosticTaskResult handleFailure(Exception e, String message, DiagnosticTask task, int timeoutInMilliseconds) {
        log.error(message, e);

        final DiagnosticTaskResult result = task.createResult();
        result.setFailedMessage(message);
        result.setExecutionTime(timeoutInMilliseconds); // could be less
        return result;
    }
}
