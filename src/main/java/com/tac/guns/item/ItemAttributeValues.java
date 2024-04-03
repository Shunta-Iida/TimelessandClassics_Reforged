package com.tac.guns.item;

public interface ItemAttributeValues {
    enum AmmoInspectType {
        NONE, UNKNOWN, VISUAL, CORRECT;

        public static AmmoInspectType fromInt(final int value) {
            for (final AmmoInspectType type : AmmoInspectType.values()) {
                if (type.ordinal() == value) {
                    return type;
                }
            }
            return NONE;
        }

        public int toInt() {
            return this.ordinal();
        }
    }

    enum FireMode {
        SAFETY("Safety"),

        SINGLE("Single fire"),

        AUTO("Full Auto"),

        THREE_ROUND_BURST("Burst"),

        SPECIAL_1("Special 1"),

        SPECIAL_2("Special 2");

        private final String label;

        FireMode(final String label) {
            this.label = label;
        }

        public static FireMode fromInt(final int value) {
            for (final FireMode mode : FireMode.values()) {
                if (mode.ordinal() == value) {
                    return mode;
                }
            }
            return SAFETY;
        }

        public int toInt() {
            return this.ordinal();
        }

        public String getLabel() {
            return this.label;
        }
    }

    enum VisualAmmoInspectValues {

        EMPTY(0f, "empty", "empty"),

        LESS_THAN_HALF(0.3f, "less than half", "∼1/4"),

        ABOUT_HALF(0.5f, "about half", "∼1/2"),

        MORE_THAN_HALF(0.8f, "more than half", ">1/2"),

        NEALY_FULL(1.0f, "nealy full", "∼full"),

        FULL(1.0f, "full", "full");


        private final float value;
        private final String label;
        private final String shortLabel;

        VisualAmmoInspectValues(final float value, final String label, final String shortLabel) {
            this.value = value;
            this.label = label;
            this.shortLabel = shortLabel;
        }

        public String getLabel() {
            return this.label;
        }

        public String getShortLabel() {
            return this.shortLabel;
        }

        public static String getLabelFromPercentage(final float percentage) {
            if (percentage == FULL.value) {
                return FULL.label;
            }

            for (final VisualAmmoInspectValues value : VisualAmmoInspectValues.values()) {
                if (percentage <= value.value) {
                    return value.label;
                }
            }
            return FULL.label;
        }

        public static String getShortLabelFromPercentage(final float percentage) {
            if (percentage == FULL.value) {
                return FULL.shortLabel;
            }

            for (final VisualAmmoInspectValues value : VisualAmmoInspectValues.values()) {
                if (percentage <= value.value) {
                    return value.shortLabel;
                }
            }
            return FULL.shortLabel;
        }

        public static VisualAmmoInspectValues getFromPercentage(final float percentage) {
            if (percentage == FULL.value) {
                return FULL;
            }

            for (final VisualAmmoInspectValues value : VisualAmmoInspectValues.values()) {
                if (percentage <= value.value) {
                    return value;
                }
            }
            return FULL;
        }

    }
}
