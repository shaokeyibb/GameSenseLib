package io.hikarilan.gamesenselib.annotations;

import java.lang.annotation.*;

/**
 * 该注解用于标记一个元素为内部元素，一个内部元素不应该被外部调用
 * <p>
 * This annotation is used to mark an element as an internal element, an internal element should not be called externally
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface Internal {
}
