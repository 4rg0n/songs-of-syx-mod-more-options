package com.github.argon.sos.mod.sdk.ui.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class UiControllersTest {
    @Test
    void test() {
        UiControllers uiControllers = new UiControllers(List.of(new TestUiController1(), new TestUiController2()));

        Assertions.assertThat(uiControllers.get(TestUiController1.class)).containsInstanceOf(TestUiController1.class);
        Assertions.assertThat(uiControllers.get(TestUiController2.class)).containsInstanceOf(TestUiController2.class);
    }

    private static class TestUiController1 extends AbstractUiController<String> {
        public TestUiController1() {
            super("test1");
        }
    }

    private static class TestUiController2 extends AbstractUiController<Integer> {
        public TestUiController2() {
            super(2);
        }
    }
}