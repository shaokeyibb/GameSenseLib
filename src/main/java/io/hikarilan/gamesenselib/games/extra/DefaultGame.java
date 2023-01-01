package io.hikarilan.gamesenselib.games.extra;

import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.games.AbstractGame;
import org.bukkit.plugin.Plugin;

/**
 * 一个 {@link io.hikarilan.gamesenselib.games.AbstractGame} 的默认实现，
 * 并不使用 {@link #generateFlowManager()}，而是直接通过构造方法传入。
 * <br/>
 * A default implementation of {@link io.hikarilan.gamesenselib.games.AbstractGame},
 * which does not use {@link #generateFlowManager()}, but passes it directly through the constructor.
 */
@SuppressWarnings("unused")
public class DefaultGame extends AbstractGame {

    public DefaultGame(Plugin plugin, FlowManager.FlowManagerBuilder flowManagerBuilder) {
        super(plugin, flowManagerBuilder);
    }

    @Override
    protected FlowManager.FlowManagerBuilder generateFlowManager() {
        return null;
    }

}
