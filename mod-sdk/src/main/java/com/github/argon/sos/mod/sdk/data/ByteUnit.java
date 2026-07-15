package com.github.argon.sos.mod.sdk.data;

import com.github.argon.sos.mod.sdk.util.ByteUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
public record ByteUnit(double value, Unit unit) {

    /**
     * Converts the current {@link ByteUnit} to a new one with the given {@link Unit}
     *
     * @param unit to convert to
     * @return new converted byte unit
     */
    public ByteUnit to(Unit unit) {
        double bytes = value * this.unit.getBytes();

        return new ByteUnit(ByteUtil.fromBytes(bytes, unit), unit);
    }

    /**
     * Unit for different byte values.
     */
    @RequiredArgsConstructor
    public enum Unit {
        BYTE(1L, 'B', "B"),
        KILOBYTE(1L << 10, 'K', "KB"),
        MEGABYTE(1L << 20, 'M', "MB"),
        GIGABYTE(1L << 30, 'G', "GB"),
        TERABYTE(1L << 40, 'T', "TB"),
        PETABYTE(1L << 50, 'P', "PB");

        @Getter
        private final long bytes;

        @Getter
        private final Character singleUnit;

        @Getter
        private final String unit;

        @Override
        public String toString() {
            return bytes + unit;
        }

        public String toStringSingleUnit() {
            return "" + bytes + singleUnit;
        }

        /**
         * Creates a new {@link Unit} based on the given unit string.
         *
         * @param unitString to create the {@link Unit} from
         * @return crated unit
         * @throws IllegalArgumentException when the given unit string is not a supported unit
         */
        public static Unit of(String unitString) {
            return switch (unitString.toLowerCase()) {
                case "b" -> Unit.BYTE;
                case "k", "kb" -> Unit.KILOBYTE;
                case "m", "mb" -> Unit.MEGABYTE;
                case "g", "gb" -> Unit.GIGABYTE;
                case "t", "tb" -> Unit.TERABYTE;
                case "p", "pb" -> Unit.PETABYTE;
                default -> throw new IllegalArgumentException(unitString + " not recognized as byte unit.");
            };
        }
    }
}
