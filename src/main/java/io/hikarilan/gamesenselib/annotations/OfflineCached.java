package io.hikarilan.gamesenselib.annotations;

import java.lang.annotation.*;

/**
 * 注解用于标识一个方法可在玩家离线状态使用，
 * 该方法返回的结果可能为缓存结果。
 * <p>
 * The annotation is used to identify a method that can be used when player offline,
 * The result returned by the method may be a cached result.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OfflineCached {
}
