package io.hikarilan.gamesenselib.modules.extra;

import com.google.common.collect.Maps;
import io.hikarilan.gamesenselib.events.game.PlayerPreJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPreQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.AbstractListenerModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import org.bukkit.Location;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * 玩家退出游戏后处理模块。
 * <br/>
 * 此模块可用于将退出游戏的玩家踢出服务器或传送到其加入游戏前的位置。
 * <p>
 * Player quit game processing module.
 * <br/>
 * This module can be used to kick players out of the server or teleport them
 * to their previous location after they quit the game.
 */
@SuppressWarnings("unused")
public class PlayerQuitGameTeleportingModule extends AbstractListenerModule {

    private final boolean kickPlayer;

    /**
     * 玩家加入大厅前所在的位置
     * <p>
     * The location where the player was before joining the lobby
     */
    private final Map<AbstractPlayer, Location> pastLocations = Maps.newHashMap();

    public PlayerQuitGameTeleportingModule(AbstractGame game, boolean kickPlayer) {
        super(game);
        this.kickPlayer = kickPlayer;
    }

    @Override
    public void onInstall() {
        super.onInstall();
        pastLocations.clear();
    }

    @Override
    public void onTick() {
    }

    @Subscribe
    public void onPlayerJoinGame(PlayerPreJoinGameEvent e) {
        if (kickPlayer) return;
        pastLocations.put(e.getPlayer(), e.getPlayer().getLocation());
    }

    @Subscribe
    public void onPlayerQuitGame(PlayerPreQuitGameEvent e) {
        if (kickPlayer) {
            e.getPlayer().kickPlayer("Game has been started or the game is full.");
            return;
        }

        if (!pastLocations.containsKey(e.getPlayer())) return;

        e.getPlayer().teleport(pastLocations.get(e.getPlayer()));
    }
}
