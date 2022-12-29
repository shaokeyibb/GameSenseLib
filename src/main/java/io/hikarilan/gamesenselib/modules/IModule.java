package io.hikarilan.gamesenselib.modules;

import io.hikarilan.gamesenselib.artifacts.IReusable;

/**
 * 代表一个模块，模块全局生效，并可以被卸载。
 * <br/>
 * 通常情况下，当一个模块被安装时，{@link #onInstall()} 方法将被调用；
 * 一个已安装模块的 {@link #onTick()} 方法将被每刻调用；
 * 当一个模块被卸载时，{@link #onUninstall()} 方法将被调用。
 * <br/>
 * 一个模块在其被实例化后可能会被重用，因此请在对应的生命周期及时初始化和清理数据避免出错。
 * <br/>
 * 一个模块在其生命周期内<b>应当</b>只存在在一个 {@link IModuleHolder} 中，
 * 同一个 {@link IModuleHolder} 中最多<b>只能</b>存在一个相同的模块。
 * <p>
 * Represent a module, the module should be loaded all the time, and can be uninstalled in-game.
 * <br/>
 * Usually, the {@link #onInstall()} method will be called when the module to be installed;
 * A {@link #onTick()} method will be called every tick in installed module;
 * Finally, the {@link #onUninstall()} method will be called when the module to be uninstalled.
 * <br/>
 * A module may be reused after it is instantiated, so please initialize and clean up data in time during the corresponding life cycle to avoid errors.
 * <br/>
 * A module <b>should</b> exist in a single {@link IModuleHolder},
 * and there's only on same module <b>can</b> be existed in an {@link IModuleHolder}.
 *
 * @see IModuleHolder
 */
@SuppressWarnings("unused")
public interface IModule extends IReusable {

    /**
     * 当该模块被安装时此方法将被调用。
     * <p>
     * Called when the module to be installed.
     *
     * @see IModuleHolder#installModule(IModule)
     */
    void onInstall();

    /**
     * 如果该模块已被安装，则该方法将被每刻调用。
     * <p>
     * Called every tick if the module has been installed.
     *
     * @see IModuleHolder#tick()
     */
    void onTick();

    /**
     * 当该模块被卸载时此方法将被调用
     * <p>
     * Called when the module to be uninstalled.
     *
     * @see IModuleHolder#uninstallModule(IModule)
     */
    void onUninstall();

    @Override
    default void init() {
        onInstall();
    }

    @Override
    default void destroy() {
        onUninstall();
    }
}
