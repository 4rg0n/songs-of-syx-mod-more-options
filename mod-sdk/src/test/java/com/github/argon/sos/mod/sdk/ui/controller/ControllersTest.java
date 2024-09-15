package com.github.argon.sos.mod.sdk.ui.controller;

import com.github.argon.sos.mod.sdk.util.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ControllersTest {
    @Test
    void test() {
        Controllers controllers = new Controllers(Lists.of(new TestController1(), new TestController2()));

        Assertions.assertThat(controllers.get(TestController1.class)).containsInstanceOf(TestController1.class);
        Assertions.assertThat(controllers.get(TestController2.class)).containsInstanceOf(TestController2.class);
    }

    private static class TestController1 extends AbstractController<String> {
        public TestController1() {
            super("test1");
        }
    }

    private static class TestController2 extends AbstractController<Integer> {
        public TestController2() {
            super(2);
        }
    }
}