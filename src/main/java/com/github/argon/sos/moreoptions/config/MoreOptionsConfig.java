package com.github.argon.sos.moreoptions.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoreOptionsConfig {

    private Events events;

    private AmbienceSounds ambienceSounds;

    private Weather weather;

    @Data
    @Builder
    public static class Weather {
        @Builder.Default
        private int rain = 100;
        @Builder.Default
        private int snow = 100;
        @Builder.Default
        private int ice = 100;
        @Builder.Default
        private int clouds = 100;
        @Builder.Default
        private int thunder = 100;
    }

    @Data
    @Builder
    public static class AmbienceSounds {
        @Builder.Default
        private int wind = 100;
        @Builder.Default
        private int windTrees = 100;
        @Builder.Default
        private int windHowl = 100;
        @Builder.Default
        private int nature = 100;
        @Builder.Default
        private int night = 100;
        @Builder.Default
        private int water = 100;
        @Builder.Default
        private int rain = 100;
        @Builder.Default
        private int thunder = 100;
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
