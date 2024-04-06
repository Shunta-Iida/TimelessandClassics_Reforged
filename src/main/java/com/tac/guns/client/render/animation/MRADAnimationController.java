package com.tac.guns.client.render.animation;

import java.io.IOException;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.BoltActionAnimationController;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModItems;

import net.minecraft.resources.ResourceLocation;

public class MRADAnimationController extends BoltActionAnimationController {
    public static int INDEX_BODY = 6;
    public static int INDEX_LEFT_HAND = 8;
    public static int INDEX_RIGHT_HAND = 0;
    public static int INDEX_MAGAZINE = 3;
    public static int INDEX_BOLT = 4;
    public static int INDEX_HANDLE = 5;
    public static int INDEX_BULLET = 2;

    public static final AnimationMeta STATIC = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/mrad_static.gltf"));
    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/mrad_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/mrad_reload_empty.gltf"));
    public static final AnimationMeta DRAW =
            new AnimationMeta(new ResourceLocation(Reference.MOD_ID, "animations/mrad_draw.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/mrad_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(
            new ResourceLocation(Reference.MOD_ID, "animations/mrad_inspect_empty.gltf"));
    public static final AnimationMeta BOLT =
            new AnimationMeta(new ResourceLocation(Reference.MOD_ID, "animations/mrad_bolt.gltf"));
    private static final MRADAnimationController instance = new MRADAnimationController();

    private MRADAnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(RELOAD_EMPTY);
            Animations.load(DRAW);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
            Animations.load(STATIC);
            Animations.load(BOLT);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.MRAD.getId(), this);
    }

    public static MRADAnimationController getInstance() {
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
            case PULL_BOLT:
                return BOLT;
            default:
                return null;
        }
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label) {
        return super.getSoundFromLabel(ModItems.MRAD.get(), label);
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
