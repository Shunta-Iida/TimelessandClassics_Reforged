package com.tac.guns.item;

public interface ItemAttributes {
    public interface Gun {
        public static final String ADDITIONAL_DAMAGE = "AdditionalDamage";
        public static final String IGNORE_AMMO = "IgnoreAmmo";
        public static final String AMMO_COUNT = "AmmoCount";
        public static final String AMMO_INSPECT_TYPE = "AmmoInspectType";

        public static final String LEVEL_DMG = "levelDmg";
        public static final String LEVEL = "level";

        public static final String GUN = "Gun";
        public static final String CUSTOM = "Custom";
        public static final String ATTACHMENTS = "Attachments";

        public static final String CURRENT_FIRE_MODE = "CurrentFireMode";
    }

    public interface Attachment {
        public static final String SCOPE = "Scope";
        public static final String Barrel = "Barrel";
        public static final String Stock = "Stock";
        public static final String Grip = "Grip";
        public static final String Magazine = "Magazine";
        public static final String UnderBarrel = "UnderBarrel";
        public static final String SideRail = "SideRail";

        public interface Magazine {
            public static final String AMMO_COUNT = "AmmoCount";
            public static final String INSPECT_TYPE = "InspectType";
        }
    }
}

