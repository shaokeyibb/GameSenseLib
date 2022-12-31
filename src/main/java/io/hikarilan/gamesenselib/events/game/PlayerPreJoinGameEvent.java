package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家加入游戏前事件。
 * <br/>
 * 该事件的发布意味着玩家已经通过 {@link PlayerAttemptToJoinGameEvent} 事件的检测，可以加入游戏，但还尚未加入游戏。
 * <br/>
 * 此时玩家的 {@link io.hikarilan.gamesenselib.players.AbstractPlayer} 实例已被创建，但该实例还尚未被添加到游戏实例的玩家列表中。
 * <br/>
 * 一个游戏实例收到此事件后应当调用 {@link AbstractGame#addPlayer(AbstractPlayer)} 将玩家加入游戏，然后发布 {@link PlayerPostJoinGameEvent} 事件。
 * <p>
 * Player pre join game event.
 * <br/>
 * The publication of this event means that the player has passed the detection of the {@link PlayerAttemptToJoinGameEvent} event
 * and can join the game, but has not yet joined the game.
 * <br/>
 * At this time, the {@link io.hikarilan.gamesenselib.players.AbstractPlayer} instance of the player has been created,
 * but the instance has not yet been added to the player list of the game instance.
 * <br/>
 * After a game instance receives this event, it should call {@link AbstractGame#addPlayer(AbstractPlayer)} to add the player to the game,
 * and then publish the {@link PlayerPostJoinGameEvent} event.
 *
 * @see PlayerAttemptToJoinGameEvent
 * @see PlayerPostJoinGameEvent
 * @see io.hikarilan.gamesenselib.modules.extra.BossBarWaitingRoomModule
 * @see io.hikarilan.gamesenselib.modules.extra.IndependentPlayerJoinGameModule
 */
public class PlayerPreJoinGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerPreJoinGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
