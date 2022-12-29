package io.hikarilan.gamesenselib.utils;

import java.time.Duration;

@SuppressWarnings("unused")
public class Durations {

    /**
     * 将一个 {@link Duration} 转换为 tick。
     * <br/>
     * 由于平台限制，小于一个 tick（50 毫秒）的 {@link Duration} 在使用时可能不准确。
     * <p>
     * Convert a {@link Duration} to tick.
     * <br/>
     * Due to a platform limitation, a {@link Duration} less than a tick (50 milliseconds) may not accurate during using.
     * @param duration the duration
     * @return tick from the duration
     */
    public static long toTick(Duration duration) {
        return duration.toMillis() / 1000 * 20;
    }

    /**
     * 从 tick 获取 {@link Duration}。
     * <p>
     * Get {@link Duration} from tick.
     * @param tick the tick
     * @return duration from the tick
     */
    public static Duration ofTick(long tick) {
        return Duration.ofMillis(tick / 20 * 1000);
    }


    /**
     * 检查一个 {@link Duration} 是否为 0 或为负。
     * 使用此方法代替 {@link Duration#isZero()}，因为后者在实际使用中可能导致一些精度问题。
     * <p>
     * Check if the {@link Duration} is zero or negative
     * This method used to replace the {@link Duration#isZero()} due to some accuracy problems.
     * @param duration the duration
     * @return is the duration zero or negative
     */
    public static boolean isZeroOrNegative(Duration duration) {
        return duration.isZero() || duration.isNegative();
    }

}
