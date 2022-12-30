package io.hikarilan.gamesenselib.artifacts;

import io.hikarilan.gamesenselib.flows.Phase;
import io.hikarilan.gamesenselib.modules.IModule;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value(staticConstructor = "of")
public class PhaseAndModule<T extends IModule> {

    @NotNull
    Phase phase;

    @NotNull
    T module;

}
