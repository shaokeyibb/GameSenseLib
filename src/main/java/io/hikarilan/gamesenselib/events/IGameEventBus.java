package io.hikarilan.gamesenselib.events;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

import java.util.Set;
import java.util.UUID;

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
    Set<IGameListener> handlerList = Sets.newHashSet();

    /**
     * Guava 事件总线实例。
     * <p>
     * Guava event bus instance.
     */
    EventBus eventBus = new EventBus("Game" + UUID.randomUUID());

    /**
     * 发布一个事件
     * <p>
     * Calls an event with the given details
     *
     * @param event the event
     * @return the event after the call
     */
    default AbstractGameEvent postEvent(AbstractGameEvent event) {
        eventBus.post(event);
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
        handlerList.add(listener);
        eventBus.register(listener);
    }

    /**
     * 反注册一个事件监听器
     * <p>
     * Unregisters an event listener
     *
     * @param listener the listener
     */
    default void unregisterListener(IGameListener listener) {
        handlerList.remove(listener);
        eventBus.unregister(listener);
    }

    /**
     * 反注册本 EventBus 管理的所有事件监听器
     * <p>
     * Unregisters all event listeners managed by this EventBus
     */
    default void unregisterAllListeners() {
        handlerList.forEach(eventBus::unregister);
        handlerList.clear();
    }

}
