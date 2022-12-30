package io.hikarilan.gamesenselib.modules.bundled;

import io.hikarilan.gamesenselib.games.AbstractGame;
import io.hikarilan.gamesenselib.modules.IModule;
import lombok.RequiredArgsConstructor;

/**
 * <b>已捆绑模块（会在游戏实例创建时自动加载）。</b>
 * <br/>
 * 用于每刻对 FlowManager 进行 tick。
 * <p>
 * <b>Bundled module (automatically loaded when the game instance is created).</b>
 * <br/>
 * Used to tick FlowManager every tick.
 */
@RequiredArgsConstructor
public class FlowTickModule implements IModule {
    private final AbstractGame game;

    @Override
    public void onInstall() {
    }

    @Override
    public void onTick() {
        game.getFlowManager().tick();
    }

    @Override
    public void onUninstall() {
    }

}
