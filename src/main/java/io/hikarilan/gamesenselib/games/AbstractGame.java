package io.hikarilan.gamesenselib.games;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.hikarilan.gamesenselib.annotations.Internal;
import io.hikarilan.gamesenselib.artifacts.IReusable;
import io.hikarilan.gamesenselib.events.IGameEventBus;
import io.hikarilan.gamesenselib.events.IGameListener;
import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.modules.IModule;
import io.hikarilan.gamesenselib.modules.IModuleHolder;
import io.hikarilan.gamesenselib.modules.bundled.BukkitEventMapperModule;
import io.hikarilan.gamesenselib.modules.bundled.FlowTickModule;
import io.hikarilan.gamesenselib.modules.bundled.ModuleTickModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 代表一个游戏实例基类。
 * <p>
 * Represent a game instance base class.
 */
@SuppressWarnings("unused")
public abstract class AbstractGame implements IReusable, IModuleHolder, IGameEventBus {

    private final Plugin plugin;

    /**
     * 该游戏实例的流程管理器。
     * <p>
     * The flow manager of this game instance.
     */
    @Getter
    @Internal
    private final FlowManager flowManager;

    /**
     * 该游戏实例的玩家列表。
     * <p>
     * The player list of this game instance.
     */
    private final Set<AbstractPlayer> players = Sets.newHashSet();

    @Getter
    private final Set<IGameListener> handlerList = Sets.newHashSet();

    @Getter
    private final EventBus eventBus = EventBus.builder()
            .eventInheritance(false)
            .sendNoSubscriberEvent(false)
            .build();

    @Getter
    private final Map<Class<? extends IModule>, IModule> installedModules = Maps.newHashMap();

    /**
     * 生成一个游戏实例，并使用 {@link #generateFlowManager()} 方法的返回值生成流程管理器。
     * <br/>
     * 此构造器对于生成更加复杂的流程管理器时非常有用。
     * 使用时，需要覆盖 {@link #generateFlowManager()} 方法。
     * <p>
     * Generate a game instance and use the return value of the {@link #generateFlowManager()} method to generate the flow manager.
     * <br/>
     * This constructor is very useful for generating more complex flow managers.
     * When used, you need to override the {@link #generateFlowManager()} method.
     */
    protected AbstractGame(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.flowManager = Objects.requireNonNull(generateFlowManager()).$game(this).build();

        init();
    }

    /**
     * 生成一个游戏实例，并使用给定的 {@link FlowManager.FlowManagerBuilder} 生成流程管理器。
     * <br/>
     * 这是最简单的生成游戏实例的方法，
     * 可以使用 {@link io.hikarilan.gamesenselib.games.extra.DefaultGame} 作为默认实现。
     * <p>
     * Generate a game instance and use the given {@link FlowManager.FlowManagerBuilder} to generate the flow manager.
     * <br/>
     * This is the simplest way to generate a game instance,
     * you may use {@link io.hikarilan.gamesenselib.games.extra.DefaultGame} as the default implementation.
     */
    public AbstractGame(@NotNull Plugin plugin, @NotNull FlowManager.FlowManagerBuilder flowManagerBuilder) {
        this.plugin = plugin;
        this.flowManager = flowManagerBuilder.$game(this).build();

        init();
    }

    /**
     * 生成一个流程管理器。
     * <br/>
     * 当使用 {@link #AbstractGame(Plugin)} 构造器时，此方法返回值不应为 {@code null}。
     * <p>
     * Generate a flow manager.
     * <br/>
     * When using the {@link #AbstractGame(Plugin)} constructor,
     * the return value of this method should not be {@code null}.
     *
     * @return the flow manager builder generated.
     */
    @Nullable
    protected abstract FlowManager.FlowManagerBuilder generateFlowManager();

    /**
     * 获取该实例内的所有玩家实例的副本。
     * <p>
     * Get a copy of all player instances in this instance.
     *
     * @param checkOnline whether to check if the player is online
     * @return copy of all player instances in this instance
     */
    public Set<AbstractPlayer> getPlayers(boolean checkOnline) {
        return players.stream()
                .filter(player -> !checkOnline || player.isOnline())
                .collect(Collectors.toSet());
    }

    /**
     * 获取该实例内的所有符合类条件的玩家实例的副本。
     * <p>
     * Get a copy of all player instances in this instance that meet the class condition.
     *
     * @param checkOnline whether to check if the player is online
     * @param clazz       the class of the player
     * @param <T>         the class of the player
     * @return copy of all player instances in this instance that meet the class condition
     */
    @NotNull
    public <T extends AbstractPlayer> Set<T> getPlayers(boolean checkOnline, @NotNull Class<T> clazz) {
        return getPlayers(checkOnline).stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    private Optional<AbstractPlayer> _findPlayer(@NotNull Player player) {
        return players.stream()
                .filter(p -> p.isWrapper(player))
                .findFirst();
    }

    private <T extends AbstractPlayer> Optional<T> _findPlayer(@NotNull Player player, @NotNull Class<T> clazz) {
        return players.stream()
                .filter(p -> p.isWrapper(player))
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }

    /**
     * 尝试在该游戏实例中查找被指定 {@link Player} 所包装的 {@link AbstractPlayer} 实例。
     * <p>
     * Try to find the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance.
     *
     * @param player the player to find
     * @return the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance.
     */
    @Nullable
    public AbstractPlayer findPlayer(@NotNull Player player) {
        return _findPlayer(player)
                .orElse(null);
    }

    /**
     * 尝试在该游戏实例中查找符合类条件且被指定 {@link Player} 所包装的 {@link AbstractPlayer} 实例。
     * <p>
     * Try to find the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance that meets the class condition.
     *
     * @param player the player to find
     * @param clazz  the class of the player
     * @param <T>    the class of the player
     * @return the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance that meets the class condition.
     */
    @Nullable
    public <T extends AbstractPlayer> T findPlayer(@NotNull Player player, @NotNull Class<T> clazz) {
        return _findPlayer(player, clazz)
                .orElse(null);
    }

    /**
     * 尝试在该游戏实例中查找被指定 {@link Player} 所包装的 {@link AbstractPlayer} 实例。
     * 如果找不到指定玩家，则返回一个设定的默认值。
     * <p>
     * Try to find the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance,
     * and return a default value if the specified player is not found.
     *
     * @param player        the player to find
     * @param defaultPlayer the default value
     * @return the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance.
     */
    @NotNull
    public AbstractPlayer findPlayer(@NotNull Player player, @NotNull Supplier<AbstractPlayer> defaultPlayer) {
        return _findPlayer(player)
                .orElseGet(defaultPlayer);
    }

    /**
     * 尝试在该游戏实例中查找符合类条件且被指定 {@link Player} 所包装的 {@link AbstractPlayer} 实例，
     * 如果找不到指定玩家，则返回一个设定的默认值。
     * <p>
     * Try to find the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance that meets the class condition,
     * and return a default value if the specified player is not found.
     *
     * @param player        the player to find
     * @param clazz         the class of the player
     * @param <T>           the class of the player
     * @param defaultPlayer the default value
     * @return the {@link AbstractPlayer} instance wrapped by the specified {@link Player} in this game instance that meets the class condition.
     */
    @NotNull
    public <T extends AbstractPlayer> T findPlayer(@NotNull Player player, @NotNull Class<T> clazz, @NotNull Supplier<T> defaultPlayer) {
        return _findPlayer(player, clazz)
                .orElseGet(defaultPlayer);
    }

    /**
     * 移除并销毁当前游戏实例内的所有玩家实例。
     * <p>
     * Remove and destroy all player instances in the current game instance.
     */
    public void removeAllPlayers() {
        Sets.newHashSet(players).forEach(AbstractPlayer::destroy);
        players.clear();
    }

    /**
     * 将指定玩家实例添加到当前游戏实例中。
     * <br/>
     * 请注意，这可能并不意味着玩家加入了游戏。
     * <p>
     * Add the specified player instance to the current game instance.
     * <br/>
     * Please note that this does not necessarily mean that the player has joined the game.
     *
     * @param player the player to add
     */
    public void addPlayer(AbstractPlayer player) {
        if (!player.isWrapper(this))
            throw new IllegalArgumentException("The player is not belongs to this game instance.");
        players.add(player);
    }

    /**
     * 将指定玩家实例从当前游戏实例中移除。
     * <br/>
     * 请注意，这可能并不意味着玩家退出了游戏。
     * <p>
     * Remove the specified player instance from the current game instance.
     * <br/>
     * Please note that this does not necessarily mean that the player has left the game.
     *
     * @param player the player to remove
     */
    public void removePlayer(AbstractPlayer player) {
        if (!player.isWrapper(this))
            throw new IllegalArgumentException("The player is not belongs to this game instance.");
        players.remove(player);
    }

    /**
     * 安装所有已捆绑模块。
     * <br/>
     * 已捆绑模块会在游戏实例创建时自动加载，用于完成一些基本功能。
     * <p>
     * Install all bundled modules.
     * <br/>
     * Bundled modules will be automatically loaded when the game instance is created to complete some basic functions.
     */
    protected void installBundledModules() {
        installModule(new ModuleTickModule(plugin, this));
        installModule(new FlowTickModule(this));
        installModule(new BukkitEventMapperModule(plugin, this));
    }

    @Override
    public void init() {
        flowManager.init();
        IModuleHolder.super.init();

        installBundledModules();
    }

    @Override
    public void destroy() {
        flowManager.destroy();
        IModuleHolder.super.destroy();

        unregisterAllListeners();
        removeAllPlayers();
    }
}
