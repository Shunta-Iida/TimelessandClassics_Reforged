package com.tac.guns.client.render.animation.module;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    public static GltfModelV2 load(AnimationMeta animationMeta) throws IOException {
        if (animationMeta != null)
            return load(animationMeta.getResourceLocation());
        else
            return null;
    }

    public static GltfModelV2 load(ResourceLocation resourceLocation) throws IOException {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(resourceLocation);
        InputStream inputStream = resource.getInputStream();
        GltfAssetReader reader = new GltfAssetReader();
        GltfAsset asset = reader.readWithoutReferences(inputStream);
        if (asset instanceof GltfAssetV2) {
            // put GltfModel into the gltfModelMap
            GltfModelV2 model = new GltfModelV2((GltfAssetV2) asset);
            gltfModelV2Map.put(resourceLocation.toString(), model);
            // refresh animationManagerMap
            List<Animation> animations = GltfAnimations.createModelAnimations(model.getAnimationModels());
            AnimationManager animationManager = new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
            animationManager.addAnimations(animations);
            animationManagerMap.put(resourceLocation.toString(), animationManager);
            // refresh animationRunnerMap
            stopAnimation(resourceLocation);
            AnimationRunner newRunner = new AnimationRunner(animationManager);
            animationRunnerMap.put(resourceLocation.toString(), newRunner);
            // initialState
            GltfModelV2 model2 = new GltfModelV2((GltfAssetV2) asset);
            List<Animation> animations2 = GltfAnimations.createModelAnimations(model2.getAnimationModels());
            AnimationManager initialStateManager = new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
            initialStateManager.addAnimations(animations2);
            initialStateManager.reset();
            initialStateManager.performStep(1);
            initialModelMap.put(resourceLocation.toString(), model2);
            return model;
        }
        inputStream.close();
        return null;
    }

    public static void specifyInitialModel(AnimationMeta animationMeta, AnimationMeta initialMeta) throws IOException {
        Resource initialResource = Minecraft.getInstance().getResourceManager()
                .getResource(initialMeta.getResourceLocation());
        InputStream inputStream = initialResource.getInputStream();
        GltfAssetReader reader = new GltfAssetReader();
        GltfAsset asset = reader.readWithoutReferences(inputStream);
        GltfModelV2 model2 = new GltfModelV2((GltfAssetV2) asset);
        List<Animation> animations2 = GltfAnimations.createModelAnimations(model2.getAnimationModels());
        AnimationManager initialStateManager = new AnimationManager(AnimationManager.AnimationPolicy.TIP_STOP);
        initialStateManager.addAnimations(animations2);
        initialStateManager.reset();
        initialStateManager.performStep(1);
        initialModelMap.put(animationMeta.getResourceLocation().toString(), model2);
    }

    private static GltfModelV2 getGltfModel(ResourceLocation resourceLocation) {
        return gltfModelV2Map.get(resourceLocation.toString());
    }

    private static GltfModelV2 getInitialModel(ResourceLocation resourceLocation) {
        return initialModelMap.get(resourceLocation.toString());
    }

    public static void pushNode(AnimationMeta animationMeta, int index) {
        if (animationMeta != null)
            pushNode(animationMeta.getResourceLocation(), index);
    }

    public static void pushNode(ResourceLocation gltfResource, int index) {
        if (gltfResource == null)
            return;
        GltfModelV2 gltfModel = getGltfModel(gltfResource);
        if (gltfModel == null) {
            bind = null;
        } else
            bind = gltfModel.getNodeModels().get(index);
        GltfModelV2 initialModel = getInitialModel(gltfResource);
        if (initialModel == null) {
            initial = null;
        } else
            initial = initialModel.getNodeModels().get(index);
        nodeModelStack.push(bind);
        initialModelStack.push(initial);
    }

    public static void popNode() {
        if (!nodeModelStack.empty())
            nodeModelStack.pop();
        if (!initialModelStack.empty())
            initialModelStack.pop();
        bind = (nodeModelStack.empty() ? null : nodeModelStack.peek());
        initial = (initialModelStack.empty() ? null : initialModelStack.peek());
    }

    public static NodeModel peekNodeModel() {
        return bind;
    }

    public static NodeModel peekInitialModel() {
        return initial;
    }

    public static AnimationRunner getAnimationRunner(ResourceLocation resourceLocation) {
        return animationRunnerMap.get(resourceLocation.toString());
    }

    public static void runAnimation(AnimationMeta animationMeta) {
        if (animationMeta != null)
            runAnimation(animationMeta.getResourceLocation());
    }

    public static void runAnimation(ResourceLocation resourceLocation) {
        runAnimation(resourceLocation, null);
    }

    public static void runAnimation(AnimationMeta animationMeta, Runnable callback) {
        if (animationMeta != null)
            runAnimation(animationMeta.getResourceLocation(), callback);
    }

    public static void runAnimation(ResourceLocation resourceLocation, Runnable callback) {
        AnimationRunner runner = getAnimationRunner(resourceLocation);
        if (runner != null) {
            if (runner.isRunning())
                return;
            runner.start(callback);
        }
    }

    public static void stopAnimation(AnimationMeta animationMeta) {
        if (animationMeta != null)
            stopAnimation(animationMeta.getResourceLocation());
    }

    public static void stopAnimation(ResourceLocation resourceLocation) {
        AnimationRunner runner = getAnimationRunner(resourceLocation);
        if (runner != null)
            runner.stop();
        AnimationManager manager = getAnimationManager(resourceLocation);
        if (manager != null) {
            manager.reset();
            manager.performStep(0);
        }
    }

    public static boolean isAnimationRunning(AnimationMeta animationMeta) {
        if (animationMeta != null)
            return isAnimationRunning(animationMeta.getResourceLocation());
        else
            return false;
    }

    public static boolean isAnimationRunning(ResourceLocation resourceLocation) {
        AnimationRunner runner = getAnimationRunner(resourceLocation);
        if (runner == null)
            return false;
        return runner.isRunning();
    }

    private static AnimationManager getAnimationManager(ResourceLocation resourceLocation) {
        return animationManagerMap.get(resourceLocation.toString());
    }

    public static PoseStack getExtraMatrixStack() {
        return extraMatrixStack;
    }

    public static void applyExtraTransform(PoseStack matrixStack) {
        matrixStack.last().pose().multiply(extraMatrixStack.last().pose());
        matrixStack.last().normal().mul(extraMatrixStack.last().normal());
    }

    public static void applyAnimationTransform(ItemStack itemStack, ItemTransforms.TransformType transformType,
            LivingEntity entity, PoseStack matrixStack) {
        if (itemStack != null && entity != null) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, entity.level, entity,
                    entity.getId());
            applyAnimationTransform(model, transformType, matrixStack);
        }
    }

    public static void applyAnimationTransform(BakedModel model, ItemTransforms.TransformType transformType,
            PoseStack matrixStack) {
        if (Animations.peekNodeModel() != null && Animations.peekInitialModel() != null) {
            ItemTransform modelTransformVec3f = (model == null ? null
                    : model.getTransforms().getTransform(transformType));
            if (modelTransformVec3f != null) {
                matrixStack.translate(modelTransformVec3f.translation.x(), modelTransformVec3f.translation.y(),
                        modelTransformVec3f.translation.z());
                matrixStack.scale(modelTransformVec3f.scale.x(), modelTransformVec3f.scale.y(),
                        modelTransformVec3f.scale.z());
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            Matrix4f animationTransition = new Matrix4f(Animations.peekNodeModel().computeGlobalTransform(null));
            Matrix4f initialTransition = new Matrix4f(Animations.peekInitialModel().computeGlobalTransform(null));
            animationTransition.transpose();
            initialTransition.transpose();
            initialTransition.invert();
            matrixStack.last().pose().multiply(animationTransition);
            matrixStack.last().pose().multiply(initialTransition);
            if (modelTransformVec3f != null) {
                matrixStack.translate(0.5, 0.5, 0.5);
                matrixStack.scale(1 / modelTransformVec3f.scale.x(), 1 / modelTransformVec3f.scale.y(),
                        1 / modelTransformVec3f.scale.z());
                matrixStack.translate(-modelTransformVec3f.translation.x(), -modelTransformVec3f.translation.y(),
                        -modelTransformVec3f.translation.z());
            }
        }
        applyExtraTransform(matrixStack);
    }
}
