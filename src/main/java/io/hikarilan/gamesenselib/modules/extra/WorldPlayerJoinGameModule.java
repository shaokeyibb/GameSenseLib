package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import io.hikarilan.gamesenselib.players.extra.DefaultGamePlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * 单世界游戏实例玩家加入模块。
 * <br/>
 * 该可模块用于处理玩家加入游戏实例请求。
 * <br/>
 * 当玩家加入指定世界时即视为玩家加入游戏实例。
 * <p>
 * Single world game instance player join module.
 * <br/>
 * This module is used to handle player join game instance request.
 * <br/>
 * When the player joins specify world, it is considered that the player joins the game instance.
 */
@RequiredArgsConstructor
public class WorldPlayerJoinGameModule implements IModule, Listener {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final AbstractGame game;
    @NotNull
    private final World world;

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
    public void onPlayerEnterWorld(PlayerChangedWorldEvent e) {
        if (e.getPlayer().getWorld() != world) return;
        if (game.postEvent(new PlayerAttemptToJoinGameEvent(game, e.getPlayer())).isCancelled()) {
            e.getPlayer().kickPlayer("Game has been started or the game is full.");
        } else {
            game.postEvent(new PlayerJoinGameEvent(game, new DefaultGamePlayer(game, e.getPlayer())));
        }
    }

    @EventHandler
    public void onPlayerLeaveWorld(PlayerChangedWorldEvent e) {
        if (e.getFrom() != world) return;
        val player = game.findPlayer(e.getPlayer());
        if (player == null) return;
        game.postEvent(new PlayerQuitGameEvent(game, player));
    }

}
