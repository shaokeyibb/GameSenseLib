package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPostJoinGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPreJoinGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.AbstractListenerModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.val;
import org.greenrobot.eventbus.Subscribe;

import java.util.Set;

/**
 * 此模块允许玩家在游戏已开始的情况下重新加入游戏。
 * <br/>
 * 此模块将会监听 {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} 事件，
 * 然后允许已加入过游戏的玩家加入游戏。
 * <br/>
 * 要实现此功能，玩家实例不应被在退出游戏时销毁，而是应当仅将玩家实例从游戏列表中移除。
 * <p>
 * This module allows players to rejoin the game while the game has started.
 * <br/>
 * This module will listen to the {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} event,
 * and then allow players who have joined the game to join the game.
 * <br/>
 * To achieve this, the player instance should not be destroyed when the player exits the game,
 * but the player instance should only be removed from the game list.
 */
@SuppressWarnings("unused")
public class IngamePlayerRejoinGameModule extends AbstractListenerModule {

    private Set<AbstractPlayer> gamingPlayers;

    public IngamePlayerRejoinGameModule(AbstractGame game) {
        super(game);
    }

    @Override
    public void onInstall() {
        super.onInstall();

        gamingPlayers = getGame().getPlayers(false);
    }

    @Override
    public void onTick() {

    }

    @Subscribe
    public void onPlayerAttemptToJoinGame(PlayerAttemptToJoinGameEvent e) {
        val player = gamingPlayers.stream().filter(it -> it.isWrapper(e.getPlayer())).findFirst();
        if (!player.isPresent()) return;
        e.setGamePlayer(player.get());
        e.setCancelled(false);
    }

    @Subscribe
    public void onPlayerJoinGame(PlayerPreJoinGameEvent e) {
        getGame().addPlayer(e.getPlayer());

        getGame().postEvent(new PlayerPostJoinGameEvent(e.getGame(), e.getPlayer()));
    }
}
