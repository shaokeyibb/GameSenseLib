package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家退出游戏事件。
 * <br/>
 * 此事件的发布意味着玩家在技术上离开了游戏，其玩家对象应当不再可用。
 * <p>
 * Player quit game event.
 * <br/>
 * The publication of this event means that the player has left the game technically,
 * and its player object should no longer be available.
 */
public class PlayerPostQuitGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerPostQuitGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
