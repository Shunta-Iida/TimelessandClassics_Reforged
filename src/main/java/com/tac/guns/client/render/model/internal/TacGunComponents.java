package com.tac.guns.client.render.model.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.tac.guns.Reference;
import com.tac.guns.client.render.model.GunComponent;

public class TacGunComponents {
    // crossbow
    public static final GunComponent BEND_L = new GunComponent(Reference.MOD_ID, "bend_l");
    public static final GunComponent BEND_R = new GunComponent(Reference.MOD_ID, "bend_r");
    public static final GunComponent BONE_L = new GunComponent(Reference.MOD_ID, "bone_l");
    public static final GunComponent BONE_R = new GunComponent(Reference.MOD_ID, "bone_r");
    public static final GunComponent STRING_L_MAIN =
            new GunComponent(Reference.MOD_ID, "string_l_main");
    public static final GunComponent STRING_R_MAIN =
            new GunComponent(Reference.MOD_ID, "string_r_main");
    public static final GunComponent STRING_L_MOVE =
            new GunComponent(Reference.MOD_ID, "string_l_move");
    public static final GunComponent STRING_R_MOVE =
            new GunComponent(Reference.MOD_ID, "string_r_move");
    public static final GunComponent WHEEL_L = new GunComponent(Reference.MOD_ID, "wheel_l");
    public static final GunComponent WHEEL_R = new GunComponent(Reference.MOD_ID, "wheel_r");
    // barrel
    public static final GunComponent BARREL = new GunComponent(Reference.MOD_ID, "barrel"); // out length barrel for adjust the
    // position
    public static final GunComponent BARREL_EXTENDED =
            new GunComponent(Reference.MOD_ID, "barrel_extended");
    public static final GunComponent BARREL_STANDARD =
            new GunComponent(Reference.MOD_ID, "barrel_standard");
    public static final GunComponent CLUMSYYY = new GunComponent(Reference.MOD_ID, "clumsyyy");
    public static final GunComponent NEKOOO = new GunComponent(Reference.MOD_ID, "nekooo");
    public static final GunComponent LOADER = new GunComponent(Reference.MOD_ID, "loader");
    public static final GunComponent ROTATE = new GunComponent(Reference.MOD_ID, "rotate");
    public static final GunComponent SCOPE_DEFAULT =
            new GunComponent(Reference.MOD_ID, "scope_default");
    public static final GunComponent LIGHT = new GunComponent(Reference.MOD_ID, "light");
    public static final GunComponent SAFETY = new GunComponent(Reference.MOD_ID, "safety");
    public static final GunComponent BIPOD = new GunComponent(Reference.MOD_ID, "bipod"); // the tactical bipod
    public static final GunComponent BULLET_CHAIN_COVER =
            new GunComponent(Reference.MOD_ID, "bullet_chain_cover"); // the clip on
                                                                                                                            // bullet chain
                                                                                                                            // (like m249)
    public static final GunComponent HAMMER = new GunComponent(Reference.MOD_ID, "hammer"); // the hammer to fire
    public static final GunComponent HANDLE_EXTRA =
            new GunComponent(Reference.MOD_ID, "handle_extra"); // extra handle
    public static final GunComponent RELEASE = new GunComponent(Reference.MOD_ID, "release"); // release mag clip
    public static final GunComponent ROCKET = new GunComponent(Reference.MOD_ID, "rocket"); // rpg7 rocket
    public static final GunComponent STOCK_FOLDED =
            new GunComponent(Reference.MOD_ID, "stock_folded"); // default folded stock;
    // render if there is no
    // stock attachment
    public static final GunComponent SLIDE_EXTENDED =
            new GunComponent(Reference.MOD_ID, "slide_extended"); // long pistol slide
    public static final GunComponent SLIDE_EXTENDED_LIGHT =
            new GunComponent(Reference.MOD_ID, "slide_extended_light"); // the
                                                                                                                                // light
                                                                                                                                // part
                                                                                                                                // move
                                                                                                                                // with
                                                                                                                                // slide
    public static final GunComponent PULL = new GunComponent(Reference.MOD_ID, "pull"); // something in barrel connect to bolt
    // handle

    // register all these component
    static {
        final Field[] fields = TacGunComponents.class.getDeclaredFields();
        for (final Field field : fields) {
            if (GunComponent.class.isAssignableFrom(field.getType())) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        final GunComponent component = (GunComponent) field.get(null);
                        component.registerThis();
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
