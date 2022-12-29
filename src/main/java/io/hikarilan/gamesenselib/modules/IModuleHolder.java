package io.hikarilan.gamesenselib.modules;

import io.hikarilan.gamesenselib.artifacts.IReusable;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 代表一个模块持有者，通常为一个游戏实例，模块持有者负责管理并 tick 所有模块。
 * <br/>
 * <b>请注意，同一个 {@link IModule} 类的不同实例将被视为同一个 {@link IModule}，因为 {@link IModule} 被设计为在一个 {@link IModuleHolder} 中仅能存在一个唯一实例</b>
 * <p>
 * Represent a module holder, usually a game instance, the module holder is used to manage and tick all the installed modules.
 * <br/>
 * <b>ATTENTION: the different instances of the same {@link IModule} class will be treated as the same, because {@link IModule} is designed so that only one unique instance can exist in an {@link IModuleHolder}</b>
 *
 * @see IModule
 */
@SuppressWarnings("unused")
public interface IModuleHolder extends IReusable {

    Map<Class<? extends IModule>, IModule> installedModules = new HashMap<>();

    /**
     * 安装一个模块。
     * 当安装时，{@link IModule#onInstall()} 方法将被执行。
     * 如果模块已被安装，则该模块会先被卸载，然后重新被安装。
     * <p>
     * Install a module.
     * The {@link IModule#onInstall()} method will be called at this time.
     * If the module has been installed yet, then the module will be re-installed after an instant uninstallation operation.
     *
     * @param module the module to be installed.
     * @throws IllegalStateException if specify module already installed.
     * @see IModule#onInstall()
     */
    default void installModule(@NotNull IModule module) {
        if (hasModule(module))
            throw new IllegalStateException("The module " + module.getClass() + " already installed.");
        module.onInstall();
        installedModules.put(module.getClass(), module);
    }

    /**
     * 卸载一个模块。
     * <br/>
     * 当卸载时，{@link IModule#onUninstall()} 方法将被执行
     * <p>
     * Uninstall a module.
     * <br/>
     * The {@link IModule#onUninstall()} method will be called at this time.
     *
     * @param module the module to be uninstalled.
     * @throws IllegalStateException if specify module not installed yet.
     * @see IModule#onUninstall()
     */
    default void uninstallModule(@NotNull Class<? extends IModule> module) {
        if (!hasModule(module))
            throw new IllegalStateException("The module " + module + " not installed yet.");
        val uninstalled = installedModules.remove(module);
        if (uninstalled != null) uninstalled.onUninstall();
    }

    /**
     * 卸载一个模块。
     * <br/>
     * 当卸载时，{@link IModule#onUninstall()} 方法将被执行。
     * <p>
     * Uninstall a module.
     * <br/>
     * The {@link IModule#onUninstall()} method will be called at this time.
     *
     * @param module the module to be uninstalled.
     * @throws IllegalStateException if specify module not installed yet.
     * @see IModule#onUninstall()
     */
    default void uninstallModule(@NotNull IModule module) {
        uninstallModule(module.getClass());
    }

    /**
     * 卸载所有模块。
     * <br/>
     * 所有模块的 {@link IModule#onUninstall()} 方法将被顺序调用。
     * <p>
     * Uninstall all modules.
     * <br/>
     * The {@link IModule#onUninstall()} method will be called by order.
     */
    default void uninstallAllModule() {
        val iter = installedModules.values().iterator();
        while (iter.hasNext()) {
            iter.next().onUninstall();
            iter.remove();
        }
    }

    /**
     * 查询一个模块是否存在
     * <p>
     * Check a module exist or not.
     *
     * @param module the module need to check.
     * @return if the module exists.
     */
    default boolean hasModule(@NotNull Class<? extends IModule> module) {
        return installedModules.containsKey(module);
    }

    /**
     * 查询一个模块是否存在
     * <p>
     * Check a module exist or not.
     *
     * @param module the module need to check.
     * @return if the module exists.
     */
    default boolean hasModule(@NotNull IModule module) {
        return installedModules.containsKey(module.getClass());
    }

    /**
     * 获得一个已安装模块。
     * <p>
     * Get an installed module.
     *
     * @param module the module need to get
     * @return the module
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default <T extends IModule> T getModule(@NotNull Class<T> module) {
        return (T) installedModules.get(module);
    }

    /**
     * tick 所有已安装模块一次。
     * 已安装模块列表将被拷贝一份后再进行 tick 以避免冲突。
     * <p>
     * tick all installed module at once.
     * the list of installed modules will be copied to avoid some conflicts.
     */
    default void tick() {
        new ArrayList<>(installedModules.values()).forEach(IModule::onTick);
    }

    @Override
    default void init() {
    }

    @Override
    default void destroy() {
        uninstallAllModule();
    }
}
