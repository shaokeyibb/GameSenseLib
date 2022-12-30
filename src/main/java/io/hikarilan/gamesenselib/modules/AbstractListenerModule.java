package io.hikarilan.gamesenselib.modules;

import io.hikarilan.gamesenselib.events.IGameListener;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 一个抽象监听器模块，会在模块安装时将其本身注册到 GameSenseLib 事件总线，并在卸载时将其本身从事件总线中注销。
 * <p>
 * An abstract listener module that will register itself to the GameSenseLib event bus when the module is installed,
 */
@RequiredArgsConstructor
public abstract class AbstractListenerModule implements IModule, IGameListener {

    @Getter
    private final AbstractGame game;

    @Override
    public void onInstall() {
        game.registerListener(this);
    }

    @Override
    public void onUninstall() {
        game.unregisterListener(this);
    }
}
