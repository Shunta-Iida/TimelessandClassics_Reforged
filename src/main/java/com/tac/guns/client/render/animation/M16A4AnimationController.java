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

public class M16A4AnimationController extends GunAnimationController {
    public static int INDEX_BODY = 6;
    public static int INDEX_LEFT_HAND = 8;
    public static int INDEX_RIGHT_HAND = 0;
    public static int INDEX_MAGAZINE = 5;
    public static int INDEX_HANDLE = 2;

    public static final AnimationMeta STATIC = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/m16a4_static.gltf"));
    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/m16a4_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/m16a4_reload_empty.gltf"));
    public static final AnimationMeta DRAW =
            new AnimationMeta(new ResourceLocation(Reference.MOD_ID, "animations/m16a4_draw.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/m16a4_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/m16a4_inspect.gltf"));
    private static final M16A4AnimationController instance = new M16A4AnimationController();

    private M16A4AnimationController() {
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
        GunAnimationController.setAnimationControllerMap(ModItems.M16A4.getId(), this);
    }

    public static M16A4AnimationController getInstance() {
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
        return super.getSoundFromLabel(ModItems.M16A4.get(), label);
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
