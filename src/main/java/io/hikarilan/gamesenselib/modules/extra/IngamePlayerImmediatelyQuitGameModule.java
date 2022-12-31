package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.events.game.PlayerPostQuitGameEvent;
import io.hikarilan.gamesenselib.events.game.PlayerPreQuitGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.AbstractListenerModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import org.greenrobot.eventbus.Subscribe;

/**
 * 玩家游戏内立刻退出游戏模块
 * <br/>
 * 默认情况下，当游戏开始后，玩家做出退出游戏的行为时，游戏实例并不会做出任何动作。
 * <br/>
 * 此模块的存在可在玩家退出游戏时立刻将其玩家对象从游戏中移除，
 * 并发布 {@link io.hikarilan.gamesenselib.events.game.PlayerPostQuitGameEvent}事件。
 * <p>
 * Player ingame immediately quit game module
 * <br/>
 * By default, when the game starts, the game instance will not take any action when the player makes an action to quit the game.
 * <br/>
 * The existence of this module can remove the player object of the player from the game immediately when the player quits the game,
 * and publish the {@link io.hikarilan.gamesenselib.events.game.PlayerPostQuitGameEvent} event.
 */
@SuppressWarnings("unused")
public class IngamePlayerImmediatelyQuitGameModule extends AbstractListenerModule {

    /**
     * 当玩家退出游戏时，是否要调用 {@link AbstractPlayer#destroy()} 方法？
     * <br/>
     * 设置为 {@code false} 可允许保留并复用 {@link AbstractPlayer} 实例。
     * <p>
     * When the player quits the game, should the {@link AbstractPlayer#destroy()} method be called?
     * <br/>
     * Set to {@code false} to allow the {@link AbstractPlayer} instance to be retained and reused.
     */
    private final boolean destroyPlayer;

    public IngamePlayerImmediatelyQuitGameModule(AbstractGame game) {
        this(game, true);
    }

    public IngamePlayerImmediatelyQuitGameModule(AbstractGame game, boolean destroyPlayer) {
        super(game);
        this.destroyPlayer = destroyPlayer;
    }

    @Override
    public void onTick() {

    }

    @Subscribe
    public void onPlayerQuitGame(PlayerPreQuitGameEvent e) {
        getGame().removePlayer(e.getPlayer());

        if (destroyPlayer) {
            e.getPlayer().destroy();
        }

        getGame().postEvent(new PlayerPostQuitGameEvent(e.getGame(), e.getPlayer()));
    }

}
