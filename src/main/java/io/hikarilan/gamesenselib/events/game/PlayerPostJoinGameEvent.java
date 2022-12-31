package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家加入游戏事件。
 * <br/>
 * 此事件的发布意味着玩家已经正式加入了游戏，可以作为游戏内的一员进行交互
 * <p>
 * Player join game event.
 * <br/>
 * The publication of this event means that the player has officially joined the game and can interact as a member of the game.
 */
public class PlayerPostJoinGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerPostJoinGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
