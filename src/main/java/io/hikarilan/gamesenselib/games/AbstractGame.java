package io.hikarilan.gamesenselib.games;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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

    @SuppressWarnings("UnstableApiUsage")
    @Getter
    private final EventBus eventBus = new EventBus();

    @Getter
    private final Map<Class<? extends IModule>, IModule> moduleMap = Maps.newHashMap();

    public AbstractGame(Plugin plugin, FlowManager.FlowManagerBuilder flowManagerBuilder) {
        this.plugin = plugin;
        this.flowManager = flowManagerBuilder.$game(this).build();
    }

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
