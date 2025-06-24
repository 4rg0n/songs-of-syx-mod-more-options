package com.github.argon.sos.mod.sdk.phase;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(ModSdkExtension.class)
class PhaseManagerTest {

    private final PhaseManager phaseManager = new PhaseManager();

    @Test
    void test() {
        Phases phasesImpl = Mockito.mock(Phases.class);

        for (Phase phase : Phase.values()) {
            phaseManager.register(phase, phasesImpl);
        }

        phaseManager.initBeforeGameCreated();
        phaseManager.initModCreateInstance();
        phaseManager.onGameLoaded(Paths.get(""));
        phaseManager.onGameSaveReloaded();
        phaseManager.initNewGameSession();
        phaseManager.initGameUpdating();
        phaseManager.onGameUpdate(1D);
        phaseManager.initGameUiPresent();
        phaseManager.onGameSaved(Paths.get(""));
        phaseManager.onCrash(new RuntimeException());

        Mockito.verify(phasesImpl, Mockito.times(1)).initBeforeGameCreated();
        Mockito.verify(phasesImpl, Mockito.times(1)).initModCreateInstance();
        Mockito.verify(phasesImpl, Mockito.times(1)).onGameLoaded(ArgumentMatchers.any(Path.class));
        Mockito.verify(phasesImpl, Mockito.times(1)).onGameSaveReloaded();
        Mockito.verify(phasesImpl, Mockito.times(1)).initNewGameSession();
        Mockito.verify(phasesImpl, Mockito.times(1)).initGameUpdating();
        Mockito.verify(phasesImpl, Mockito.times(1)).onGameUpdate(1D);
        Mockito.verify(phasesImpl, Mockito.times(1)).initGameUiPresent();
        Mockito.verify(phasesImpl, Mockito.times(1)).onGameSaved(ArgumentMatchers.any(Path.class));
        Mockito.verify(phasesImpl, Mockito.times(1)).onCrash(ArgumentMatchers.any(Throwable.class));
    }
}