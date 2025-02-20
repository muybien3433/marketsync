package pl.muybien.finance;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceUpdateException;

import java.util.Comparator;
import java.util.concurrent.*;

@Service
@Slf4j
@Getter
@Setter
public abstract class FinanceUpdater {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final PriorityBlockingQueue<Runnable> queue =
            new PriorityBlockingQueue<>(10, Comparator.comparingInt(Object::hashCode));
    private static final ConcurrentHashMap<String, Boolean> tasks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> taskPriorities = new ConcurrentHashMap<>();

    private boolean isUpdate = false;

    protected abstract void scheduleUpdate();
    protected abstract void updateAssets();

    protected void updateQueue(String task, int priority) {
        if (tasks.putIfAbsent(task, true) == null) {
            taskPriorities.put(task, priority);

            queue.offer(() -> {
                try {
                    setUpdate(true);
                    log.info("Processing task: {}", task);
                    updateAssets();
                } catch (Exception e) {
                    throw new FinanceUpdateException("Failed to update assets", e);
                } finally {
                    setUpdate(false);
                    tasks.remove(task);
                    taskPriorities.remove(task);
                    processNext();
                }
            });

            processNext();
        } else {
            log.info("Task {} is already in queue, skipping duplicate.", task);
        }
    }

    private synchronized void processNext() {
        if (!queue.isEmpty()) {
            Runnable nextTask = queue.poll();
            if (nextTask != null) {
                executor.submit(nextTask);
            }
        }
    }
}
