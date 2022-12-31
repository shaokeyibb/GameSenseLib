package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractCancellableGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家尝试加入游戏事件。
 * <br/>
 * 该事件应由指定游戏实例或模块监听，用于检测玩家是否可以加入指定的游戏实例。
 * 通过调用 {@link #setCancelled(boolean)} 方法可以允许/拒绝玩家加入游戏。
 * <br/>
 * 该事件通过后，应由指定游戏实例（模块）发布 {@link PlayerPreJoinGameEvent} 事件，以完成玩家加入游戏的操作。
 * <br/>
 * 默认情况下，玩家将不被允许加入游戏。
 * <p>
 * Player attempt to join game event.
 * <br/>
 * This event should be listened by the specified game instance or module to detect whether the player can join the specified game instance.
 * By calling the {@link #setCancelled(boolean)} method, the player can be allowed or rejected from joining the game.
 * <br/>
 * After this event, the specified game instance (module) should publish the {@link PlayerPreJoinGameEvent} event to complete the operation of the player joining the game.
 * <br/>
 * By default, the player will not be allowed to join the game.
 *
 * @see PlayerPreJoinGameEvent
 * @see io.hikarilan.gamesenselib.modules.extra.BossBarWaitingRoomModule
 * @see io.hikarilan.gamesenselib.modules.extra.IndependentPlayerJoinGameModule
 */
public class PlayerAttemptToJoinGameEvent extends AbstractCancellableGameEvent {

    @Getter
    private final Player player;

    /**
     * 用于指定加入游戏玩家的 {@link AbstractPlayer} 实例。
     * <br/>
     * 这可以适用于玩家已经加入游戏的情况，例如玩家在退出游戏后重新加入游戏。
     * <p>
     * The {@link AbstractPlayer} instance for the player joining the game.
     * <br/>
     * This can be used in the case where the player has already joined the game,
     * such as when the player rejoins the game after leaving the game.
     */
    @Getter
    @Setter
    @Nullable
    private AbstractPlayer gamePlayer;

    public PlayerAttemptToJoinGameEvent(AbstractGame game, Player player) {
        super(game);
        this.player = player;

        // deny join by default
        setCancelled(true);
    }
}
