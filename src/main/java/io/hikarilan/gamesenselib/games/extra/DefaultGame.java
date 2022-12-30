package io.hikarilan.gamesenselib.games.extra;

import io.hikarilan.gamesenselib.flows.FlowManager;
import io.hikarilan.gamesenselib.games.AbstractGame;
import org.bukkit.plugin.Plugin;

/**
 * 一个 {@link io.hikarilan.gamesenselib.games.AbstractGame} 的默认实现
 */
@SuppressWarnings("unused")
public class DefaultGame extends AbstractGame {

    public DefaultGame(Plugin plugin, FlowManager.FlowManagerBuilder flowManagerBuilder) {
        super(plugin, flowManagerBuilder);
    }

}
