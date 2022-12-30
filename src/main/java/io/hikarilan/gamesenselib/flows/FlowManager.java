package io.hikarilan.gamesenselib.flows;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.hikarilan.gamesenselib.artifacts.IReusable;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 代表一个流程管理器，通常被一个游戏实例所持有，负责管理游戏流程。
 * <br/>
 * 流程管理器管理一个含有多个优先级的游戏流程，每个优先级包含一个流程，每个流程包含多个阶段，
 * 一个游戏实例应当每刻调用 {@link #tick()} 方法以运行流程管理器，如此一来，流程管理器会以如下方式运行：
 * <br/>
 * 位于最低优先级的流程将会首先运行，然后是更高优先级的流程被运行，直到所有优先级的流程被运行完毕；
 * <br/>
 * 单个流程可以被看作并行的运行其包含的所有阶段内的 {@link Phase#tick(AbstractGame)} 方法，而其返回值将会共同决定是否进入下一个流程，
 * 当且仅当该流程所有阶段的 {@link Phase#tick(AbstractGame)} 方法均返回 <code>true</code>，即所有阶段均已结束时，才会进入下一个流程。
 * <p>
 * Represents a flow manager, usually held by a game instance, responsible for managing the game flow.
 * <br/>
 * A flow manager manage game flows including multiple priorities, each priority include a flow, the flow will include multiple phases,
 * A game instance should call the {@link #tick()} every tick to make the flow manager run, so that,
 * the flow manager will be run by the following ways:
 * <br/>
 * Flow at the lowest priority will be run first, then higher priority flow will be run until all priority flows have been run;
 * <br/>
 * A single flow can be regarded as running {@link Phase#tick(AbstractGame)} methods in all phases in parallel,
 * and its return value will jointly determine whether to enter the next flow,
 * Only when all the {@link Phase#tick(AbstractGame)} method in the phases return <code>true</code>, i.e. all the phase in the flow ended,
 * the next flow will be entered.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class FlowManager implements IReusable {

    private final AbstractGame game;

    private final Map<Integer, List<Phase>> flows;

    private int pointer;

    /**
     * 尝试进入下一个流程。
     * <br/>
     * 如果已达到最高优先级，则不会进入下一个流程；
     * 如果下一个优先级不存在，则会直接跳过并继续尝试进入下一个流程。
     * <p>
     * Attempt to enter next flow.
     * <br/>
     * If the maximum number of phases is reached, will not enter the next flow;
     * If next priority is not exist, the priority will be skipped and continue to attempt to enter.
     *
     * @return Should enter next flow.
     */
    private boolean next() {
        // If the maximum number of phases is reached, stop going to the next flow.
        if (pointer + 1 > flows.keySet().stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0))
            return false;

        // If next priority is not exist, continue to next flow.
        if (!flows.containsKey(++pointer))
            return next();

        // enter next flow.
        return true;
    }

    /**
     * 进行一次 tick
     * <p>
     * tick once.
     *
     * @return Should enter next flow.
     * @see FlowManager
     */
    public boolean tick() {
        AtomicBoolean isFinish = new AtomicBoolean(true);

        // get current flow
        var flow = flows.get(pointer);

        // if flow exists, tick all phases
        if (flow != null) {
            flow.forEach(it -> {
                // if any phase return false, the flow will be considered unfinished.
                if (!it.tick(game)) isFinish.set(false);
            });
        }

        // if the flow finished (all phases return true), enter next flow.
        if (isFinish.get()) {
            return next();
        }

        // or, continue this flow.
        return false;
    }

    @Override
    public void init() {
        flows.values().stream().flatMap(List::stream).forEach(Phase::init);
        pointer = 0;
    }

    @Override
    public void destroy() {
        flows.values().stream().flatMap(List::stream).forEach(Phase::destroy);
    }

    @NotNull
    public static FlowManager.FlowManagerBuilder builder() {
        return new FlowManagerBuilder();
    }

    @ToString
    public static class FlowManagerBuilder {

        private AbstractGame game;
        private final Map<Integer, List<Phase>> flows = Maps.newConcurrentMap();

        public FlowManager.FlowManagerBuilder $game(AbstractGame game) {
            this.game = game;
            return this;
        }

        /**
         * 为指定优先级的游戏流程添加游戏阶段
         * <p>
         * Add phase to specify flow of priority
         *
         * @param priority priority of the flow
         * @param supplier phase function
         * @return this builder
         */
        @NotNull
        public FlowManager.FlowManagerBuilder addPhase(int priority, @NotNull Supplier<@NotNull Phase> supplier) {
            flows.putIfAbsent(priority, Lists.newArrayList());
            flows.get(priority).add(supplier.get());
            return this;
        }

        /**
         * 为指定优先级的游戏流程添加游戏阶段
         * <p>
         * Add phases to specify flow of priority
         *
         * @param priority priority of the flow
         * @param supplier phases function
         * @return this builder
         */
        @NotNull
        public FlowManager.FlowManagerBuilder addAllPhase(int priority, @NotNull Supplier<@NotNull Collection<@NotNull Phase>> supplier) {
            flows.putIfAbsent(priority, Lists.newArrayList());
            flows.get(priority).addAll(supplier.get());
            return this;
        }

        /**
         * @throws IllegalStateException if game is null
         */
        @NotNull
        public FlowManager build() {
            if (game == null) {
                throw new IllegalStateException("Game instance is not set.");
            }
            return new FlowManager(game, flows);
        }

    }
}
