package io.hikarilan.gamesenselib.players;

import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 代表一个游戏内的玩家基类。
 * <br/>
 * {@link AbstractPlayer} 不是一个 {@link Player} 的直接包装对象，
 * 其通过调度器延迟执行或缓存的方式而允许部分操作在一个 {@link Player} 离线的情况下工作。
 * <br/>
 * 您应当尽量避免操作正在游戏中的 {@link AbstractPlayer} 的 {@link Player} 对象。
 * <br/>
 * 一个玩家实例的生命周期应当仅限于一局游戏内，当该局游戏流程结束，应当调用 {@link #destroy()} 方法销毁该玩家实例
 * （即使该游戏可以重新开始，也应当销毁该玩家实例，然后重新生成一个新的玩家实例）。
 * <p>
 * Represent a player base class in the game.
 * <br/>
 * {@link AbstractPlayer} is not a direct wrapper object of {@link Player},
 * and it allows some operations to work even if the {@link Player} is offline
 * by delaying execution through the scheduler or cache.
 * <br/>
 * You should avoid operating the {@link Player} object of the {@link AbstractPlayer} that is in the game.
 * <br/>
 * The lifetime of a player instance should only be limited to a game. when the game flow ends,
 * you should call the {@link #destroy()} method to destroy the player instance
 * (even if the game can be restarted, you should destroy the player instance and then regenerate a new player instance).
 */
@ToString
public abstract class AbstractPlayer {

    /**
     * 玩家所在的游戏实例
     * <p>
     * The game instance where the player is located
     */
    private final AbstractGame game;

    /**
     * 玩家的唯一标识符（与 {@link Player#getUniqueId()} 相同）
     * <p>
     * The unique identifier of the player (same as {@link Player#getUniqueId()})
     */
    private final UUID uniqueId;

    protected AbstractPlayer(AbstractGame game, Player player) {
        this.game = game;
        this.uniqueId = player.getUniqueId();
    }

    /**
     * 销毁此玩家实例
     * <br/>
     * 这将重置该玩家的所有状态
     * <p>
     * Destroy this player instance
     * <br/>
     * This will reset all the states of this player
     */
    public void destroy() {

    }
}
