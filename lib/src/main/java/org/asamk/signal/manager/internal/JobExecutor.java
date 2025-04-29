package org.asamk.signal.manager.internal;

import org.asamk.signal.manager.helper.Context;
import org.asamk.signal.manager.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobExecutor implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
    private final Context context;
    private final ExecutorService executorService;
    private Job running;
    private final Queue<Job> queue = new ArrayDeque<>();
    private boolean terminating = false;

    public JobExecutor(final Context context) {
        this.context = context;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void enqueueJob(Job job) {
        boolean executorShutdown = executorService.isShutdown();

        synchronized (queue) {
            // Ensure that if terminating is set, no new
            // jobs will be queued, and if the queue is empty
            // when terminating was set, it'll remain empty.
            if (executorShutdown || terminating) {
                logger.debug("Not enqueuing {} job, shutting down", job.getClass().getSimpleName());
                return;
            }

            logger.trace("Enqueuing {} job", job.getClass().getSimpleName());
            queue.add(job);
        }

        runNextJob();
    }

    private void runNextJob() {
        Job job;
        synchronized (queue) {
            if (running != null) {
                return;
            }
            job = queue.poll();
            running = job;

            // queue is empty
            if (job == null) {
                queue.notifyAll();
                return;
            }
        }

        logger.debug("Running {} job", job.getClass().getSimpleName());
        executorService.execute(() -> {
            try {
                job.run(context);
            } catch (Throwable e) {
                logger.warn("Job {} failed", job.getClass().getSimpleName(), e);
            } finally {
                synchronized (queue) {
                    running = null;
                }
                logger.debug("Finished {} job", job.getClass().getSimpleName());
                runNextJob();
            }
        });
    }

    @Override
    public void close() {
        logger.debug("Stopping JobExecutor: waiting for the queue to empty");
        synchronized (queue) {
            // Stop accepting new jobs.
            terminating = true;

            // Wait till queued jobs are processed and the queue is empty.
            while (!queue.isEmpty()){
                try {
                    queue.wait(1000L);
                } catch (InterruptedException e) {
                    logger.info("Discarding JobExecutor job queue");
                    queue.clear();
                    break;
                }
            }
        }

        logger.debug("Stopping JobExecutor: waiting for the last job to finish");
        executorService.close();

        logger.debug("Stopped JobExecutor");
    }
}
