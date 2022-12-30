package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家加入游戏事件。
 * <p>
 * Player join game event.
 *
 * @see PlayerAttemptToJoinGameEvent
 * @see io.hikarilan.gamesenselib.modules.extra.BossBarWaitingRoomModule
 * @see io.hikarilan.gamesenselib.modules.extra.IndependentPlayerJoinGameModule
 */
public class PlayerJoinGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerJoinGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
