package io.hikarilan.gamesenselib;

import io.hikarilan.gamesenselib.flows.Phase;
import io.hikarilan.gamesenselib.games.extra.GameTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

@SuppressWarnings("unused")
public final class GameSenseLib extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        GameTemplate.of(this)
                .withWaitingRoom(
                        1,
                        1, Duration.ofSeconds(10), new Location(Bukkit.getWorld("world"), 0, 64, 0))
                .withBroadcastMessagePlayerJoinAndQuit(
                        player -> "Player Join the game: " + player.getName(),
                        player -> "Player Quit the game: " + player.getName()
                )
                .addPhase(
                        0, () -> Phase.builder()
                                .onStart((it) -> Bukkit.broadcastMessage("Game Start!"))
                                .build()
                )
                .independent()
                .build();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
