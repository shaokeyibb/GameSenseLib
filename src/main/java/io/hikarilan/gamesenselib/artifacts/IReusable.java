package io.hikarilan.gamesenselib.artifacts;

/**
 * 代表一个可复用的构件。
 * <p>
 * Represent an artifact can be reused.
 */
@SuppressWarnings("unused")
public interface IReusable {

    /**
     * 初始化构件
     * <p>
     * Initial the artifact
     */
    void init();

    /**
     * 销毁构件
     * <p>
     * Destroy the artifact
     */
    void destroy();

    /**
     * 重载构件
     * <p>
     * Reload the artifact
     */
    default void reload() {
        destroy();
        init();
    }

}
