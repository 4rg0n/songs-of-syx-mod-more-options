package com.github.argon.sos.mod.sdk.util;

import com.github.argon.sos.mod.testing.ModSdkExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LIST;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class ListsTest {

    @Test
    void fromGameLIST() {
        ArrayList<String> gameList = new ArrayList<>(2);
        gameList.add("TEST1");
        gameList.add("TEST2");

        assertThat(Lists.fromGameLIST(gameList)).isEqualTo(List.of("TEST1", "TEST2"));
    }

    @Test
    void toGameLIST() {
        LIST<String> gameList = Lists.toGameLIST(List.of("TEST1", "TEST2"));

        assertThat(gameList).hasSize(2);
        assertThat(gameList.get(0)).isEqualTo("TEST1");
        assertThat(gameList.get(1)).isEqualTo("TEST2");
    }

    @Test
    void compare() {
        assertThat(Lists.compare(List.of("TEST1", "TEST2"), List.of("TEST1", "TEST2"))).isTrue();
        assertThat(Lists.compare(List.of("TEST1", "TEST2"), List.of("TEST1", "DIFFERENT"))).isFalse();

        assertThat(Lists.compare(List.of("TEST1", "TEST2"), Sets.of("TEST1", "TEST2"))).isTrue();
        assertThat(Lists.compare(List.of("TEST1", "TEST2"), Sets.of("TEST1", "DIFFERENT"))).isFalse();
    }

    @Test
    void copy() {
        assertThat(Lists.slice(List.of("TEST1", "TEST2"), 0, 0)).isEqualTo(List.of("TEST1"));
        assertThat(Lists.slice(List.of("TEST1", "TEST2"), 0, 3)).isEqualTo(Lists.ofList(List.of("TEST1", "TEST2")));
        assertThat(Lists.slice(List.of("TEST1", "TEST2"), 3, 1)).isEqualTo(List.of());
    }
}