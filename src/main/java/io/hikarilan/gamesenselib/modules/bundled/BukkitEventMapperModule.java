package io.hikarilan.gamesenselib.modules.bundled;

import io.hikarilan.gamesenselib.events.game.PlayerQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * <b>已捆绑模块（会在游戏实例创建时自动加载）。</b>
 * <br/>
 * 用于处理 Bukkit 玩家事件并将其映射到 {@link io.hikarilan.gamesenselib.players.AbstractPlayer}
 * 或指定 {@link io.hikarilan.gamesenselib.games.AbstractGame} 的事件总线上。
 * <p>
 * <b>Bundled module (automatically loaded when the game instance is created).</b>
 * <br/>
 * Used to handle Bukkit player events and map them to {@link io.hikarilan.gamesenselib.players.AbstractPlayer}
 * or the event bus of the specified {@link io.hikarilan.gamesenselib.games.AbstractGame}.
 */
@RequiredArgsConstructor
public class BukkitEventMapperModule implements IModule, Listener {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final AbstractGame game;

    @Override
    public void onInstall() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
        // Find the player in the game.
        val player = game.findPlayer(e.getPlayer());
        if (player == null) return;
        // Update cache when player join server
        player.updateCache();
        player.consumeAllQueue(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // Find the player in the game.
        val player = game.findPlayer(e.getPlayer());
        if (player == null) return;
        // Call event
        game.postEvent(new PlayerQuitGameEvent(game, player));
    }
}
