package com.github.argon.sos.mod.sdk.util;

import org.junit.jupiter.api.Test;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LIST;

import static org.assertj.core.api.Assertions.assertThat;

class ListsTest {

    @Test
    void fromGameLIST() {
        ArrayList<String> gameList = new ArrayList<>(2);
        gameList.add("TEST1");
        gameList.add("TEST2");

        assertThat(Lists.fromGameLIST(gameList)).isEqualTo(Lists.of("TEST1", "TEST2"));
    }

    @Test
    void toGameLIST() {
        LIST<String> gameList = Lists.toGameLIST(Lists.of("TEST1", "TEST2"));

        assertThat(gameList).hasSize(2);
        assertThat(gameList.get(0)).isEqualTo("TEST1");
        assertThat(gameList.get(1)).isEqualTo("TEST2");
    }

    @Test
    void compare() {
        assertThat(Lists.compare(Lists.of("TEST1", "TEST2"), Lists.of("TEST1", "TEST2"))).isTrue();
        assertThat(Lists.compare(Lists.of("TEST1", "TEST2"), Lists.of("TEST1", "DIFFERENT"))).isFalse();

        assertThat(Lists.compare(Lists.of("TEST1", "TEST2"), Sets.of("TEST1", "TEST2"))).isTrue();
        assertThat(Lists.compare(Lists.of("TEST1", "TEST2"), Sets.of("TEST1", "DIFFERENT"))).isFalse();
    }

    @Test
    void copy() {
        assertThat(Lists.slice(Lists.of("TEST1", "TEST2"), 0, 0)).isEqualTo(Lists.of("TEST1"));
        assertThat(Lists.slice(Lists.of("TEST1", "TEST2"), 0, 3)).isEqualTo(Lists.of(Lists.of("TEST1", "TEST2")));
        assertThat(Lists.slice(Lists.of("TEST1", "TEST2"), 3, 1)).isEqualTo(Lists.of());
    }
}