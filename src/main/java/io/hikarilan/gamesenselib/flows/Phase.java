package io.hikarilan.gamesenselib.flows;

import io.hikarilan.gamesenselib.artifacts.IReusable;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Builder;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.hikarilan.gamesenselib.utils.Durations.isZeroOrNegative;
import static io.hikarilan.gamesenselib.utils.Durations.ofTick;

/**
 * 代表一局游戏中某个固定的阶段。
 * <br/>
 * 一个阶段将被 {@link FlowManager} 在指定<b>优先级</b>执行。同一<b>优先级</b>可能有多个阶段被处理。
 * <br/>
 * 一个阶段被分为三个部分，即 {@link #onStart}, {@link #onTick} 和 {@link #onEnd}，这些部分将会在不同时刻被执行；
 * <br/>
 * 当该阶段第一次被执行时，{@link #onStart} 将被调用；
 * <br/>
 * 当该阶段下一次被执行时，{@link #onTick} 将被调用，此时函数式接口将返回一个 Boolean 值，
 * 该 Boolean 值代表是否应当进入结束部分，如果为 <code>false</code>，则下一 tick {@link #onTick} 将被继续调用，
 * 否则，下一次被执行时将会调用 {@link #onEnd} 方法。
 * <br/>
 * 一个阶段在其被实例化后可能会被重用，因此请在对应的生命周期及时初始化和清理数据避免出错。
 * <p>
 * Represents a fixed phase in a game.
 * <br/>
 * A phase will be executed at the specified <b>flow</b>. Multiple phases may be processed at the same <b>flow</b>.
 * <br/>
 * A phase will be spilt into 3 parts, they are {@link #onStart}, {@link #onTick} and {@link #onEnd}, these parts will be executed at different ticks.
 * <br/>
 * When the phase be executed at first time, the {@link #onStart} will be invoked.
 * <br/>
 * When the next tick the phase be executed, the {@link #onTick} will be invoked, at that time the function will return a Boolean value,
 * the value represent whether the phase will be ended at next tick, if <code>false</code>, the {@link #onTick} method will be continued to invoke next tick.
 * Or, the {@link #onEnd} will be invoked next tick.
 * <br/>
 * A phase may be reused after it is instantiated, so please initialize and clean up data in time during the corresponding life cycle to avoid errors.
 *
 * @see FlowManager
 */
@SuppressWarnings("unused")
public class Phase implements IReusable {

    /**
     * 当阶段开始时被调用。
     * <p>
     * Invoked when phase first run.
     */
    Consumer<AbstractGame> onStart;
    /**
     * 当阶段持续运行时被调用，直到所有同一优先级的阶段均希望结束运行。
     * <p>
     * Invoke when phase running, until all phases in the same time need be ended the runs.
     */
    Function<AbstractGame, Boolean> onTick;
    /**
     * 当阶段结束时被调用。
     * <p>
     * Invoked when phase ended it run.
     */
    Consumer<AbstractGame> onEnd;

    @Builder
    public Phase(@Nullable Consumer<AbstractGame> onStart, @Nullable Function<AbstractGame, Boolean> onTick, @Nullable Consumer<AbstractGame> onEnd) {
        this.onStart = onStart;
        this.onTick = onTick;
        this.onEnd = onEnd;
        if (onStart == null) {
            this.onStart = (_ignored) -> {
            };
        }
        if (onTick == null) {
            this.onTick = (_ignored) -> true;
        }
        if (onEnd == null) {
            this.onEnd = (_ignored) -> {
            };
        }
    }

    /**
     * 在游戏的一个阶段中延迟指定时间
     * <p>
     * Delay specify duration in the game
     * <p>
     * Here's an example to delay 100 tick as a phase.
     * <pre><code>
     *     Phase.builder()
     *     .onTick(delay(Durations.ofTick(100)))
     *     .build();
     * </code></pre>
     *
     * @param duration delay duration
     * @return onTick function
     */
    @NotNull
    public static Supplier<Boolean> delay(@NotNull Duration duration) {
        val remain = new AtomicReference<>(duration);
        return () -> isZeroOrNegative(remain.updateAndGet(it -> it.minus(ofTick(1))));
    }

    /**
     * {@link #onStart} 是否已调用。
     * <p>
     * Is {@link #onStart} invoked.
     */

    private boolean isStartFinish;
    /**
     * onTick 是否返回 true。
     * <p>
     * Is {@link #onTick} return true in this tick.
     */
    private boolean isTickFinish;
    /**
     * onEnd 是否已调用
     * <p>
     * Is {@link #onEnd} invoked;
     */
    private boolean isEndFinish;

    /**
     * 进行一次 tick。
     * <p>
     * tick once.
     *
     * @return whether enter next <b>flow</b> or not
     * @see Phase
     */
    public boolean tick(AbstractGame game) {
        if (!isStartFinish) {
            onStart.accept(game);
            isStartFinish = true;
            return false;
        }

        if (!isTickFinish) {
            isTickFinish = onTick.apply(game);
            return false;
        }

        if (!isEndFinish) {
            onEnd.accept(game);
            isEndFinish = true;
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        isStartFinish = false;
        isTickFinish = false;
        isEndFinish = false;
    }

    @Override
    public void destroy() {
    }
}
