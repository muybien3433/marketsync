package pl.muybien.finance.updater;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceUpdateException;

import javax.annotation.PreDestroy;
import java.util.Set;
import java.util.concurrent.*;

@Service
@Slf4j
@Getter
@Setter
public abstract class FinanceUpdater {

    protected abstract void scheduleUpdate();
    protected abstract void updateAssets();

    private final ExecutorService taskExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<FinanceUpdateTask> taskQueue = new LinkedBlockingQueue<>();
    private final Set<String> pendingTasks = ConcurrentHashMap.newKeySet();
    private volatile boolean isUpdating = false;

    protected FinanceUpdater() {
        startTaskProcessing();
    }

    public void enqueueUpdate(String taskIdentifier) {
        if (pendingTasks.add(taskIdentifier)) {
            boolean enqueued = taskQueue.offer(new FinanceUpdateTask(taskIdentifier));
            if (enqueued) {
                log.debug("Task {} added to queue", taskIdentifier);
            } else {
                log.warn("Failed to add task {} to queue", taskIdentifier);
                pendingTasks.remove(taskIdentifier);
            }
        } else {
            log.info("Task {} already pending execution", taskIdentifier);
        }
    }

    private void startTaskProcessing() {
        taskExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    FinanceUpdateTask task = taskQueue.take();
                    processTask(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Task processing interrupted");
                    break;
                } catch (Exception e) {
                    log.error("Unexpected error in task processor", e);
                }
            }
        });
    }

    private synchronized void processTask(FinanceUpdateTask task) {
        if (!isUpdating) {
            try {
                isUpdating = true;
                log.info("Processing task: {}", task.taskKey());
                updateAssets();
            } catch (Exception e) {
                log.error("Failed to process task: {}", task.taskKey(), e);
                throw new FinanceUpdateException("Asset update failed", e);
            } finally {
                isUpdating = false;
                pendingTasks.remove(task.taskKey());
            }
        } else {
            log.info("Update already in progress. Re-queuing task: {}", task.taskKey());
            boolean requeued = taskQueue.offer(task);
            if (!requeued) {
                log.error("Failed to re-queue task: {}", task.taskKey());
                pendingTasks.remove(task.taskKey());
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down finance updater");
        taskExecutor.shutdownNow();
        try {
            if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Task executor did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Shutdown interrupted");
        }
    }

    private record FinanceUpdateTask(String taskKey) {
    }
}