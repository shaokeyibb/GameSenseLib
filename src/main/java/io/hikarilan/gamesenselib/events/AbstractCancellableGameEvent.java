package io.hikarilan.gamesenselib.events;

import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Getter;
import lombok.Setter;

/**
 * 代表一个可取消的游戏事件。
 * <br/>
 * 一个可被取消的事件不会被游戏实例继续执行，但仍会继续传递给其他监听器。
 * <p>
 * Represents a cancellable game event.
 * <br/>
 * A cancellable event will not be executed by the game instance, but will still be passed to other listeners.
 */
public abstract class AbstractCancellableGameEvent extends AbstractGameEvent {

    /**
     * Get/Set the cancellation state of this event.
     * A cancelled event will not be executed in the server, but will still pass to other event listeners
     */
    @Getter
    @Setter
    private boolean cancelled = false;

    protected AbstractCancellableGameEvent(AbstractGame game) {
        super(game);
    }
}