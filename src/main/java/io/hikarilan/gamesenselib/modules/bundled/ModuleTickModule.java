package io.hikarilan.gamesenselib.modules.bundled;

import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * <b>已捆绑模块（会在游戏实例创建时自动加载）。</b>
 * <br/>
 * 用于每刻对所有模块进行 tick。
 * <p>
 * <b>Bundled module (automatically loaded when the game instance is created).</b>
 * <br/>
 * Used to tick all modules every tick.
 */
@RequiredArgsConstructor
public class ModuleTickModule implements IModule {

    private final Plugin plugin;
    private final AbstractGame game;
    private BukkitTask ticker;

    @Override
    public void onInstall() {
        ticker = Bukkit.getScheduler().runTaskTimer(plugin, game::tick, 0, 1);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onUninstall() {
        ticker.cancel();
    }
}
