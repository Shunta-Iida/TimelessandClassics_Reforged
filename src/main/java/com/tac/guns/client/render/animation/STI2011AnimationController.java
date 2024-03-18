package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.*;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class STI2011AnimationController extends PistalAnimationController {
    public static int INDEX_BODY = 9;
    public static int INDEX_SLIDE = 7;
    public static int INDEX_MAG = 5;
    public static int INDEX_EXTRA_MAG = 2;
    public static int INDEX_LEFT_HAND = 13;
    public static int INDEX_RIGHT_HAND = 10;
    public static int INDEX_BULLET1 = 3;
    public static int INDEX_BULLET2 = 0;
    public static int INDEX_HAMMER = 6;

    public static final AnimationMeta RELOAD_NORM = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_reload_norm.gltf"));
    public static final AnimationMeta DRAW = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_draw.gltf"));
    public static final AnimationMeta RELOAD_EMPTY = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_reload_empty.gltf"));
    public static final AnimationMeta STATIC = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_static.gltf"));
    public static final AnimationMeta INSPECT = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY = new AnimationMeta(
            new ResourceLocation("tac", "animations/sti2011_inspect_empty.gltf"));
    private static final STI2011AnimationController instance = new STI2011AnimationController();

    @Override
    public AnimationMeta getAnimationFromLabel(AnimationLabel label) {
        switch (label) {
            case RELOAD_NORMAL:
                return RELOAD_NORM;
            case RELOAD_EMPTY:
                return RELOAD_EMPTY;
            case DRAW:
                return DRAW;
            case STATIC:
                return STATIC;
            case INSPECT:
                return INSPECT;
            case INSPECT_EMPTY:
                return INSPECT_EMPTY;
            default:
                return null;
        }
    }

    private STI2011AnimationController() {
        try {
            Animations.load(RELOAD_NORM);
            Animations.load(DRAW);
            Animations.load(RELOAD_EMPTY);
            Animations.load(STATIC);
            Animations.load(INSPECT);
            Animations.load(INSPECT_EMPTY);
        } catch (IOException e) {
            GunMod.LOGGER.fatal(e.getStackTrace());
        }
        enableStaticState();
        GunAnimationController.setAnimationControllerMap(ModItems.STI2011.getId(), this);
    }

    @Override
    public AnimationSoundMeta getSoundFromLabel(AnimationLabel label) {
        return super.getSoundFromLabel(ModItems.STI2011.get(), label);
    }

    public static STI2011AnimationController getInstance() {
        return instance;
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

    @Override
    public int getSlideNodeIndex() {
        return INDEX_SLIDE;
    }

    @Override
    public int getMagazineNodeIndex() {
        return INDEX_MAG;
    }
}
