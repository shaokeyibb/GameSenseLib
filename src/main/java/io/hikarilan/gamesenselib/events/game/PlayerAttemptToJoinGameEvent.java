package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractCancellableGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * 玩家尝试加入游戏事件。
 * <br/>
 * 该事件应由指定游戏实例或模块监听，用于检测玩家是否可以加入指定的游戏实例。
 * 通过调用 {@link #setCancelled(boolean)} 方法可以拒绝玩家加入游戏。
 * <br/>
 * 该事件通过后，应由指定游戏实例（模块）发布 {@link PlayerJoinGameEvent} 事件，以完成玩家加入游戏的操作。
 * <p>
 * Player attempt to join game event.
 * <br/>
 * This event should be listened by the specified game instance or module to detect whether the player can join the specified game instance.
 * By calling the {@link #setCancelled(boolean)} method, the player can be rejected from joining the game.
 * <br/>
 * After this event, the specified game instance (module) should publish the {@link PlayerJoinGameEvent} event to complete the operation of the player joining the game.
 *
 * @see PlayerJoinGameEvent
 * @see io.hikarilan.gamesenselib.modules.extra.BossBarWaitingRoomModule
 * @see io.hikarilan.gamesenselib.modules.extra.IndependentPlayerJoinGameModule
 */
public class PlayerAttemptToJoinGameEvent extends AbstractCancellableGameEvent {

    @Getter
    private final Player player;

    public PlayerAttemptToJoinGameEvent(AbstractGame game, Player player) {
        super(game);
        this.player = player;

        // deny join by default
        setCancelled(true);
    }
}
