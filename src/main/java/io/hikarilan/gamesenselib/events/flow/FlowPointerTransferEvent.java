package io.hikarilan.gamesenselib.events.flow;

import io.hikarilan.gamesenselib.events.AbstractGameEvent;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Getter;

/**
 * Flow 指针转移事件。
 * <br/>
 * <b>这不是必须的，但我们强烈建议您仅在 {@link io.hikarilan.gamesenselib.modules.IModule} 中监听此事件以确保安全</b>
 * <br/>
 * 此事件用于修改 {@link io.hikarilan.gamesenselib.flows.FlowManager} 中 Flow 指针的位置以重定向下一个 Flow。
 * <br/>
 * 此事件会在即将进入下一个 Flow 时触发，您可以选择监听此事件，然后通过调用 {@link #setPointer(int)} 更改指针值（也即运行优先级）。
 * <br/>
 * 如果该值保持默认值（-1），则游戏将会正常进入下一个 Flow。
 * <p>
 * Flow Pointer Transfer Event.
 * <br/>
 * <b>This is not required,
 * but we strongly recommend that you only listen to this event in {@link io.hikarilan.gamesenselib.modules.IModule} to ensure safety</b>
 * <br/>
 * This event is used to modify the position of the Flow pointer
 * in {@link io.hikarilan.gamesenselib.flows.FlowManager} to redirect the next Flow.
 * <br/>
 * This event will be triggered when the next Flow is about to be entered,
 * and you can choose to listen to this event and then call {@link #setPointer(int)} to change the pointer value (that is, the priority of execution).
 * <br/>
 * If the value remains the default value (-1),
 * the game will enter the next Flow normally.
 */
public class FlowPointerTransferEvent extends AbstractGameEvent {

    @Getter
    private int pointer = -1;

    public FlowPointerTransferEvent(AbstractGame game) {
        super(game);
    }

    public void setPointer(int pointer) {
        if (pointer < 0) throw new IllegalArgumentException("Priority should start from 0.");
        this.pointer = pointer;
    }
}
