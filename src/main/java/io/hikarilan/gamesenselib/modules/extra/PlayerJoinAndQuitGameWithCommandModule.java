package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPreJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPreQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import io.hikarilan.gamesenselib.players.extra.DefaultGamePlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家加入和退出游戏的命令模块。
 * <br/>
 * 此模块允许玩家通过设定的命令加入和退出游戏。
 * <p>
 * Player join and quit game with command module.
 * <br/>
 * This module allows players to join and quit the game by setting the command.
 */
@RequiredArgsConstructor
public class PlayerJoinAndQuitGameWithCommandModule implements IModule, Listener {

    @NotNull
    private final Plugin plugin;

    @NotNull
    private final AbstractGame game;

    /**
     * 加入游戏的命令。设置为 null 则禁用此功能。
     * <p>
     * The command to join the game. Set to null to disable this feature.
     */
    @Nullable
    private final String joinCommand;

    /**
     * 退出游戏的命令。设置为 null 则禁用此功能。
     * <p>
     * The command to quit the game. Set to null to disable this feature.
     */
    @Nullable
    private final String quitCommand;

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
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (joinCommand != null && e.getMessage().substring(1).equals(joinCommand)) {
            val event = new PlayerAttemptToJoinGameEvent(game, e.getPlayer());
            game.postEvent(event);
            if (event.isCancelled()) return;
            game.postEvent(new PlayerPreJoinGameEvent(game, event.getGamePlayer() == null ? new DefaultGamePlayer(game, e.getPlayer()) : event.getGamePlayer()));
            e.setCancelled(true);
            return;
        }
        if (quitCommand != null && e.getMessage().substring(1).equals(quitCommand)) {
            val player = game.findPlayer(e.getPlayer());
            if (player == null) return;
            game.postEvent(new PlayerPreQuitGameEvent(game, player));
            e.setCancelled(true);
        }
    }
}
