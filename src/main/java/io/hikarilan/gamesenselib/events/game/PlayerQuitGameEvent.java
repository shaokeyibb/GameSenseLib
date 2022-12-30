package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家退出游戏事件。
 * <p>
 * Player quit game event.
 */
public class PlayerQuitGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerQuitGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
