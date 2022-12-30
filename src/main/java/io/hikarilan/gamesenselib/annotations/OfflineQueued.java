package io.hikarilan.gamesenselib.annotations;

import java.lang.annotation.*;

/**
 * 该注解用于标识一个方法可在玩家离线状态使用，
 * 该方法调用后将会在玩家下次上线时执行。
 * <p>
 * This annotation is used to identify a method that can be used when player offline,
 * After the method is called, it will be executed when the player logs in next time.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OfflineQueued {
}
