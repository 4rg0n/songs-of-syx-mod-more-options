package com.github.argon.sos.mod.sdk.data.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.game.api.GameSaveApi;
import com.github.argon.sos.mod.sdk.json.JacksonService;
import com.github.argon.sos.mod.sdk.json.store.JsonStore;
import com.github.argon.sos.mod.sdk.json.store.JsonStoreManager;
import com.github.argon.sos.mod.sdk.json.store.filepath.SaveFilePathGenerator;
import com.github.argon.sos.mod.testing.GameExtension;
import com.github.argon.sos.mod.testing.ModSdkExtension;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({ModSdkExtension.class, GameExtension.class, MockitoExtension.class})
class GameSaverTest {

    private IOService ioServiceMock;
    private GameSaver gameSaver;

    @Captor
    ArgumentCaptor<Path> pathCaptor;
    @Captor
    ArgumentCaptor<String> contentCaptor;

    @BeforeEach
    void setUp() {
        ioServiceMock = Mockito.mock(IOService.class);
        JacksonService jacksonService = ModSdkModule.Factory.newJacksonService(
            new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true),
            ioServiceMock);

        GameSaveApi gameSaveApiMock = Mockito.mock(GameSaveApi.class);
        Mockito.when(gameSaveApiMock.getSaveStamp()).thenReturn("test-save-stamp");

        SaveFilePathGenerator saveFilePathGenerator = new SaveFilePathGenerator(gameSaveApiMock);
        JsonStore jsonStore = new JsonStore(jacksonService);

        JsonStoreManager jsonStoreManager = new JsonStoreManager(saveFilePathGenerator, jsonStore);
        gameSaver = new GameSaver(Paths.get("test"), jsonStoreManager);
    }

    @Test
    void save_withAnnotation() throws Exception {
        gameSaver.save(new TestSaveDataWithAnnotation());
        Mockito.verify(ioServiceMock).write(pathCaptor.capture(), contentCaptor.capture());

        assertThat(pathCaptor.getValue()).isEqualTo(Paths.get("test/test-save-stamp.Test.txt"));
        assertThat(contentCaptor.getValue()).isEqualToIgnoringWhitespace("{\"test\": \"test\"}");
    }

    @Test
    void save() throws Exception {
        gameSaver.save(new TestSaveData());
        Mockito.verify(ioServiceMock).write(pathCaptor.capture(), contentCaptor.capture());

        assertThat(pathCaptor.getValue()).isEqualTo(Paths.get("test/test-save-stamp.GameSaverTest.TestSaveData.txt"));
        assertThat(contentCaptor.getValue()).isEqualToIgnoringWhitespace("{\"test\": \"test\"}");
    }

    @Test
    void load() throws Exception {
        Mockito.when(ioServiceMock.read(any(Path.class))).thenReturn("{\"test\": \"test\"}");

        Optional<TestSaveData> testSaveData = gameSaver.load(TestSaveData.class);
        Mockito.verify(ioServiceMock).read(pathCaptor.capture());

        assertThat(pathCaptor.getValue()).isEqualTo(Paths.get("test/test-save-stamp.GameSaverTest.TestSaveData.txt"));
        assertThat(testSaveData).hasValue(new TestSaveData());
    }

    @Data
    @SaveData("Test")
    private static class TestSaveDataWithAnnotation {
        private String test = "test";
    }

    @Data
    private static class TestSaveData {
        private String test = "test";
    }
}