# GameSenseLib

Build your own Minecraft minigame with flexible, extensible, and modular API.

Build your game with a simple style API with some built-in modules:

```java
public class ExampleGame {
    public static AbstractGame simpleGame() {
        GameTemplate.of(plugin)
                .withWaitingRoom(
                        1, // min player
                        2, // max player
                        Duration.ofSeconds(10), // waiting duration
                        new Location(Bukkit.getWorld("game_world"), 0, 64, 0) // waiting room spawn location
                )
                .withBroadcastMessagePlayerJoinAndQuit(
                        player -> "Player Join the game: " + player.getName(),
                        player -> "Player Quit the game: " + player.getName()
                )
                .setRemovePlayerOnQuit(true)
                .setTeleportPlayerOnQuit(true)
                .setQuitCommand("quit")
                .setJoinCommand("join")
                .addPhase(
                        0, // priority
                        () -> Phase.builder()
                                .onStart((it) -> Bukkit.broadcastMessage("Game Start!"))
                                .build()
                )
                .world(Bukkit.getWorld("game_world"))
                .build();
    }
}
```

Or, try a more complex, flexible style:

```java
public class ExampleGame extends AbstractGame {

    public ExampleGame(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        super.init();

        Bukkit.broadcastMessage("Game Created...");

        installModule(new ExampleModule(this));

        // do your own stuff...
    }

    @Override
    protected FlowManager.FlowManagerBuilder generateFlowManager() {
        FlowManager.FlowManagerBuilder builder = FlowManager.builder();

        builder.addPhase(
                0, // priority
                () -> Phase.builder()
                        .onStart((it) -> Bukkit.broadcastMessage("Game Start!"))
                        .build()
        );

        PhaseAndModule<PhaseBlockingModule> blockingModule = ExtraPhases.blockingPhase();
        builder.addPhase(
                1,
                blockingModule::getPhase
        );
        builder.addPhase(
                1,
                () -> Phase.builder()
                        .onStart((it) -> Bukkit.broadcastMessage("Waiting for game end..."))
                        .build()
        );

        builder.addPhase(
                2,
                () -> Phase.builder()
                        .onStart((it) -> Bukkit.broadcastMessage("Game end!"))
                        .build()
        );

        return builder;
    }
}
```

## Getting Started

Please read the [Wiki](https://github.com/shaokeyibb/GameSenseLib/wiki) for more information.

## License

GameSenseLib is licensed under the [Apache License 2.0](LICENSE).