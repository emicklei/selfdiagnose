package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.TaskBackgroundRunner;
import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Suite of unit tests for {@link TaskBackgroundRunner}.
 *
 * @author Yaroslav Skopets
 */
public class TaskBackgroundRunnerTest extends TestCase {

    private final ExecutionContext context = new ExecutionContext();

    private ExecutorService executor;

    @Override
    protected void setUp() throws Exception {
        // `ExecutorService` that is very close to the one that will be used in production
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
    }

    @Override
    protected void tearDown() throws Exception {
        executor.shutdown();
    }

    public void testTaskThatDoesNotCompleteWithinTimeoutShouldFail() {
        // setup
        final TaskBackgroundRunner runner = new TaskBackgroundRunner(executor);

        // given
        final SleepTask slowTask = new SleepTask(100 * 10);
        slowTask.setTimeoutInMilliSeconds(100);

        // when
        final DiagnosticTaskResult slowTaskRun = runner.runWithin(slowTask, context, slowTask.getTimeoutInMilliSeconds());

        // then
        assertTrue(slowTaskRun.isFailed());
    }

    public void testTaskThatDoesNotCompleteWithinTimeoutShouldReleaseThread() throws InterruptedException {
        // setup
        final TaskBackgroundRunner runner = new TaskBackgroundRunner(executor);

        // given
        final SleepTask slowTask = new SleepTask(100 * 10);
        slowTask.setTimeoutInMilliSeconds(100);

        // when
        final DiagnosticTaskResult slowTaskRun = runner.runWithin(slowTask, context, slowTask.getTimeoutInMilliSeconds());

        // then
        assertTrue(slowTaskRun.isFailed());

        // given
        final SuccessfulTask normalTask = new SuccessfulTask("passing check");
        normalTask.setTimeoutInMilliSeconds(100);

        // when

        // give JVM a few cycles to switch threads and interrupt cancelled task
        Thread.sleep(10);
        // schedule the next task
        final DiagnosticTaskResult normalTaskRun = runner.runWithin(normalTask, context, normalTask.getTimeoutInMilliSeconds());

        // then
        assertTrue(normalTaskRun.isPassed());
    }

    public void testTaskShouldFailIfThereAreNoMoreThreadsLeftInThePool() throws InterruptedException {
        // setup
        final TaskBackgroundRunner runner = new TaskBackgroundRunner(executor);

        // given
        final SleepTask slowTask = new SleepTask(100 * 10);
        slowTask.setTimeoutInMilliSeconds(100);

        // when

        // all threads in the pool are blocked by long-running tasks
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                runner.runWithin(slowTask, context, slowTask.getTimeoutInMilliSeconds());
            }
        });
        // give JVM a few cycles to switch threads and start the task
        Thread.sleep(10);

        // given
        final SuccessfulTask normalTask = new SuccessfulTask("passing check");
        normalTask.setTimeoutInMilliSeconds(100);

        final DiagnosticTaskResult normalTaskRun = runner.runWithin(normalTask, context, normalTask.getTimeoutInMilliSeconds());

        // then
        assertTrue(normalTaskRun.isFailed()); // no more free threads in the pool
    }
}
