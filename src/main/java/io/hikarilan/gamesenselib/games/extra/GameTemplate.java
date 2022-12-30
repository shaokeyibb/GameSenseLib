package io.hikarilan.gamesenselib.games.extra;

import com.google.common.collect.Sets;
import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.flows.Phase;
import io.hikarilan.gamesenselib.flows.extra.ExtraPhases;
import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.extra.IndependentPlayerJoinGameModule;
import io.hikarilan.gamesenselib.modules.extra.WorldPlayerJoinGameModule;
import lombok.Builder;
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
@Builder
public class GameTemplate {

    private final Set<GameConfigurator> gameConfigurators = Sets.newHashSet();

    private final FlowManager.FlowManagerBuilder flowManagerBuilder = FlowManager.builder();

    private final Plugin plugin;

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
        return new IndependentGameTemplate(this);
    }

    /**
     * 为指定 {@link AbstractGame} 实例应用所有的 {@link GameConfigurator}
     * <p>
     * Apply all {@link GameConfigurator} to the specified {@link AbstractGame} instance
     *
     * @param game game instance
     */
    private void applyConfigurators(AbstractGame game) {
        gameConfigurators.forEach(configurator -> configurator.configure(game));
    }

    @RequiredArgsConstructor
    protected static class SharedGameTemplate {
        @NotNull
        private final GameTemplate gameTemplate;

        public AbstractGame build() {
            val game = new DefaultGame(gameTemplate.plugin, gameTemplate.flowManagerBuilder);
            gameTemplate.applyConfigurators(game);
            return game;
        }
    }

    @RequiredArgsConstructor
    protected static class WorldGameTemplate {
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
    protected static class IndependentGameTemplate {
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
        void configure(AbstractGame game);
    }

}
