package io.hikarilan.gamesenselib.artifacts;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.function.Consumer;

/**
 * 一个持有 {@link Consumer} 队列的对象。
 * <p>
 * An object that holds a {@link Consumer} queue.
 *
 * @param <T> the given argument of the {@link Consumer}
 */
public interface IConsumerQueueHolder<T> {
    /**
     * 获取队列
     * <p>
     * Get the queue
     *
     * @return the queue
     */
    @NotNull
    Queue<Consumer<T>> getQueue();

    /**
     * 取出一个 {@link Consumer}（如果有）并执行
     * <p>
     * Take out a {@link Consumer} (if any) and execute it
     *
     * @param t the given argument
     * @return return true if the queue has next element
     */
    default boolean consumeQueue(T t) {
        val element = getQueue().poll();
        if (element == null) return false;
        element.accept(t);
        return getQueue().peek() != null;
    }

    /**
     * 取出并执行所有的 {@link Consumer}，直到队列为空
     * <p>
     * Take out and execute all {@link Consumer}s until the queue is empty
     *
     * @param t the given argument
     */
    @SuppressWarnings("StatementWithEmptyBody")
    default void consumeAllQueue(T t) {
        while (consumeQueue(t)) ;
    }
}
