package pl.muybien.updater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.muybien.finance.exception.FinanceUpdateException;

import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public abstract class QueueUpdater {

    private static final int MAX_QUEUE_CAPACITY = 100;
    private static final int maxRetries = 3;

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
        try {
            log.info("Processing task: {}", task.taskKey());
            executeUpdateWithRetry(task);
        } finally {
            activeTasks.decrementAndGet();
            pendingTasks.remove(task.taskKey());
        }
    }

    private static void executeUpdateWithRetry(FinanceUpdateTask task) {
        int attempts = 1;
        while (attempts <= maxRetries) {
            try {
                task.updater().run();
                return;
            } catch (Exception e) {
                if (attempts == maxRetries) {
                    log.error("Failed to process task {} after {} attempts", task.taskKey(), maxRetries, e);
                    throw new FinanceUpdateException("Asset update failed after retries", e);
                }
                log.warn("Retrying task {} (attempt {}/{})", task.taskKey(), attempts, maxRetries);
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