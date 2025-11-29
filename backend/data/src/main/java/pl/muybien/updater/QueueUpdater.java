package pl.muybien.updater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.muybien.exception.DataUpdateException;

import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public abstract class QueueUpdater {

    private static final int MAX_QUEUE_CAPACITY = 50;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BACKOFF_MS = 5000;

    private static final ExecutorService taskExecutor = Executors.newSingleThreadExecutor();
    private static final BlockingQueue<FinanceUpdateTask> taskQueue = new LinkedBlockingQueue<>(MAX_QUEUE_CAPACITY);
    private static final Set<String> pendingTasks = ConcurrentHashMap.newKeySet();
    private static final AtomicInteger activeTasks = new AtomicInteger(0);
    private static volatile boolean processingStarted = false;

    public abstract void scheduleUpdate();
    public abstract void updateAssets();

    protected QueueUpdater() {
        startTaskProcessingOnce();
    }

    private static void startTaskProcessingOnce() {
        synchronized (QueueUpdater.class) {
            if (!processingStarted) {
                startTaskProcessing();
                processingStarted = true;
            }
        }
    }

    private static void startTaskProcessing() {
        taskExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    FinanceUpdateTask task = taskQueue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Task processing interrupted");
                    break;
                }
            }
        });
    }

    public void enqueueUpdate(String taskIdentifier) {
        log.debug("Enqueuing update for task: " + taskIdentifier);
        if (pendingTasks.add(taskIdentifier)) {
            try {
                Runnable updater = this::updateAssets;
                FinanceUpdateTask task = new FinanceUpdateTask(taskIdentifier, updater);
                boolean enqueued = taskQueue.offer(task, 500, TimeUnit.MILLISECONDS);

                if (enqueued) {
                    log.debug("Task {} added to queue", taskIdentifier);
                } else {
                    log.error("Task queue full, rejecting {}", taskIdentifier);
                    pendingTasks.remove(taskIdentifier);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Task enqueue interrupted for {}", taskIdentifier);
                pendingTasks.remove(taskIdentifier);
            }
        } else {
            log.debug("Task {} already queued", taskIdentifier);
        }
    }

    private static void processTask(FinanceUpdateTask task) {
        activeTasks.incrementAndGet();
        long startNs = System.nanoTime();
        try {
            log.info("Processing task: {}", task.taskKey());
            executeUpdateWithRetry(task);
            double seconds = (System.nanoTime() - startNs) / 1_000_000_000.0;
            log.info("Task {} completed successfully in {}s", task.taskKey(), String.format("%.3f", seconds));
        } catch (Exception e) {
            double seconds = (System.nanoTime() - startNs) / 1_000_000_000.0;
            log.error("Task {} failed after {}s", task.taskKey(), String.format("%.3f", seconds), e);
            // TODO: Notify support with e
        } finally {
            activeTasks.decrementAndGet();
            pendingTasks.remove(task.taskKey());
        }
    }

    private static void executeUpdateWithRetry(FinanceUpdateTask task) {
        int attempts = 1;
        while (attempts <= MAX_RETRIES) {
            try {
                task.updater().run();
                return;
            } catch (Exception e) {
                if (attempts == MAX_RETRIES) {
                    log.error("Failed to process task {} after {} attempts", task.taskKey(), MAX_RETRIES, e);
                    throw new DataUpdateException("Data update failed after retries", e);
                }

                long backoff = RETRY_BACKOFF_MS * attempts;
                log.warn("Retrying task {} (attempt {}/{}) after {} ms", task.taskKey(), attempts, MAX_RETRIES, backoff);

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new DataUpdateException("Retry interrupted", ie);
                }

                attempts++;
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Initiating graceful shutdown");
        if (!taskExecutor.isShutdown()) {
            taskExecutor.shutdown();
            try {
                if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Forcing shutdown after timeout");
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Shutdown interrupted");
            }
        }
    }

    private record FinanceUpdateTask(String taskKey, Runnable updater) {
        @Override
        public String toString() {
            return "Task[" + taskKey + "]";
        }
    }
}