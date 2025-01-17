package com.tac.guns.client.render.animation;

import java.io.IOException;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SCAR_MK20AnimationController extends GunAnimationController {
    public static int INDEX_BODY = 3;
    public static int INDEX_LEFT_HAND = 7;
    public static int INDEX_RIGHT_HAND = 4;
    public static int INDEX_MAGAZINE = 1;
    public static int INDEX_MAGAZINE2 = 2;
    public static int INDEX_BOLT = 0;
    // public static int INDEX_CHARGE_HANDLE = 1;

    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_reload_empty.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_draw.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_inspect.gltf"));
    public static final AnimationMeta STATIC = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/scar_h_static.gltf"));
    private static final SCAR_MK20AnimationController instance = new SCAR_MK20AnimationController();

    private SCAR_MK20AnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(RELOAD_EMPTY);
            Animations.load(DRAW);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
            Animations.load(STATIC);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.SCAR_MK20.getId(), this);
    }

    public static SCAR_MK20AnimationController getInstance() {
        return instance;
    }

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label) {
            case RELOAD_EMPTY:
                return RELOAD_EMPTY;
            case RELOAD_NORMAL:
                return RELOAD_NORM;
            case DRAW:
                return DRAW;
            case INSPECT:
                return INSPECT;
            case INSPECT_EMPTY:
                return INSPECT_EMPTY;
            case STATIC:
                return STATIC;
            default:
                return null;
        }
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label) {
        return super.getSoundFromLabel(ModItems.SCAR_MK20.get(), label);
    }

    @Override
    protected int getAttachmentsNodeIndex() {
        return INDEX_BODY;
    }

    @Override
    protected int getRightHandNodeIndex() {
        return INDEX_RIGHT_HAND;
    }

    @Override
    protected int getLeftHandNodeIndex() {
        return INDEX_LEFT_HAND;
    }
}
