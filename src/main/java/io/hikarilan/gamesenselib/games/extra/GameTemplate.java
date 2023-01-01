package io.hikarilan.gamesenselib.games.extra;

import com.google.common.collect.Sets;
import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.flows.Phase;
import io.hikarilan.gamesenselib.flows.extra.ExtraPhases;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.extra.*;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * GameTemplate 可用于快速生成 {@link AbstractGame} 实例
 * <br/>
 * 对于更高的自定义需求，我们建议您直接拓展 {@link AbstractGame} 类
 * <p>
 * GameTemplate can be used to quickly generate an instance of {@link AbstractGame}
 * <br/>
 * For higher custom requirements, we recommend that you directly extend the {@link AbstractGame} class
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor(staticName = "of")
public class GameTemplate {

    private final Set<GameConfigurator> gameConfigurators = Sets.newHashSet();

    private final FlowManager.FlowManagerBuilder flowManagerBuilder = FlowManager.builder();

    private final Plugin plugin;

    /**
     * @see #setRemovePlayerOnQuit(boolean)
     */
    private boolean removePlayerOnQuit = true;

    /**
     * @see #setAllowRejoinPlayer(boolean)
     */
    private boolean allowRejoinPlayer = false;

    /**
     * @see #setTeleportPlayerOnQuit(boolean)
     */
    private boolean teleportPlayerOnQuit = true;

    /**
     * 为指定优先级的游戏流程添加游戏阶段，优先级应大于等于 0。
     * <br/>
     * 指定的优先级将会 +10，以便于 {@link GameTemplate} 注入游戏流程。
     * <p>
     * Add phase to specify flow of priority, priority should start from 0.
     * <br/>
     * The specified priority will be +10 to facilitate the injection of the game flow by {@link GameTemplate}.
     *
     * @param priority priority of the flow
     * @param supplier phase function
     * @return this builder
     */
    public GameTemplate addPhase(int priority, @NotNull Supplier<@NotNull Phase> supplier) {
        if (priority < 0) throw new IllegalArgumentException("Priority should start from 0.");
        flowManagerBuilder.addPhase(priority + 10, supplier);
        return this;
    }

    /**
     * 为指定优先级的游戏流程添加游戏阶段，优先级应大于等于 0。
     * <br/>
     * 指定的优先级将会 +10，以便于 {@link GameTemplate} 注入游戏流程。
     * <p>
     * Add phases to specify flow of priority, priority should start from 0.
     * <br/>
     * The specified priority will be +10 to facilitate the injection of the game flow by {@link GameTemplate}.
     *
     * @param priority priority of the flow
     * @param supplier phases function
     * @return this builder
     */
    public GameTemplate addAllPhase(int priority, @NotNull Supplier<@NotNull Collection<@NotNull Phase>> supplier) {
        if (priority < 0) throw new IllegalArgumentException("Priority should start from 0.");
        flowManagerBuilder.addAllPhase(priority + 10, supplier);
        return this;
    }

    /**
     * 为游戏添加一个等待大厅，游戏将在达到最小玩家数并持续指定时间后开始。
     * <p>
     * Add a waiting room to the game,
     * the game will start after reaching the minimum number of players and continuing for the specified time.
     *
     * @param minPlayerCount minimum number of players
     * @param maxPlayerCount maximum number of players
     * @param countdown      countdown time
     * @param lobbyLocation  lobby location, null to do not move players
     * @see ExtraPhases#waitingForPlayersPhase(int, int, Duration, Location, Runnable)
     */
    public GameTemplate withWaitingRoom(int minPlayerCount,
                                        int maxPlayerCount,
                                        @NotNull Duration countdown,
                                        @Nullable Location lobbyLocation) {
        flowManagerBuilder.addAllPhase(0, () -> ExtraPhases.waitingForPlayersPhase(minPlayerCount, maxPlayerCount, countdown, lobbyLocation, null));
        return this;
    }

    /**
     * 添加一个广播模块，当玩家加入服务器或退出服务器时，将会为所有在此游戏实例内的玩家广播一条消息。
     * <br/>
     * 如果 {@code joinMessage} 为 {@code null}，则不会广播玩家加入游戏的消息。
     * <br/>
     * 如果 {@code quitMessage} 为 {@code null}，则不会广播玩家退出游戏的消息。
     * <p>
     * Add a broadcast module, when a player joins the server or exits the server,
     * a message will be broadcast to all players in this game instance.
     * <br/>
     * If {@code joinMessage} is {@code null}, the player will not broadcast the message when joining the game.
     * <br/>
     * If {@code quitMessage} is {@code null}, the player will not broadcast the message when exiting the game.
     *
     * @param joinMessage format of join message, or {@code null} if no message should be broadcast
     * @param quitMessage format of quit message, or {@code null} if no message should be broadcast
     */
    public GameTemplate withBroadcastMessagePlayerJoinAndQuit(@Nullable Function<AbstractPlayer, String> joinMessage,
                                                              @Nullable Function<AbstractPlayer, String> quitMessage) {
        gameConfigurators.add(new PlayerJoinAndQuitGameBroadcastConfigurator(joinMessage, quitMessage));
        return this;
    }

    /**
     * 当游戏开始后，移除退出游戏玩家的玩家实例（默认启用）
     * <br/>
     * 默认情况下，当游戏开始后，玩家做出退出游戏的行为时，游戏实例并不会做出任何动作。
     * <br/>
     * 开启此设置后，当玩家退出游戏时，游戏实例会移除该玩家的玩家实例（即在技术上令该玩家退出游戏）。
     * <p>
     * When the game starts, remove the player instance of the player who quit the game (enabled by default)
     * <br/>
     * By default, when the game starts,
     * the game instance does not take any action when the player makes a quit game action.
     * <br/>
     * After enabling this setting, when the player quits the game,
     * the game instance will remove the player instance of the player (that is, technically make the player quit the game).
     *
     * @see IngamePlayerImmediatelyQuitGameModule
     */
    public GameTemplate setRemovePlayerOnQuit(boolean removePlayerOnQuit) {
        this.removePlayerOnQuit = removePlayerOnQuit;
        return this;
    }

    /**
     * 当玩家退出游戏后，允许玩家重新加入游戏（默认关闭）
     * <br/>
     * 启用此项，当玩家退出游戏后，玩家的玩家实例将依然保留在游戏实例中。
     * <br/>
     * 当玩家试图重新加入游戏时，其将被允许回到游戏中。
     * <p>
     * When the player quits the game,
     * allow the player to rejoin the game (default disabled)
     * <br/>
     * Enable this item, when the player quits the game,
     * the player's player instance will still be retained in the game instance.
     * <br/>
     * When the player tries to rejoin the game, he will be allowed to return to the game.
     */
    public GameTemplate setAllowRejoinPlayer(boolean allowRejoinPlayer) {
        this.allowRejoinPlayer = allowRejoinPlayer;
        return this;
    }

    /**
     * 将退出游戏的玩家踢出服务器或传送到其加入游戏前的位置（默认启用）
     * <br/>
     * 如果该游戏是一个 world 或 shared 实例，则玩家会被传送至其加入游戏前所在的位置；
     * 如果该游戏是一个 independent 实例，则玩家会被踢出服务器。
     * <p>
     * Kick out the player who quit the game from the server
     * or teleport to the position before joining the game (enabled by default)
     * <br/>
     * If the game is a world or shared instance,
     * the player will be teleported to the position where he was before joining the game;
     * If the game is an independent instance,
     * the player will be kicked out of the server.
     */
    public GameTemplate setTeleportPlayerOnQuit(boolean teleportPlayerOnQuit) {
        this.teleportPlayerOnQuit = teleportPlayerOnQuit;
        return this;
    }

    /**
     * 创建一个共享游戏实例
     * <br/>
     * 一个共享游戏实例可在单个服务端实例中存在多个，
     * 并通过手动方式来加入。
     * <p>
     * Create a shared game instance
     * <br/>
     * A shared game instance can exist in multiple server instances,
     * and can be joined manually.
     *
     * @return shared game template
     */
    public SharedGameTemplate shared() {
        applySettings(0);
        return new SharedGameTemplate(this);
    }

    /**
     * 创建一个单世界游戏实例
     * <br/>
     * 一个单世界游戏实例只能只能在一个世界中存在一个，但可以在单个服务端实例中存在多个。
     * 当玩家加入该世界时即自动加入游戏。
     * <p>
     * Create a single world game instance
     * <br/>
     * A single world game instance can only exist in one world, but can exist in multiple server instances.
     * When the player joins the world, they automatically join the game.
     *
     * @return independent game template
     */
    public WorldGameTemplate world(World world) {
        applySettings(1);
        return new WorldGameTemplate(this, world);
    }

    /**
     * 创建一个独立游戏实例
     * <br/>
     * 一个独立游戏实例只能在单个服务端实例中存在一个，
     * 当玩家加入该服务器实例时即自动加入游戏。
     * <p>
     * Create an independent game instance
     * <br/>
     * An independent game instance can only exist in a single server instance,
     * and players will automatically join the game when they join the server instance.
     *
     * @return independent game template
     */
    public IndependentGameTemplate independent() {
        applySettings(2);
        return new IndependentGameTemplate(this);
    }

    private void applySettings(int instanceType /* 0 for shared, 1 for world, 2 for independent */) {
        if (removePlayerOnQuit) {
            // make sure the module is added after the game starts
            addPhase(0, () -> Phase.builder().onStart(it -> it.installModule(new IngamePlayerImmediatelyQuitGameModule(it, !allowRejoinPlayer))).build());
        }
        if (allowRejoinPlayer) {
            // make sure the module is added after the game starts
            addPhase(0, () -> Phase.builder().onStart(it -> it.installModule(new IngamePlayerRejoinGameModule(it))).build());
        }
        if (teleportPlayerOnQuit) {
            if (instanceType == 0 || instanceType == 1) {
                gameConfigurators.add(new PlayerQuitGameTeleportingConfigurator(false));
            } else if (instanceType == 2) {
                gameConfigurators.add(new PlayerQuitGameTeleportingConfigurator(true));
            }
        }
    }

    /**
     * 为指定 {@link AbstractGame} 实例应用所有的 {@link GameConfigurator}
     * <p>
     * Apply all {@link GameConfigurator} to the specified {@link AbstractGame} instance
     *
     * @param game game instance
     */
    private void applyConfigurators(AbstractGame game) {
        gameConfigurators.forEach(configurator -> configurator.configure(plugin, game));
    }

    @RequiredArgsConstructor
    public static class SharedGameTemplate {
        @NotNull
        private final GameTemplate gameTemplate;

        public AbstractGame build() {
            val game = new DefaultGame(gameTemplate.plugin, gameTemplate.flowManagerBuilder);
            gameTemplate.applyConfigurators(game);
            return game;
        }
    }

    @RequiredArgsConstructor
    public static class WorldGameTemplate {
        @NotNull
        private final GameTemplate gameTemplate;
        @NotNull
        private final World world;

        public AbstractGame build() {
            val game = new DefaultGame(gameTemplate.plugin, gameTemplate.flowManagerBuilder);
            gameTemplate.applyConfigurators(game);
            game.installModule(new WorldPlayerJoinGameModule(gameTemplate.plugin, game, world));
            return game;
        }
    }

    @RequiredArgsConstructor
    public static class IndependentGameTemplate {
        @NotNull
        private final GameTemplate gameTemplate;

        public AbstractGame build() {
            val game = new DefaultGame(gameTemplate.plugin, gameTemplate.flowManagerBuilder);
            gameTemplate.applyConfigurators(game);
            game.installModule(new IndependentPlayerJoinGameModule(gameTemplate.plugin, game));
            return game;
        }
    }


    protected interface GameConfigurator {
        void configure(Plugin plugin, AbstractGame game);
    }

    @RequiredArgsConstructor
    public static class PlayerJoinAndQuitGameBroadcastConfigurator implements GameConfigurator {
        @Nullable
        private final Function<AbstractPlayer, String> joinMessage;
        @Nullable
        private final Function<AbstractPlayer, String> quitMessage;

        @Override
        public void configure(Plugin plugin, AbstractGame game) {
            game.installModule(new PlayerJoinAndQuitGameBroadcastModule(game, joinMessage, quitMessage));
        }
    }

    @RequiredArgsConstructor
    public static class PlayerQuitGameTeleportingConfigurator implements GameConfigurator {

        private final boolean kickPlayer;

        @Override
        public void configure(Plugin plugin, AbstractGame game) {
            game.installModule(new PlayerQuitGameTeleportingModule(game, kickPlayer));
        }
    }

}
