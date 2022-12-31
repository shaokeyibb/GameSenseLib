package io.hikarilan.gamesenselib.flows.extra;

import com.google.common.collect.Sets;
import io.hikarilan.gamesenselib.artifacts.PhaseAndModule;
import io.hikarilan.gamesenselib.flows.Phase;
import io.hikarilan.gamesenselib.modules.extra.BossBarWaitingRoomModule;
import io.hikarilan.gamesenselib.modules.extra.PhaseBlockingModule;
import io.hikarilan.gamesenselib.players.AbstractPlayer;
import lombok.val;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 额外阶段。
 * <p>
 * Extra phases.
 */
@SuppressWarnings("unused")
public class ExtraPhases {

    /**
     * 创建一个阻塞 Phase。
     * <br/>
     * 该 Phase 将会自动安装和卸载 {@link PhaseBlockingModule}。
     * <br/>
     * 该阶段所在优先级将会被持续阻塞，直到同优先级的其他阶段（或某个模块）调用 {@link PhaseBlockingModule#unlock()} 解除阻塞。
     * <br/>
     * 可被用于诸如等待玩家加入游戏的场景。
     * <p>
     * Create a blocking phase.
     * <br/>
     * The phase will automatically install and uninstall {@link PhaseBlockingModule}.
     * <br/>
     * The priority of the phase will be blocked continuously until other phases at the same priority
     * (or a module) call {@link PhaseBlockingModule#unlock()} to unblock.
     * <br/>
     * Can be used for scenarios such as waiting for players to join the game.
     *
     * @return phase and module for blocking.
     */
    public static PhaseAndModule<PhaseBlockingModule> blockingPhase() {
        val module = new PhaseBlockingModule();
        return PhaseAndModule.of(Phase.builder()
                .onStart(game -> game.installModule(module))
                .onTick(game -> module.block())
                .onEnd(game -> game.uninstallModule(module))
                .build(), module);
    }


    /**
     * 创建一个包含一个等待大厅和阻塞 Phase 的 Flow。
     * <br/>
     * 该 Flow 将会自动安装和卸载 {@link BossBarWaitingRoomModule} 和 {@link PhaseBlockingModule}。
     * <br/>
     * 该阶段所在优先级将会被持续阻塞，玩家人数满足条件后自动解除阻塞。
     * <p>
     * Create a flow containing a waiting room and a blocking phase.
     * <br/>
     * The flow will automatically install and uninstall {@link BossBarWaitingRoomModule} and {@link PhaseBlockingModule}.
     * <br/>
     * The priority of the phase will be blocked continuously,
     * and the blocking will be automatically released after the number of players meets the conditions.
     *
     * @return phase for waiting for game start.
     * @see #waitingForPlayersPhase(int, int, Duration, Location, Runnable, Class)
     * @see BossBarWaitingRoomModule
     */
    @NotNull
    public static Collection<Phase> waitingForPlayersPhase(int minPlayerCount,
                                                           int maxPlayerCount,
                                                           @NotNull Duration countdown,
                                                           @Nullable Location lobbyLocation,
                                                           @Nullable Runnable onFinish) {
        return waitingForPlayersPhase(minPlayerCount, maxPlayerCount, countdown, lobbyLocation, onFinish, AbstractPlayer.class);
    }

    /**
     * 创建一个包含一个等待大厅和阻塞 Phase 的 Flow。
     * <br/>
     * 该 Flow 将会自动安装和卸载 {@link BossBarWaitingRoomModule} 和 {@link PhaseBlockingModule}。
     * <br/>
     * 该阶段所在优先级将会被持续阻塞，玩家人数满足条件后自动解除阻塞。
     * <p>
     * Create a flow containing a waiting room and a blocking phase.
     * <br/>
     * The flow will automatically install and uninstall {@link BossBarWaitingRoomModule} and {@link PhaseBlockingModule}.
     * <br/>
     * The priority of the phase will be blocked continuously,
     * and the blocking will be automatically released after the number of players meets the conditions.
     *
     * @return phase for waiting for game start.
     * @see #waitingForPlayersPhase(int, int, Duration, Location, Runnable)
     * @see BossBarWaitingRoomModule
     */
    @NotNull
    public static Collection<Phase> waitingForPlayersPhase(int minPlayerCount,
                                                           int maxPlayerCount,
                                                           @NotNull Duration countdown,
                                                           @Nullable Location lobbyLocation,
                                                           @Nullable Runnable onFinish,
                                                           @NotNull Class<? extends AbstractPlayer> ingamePlayerClass) {
        val blockingPhaseAndModule = blockingPhase();
        AtomicReference<BossBarWaitingRoomModule> waitingRoomModule = new AtomicReference<>();
        return Sets.newHashSet(blockingPhaseAndModule.getPhase(), Phase.builder()
                .onStart(game -> {
                            waitingRoomModule.set(new BossBarWaitingRoomModule(game, blockingPhaseAndModule.getModule(), minPlayerCount, maxPlayerCount, lobbyLocation, countdown, onFinish, ingamePlayerClass));
                            game.installModule(waitingRoomModule.get());
                        }
                )
                .onTick(game -> blockingPhaseAndModule.getModule().block())
                .onEnd(game -> game.uninstallModule(waitingRoomModule.get()))
                .build());
    }

}
