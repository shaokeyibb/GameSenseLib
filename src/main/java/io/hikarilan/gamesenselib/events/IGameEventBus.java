package io.hikarilan.gamesenselib.events;

import com.google.common.eventbus.EventBus;

import java.util.Set;

/**
 * 代表一个游戏事件总线。
 * 游戏事件总线通常被一个游戏实例所持有，用于发布事件和注册/反注册事件监听器
 * <br/>
 * 一个游戏实例拥有一个固定的事件总线，这可以避免与其他游戏实例发布的事件混淆。
 * <p>
 * Represents a game event bus.
 * The game event bus is usually held by a game instance to publish events and register/unregister event listeners
 * <br/>
 * A game instance has a fixed event bus, which can avoid confusion with events published by other game instances.
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public interface IGameEventBus {

    /**
     * 该事件总线管理的所有事件监听器列表。
     * <p>
     * The list of all event listeners managed by this event bus.
     */
    Set<IGameListener> getHandlerList();

    /**
     * Guava 事件总线实例。
     * <p>
     * Guava event bus instance.
     */
    EventBus getEventBus();

    /**
     * 发布一个事件
     * <p>
     * Calls an event with the given details
     *
     * @param event the event
     * @return the event after the call
     */
    default AbstractGameEvent postEvent(AbstractGameEvent event) {
        getEventBus().post(event);
        return event;
    }

    /**
     * 注册一个事件监听器
     * <p>
     * Registers an event listener
     *
     * @param listener the listener
     */
    default void registerListener(IGameListener listener) {
        getHandlerList().add(listener);
        getEventBus().register(listener);
    }

    /**
     * 反注册一个事件监听器
     * <p>
     * Unregisters an event listener
     *
     * @param listener the listener
     */
    default void unregisterListener(IGameListener listener) {
        getHandlerList().remove(listener);
        getEventBus().unregister(listener);
    }

    /**
     * 反注册本 EventBus 管理的所有事件监听器
     * <p>
     * Unregisters all event listeners managed by this EventBus
     */
    default void unregisterAllListeners() {
        getHandlerList().forEach(getEventBus()::unregister);
        getHandlerList().clear();
    }

}
