package com.tac.guns.client.render.animation.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import de.javagl.jgltf.model.GltfAnimations;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.animation.Animation;
import de.javagl.jgltf.model.animation.AnimationManager;
import de.javagl.jgltf.model.animation.AnimationRunner;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v2.GltfModelV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Animations {
    private static final Stack<NodeModel> nodeModelStack = new Stack<>();
    private static final Stack<NodeModel> initialModelStack = new Stack<>();
    private static final PoseStack extraMatrixStack = new PoseStack();
    private static NodeModel bind;
    private static NodeModel initial;
    private static final Map<String, GltfModelV2> gltfModelV2Map = new HashMap<>();
    private static final Map<String, GltfModelV2> initialModelMap = new HashMap<>();
    private static final Map<String, AnimationRunner> animationRunnerMap = new HashMap<>();
    private static final Map<String, AnimationManager> animationManagerMap = new HashMap<>();

    public static GltfModelV2 load(final AnimationMeta animationMeta) throws IOException {
        if (animationMeta != null)
            return Animations.load(animationMeta.getResourceLocation());
        else
            return null;
    }

    public static GltfModelV2 load(final ResourceLocation resourceLocation) throws IOException {
        final Resource resource =
                Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        final InputStream inputStream = resource.getInputStream();
        final GltfAssetReader reader = new GltfAssetReader();
        final GltfAsset asset = reader.readWithoutReferences(inputStream);
        if (asset instanceof GltfAssetV2) {
            // put GltfModel into the gltfModelMap
            final GltfModelV2 model = new GltfModelV2((GltfAssetV2) asset);
            Animations.gltfModelV2Map.put(resourceLocation.toString(), model);
            // refresh animationManagerMap
            final List<Animation> animations =
                    GltfAnimations.createModelAnimations(model.getAnimationModels());
            final AnimationManager animationManager =
                    new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
            animationManager.addAnimations(animations);
            Animations.animationManagerMap.put(resourceLocation.toString(), animationManager);
            // refresh animationRunnerMap
            Animations.stopAnimation(resourceLocation);
            final AnimationRunner newRunner = new AnimationRunner(animationManager);
            Animations.animationRunnerMap.put(resourceLocation.toString(), newRunner);
            // initialState
            final GltfModelV2 model2 = new GltfModelV2((GltfAssetV2) asset);
            final List<Animation> animations2 =
                    GltfAnimations.createModelAnimations(model2.getAnimationModels());
            final AnimationManager initialStateManager =
                    new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
            initialStateManager.addAnimations(animations2);
            initialStateManager.reset();
            initialStateManager.performStep(1);
            Animations.initialModelMap.put(resourceLocation.toString(), model2);
            return model;
        }
        inputStream.close();
        return null;
    }

    public static void specifyInitialModel(final AnimationMeta animationMeta,
            final AnimationMeta initialMeta) throws IOException {
        final Resource initialResource = Minecraft.getInstance().getResourceManager()
                .getResource(initialMeta.getResourceLocation());
        final InputStream inputStream = initialResource.getInputStream();
        final GltfAssetReader reader = new GltfAssetReader();
        final GltfAsset asset = reader.readWithoutReferences(inputStream);
        final GltfModelV2 model2 = new GltfModelV2((GltfAssetV2) asset);
        final List<Animation> animations2 =
                GltfAnimations.createModelAnimations(model2.getAnimationModels());
        final AnimationManager initialStateManager =
                new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
        initialStateManager.addAnimations(animations2);
        initialStateManager.reset();
        initialStateManager.performStep(1);
        Animations.initialModelMap.put(animationMeta.getResourceLocation().toString(), model2);
    }

    private static GltfModelV2 getGltfModel(final ResourceLocation resourceLocation) {
        return Animations.gltfModelV2Map.get(resourceLocation.toString());
    }

    private static GltfModelV2 getInitialModel(final ResourceLocation resourceLocation) {
        return Animations.initialModelMap.get(resourceLocation.toString());
    }

    public static void pushNode(final AnimationMeta animationMeta, final int index) {
        if (animationMeta != null)
            Animations.pushNode(animationMeta.getResourceLocation(), index);
    }

    public static void pushNode(final ResourceLocation gltfResource, final int index) {
        if (gltfResource == null)
            return;
        final GltfModelV2 gltfModel = Animations.getGltfModel(gltfResource);
        if (gltfModel == null) {
            Animations.bind = null;
        } else
            Animations.bind = gltfModel.getNodeModels().get(index);
        final GltfModelV2 initialModel = Animations.getInitialModel(gltfResource);
        if (initialModel == null) {
            Animations.initial = null;
        } else
            Animations.initial = initialModel.getNodeModels().get(index);
        Animations.nodeModelStack.push(Animations.bind);
        Animations.initialModelStack.push(Animations.initial);
    }

    public static void popNode() {
        if (!Animations.nodeModelStack.empty())
            Animations.nodeModelStack.pop();
        if (!Animations.initialModelStack.empty())
            Animations.initialModelStack.pop();
        Animations.bind =
                (Animations.nodeModelStack.empty() ? null : Animations.nodeModelStack.peek());
        Animations.initial =
                (Animations.initialModelStack.empty() ? null : Animations.initialModelStack.peek());
    }

    public static NodeModel peekNodeModel() {
        return Animations.bind;
    }

    public static NodeModel peekInitialModel() {
        return Animations.initial;
    }

    public static AnimationRunner getAnimationRunner(final ResourceLocation resourceLocation) {
        return Animations.animationRunnerMap.get(resourceLocation.toString());
    }

    public static void runAnimation(final AnimationMeta animationMeta) {
        if (animationMeta != null)
            Animations.runAnimation(animationMeta.getResourceLocation());
    }

    public static void runAnimation(final ResourceLocation resourceLocation) {
        Animations.runAnimation(resourceLocation, null);
    }

    public static void runAnimation(final AnimationMeta animationMeta, final Runnable callback) {
        if (animationMeta != null)
            Animations.runAnimation(animationMeta.getResourceLocation(), callback);
    }

    public static void runAnimation(final AnimationMeta animationMeta, final Runnable callback,
            final float speed) {
        if (animationMeta != null)
            Animations.runAnimation(animationMeta.getResourceLocation(), callback, speed);
    }

    public static void runAnimation(final ResourceLocation resourceLocation,
            final Runnable callback) {
        final AnimationRunner runner = Animations.getAnimationRunner(resourceLocation);
        if (runner != null) {
            if (runner.isRunning())
                return;
            runner.start(callback);
        }
    }

    public static void runAnimation(final ResourceLocation resourceLocation,
            final Runnable callback, final float speed) {
        final AnimationRunner runner = Animations.getAnimationRunner(resourceLocation);
        if (runner != null) {
            if (runner.isRunning())
                return;
            runner.start(callback, speed);
        }
    }

    public static void stopAnimation(final AnimationMeta animationMeta) {
        if (animationMeta != null)
            Animations.stopAnimation(animationMeta.getResourceLocation());
    }

    public static void stopAnimation(final ResourceLocation resourceLocation) {
        final AnimationRunner runner = Animations.getAnimationRunner(resourceLocation);
        if (runner != null)
            runner.stop();
        final AnimationManager manager = Animations.getAnimationManager(resourceLocation);
        if (manager != null) {
            manager.reset();
            manager.performStep(0);
        }
    }

    public static boolean isAnimationRunning(final AnimationMeta animationMeta) {
        if (animationMeta != null)
            return Animations.isAnimationRunning(animationMeta.getResourceLocation());
        else
            return false;
    }

    public static boolean isAnimationRunning(final ResourceLocation resourceLocation) {
        final AnimationRunner runner = Animations.getAnimationRunner(resourceLocation);
        if (runner == null)
            return false;
        return runner.isRunning();
    }

    private static AnimationManager getAnimationManager(final ResourceLocation resourceLocation) {
        return Animations.animationManagerMap.get(resourceLocation.toString());
    }

    public static PoseStack getExtraMatrixStack() {
        return Animations.extraMatrixStack;
    }

    public static void applyExtraTransform(final PoseStack matrixStack) {
        matrixStack.last().pose().multiply(Animations.extraMatrixStack.last().pose());
        matrixStack.last().normal().mul(Animations.extraMatrixStack.last().normal());
    }

    public static void applyAnimationTransform(final ItemStack itemStack,
            final ItemTransforms.TransformType transformType, final LivingEntity entity,
            final PoseStack matrixStack) {
        if (itemStack != null && entity != null) {
            final BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack,
                    entity.level, entity, entity.getId());
            Animations.applyAnimationTransform(model, transformType, matrixStack);
        }
    }

    public static void applyAnimationTransform(final BakedModel model,
            final ItemTransforms.TransformType transformType, final PoseStack matrixStack) {
        if (Animations.peekNodeModel() != null && Animations.peekInitialModel() != null) {
            final ItemTransform modelTransformVec3f =
                    (model == null ? null : model.getTransforms().getTransform(transformType));
            if (modelTransformVec3f != null) {
                matrixStack.translate(modelTransformVec3f.translation.x(),
                        modelTransformVec3f.translation.y(), modelTransformVec3f.translation.z());
                matrixStack.scale(modelTransformVec3f.scale.x(), modelTransformVec3f.scale.y(),
                        modelTransformVec3f.scale.z());
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            final Matrix4f animationTransition =
                    new Matrix4f(Animations.peekNodeModel().computeGlobalTransform(null));
            final Matrix4f initialTransition =
                    new Matrix4f(Animations.peekInitialModel().computeGlobalTransform(null));
            animationTransition.transpose();
            initialTransition.transpose();
            initialTransition.invert();
            matrixStack.last().pose().multiply(animationTransition);
            matrixStack.last().pose().multiply(initialTransition);
            if (modelTransformVec3f != null) {
                matrixStack.translate(0.5, 0.5, 0.5);
                matrixStack.scale(1 / modelTransformVec3f.scale.x(),
                        1 / modelTransformVec3f.scale.y(), 1 / modelTransformVec3f.scale.z());
                matrixStack.translate(-modelTransformVec3f.translation.x(),
                        -modelTransformVec3f.translation.y(), -modelTransformVec3f.translation.z());
            }
        }
        Animations.applyExtraTransform(matrixStack);
    }
}
