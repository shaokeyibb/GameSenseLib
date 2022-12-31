package io.hikarilan.gamesenselib.modules.extra;

import com.google.common.eventbus.Subscribe;
import io.hikarilan.gamesenselib.events.game.PlayerJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.AbstractListenerModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 玩家加入和退出游戏时的消息广播模块。
 * <br/>
 * 当玩家加入服务器或退出服务器时，将会为所有在此游戏实例内的玩家广播一条消息。
 * <p>
 * A module for broadcasting messages when players join and quit the game.
 * <br/>
 * When a player joins or quits the server, a message will be broadcast to all players in this game instance.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class PlayerJoinAndQuitGameBroadcastModule extends AbstractListenerModule {
    @Nullable
    private final Function<AbstractPlayer, String> joinMessage;
    @Nullable
    private final Function<AbstractPlayer, String> quitMessage;

    /**
     * 创建一个模块。
     * <br/>
     * 如果 {@code joinMessage} 为 {@code null}，则不会广播玩家加入游戏的消息。
     * <br/>
     * 如果 {@code quitMessage} 为 {@code null}，则不会广播玩家退出游戏的消息。
     * <p>
     * Create a module.
     * <br/>
     * If {@code joinMessage} is {@code null}, no message will be broadcast when a player joins the game.
     * <br/>
     * If {@code quitMessage} is {@code null}, no message will be broadcast when a player quits the game.
     *
     * @param game        the game instance
     * @param joinMessage format of join message, or {@code null} if no message should be broadcast
     * @param quitMessage format of quit message, or {@code null} if no message should be broadcast
     */
    public PlayerJoinAndQuitGameBroadcastModule(@NotNull AbstractGame game, @Nullable Function<AbstractPlayer, String> joinMessage, @Nullable Function<AbstractPlayer, String> quitMessage) {
        super(game);
        this.joinMessage = joinMessage;
        this.quitMessage = quitMessage;
    }

    @Override
    public void onTick() {
    }

    @Subscribe
    public void onPlayerJoinGame(PlayerJoinGameEvent e) {
        if (joinMessage == null) return;
        val message = joinMessage.apply(e.getPlayer());
        getGame().getPlayers(true).forEach(player -> player.sendMessage(message));
    }

    @Subscribe
    public void onPlayerQuitGame(PlayerQuitGameEvent e) {
        if (quitMessage == null) return;
        val message = quitMessage.apply(e.getPlayer());
        getGame().getPlayers(true).forEach(player -> player.sendMessage(message));
    }
}
