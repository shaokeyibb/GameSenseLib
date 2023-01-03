package io.hikarilan.gamesenselib.players;

import com.google.common.collect.Queues;
import io.hikarilan.gamesenselib.annotations.OfflineCached;
import io.hikarilan.gamesenselib.annotations.OfflineQueued;
import io.hikarilan.gamesenselib.artifacts.IConsumerQueueHolder;
import io.hikarilan.gamesenselib.games.AbstractGame;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 代表一个游戏内的玩家基类。
 * <br/>
 * {@link AbstractPlayer} 不是一个 {@link Player} 的直接包装对象，
 * 其通过队列延迟执行或缓存的方式而允许部分操作在一个 {@link Player} 离线的情况下工作。
 * <br/>
 * 您应当尽量避免操作正在游戏中的 {@link AbstractPlayer} 的 {@link Player} 对象。
 * <br/>
 * 一个玩家实例的生命周期应当仅限于一局游戏内，当该局游戏流程结束，应当调用 {@link #destroy()} 方法销毁该玩家实例
 * （即使该游戏可以重新开始，也应当销毁该玩家实例，然后重新生成一个新的玩家实例）。
 * <p>
 * Represent a player base class in the game.
 * <br/>
 * {@link AbstractPlayer} is not a direct wrapper object of {@link Player},
 * and it allows some operations to work even if the {@link Player} is offline
 * by delaying execution through the queue or cache.
 * <br/>
 * You should avoid operating the {@link Player} object of the {@link AbstractPlayer} that is in the game.
 * <br/>
 * The lifetime of a player instance should only be limited to a game. when the game flow ends,
 * you should call the {@link #destroy()} method to destroy the player instance
 * (even if the game can be restarted, you should destroy the player instance and then regenerate a new player instance).
 */
@SuppressWarnings("unused")
@ToString
public abstract class AbstractPlayer implements IConsumerQueueHolder<Player> {

    /**
     * 玩家所在的游戏实例
     * <p>
     * The game instance where the player is located
     */
    private final AbstractGame game;

    /**
     * 玩家的唯一标识符（与 {@link Player#getUniqueId()} 相同）
     * <p>
     * The unique identifier of the player (same as {@link Player#getUniqueId()})
     */
    private final UUID uniqueId;

    /**
     * 玩家实例是否已被销毁。
     * <br/>
     * 一个已被销毁的玩家实例不应再被使用，相反，他们应被立即丢弃。
     * <p>
     * Whether the player instance has been destroyed.
     * <br/>
     * A destroyed player instance should not be used again, instead, they should be discarded immediately.
     */
    @Getter
    private boolean destroyed = false;

    /**
     * 获取玩家的名字（与 {@link Player#getName()} 相同）。
     * <p>
     * Get the name of the player (same as {@link Player#getName()}).
     */
    @Getter(onMethod_ = {@OfflineCached})
    private final String name;

    /**
     * 初始化时玩家的显示名称（初始值与 {@link Player#getDisplayName()} 相同）。
     * <br/>
     * 该值仅用于恢复玩家先前的显示名称。
     * <p>
     * The display name of the player when initialized (the initial value is the same as {@link Player#getDisplayName()}).
     * <br/>
     * This value is only used to restore the player's previous display name.
     */
    private final String _displayName;

    /**
     * 玩家当前的显示名称，其值在玩家在线时始终与 {@link Player#getDisplayName()} 相同。
     * <p>
     * The current display name of the player,
     * always the same as {@link Player#getDisplayName()} when the player is online.
     */
    private String displayName;

    /**
     * 玩家当前的位置的缓存值，只在玩家退出游戏时更新。
     * <p>
     * The cached value of the player's current location,
     * only updated when the player exits the game.
     *
     * @see #getLocation()
     */
    private Location location;

    /**
     * 延迟执行队列，用于在玩家离线时缓存操作。
     * <p>
     * Delayed execution queue, used to cache operations when the player is offline.
     */
    private final Queue<Consumer<Player>> consumerQueue = Queues.newSynchronousQueue();

    protected AbstractPlayer(@NotNull AbstractGame game, @NotNull Player player) {
        this.game = game;
        this.uniqueId = player.getUniqueId();

        this.name = player.getName();
        this._displayName = player.getDisplayName();
        this.displayName = player.getDisplayName();
    }

    /**
     * 获取 Bukkit 玩家实例，当玩家不在线时，该方法可能将返回 {@code null}。
     * <p>
     * Get the Bukkit player instance. When the player is not online, this method may return {@code null}.
     *
     * @return the bukkit player instance, or {@code null} when the player is offline.
     */
    @ApiStatus.Internal
    protected Player getRawPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    /**
     * 查询该 {@link AbstractPlayer} 对象是否包装了指定的 {@link Player} 对象
     * <p>
     * Check whether the {@link AbstractPlayer} object wraps the specified {@link Player} object
     *
     * @param other the other player
     * @return {@code true} if this {@link AbstractPlayer} object wraps the specified {@link Player} object
     */
    @ApiStatus.Internal
    public boolean isWrapper(Player other) {
        return uniqueId.equals(other.getUniqueId());
    }

    /**
     * 查询该 {@link AbstractPlayer} 对象是否属于指定的 {@link AbstractGame} 对象
     * <p>
     * Check whether the {@link AbstractPlayer} object belongs to the specified {@link AbstractGame} object
     *
     * @param game the game
     * @return {@code true} if this {@link AbstractPlayer} object belongs to the specified {@link AbstractGame} object
     */
    @ApiStatus.Internal
    public boolean isWrapper(AbstractGame game) {
        return this.game == game;
    }

    /**
     * 检测玩家是否在线
     * <p>
     * Check if the player is online
     *
     * @return {@code true} if the player is online, otherwise {@code false}
     */
    public boolean isOnline() {
        val player = getRawPlayer();
        return player != null && player.isOnline();
    }

    /**
     * 获取玩家当前的显示名称，其值在玩家在线时始终与 {@link Player#getDisplayName()} 相同。
     * <p>
     * Gets the current display name of the player,
     * always the same as {@link Player#getDisplayName()} when the player is online.
     *
     * @return the current display name of the player
     */
    @OfflineCached
    public String getDisplayName() {
        if (isOnline()) return getRawPlayer().getDisplayName();
        return displayName;
    }

    /**
     * 设置玩家的显示名称，当玩家不在线时，该值将在玩家下次上线时生效。
     * <p>
     * Set the display name of the player,
     * When the player is offline, this value will take effect when the player logs in next time.
     *
     * @param displayName the new display name of the player
     */
    @OfflineQueued
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        runWhenOnline(player -> player.setDisplayName(displayName));
    }

    /**
     * 获取玩家当前的位置，当玩家不在线时，将返回玩家最后一次下线时所在的位置。
     * <p>
     * Get the current location of the player,
     * when the player is offline, the location where the player last logged off will be returned.
     *
     * @return the current location of the player, or the location where the player last logged off.
     */
    @OfflineCached
    public Location getLocation() {
        if (isOnline()) return getRawPlayer().getLocation();
        return location;
    }

    /**
     * 将玩家传送到指定位置。
     * <p>
     * Teleport the player to the specified location.
     *
     * @param location the location to teleport.
     */
    @OfflineQueued
    public void teleport(Location location) {
        runWhenOnline(player -> player.teleport(location));
    }

    /**
     * 发送一条信息给玩家。
     * <p>
     * Send a message to the player.
     *
     * @param message Message to be displayed
     * @see Player#sendMessage(String)
     */
    public void sendMessage(String message) {
        if (!isOnline()) return;
        getRawPlayer().sendMessage(message);
    }

    /**
     * 发送一条消息到此玩家的 Actionbar
     * <p>
     * Send a message to the Actionbar of this player
     *
     * @param message Message to be displayed in the Actionbar
     */
    public void sendActionBar(String message) {
        if (!isOnline()) return;
        getRawPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * 向播放器发送标题和副标题消息。
     * 如果这两个值中的任何一个为空，它们将不会被发送并且显示将保持不变。
     * 如果它们是空字符串，显示将被更新。
     * 如果字符串包含新行，则仅发送第一行。
     * 所有计时值都可以取值 -1 以指示它们将使用最后发送的值（如果没有显示过 title，则使用默认值）。
     * <p>
     * Sends a title and a subtitle message to the player. If either of these
     * values are null, they will not be sent and the display will remain
     * unchanged. If they are empty strings, the display will be updated as
     * such. If the strings contain a new line, only the first line will be
     * sent. All timings values may take a value of -1 to indicate that they
     * will use the last value sent (or the defaults if no title has been
     * displayed).
     *
     * @param title    Title text
     * @param subtitle Subtitle text
     * @param fadeIn   time in ticks for titles to fade in. Defaults to 10.
     * @param stay     time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut  time in ticks for titles to fade out. Defaults to 20.
     * @see Player#sendTitle(String, String, int, int, int)
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (!isOnline()) return;
        getRawPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * 踢出玩家。
     * <p>
     * Kicks player with custom kick message.
     *
     * @param message kick message
     * @see Player#kickPlayer(String)
     */
    @OfflineQueued
    public void kickPlayer(String message) {
        runWhenOnline(player -> player.kickPlayer(message));
    }


    /**
     * 销毁此玩家实例
     * <br/>
     * 这将重置该玩家的所有状态
     * <p>
     * Destroy this player instance
     * <br/>
     * This will reset all the states of this player
     */
    public void destroy() {
        destroyed = true;

        consumerQueue.clear();

        // rollback player information (use consumer queue when player offline)
        setDisplayName(_displayName);
    }

    /**
     * 更新一次缓存。
     * <br/>
     * 这将从 Bukkit 获取玩家的最新信息并更新。
     * <br/>
     * 这要求玩家必须在线。
     * <p>
     * Update the cache.
     * <br/>
     * This will get the latest information of the player from Bukkit and update it.
     * <br/>
     * This requires the player to be online.
     *
     * @throws IllegalStateException if the player is offline.
     */
    public void updateCache() {
        if (!isOnline()) throw new IllegalStateException("Cannot update cache when player offline");

        setDisplayName(getRawPlayer().getDisplayName());

        location = getRawPlayer().getLocation();
    }

    @NotNull
    @Override
    public Queue<Consumer<Player>> getQueue() {
        return consumerQueue;
    }

    public void runWhenOnline(Consumer<Player> onOnline) {
        if (isOnline()) {
            onOnline.accept(getRawPlayer());
        } else {
            consumerQueue.offer(onOnline);
        }
    }
}
