package com.tac.guns.client.render.animation;

import com.tac.guns.GunMod;
import com.tac.guns.client.render.animation.module.AnimationMeta;
import com.tac.guns.client.render.animation.module.AnimationSoundMeta;
import com.tac.guns.client.render.animation.module.Animations;
import com.tac.guns.client.render.animation.module.GunAnimationController;
import com.tac.guns.init.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class M1A1AnimationController extends GunAnimationController {
    public static int INDEX_BODY = 2;
    public static int INDEX_LEFT_HAND = 6;
    public static int INDEX_RIGHT_HAND = 3;
    public static int INDEX_MAGAZINE = 0;
    public static int INDEX_BOLT = 1;

    public static final AnimationMeta STATIC =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_static.gltf"));
    public static final AnimationMeta RELOAD_NORM =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_reload_norm.gltf"));
    public static final AnimationMeta RELOAD_EMPTY =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_reload_empty.gltf"));
    public static final AnimationMeta DRAW =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_draw.gltf"));
    public static final AnimationMeta INSPECT =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_inspect.gltf"));
    public static final AnimationMeta INSPECT_EMPTY =
            new AnimationMeta(new ResourceLocation("tac", "animations/m1a1_smg_inspect.gltf"));
    private static final M1A1AnimationController instance = new M1A1AnimationController();

    private M1A1AnimationController() {
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
        GunAnimationController.setAnimationControllerMap(ModItems.M1A1_SMG.getId(), this);
    }

    public static M1A1AnimationController getInstance() {
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
        return super.getSoundFromLabel(ModItems.M1A1_SMG.get(), label);
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
