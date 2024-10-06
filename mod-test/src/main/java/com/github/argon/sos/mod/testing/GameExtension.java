package com.github.argon.sos.mod.testing;

import init.error.ErrorHandler;
import init.paths.PATHS;
import init.settings.S;
import init.text.D;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import snake2d.*;
import snake2d.util.sets.LIST;
import util.spritecomposer.Initer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Will provide game classes and resources for testing
 */
public class GameExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        // init core
        CORE.init(new ErrorHandler());

        // init paths
        String[] modNames = extractMods(testInstance.getClass());
        PATHS.init(modNames, "en", true);

        // init dictionary
        D.init();

        // create core
        CORE.create(S.get().make());

        TextureHolder textureHolder = new Initer() {
            public void createAssets() throws IOException {
                // texture songsofsyx\cache\data is empty =/
//                UI.init();
            }
        }.get("game", PATHS.textureSize(), 0);



//        CORE.start(new CORE_STATE.Constructor() {
//            @Override
//            public CORE_STATE getState() {
//                return new StateMock();
//            }
//        });
    }

    private String[] extractMods(Class<?> testClass) {
        return Arrays.stream(
                Optional.ofNullable(testClass.getAnnotation(TestMods.class))
                .map(TestMods::value)
                .orElse(new String[]{}))
            .toArray(String[]::new);
    }

    private static class StateMock extends CORE_STATE {


        @Override
        protected void update(float ds, double slowTheFuckDown) {

        }

        @Override
        protected void keyPush(LIST<KeyBoard.KeyEvent> keys, boolean hasCleared) {

        }

        @Override
        protected void mouseClick(MButt button) {

        }

        @Override
        protected void render(Renderer r, float ds) {

        }
    }
}
