package io.hikarilan.gamesenselib.modules.extra;

import io.hikarilan.gamesenselib.flows.extra.ExtraPhases;
import io.hikarilan.gamesenselib.modules.IModule;
import lombok.Getter;

/**
 * 阶段阻塞模块。
 * 用于配合 {@link ExtraPhases#blockingPhase()} 使用，阻塞指定阶段的进入。
 * <p>
 * Phase blocking module.
 * Used to block the entry of the specified phase in conjunction with {@link ExtraPhases#blockingPhase()}.
 *
 * @see ExtraPhases#blockingPhase()
 */
public class PhaseBlockingModule implements IModule {

    @Getter
    private boolean blocking;

    @Override
    public void onInstall() {
        blocking = false;
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onUninstall() {
        unlock();
    }

    /**
     * 检查是否阻塞。
     * <p>
     * Check if it is blocked.
     *
     * @return blocked of not
     */
    public boolean block() {
        return !blocking;
    }

    /**
     * 解除阻塞。
     * <p>
     * Unlock the block.
     */
    public void unlock() {
        blocking = true;
    }
}
