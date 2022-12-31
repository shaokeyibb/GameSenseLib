package io.hikarilan.gamesenselib.modules.extra;

import com.google.common.collect.Maps;
import io.hikarilan.gamesenselib.events.game.*;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.AbstractListenerModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import io.hikarilan.gamesenselib.utils.Durations;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;

/**
 * 带有 BossBar 显示的等待大厅模块
 * <br/>
 * 等待大厅模块创建了一个等待大厅，玩家可以在等待大厅中等待游戏开始。
 * <br/>
 * 等待过程将会阻塞游戏的流程，直到游戏开始。
 * <br/>
 * 该等待大厅通过监听 {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} 事件接收玩家的加入请求。
 * <br/>
 * 可以通过向 {@link AbstractGame} 发布一个 {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} 事件来请求玩家加入游戏。
 * <br/>
 * 当等待大厅人数已满时，该事件将会被取消。
 * <p>
 * Waiting room module with BossBar display
 * <br/>
 * The waiting room module creates a waiting room, where players can wait for the game to start.
 * <br/>
 * The waiting process will block the game flow until the game starts.
 * <br/>
 * The waiting room receives player join requests by listening to the {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} event.
 * <br/>
 * You can request a player to join the game by publishing a {@link io.hikarilan.gamesenselib.events.game.PlayerAttemptToJoinGameEvent} event to {@link AbstractGame}.
 * <br/>
 * When the waiting room is full, the event will be canceled.
 */
@SuppressWarnings("unused")
public class BossBarWaitingRoomModule extends AbstractListenerModule {

    /**
     * 流程阻塞模块。
     * <br/>
     * 用于阻塞流程，直到游戏开始。
     * <p>
     * Flow blocking module.
     * <br/>
     * Used to block the flow until the game starts.
     */
    @NotNull
    private final PhaseBlockingModule phaseBlockingModule;

    /**
     * 开始进行游戏开始倒计时所需的最小玩家数。
     * <p>
     * The minimum number of players required to start the game start countdown.
     */
    private final int minPlayerCount;

    /**
     * 等待大厅可接受的最大玩家数
     * <p>
     * The maximum number of players that the waiting room can accept
     */
    private final int maxPlayerCount;

    /**
     * 当玩家进入等待大厅时将会被传送到的位置。
     * <br/>
     * 设置为 null 时将不会传送玩家。
     * <p>
     * The location where the player will be teleported to when they enter the waiting room.
     * <br/>
     * If set to null, the player will not be teleported.
     */
    @Nullable
    private final Location lobbyLocation;

    /**
     * 游戏开始倒计时时间。
     * <br/>
     * 游戏将在满足最小玩家数后开始倒计时，倒计时结束后游戏开始。
     * <p>
     * Game start countdown time.
     * <br/>
     * The game will start counting down after meeting the minimum number of players,
     */
    @NotNull
    private final Duration countdown;

    @Nullable
    private final Runnable onFinish;

    /**
     * 游戏玩家 Class 对象。
     * <br/>
     * 这用于筛选游戏玩家和非游戏玩家（例如观察玩家）。
     * <br/>
     * 构造器默认会采用 {@link AbstractPlayer} 作为游戏玩家，即所有加入游戏的玩家都是游戏玩家。
     * <p>
     * Game player Class object.
     * <br/>
     * This is used to filter game players and non-game players (such as observers).
     * <br/>
     * The constructor will use {@link AbstractPlayer} as the game player by default,
     * that is, all players who join the game are game players.
     */
    @NotNull
    private final Class<? extends AbstractPlayer> ingamePlayerClass;

    /**
     * 等待大厅当前状态
     * <p>
     * Waiting room current status
     */
    private Status status;

    /**
     * 剩余时间倒计时
     * <p>
     * Remaining time countdown
     */
    private long timer;

    /**
     * 倒计时提示 Bossbar
     * <p>
     * Countdown prompt Bossbar
     */
    private BossBar timerBossbar;

    /**
     * 玩家加入大厅前所在的位置
     * <p>
     * The location where the player was before joining the lobby
     */
    private final Map<AbstractPlayer, Location> pastLocations = Maps.newHashMap();

    /**
     * 创建一个等待大厅模块。
     * <p>
     * Create a waiting room module.
     *
     * @param game                game instance
     * @param phaseBlockingModule phase blocking module
     * @param minPlayerCount      minimum player count
     * @param maxPlayerCount      maximum player count
     * @param lobbyLocation       lobby location
     * @param countdown           countdown
     * @param onFinish            callback when countdown finish
     * @param ingamePlayerClass   ingame player class
     */
    public BossBarWaitingRoomModule(@NotNull AbstractGame game,
                                    @NotNull PhaseBlockingModule phaseBlockingModule,
                                    int minPlayerCount,
                                    int maxPlayerCount,
                                    @Nullable Location lobbyLocation,
                                    @NotNull Duration countdown,
                                    @Nullable Runnable onFinish,
                                    @NotNull Class<? extends AbstractPlayer> ingamePlayerClass) {
        super(game);
        this.phaseBlockingModule = phaseBlockingModule;
        this.minPlayerCount = minPlayerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.lobbyLocation = lobbyLocation;
        this.countdown = countdown;
        this.onFinish = onFinish;
        this.ingamePlayerClass = ingamePlayerClass;
    }

    /**
     * 创建一个等待大厅模块。
     * <p>
     * Create a waiting room module.
     *
     * @param game                game instance
     * @param phaseBlockingModule phase blocking module
     * @param minPlayerCount      minimum player count
     * @param maxPlayerCount      maximum player count
     * @param lobbyLocation       lobby location
     * @param countdown           countdown
     * @param onFinish            callback when countdown finish
     */
    public BossBarWaitingRoomModule(@NotNull AbstractGame game,
                                    @NotNull PhaseBlockingModule phaseBlockingModule,
                                    int minPlayerCount,
                                    int maxPlayerCount,
                                    @Nullable Location lobbyLocation,
                                    @NotNull Duration countdown,
                                    @Nullable Runnable onFinish) {
        this(game, phaseBlockingModule, minPlayerCount, maxPlayerCount, lobbyLocation, countdown, onFinish, AbstractPlayer.class);
    }

    private void onFinish() {
        phaseBlockingModule.unlock();
        if (onFinish != null) {
            onFinish.run();
        }
    }

    @Override
    public void onTick() {
        val playerCount = getGame().getPlayers(true, ingamePlayerClass).size();

        switch (status) {
            case INITIALING: {
                timerBossbar.setVisible(true);
                if (playerCount < minPlayerCount) {
                    timer = Durations.toTick(countdown);
                    timerBossbar.setTitle(null);
                    timerBossbar.setColor(BarColor.WHITE);
                    timerBossbar.setProgress(playerCount / (double) minPlayerCount);
                    return;
                }
                status = Status.WAITING;
                break;
            }
            case WAITING: {
                if (playerCount < minPlayerCount) {
                    status = Status.INITIALING;
                    return;
                }
                timerBossbar.setColor(BarColor.RED);
                timer--;
                if (timer > 0) {
                    timerBossbar.setProgress(timer / (double) Durations.toTick(countdown));
                } else if (timer == 0L) {
                    onFinish();
                    return;
                }
                break;
            }
        }
    }

    @Override
    public void onInstall() {
        super.onInstall();

        status = Status.INITIALING;
        timer = Durations.toTick(countdown);
        timerBossbar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SOLID);
        pastLocations.clear();
    }

    @Override
    public void onUninstall() {
        super.onUninstall();

        timerBossbar.removeAll();
    }

    @Subscribe
    public void onAttemptToJoin(PlayerAttemptToJoinGameEvent e) {
        val playerCount = getGame().getPlayers(true, ingamePlayerClass).size();
        if (playerCount >= maxPlayerCount) return;
        e.setCancelled(false);
    }

    @Subscribe
    public void onPlayerJoinGame(PlayerPreJoinGameEvent e) {
        e.getPlayer().runWhenOnline(player -> timerBossbar.addPlayer(player));

        if (lobbyLocation != null) {
            e.getPlayer().teleport(lobbyLocation);
        }

        getGame().addPlayer(e.getPlayer());

        getGame().postEvent(new PlayerPostJoinGameEvent(e.getGame(), e.getPlayer()));
    }

    @Subscribe
    public void onPlayerQuitGame(PlayerPreQuitGameEvent e) {
        e.getPlayer().runWhenOnline(player -> timerBossbar.removePlayer(player));

        if (pastLocations.containsKey(e.getPlayer())) {
            e.getPlayer().teleport(pastLocations.get(e.getPlayer()));
        }

        getGame().removePlayer(e.getPlayer());

        getGame().postEvent(new PlayerPostQuitGameEvent(e.getGame(), e.getPlayer()));
    }

    private enum Status {
        /**
         * 等待最小玩家加入大厅状态。
         * <p>
         * Waiting for the minimum number of players to join the lobby status.
         */
        INITIALING,

        /**
         * 已达到最小玩家数，等待更多玩家加入大厅状态。
         * <p>
         * Waiting for more players to join the lobby after reaching the minimum number of players.
         */
        WAITING,
    }
}
