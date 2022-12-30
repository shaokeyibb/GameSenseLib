package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerJoinGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import io.hikarilan.gamesenselib.players.extra.DefaultGamePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * 独立游戏实例玩家加入模块。
 * <br/>
 * 该可模块用于处理玩家加入游戏实例请求。
 * <br/>
 * 当玩家加入服务器时即视为玩家加入游戏实例。
 * <p>
 * Independent game instance player join module.
 * <br/>
 * This module is used to handle player join game instance request.
 * <br/>
 * When the player joins the server, it is considered that the player joins the game instance.
 */
@RequiredArgsConstructor
public class IndependentPlayerJoinGameModule implements IModule, Listener {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final AbstractGame game;

    @Override
    public void onInstall() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onUninstall() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (game.postEvent(new PlayerAttemptToJoinGameEvent(game, e.getPlayer())).isCancelled()) {
            e.getPlayer().kickPlayer("Game has been started or the game is full.");
        } else {
            game.postEvent(new PlayerJoinGameEvent(game, new DefaultGamePlayer(game, e.getPlayer())));
        }
    }
}
