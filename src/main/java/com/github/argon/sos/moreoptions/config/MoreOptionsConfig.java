package com.github.argon.sos.moreoptions.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoreOptionsConfig {

    private Events events;

    private AmbienceSounds ambienceSounds;

    private Particles particles;

    @Data
    @Builder
    public static class Particles {
        @Builder.Default
        private boolean snowRain = true;
    }

    @Data
    @Builder
    public static class AmbienceSounds {
        private int wind;
        private int windTrees;
        private int windHowl;
        private int nature;
        private int night;
        private int water;
        private int rain;
        private int thunder;
    }

    @Data
    @Builder
    public static class Events {
        @Builder.Default
        private boolean disease = true;
        @Builder.Default
        private boolean slaver = true;
        @Builder.Default
        private boolean riot = true;
        @Builder.Default
        private boolean uprising = true;
        @Builder.Default
        private boolean killer = true;
        @Builder.Default
        private boolean temperature = true;
        @Builder.Default
        private boolean farm = true;
        @Builder.Default
        private boolean pasture = true;
        @Builder.Default
        private boolean orchard = true;
        @Builder.Default
        private boolean fish = true;
        @Builder.Default
        private boolean raceWars = true;
        @Builder.Default
        private boolean advice = true;
        @Builder.Default
        private boolean accident = true;
        @Builder.Default
        private boolean worldFactionExpand = true;
        @Builder.Default
        private boolean worldFactionBreak = true;
        @Builder.Default
        private boolean worldPopup = true;
        @Builder.Default
        private boolean worldWar = true;
        @Builder.Default
        private boolean worldWarPlayer = true;
        @Builder.Default
        private boolean worldWarPeace = true;
        @Builder.Default
        private boolean worldRaider = true;
        @Builder.Default
        private boolean worldRebellion = true;
        @Builder.Default
        private boolean worldPlague = true;

    }
}
