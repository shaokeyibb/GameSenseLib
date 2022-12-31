package io.hikarilan.gamesenselib.events.game;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;

/**
 * 玩家退出游戏前事件。
 * <br/>
 * 该事件的发布意味着玩家客观上已经做出了可能退出游戏的举动，例如切换到其他世界，退出服务器等。
 * 但其在技术上还尚未离开游戏。
 * <br/>
 * 您可以决定该玩家是否真正退出游戏，
 * 如果您决定让玩家退出游戏，您应当调用 {@link AbstractGame#removePlayer(AbstractPlayer)} 将玩家从游戏中移除，
 * 然后发布 {@link PlayerPostQuitGameEvent} 事件。
 * <p>
 * Player pre quit game event.
 * <br/>
 * The publication of this event means that the player has objectively made a possible action to quit the game,
 * such as switching to another world, exiting the server, etc.
 * But it has not left the game technically.
 * <br/>
 * You can decide whether the player really quits the game.
 * If you decide to let the player quit the game, you should call {@link AbstractGame#removePlayer(AbstractPlayer)}
 * to remove the player from the game,
 * and then publish the {@link PlayerPostQuitGameEvent} event.
 */
public class PlayerPreQuitGameEvent extends AbstractGameEvent {

    @Getter
    private final AbstractPlayer player;

    public PlayerPreQuitGameEvent(AbstractGame game, AbstractPlayer player) {
        super(game);
        this.player = player;
    }
}
