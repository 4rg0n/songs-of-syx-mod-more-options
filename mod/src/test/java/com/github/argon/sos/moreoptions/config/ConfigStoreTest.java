package com.github.argon.sos.moreoptions.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.config.ConfigVersionHandlers;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.file.ResourceService;
import com.github.argon.sos.mod.sdk.game.api.GameSaveApi;
import com.github.argon.sos.mod.sdk.json.JacksonService;
import com.github.argon.sos.mod.sdk.json.JsonGameService;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.testing.TestResourceService;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.JsonConfigStoreFactory;
import com.github.argon.sos.moreoptions.testing.ModExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModExtension.class)
class ConfigStoreTest {

    private ConfigStore configStore;
    private AtomicReference<List<String>> writtenJsonContents = new AtomicReference<>();
    private Path configPath = Paths.get("configs/MoreOptionsConfigV5.txt");
    private ConfigDefaults configDefaultsMock;
    private ResourceService resourceServiceMock;
    @TestResourceService
    private ResourceService testResourceService;

    @BeforeEach
    void setUp() {
        Loggers.setLevels(Level.DEBUG);
        writtenJsonContents.set(new ArrayList<>());
        resourceServiceMock = new ResourceService() {
            @Override
            public void write(Path path, String content) throws IOException {
                writtenJsonContents.get().add(content);
            }

            @Override
            public boolean delete(Path path) throws IOException {
                return true;
            }
        };

        JacksonService jacksonService = ModSdkModule.Factory.newJacksonService(
            new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true),
            resourceServiceMock);
        JsonGameService jsonGameService = ModSdkModule.Factory.newJsonGameService(JsonWriters.jsonEPretty(), resourceServiceMock);

        GameSaveApi gameSaveApiMock = Mockito.mock(GameSaveApi.class);
        Mockito.when(gameSaveApiMock.getSaveStamp()).thenReturn("test-save-stamp");
        JsonConfigStoreFactory jsonConfigStoreFactory = ModModule.Factory.newConfigFactory(
            gameSaveApiMock,
            jacksonService,
            jsonGameService,
            resourceServiceMock,
            JsonConfigStoreFactory.PathsConfig.builder()
                .configFile(configPath)
                .raceConfigFolder(Paths.get("configs/races"))
                .boosterConfigFolder(Paths.get("configs/boosters"))
                .build()
        );
        JsonConfigStore jsonConfigStore = jsonConfigStoreFactory.newJsonConfigStoreV5();
        ConfigVersionHandlers<MoreOptionsV5Config> configVersionHandlers = ModModule.Factory.newConfigVersionHandlers(jsonConfigStore, jsonConfigStoreFactory);
        ConfigService configService = ModModule.Factory.newConfigService(jsonConfigStore, configVersionHandlers, ModSdkModule.jsonGameService());

        configDefaultsMock = Mockito.mock(ConfigDefaults.class);
        configStore = ModModule.Factory.newConfigStore(configService, configDefaultsMock, ModSdkModule.stateManager());
    }

    @Test
    void save() throws Exception {
        configStore.save(TestData.newConfig());
        String expectedConfigJson = testResourceService.read(configPath);
        String expectedBoostersConfigJson = testResourceService.read(Paths.get("configs/boosters/test-save-stamp.BoostersConfig.txt"));
        String expectedRacesConfigJson = testResourceService.read(Paths.get("configs/races/test-save-stamp.RacesConfig.txt"));

        assertThat(expectedConfigJson).isNotNull();
        assertThat(expectedBoostersConfigJson).isNotNull();
        assertThat(expectedRacesConfigJson).isNotNull();
        assertThat(writtenJsonContents.get()).hasSize(3);
        assertThat(writtenJsonContents.get().get(0)).isEqualToIgnoringNewLines(expectedConfigJson);
        assertThat(writtenJsonContents.get().get(1)).isEqualToIgnoringNewLines(expectedRacesConfigJson);
        assertThat(writtenJsonContents.get().get(2)).isEqualToIgnoringNewLines(expectedBoostersConfigJson);
    }

    @Test
    void init() {
        Mockito.when(configDefaultsMock.newConfig()).thenReturn(TestData.newEmptyConfig());
        configStore.init();
        assertThat(configStore.getCurrentConfig()).isEqualTo(TestData.newMergedEmptyConfig());
    }

    @Test
    void init_withMergeDefaults() {
        Mockito.when(configDefaultsMock.newConfig()).thenReturn(TestData.newDefaultConfig());
        configStore.init();
        assertThat(configStore.getCurrentConfig()).isEqualTo(TestData.newMergedDefaultConfig());
    }
}