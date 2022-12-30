package io.hikarilan.gamesenselib.events;

import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * 所有游戏事件的基类
 * <p>
 * Base class of all game events.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractGameEvent {

    /**
     * 事件发生的游戏实例
     * <p>
     * The game instance where the event fired.
     */
    @Getter
    private final AbstractGame game;

    /**
     * 检查事件属于指定的游戏实例
     * <p>
     * Check if the event belongs to the specified game instance.
     *
     * @param game game instance
     * @return true if the event belongs to the specified game instance
     */
    public boolean canHandle(@Nullable AbstractGame game) {
        return this.game == game;
    }
}