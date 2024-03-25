package com.tac.guns.item.attachment;

import com.tac.guns.item.attachment.impl.Attachment;

import javax.annotation.Nullable;

/**
 * The base attachment interface
 * <p>
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public interface IAttachment<T extends Attachment> {
    Type getType();

    T getProperties();

    int easyColorStart = 11;
    int easyColorEnd = 13;

    int standardAttStart = 0;
    int standardAttEnd = 6;

    /*
     * Instructions for editing type enum
     * 
     * Each value is given it's own ID to search using
     */
    enum Type {
        SCOPE("slot.tac.attachment.scope", "Scope", 0),
        BARREL("slot.tac.attachment.barrel", "Barrel", 1),
        STOCK("slot.tac.attachment.stock", "Stock", 2),
        UNDER_BARREL("slot.tac.attachment.under_barrel", "Under_Barrel", 3),
        SIDE_RAIL("slot.tac.attachment.side_rail", "Side_Rail", 4),
        EXTENDED_MAG("slot.tac.attachment.extended_mag", "Extended_Mag", 5),
        GUN_SKIN("slot.tac.attachment.skin", "Gun_Skin", 6),
        // additional types
        IR_DEVICE("slot.tac.attachment.ir_device", "Ir_Device", 7),
        OLD_SCOPE("slot.tac.attachment.old_scope", "OldScope", 8),
        PISTOL_SCOPE("slot.tac.attachment.pistol_scope", "PistolScope", 9),
        PISTOL_BARREL("slot.tac.attachment.pistol_barrel", "PistolBarrel", 10),

        // LAST 3 USED FOR IEasyColor
        SCOPE_RETICLE_COLOR("slot.tac.attahment.scope.reticle_color", "Reticle_Color", 11),
        SCOPE_BODY_COLOR("slot.tac.attachment.scope.body_color", "Body_Color", 12),
        SCOPE_GLASS_COLOR("slot.tac.attachment.scope.glass_color", "Glass_Color", 13);

        private String translationKey;
        private String tagKey;

        private int id;

        Type(String translationKey, String tagKey, int id) {
            this.translationKey = translationKey;
            this.tagKey = tagKey;
            this.id = id;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }

        public String getTagKey() {
            return this.tagKey;
        }

        public int getId() {
            return this.id;
        }

        @Nullable
        public static Type byTagKey(String s) {
            for (Type type : values())
                if (type.tagKey.equalsIgnoreCase(s))
                    return type;
            return null;
        }

        @Nullable
        public static Type valueOf(int search) {
            for (Type type : values())
                if (type.id == search)
                    return type;
            return null;
        }
    }
}
