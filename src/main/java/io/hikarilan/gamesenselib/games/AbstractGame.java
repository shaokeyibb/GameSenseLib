package io.hikarilan.gamesenselib.games;

import com.google.common.collect.Sets;
import io.hikarilan.gamesenselib.artifacts.IReusable;
import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.modules.IModuleHolder;
import io.hikarilan.gamesenselib.modules.bundled.ModuleTickModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * 代表一个游戏实例基类。
 * <p>
 * Represent a game instance base class.
 */
public abstract class AbstractGame implements IReusable, IModuleHolder {

    private final Plugin plugin;

    /**
     * 该游戏实例的流程管理器。
     * <p>
     * The flow manager of this game instance.
     */
    private final FlowManager flowManager;

    /**
     * 该游戏实例的玩家列表。
     * <p>
     * The player list of this game instance.
     */
    private final Set<AbstractPlayer> players = Sets.newHashSet();

    public AbstractGame(Plugin plugin, FlowManager.FlowManagerBuilder flowManagerBuilder) {
        this.plugin = plugin;
        this.flowManager = flowManagerBuilder.$game(this).build();
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

        removeAllPlayers();
    }
}
