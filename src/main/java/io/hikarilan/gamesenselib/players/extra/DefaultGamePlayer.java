package io.hikarilan.gamesenselib.players.extra;

import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 一个 {@link io.hikarilan.gamesenselib.players.AbstractPlayer} 的默认实现,
 * 可以直接使用作为一个游戏内玩家。
 * <br/>
 * 在更复杂的情况下（例如需要区分旁观玩家和游戏内玩家），建议继承 {@link io.hikarilan.gamesenselib.players.AbstractPlayer} 并实现自己的玩家类。
 * <p>
 * A default implementation of {@link io.hikarilan.gamesenselib.players.AbstractPlayer},
 * Can be used directly as a player in the game.
 * <br/>
 * In more complex cases (such as the need to distinguish between spectators and players in the game),
 * it is recommended to inherit {@link io.hikarilan.gamesenselib.players.AbstractPlayer} and implement your own player class.
 */
@SuppressWarnings("unused")
public class DefaultGamePlayer extends AbstractPlayer {

    public DefaultGamePlayer(@NotNull AbstractGame game, @NotNull Player player) {
        super(game, player);
    }

}
