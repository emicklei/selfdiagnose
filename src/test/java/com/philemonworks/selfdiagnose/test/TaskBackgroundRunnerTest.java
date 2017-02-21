package com.philemonworks.selfdiagnose.test;

import com.philemonworks.selfdiagnose.DiagnosticTaskResult;
import com.philemonworks.selfdiagnose.ExecutionContext;
import com.philemonworks.selfdiagnose.TaskBackgroundRunner;
import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
        executor = new ThreadPoolExecutor(1, 1, 0L, MILLISECONDS, new SynchronousQueue<Runnable>());
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

        // long-running task must be interrupted (we might need to wait a little)
        assertTrue(slowTask.getInterruptedSignal().await(100, MILLISECONDS));
    }

    public void testTaskShouldFailIfThereAreNoMoreThreadsLeftInThePool() throws InterruptedException {
        // setup
        final TaskBackgroundRunner runner = new TaskBackgroundRunner(executor);

        // given
        final SleepTask slowTask = new SleepTask(100 * 10);
        slowTask.setTimeoutInMilliSeconds(100 * 3);

        // when

        // start a long-running task from another tread
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                runner.runWithin(slowTask, context, slowTask.getTimeoutInMilliSeconds());
            }
        });

        // then

        // all threads in the pool (just 1 in our case) must be occupied by long-running tasks
        assertTrue(slowTask.getStartedSignal().await(100, MILLISECONDS));

        // given
        final SuccessfulTask normalTask = new SuccessfulTask("passing check");
        normalTask.setTimeoutInMilliSeconds(100);

        // when
        final DiagnosticTaskResult normalTaskRun = runner.runWithin(normalTask, context, normalTask.getTimeoutInMilliSeconds());

        // then
        assertTrue(normalTaskRun.isFailed()); // no more free threads in the pool
    }
}
