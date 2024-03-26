/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model.v2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.AccessorSparse;
import de.javagl.jgltf.impl.v2.AccessorSparseIndices;
import de.javagl.jgltf.impl.v2.AccessorSparseValues;
import de.javagl.jgltf.impl.v2.Animation;
import de.javagl.jgltf.impl.v2.AnimationChannel;
import de.javagl.jgltf.impl.v2.AnimationChannelTarget;
import de.javagl.jgltf.impl.v2.AnimationSampler;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.Camera;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Sampler;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.AnimationModel.Channel;
import de.javagl.jgltf.model.AnimationModel.Interpolation;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.Utils;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel.DefaultChannel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel.DefaultSampler;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.impl.DefaultCameraModel;
import de.javagl.jgltf.model.impl.DefaultImageModel;
import de.javagl.jgltf.model.impl.DefaultMaterialModel;
import de.javagl.jgltf.model.impl.DefaultMeshModel;
import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jgltf.model.impl.DefaultNodeModel;
import de.javagl.jgltf.model.impl.DefaultSceneModel;
import de.javagl.jgltf.model.impl.DefaultSkinModel;
import de.javagl.jgltf.model.impl.DefaultTextureModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v2.gl.Materials;

/**
 * Implementation of a {@link GltfModel}, based on a {@link GlTF glTF 2.0}.<br>
 */
public final class GltfModelV2 implements GltfModel {
    /**
     * The logger used in this class
     */
    private static final Logger logger = Logger.getLogger(GltfModelV2.class.getName());

    /**
     * The {@link GltfAssetV2} of this model
     */
    private final GltfAssetV2 gltfAsset;

    /**
     * The {@link GlTF} of this model
     */
    private final GlTF gltf;

    /**
     * The binary data that is associated with this model in the case
     * that the glTF was a binary glTF
     */
    private final ByteBuffer binaryData;

    /**
     * The {@link AccessorModel} instances that have been created from
     * the {@link Accessor} instances
     */
    private final List<DefaultAccessorModel> accessorModels;

    /**
     * The {@link AnimationModel} instances that have been created from
     * the {@link Animation} instances
     */
    private final List<DefaultAnimationModel> animationModels;

    /**
     * The {@link BufferModel} instances that have been created from
     * the {@link Buffer} instances
     */
    private final List<DefaultBufferModel> bufferModels;

    /**
     * The {@link BufferViewModel} instances that have been created from
     * the {@link Buffer} instances
     */
    private final List<DefaultBufferViewModel> bufferViewModels;

    /**
     * The {@link CameraModel} instances that have been created from
     * the {@link Camera} references of {@link Node} instances
     */
    private final List<DefaultCameraModel> cameraModels;

    /**
     * The {@link ImageModel} instances that have been created from
     * the {@link Image} references of {@link Node} instances
     */
    private final List<DefaultImageModel> imageModels;

    /**
     * The {@link MaterialModel} instances that have been created from
     * the {@link Material} instances
     */
    private final List<DefaultMaterialModel> materialModels;

    /**
     * The {@link MeshModel} instances that have been created from
     * the {@link Mesh} instances
     */
    private final List<DefaultMeshModel> meshModels;

    /**
     * The {@link NodeModel} instances that have been created from
     * the {@link Node} instances
     */
    private final List<DefaultNodeModel> nodeModels;

    /**
     * The {@link SceneModel} instances that have been created from
     * the {@link Scene} instances
     */
    private final List<DefaultSceneModel> sceneModels;

    /**
     * The {@link SkinModel} instances that have been created from
     * the {@link Skin} instances
     */
    private final List<DefaultSkinModel> skinModels;

    /**
     * The {@link TextureModel} instances that have been created from
     * the {@link Texture} instances
     */
    private final List<DefaultTextureModel> textureModels;

    /**
     * The {@link MaterialModelHandler} that will manage the
     * {@link MaterialModel} instances that have to be created
     */
    private final MaterialModelHandler materialModelHandler;

    /**
     * Creates a new model for the given glTF
     * 
     * @param gltfAsset The {@link GltfAssetV2}
     */
    public GltfModelV2(final GltfAssetV2 gltfAsset) {
        this.gltfAsset = Objects.requireNonNull(gltfAsset, "The gltfAsset may not be null");
        this.gltf = gltfAsset.getGltf();

        final ByteBuffer binaryData = gltfAsset.getBinaryData();
        if (binaryData != null && binaryData.capacity() > 0) {
            this.binaryData = binaryData;
        } else {
            this.binaryData = null;
        }

        this.accessorModels = new ArrayList<DefaultAccessorModel>();
        this.animationModels = new ArrayList<DefaultAnimationModel>();
        this.bufferModels = new ArrayList<DefaultBufferModel>();
        this.bufferViewModels = new ArrayList<DefaultBufferViewModel>();
        this.cameraModels = new ArrayList<DefaultCameraModel>();
        this.imageModels = new ArrayList<DefaultImageModel>();
        this.materialModels = new ArrayList<DefaultMaterialModel>();
        this.meshModels = new ArrayList<DefaultMeshModel>();
        this.nodeModels = new ArrayList<DefaultNodeModel>();
        this.sceneModels = new ArrayList<DefaultSceneModel>();
        this.skinModels = new ArrayList<DefaultSkinModel>();
        this.textureModels = new ArrayList<DefaultTextureModel>();

        this.materialModelHandler = new MaterialModelHandler();

        this.createAccessorModels();
        this.createAnimationModels();
        this.createBufferModels();
        this.createBufferViewModels();
        this.createImageModels();
        this.createMeshModels();
        this.createNodeModels();
        this.createSceneModels();
        this.createSkinModels();
        this.createTextureModels();

        this.initBufferModels();
        this.initBufferViewModels();

        this.initAccessorModels();
        this.initAnimationModels();
        this.initImageModels();
        this.initMeshModels();
        this.initNodeModels();
        this.initSceneModels();
        this.initSkinModels();
        this.initTextureModels();

        this.instantiateCameraModels();
        // instantiateMaterialModels();
    }

    /**
     * Create the {@link AccessorModel} instances
     */
    private void createAccessorModels() {
        final List<Accessor> accessors = Optionals.of(this.gltf.getAccessors());
        for (int i = 0; i < accessors.size(); i++) {
            final Accessor accessor = accessors.get(i);
            final Integer componentType = accessor.getComponentType();
            final Integer count = accessor.getCount();
            final ElementType elementType = ElementType.forString(accessor.getType());
            final DefaultAccessorModel accessorModel =
                    new DefaultAccessorModel(componentType, count, elementType);
            this.accessorModels.add(accessorModel);
        }
    }

    /**
     * Create the {@link AnimationModel} instances
     */
    private void createAnimationModels() {
        final List<Animation> animations = Optionals.of(this.gltf.getAnimations());
        for (int i = 0; i < animations.size(); i++) {
            this.animationModels.add(new DefaultAnimationModel());
        }
    }

    /**
     * Create the {@link BufferModel} instances
     */
    private void createBufferModels() {
        final List<Buffer> buffers = Optionals.of(this.gltf.getBuffers());
        for (int i = 0; i < buffers.size(); i++) {
            final Buffer buffer = buffers.get(i);
            final DefaultBufferModel bufferModel = new DefaultBufferModel();
            bufferModel.setUri(buffer.getUri());
            this.bufferModels.add(bufferModel);
        }
    }

    /**
     * Create the {@link BufferViewModel} instances
     */
    private void createBufferViewModels() {
        final List<BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());
        for (int i = 0; i < bufferViews.size(); i++) {
            final BufferView bufferView = bufferViews.get(i);
            final DefaultBufferViewModel bufferViewModel =
                    GltfModelV2.createBufferViewModel(bufferView);
            this.bufferViewModels.add(bufferViewModel);
        }
    }

    /**
     * Create a {@link DefaultBufferViewModel} for the given {@link BufferView}
     * 
     * @param bufferView The {@link BufferView}
     * @return The {@link BufferViewModel}
     */
    private static DefaultBufferViewModel createBufferViewModel(final BufferView bufferView) {
        final int byteOffset = Optionals.of(bufferView.getByteOffset(), 0);
        final int byteLength = bufferView.getByteLength();
        final Integer byteStride = bufferView.getByteStride();
        final Integer target = bufferView.getTarget();
        final DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(target);
        bufferViewModel.setByteOffset(byteOffset);
        bufferViewModel.setByteLength(byteLength);
        bufferViewModel.setByteStride(byteStride);
        return bufferViewModel;
    }

    /**
     * Create the {@link ImageModel} instances
     */
    private void createImageModels() {
        final List<Image> images = Optionals.of(this.gltf.getImages());
        for (int i = 0; i < images.size(); i++) {
            final Image image = images.get(i);
            final String mimeType = image.getMimeType();
            final DefaultImageModel imageModel = new DefaultImageModel(mimeType, null);
            final String uri = image.getUri();
            imageModel.setUri(uri);
            this.imageModels.add(imageModel);
        }
    }

    /**
     * Create the {@link MeshModel} instances
     */
    private void createMeshModels() {
        final List<Mesh> meshes = Optionals.of(this.gltf.getMeshes());
        for (int i = 0; i < meshes.size(); i++) {
            this.meshModels.add(new DefaultMeshModel());
        }
    }

    /**
     * Create the {@link NodeModel} instances
     */
    private void createNodeModels() {
        final List<Node> nodes = Optionals.of(this.gltf.getNodes());
        for (int i = 0; i < nodes.size(); i++) {
            this.nodeModels.add(new DefaultNodeModel());
        }
    }

    /**
     * Create the {@link SceneModel} instances
     */
    private void createSceneModels() {
        final List<Scene> scenes = Optionals.of(this.gltf.getScenes());
        for (int i = 0; i < scenes.size(); i++) {
            this.sceneModels.add(new DefaultSceneModel());
        }
    }

    /**
     * Create the {@link SkinModel} instances
     */
    private void createSkinModels() {
        final List<Skin> skins = Optionals.of(this.gltf.getSkins());
        for (int i = 0; i < skins.size(); i++) {
            this.skinModels.add(new DefaultSkinModel(null));
        }
    }

    /**
     * Create the {@link TextureModel} instances
     */
    private void createTextureModels() {
        final List<Texture> textures = Optionals.of(this.gltf.getTextures());
        final List<Sampler> samplers = Optionals.of(this.gltf.getSamplers());
        for (int i = 0; i < textures.size(); i++) {
            final Texture texture = textures.get(i);
            final Integer samplerIndex = texture.getSampler();

            Integer magFilter = GltfConstants.GL_LINEAR;
            Integer minFilter = GltfConstants.GL_LINEAR;
            int wrapS = GltfConstants.GL_REPEAT;
            int wrapT = GltfConstants.GL_REPEAT;

            if (samplerIndex != null) {
                final Sampler sampler = samplers.get(samplerIndex);
                magFilter = sampler.getMagFilter();
                minFilter = sampler.getMinFilter();
                wrapS = Optionals.of(sampler.getWrapS(), sampler.defaultWrapS());
                wrapT = Optionals.of(sampler.getWrapT(), sampler.defaultWrapT());
            }

            this.textureModels.add(new DefaultTextureModel(magFilter, minFilter, wrapS, wrapT));
        }
    }

    /**
     * Initialize the {@link AccessorModel} instances
     */
    private void initAccessorModels() {
        final List<Accessor> accessors = Optionals.of(this.gltf.getAccessors());
        for (int i = 0; i < accessors.size(); i++) {
            final Accessor accessor = accessors.get(i);
            final DefaultAccessorModel accessorModel = this.accessorModels.get(i);

            final int byteOffset = Optionals.of(accessor.getByteOffset(), 0);
            accessorModel.setByteOffset(byteOffset);

            final AccessorSparse accessorSparse = accessor.getSparse();
            if (accessorSparse == null) {
                this.initDenseAccessorModel(i, accessor, accessorModel);
            } else {
                this.initSparseAccessorModel(i, accessor, accessorModel);
            }
        }
    }

    /**
     * Initialize the {@link AccessorModel} by setting its
     * {@link AccessorModel#getBufferViewModel() buffer view model}
     * for the case that the accessor is dense (i.e. not sparse)
     * 
     * @param accessorIndex The accessor index. Only used for constructing
     *                      the URI string of buffers that may have to be created
     *                      internally
     * @param accessor      The {@link Accessor}
     * @param accessorModel The {@link AccessorModel}
     */
    private void initDenseAccessorModel(final int accessorIndex, final Accessor accessor,
            final DefaultAccessorModel accessorModel) {
        final Integer bufferViewIndex = accessor.getBufferView();
        if (bufferViewIndex != null) {
            // When there is a BufferView referenced from the accessor, then
            // the corresponding BufferViewModel may be assigned directly
            final DefaultBufferViewModel bufferViewModel =
                    this.bufferViewModels.get(bufferViewIndex);
            accessorModel.setBufferViewModel(bufferViewModel);
        } else {
            // When there is no BufferView referenced from the accessor,
            // then a NEW BufferViewModel (and Buffer) have to be created
            final int count = accessorModel.getCount();
            final int elementSizeInBytes = accessorModel.getElementSizeInBytes();
            final int byteLength = elementSizeInBytes * count;
            final ByteBuffer bufferData = Buffers.create(byteLength);
            final String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
            final DefaultBufferViewModel bufferViewModel =
                    GltfModelV2.createBufferViewModel(uriString, bufferData);
            accessorModel.setBufferViewModel(bufferViewModel);
        }

        final BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        final Integer byteStride = bufferViewModel.getByteStride();
        if (byteStride == null) {
            accessorModel.setByteStride(accessorModel.getElementSizeInBytes());
        } else {
            accessorModel.setByteStride(byteStride);
        }
    }

    /**
     * Initialize the given {@link AccessorModel} by setting its
     * {@link AccessorModel#getBufferViewModel() buffer view model}
     * for the case that the accessor is sparse.
     * 
     * @param accessorIndex The accessor index. Only used for constructing
     *                      the URI string of buffers that may have to be created
     *                      internally
     * @param accessor      The {@link Accessor}
     * @param accessorModel The {@link AccessorModel}
     */
    private void initSparseAccessorModel(final int accessorIndex, final Accessor accessor,
            final DefaultAccessorModel accessorModel) {
        // When the (sparse!) Accessor already refers to a BufferView,
        // then this BufferView has to be replaced with a new one,
        // to which the data substitution will be applied
        final int count = accessorModel.getCount();
        final int elementSizeInBytes = accessorModel.getElementSizeInBytes();
        final int byteLength = elementSizeInBytes * count;
        final ByteBuffer bufferData = Buffers.create(byteLength);
        final String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
        final DefaultBufferViewModel denseBufferViewModel =
                GltfModelV2.createBufferViewModel(uriString, bufferData);
        accessorModel.setBufferViewModel(denseBufferViewModel);
        accessorModel.setByteOffset(0);

        final Integer bufferViewIndex = accessor.getBufferView();
        if (bufferViewIndex != null) {
            // If the accessor refers to a BufferView, then the corresponding
            // data serves as the basis for the initialization of the values,
            // before the sparse substitution is applied
            final Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> {
                GltfModelV2.logger.fine(
                        "Substituting sparse accessor data," + " based on existing buffer view");

                final DefaultBufferViewModel baseBufferViewModel =
                        this.bufferViewModels.get(bufferViewIndex);
                final ByteBuffer baseBufferViewData = baseBufferViewModel.getBufferViewData();
                final AccessorData baseAccessorData =
                        AccessorDatas.create(accessorModel, baseBufferViewData);
                final AccessorData denseAccessorData =
                        AccessorDatas.create(accessorModel, bufferData);
                this.substituteSparseAccessorData(accessor, accessorModel, denseAccessorData,
                        baseAccessorData);
            };
            denseBufferViewModel.setSparseSubstitutionCallback(sparseSubstitutionCallback);
        } else {
            // When the sparse accessor does not yet refer to a BufferView,
            // then a new one is created,
            final Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> {
                GltfModelV2.logger.fine(
                        "Substituting sparse accessor data, " + "without an existing buffer view");

                final AccessorData denseAccessorData =
                        AccessorDatas.create(accessorModel, bufferData);
                this.substituteSparseAccessorData(accessor, accessorModel, denseAccessorData, null);
            };
            denseBufferViewModel.setSparseSubstitutionCallback(sparseSubstitutionCallback);
        }
    }

    /**
     * Create a new {@link BufferViewModel} with an associated
     * {@link BufferModel} that serves as the basis for a sparse accessor, or
     * an accessor that does not refer to a {@link BufferView})
     * 
     * @param uriString  The URI string that will be assigned to the
     *                   {@link BufferModel} that is created internally. This string
     *                   is not strictly required, but helpful for debugging, at
     *                   least
     * @param bufferData The buffer data
     * @return The new {@link BufferViewModel}
     */
    private static DefaultBufferViewModel createBufferViewModel(final String uriString,
            final ByteBuffer bufferData) {
        final DefaultBufferModel bufferModel = new DefaultBufferModel();
        bufferModel.setUri(uriString);
        bufferModel.setBufferData(bufferData);

        final DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(null);
        bufferViewModel.setByteOffset(0);
        bufferViewModel.setByteLength(bufferData.capacity());
        bufferViewModel.setBufferModel(bufferModel);

        return bufferViewModel;
    }

    /**
     * Substitute the sparse accessor data in the given dense
     * {@link AccessorData} for the given {@link AccessorModel}
     * based on the sparse accessor data that is defined in the given
     * {@link Accessor}.
     * 
     * @param accessor          The {@link Accessor}
     * @param accessorModel     The {@link AccessorModel}
     * @param denseAccessorData The dense {@link AccessorData}
     * @param baseAccessorData  The optional {@link AccessorData} that contains
     *                          the base data. If this is not <code>null</code>,
     *                          then it will be used
     *                          to initialize the {@link AccessorData}, before the
     *                          sparse data
     *                          substitution takes place
     */
    private void substituteSparseAccessorData(final Accessor accessor,
            final AccessorModel accessorModel, final AccessorData denseAccessorData,
            final AccessorData baseAccessorData) {
        final AccessorSparse accessorSparse = accessor.getSparse();
        final int count = accessorSparse.getCount();

        final AccessorSparseIndices accessorSparseIndices = accessorSparse.getIndices();
        final AccessorData sparseIndicesAccessorData =
                this.createSparseIndicesAccessorData(accessorSparseIndices, count);

        final AccessorSparseValues accessorSparseValues = accessorSparse.getValues();
        final ElementType elementType = accessorModel.getElementType();
        final AccessorData sparseValuesAccessorData =
                this.createSparseValuesAccessorData(accessorSparseValues,
                        accessorModel.getComponentType(), elementType.getNumComponents(), count);

        AccessorSparseUtils.substituteAccessorData(denseAccessorData, baseAccessorData,
                sparseIndicesAccessorData, sparseValuesAccessorData);
    }

    /**
     * Create the {@link AccessorData} for the given
     * {@link AccessorSparseIndices}
     * 
     * @param accessorSparseIndices The {@link AccessorSparseIndices}
     * @param count                 The count from the {@link AccessorSparse}
     * @return The {@link AccessorData}
     */
    private AccessorData createSparseIndicesAccessorData(
            final AccessorSparseIndices accessorSparseIndices, final int count) {
        final Integer componentType = accessorSparseIndices.getComponentType();
        final Integer bufferViewIndex = accessorSparseIndices.getBufferView();
        final BufferViewModel bufferViewModel = this.bufferViewModels.get(bufferViewIndex);
        final ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
        final int byteOffset = Optionals.of(accessorSparseIndices.getByteOffset(), 0);
        return AccessorDatas.create(componentType, bufferViewData, byteOffset, count, 1, null);
    }

    /**
     * Create the {@link AccessorData} for the given
     * {@link AccessorSparseValues}
     * 
     * @param accessorSparseValues    The {@link AccessorSparseValues}
     * @param componentType           The component type of the {@link Accessor}
     * @param numComponentsPerElement The number of components per element
     *                                of the {@link AccessorModel#getElementType()
     *                                accessor element type}
     * @param count                   The count from the {@link AccessorSparse}
     * @return The {@link AccessorData}
     */
    private AccessorData createSparseValuesAccessorData(
            final AccessorSparseValues accessorSparseValues, final int componentType,
            final int numComponentsPerElement, final int count) {
        final Integer bufferViewIndex = accessorSparseValues.getBufferView();
        final BufferViewModel bufferViewModel = this.bufferViewModels.get(bufferViewIndex);
        final ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
        final int byteOffset = Optionals.of(accessorSparseValues.getByteOffset(), 0);
        return AccessorDatas.create(componentType, bufferViewData, byteOffset, count,
                numComponentsPerElement, null);
    }

    /**
     * Initialize the {@link AnimationModel} instances
     */
    private void initAnimationModels() {
        final List<Animation> animations = Optionals.of(this.gltf.getAnimations());
        for (int i = 0; i < animations.size(); i++) {
            final Animation animation = animations.get(i);
            final DefaultAnimationModel animationModel = this.animationModels.get(i);
            animationModel.setName(animation.getName());
            final List<AnimationChannel> channels = Optionals.of(animation.getChannels());
            for (final AnimationChannel animationChannel : channels) {
                final Channel channel = this.createChannel(animation, animationChannel);
                animationModel.addChannel(channel);
            }
        }
    }

    /**
     * Create the {@link Channel} object for the given animation and animation
     * channel
     * 
     * @param animation        The {@link Animation}
     * @param animationChannel The {@link AnimationChannel}
     * @return The {@link Channel}
     */
    private Channel createChannel(final Animation animation,
            final AnimationChannel animationChannel) {
        final List<AnimationSampler> samplers = Optionals.of(animation.getSamplers());

        final int samplerIndex = animationChannel.getSampler();
        final AnimationSampler animationSampler = samplers.get(samplerIndex);

        final int inputAccessorIndex = animationSampler.getInput();
        final DefaultAccessorModel inputAccessorModel = this.accessorModels.get(inputAccessorIndex);

        final int outputAccessorIndex = animationSampler.getOutput();
        final DefaultAccessorModel outputAccessorModel =
                this.accessorModels.get(outputAccessorIndex);

        final String interpolationString = animationSampler.getInterpolation();
        final Interpolation interpolation = interpolationString == null ? Interpolation.LINEAR
                : Interpolation.valueOf(interpolationString);

        final AnimationModel.Sampler sampler =
                new DefaultSampler(inputAccessorModel, interpolation, outputAccessorModel);

        final AnimationChannelTarget animationChannelTarget = animationChannel.getTarget();

        final Integer nodeIndex = animationChannelTarget.getNode();
        NodeModel nodeModel = null;
        if (nodeIndex == null) {
            // Should not happen yet. Targets always refer to nodes
            GltfModelV2.logger.warning("No node index given for animation channel target");
        } else {
            nodeModel = this.nodeModels.get(nodeIndex);
        }
        final String path = animationChannelTarget.getPath();

        final Channel channel = new DefaultChannel(sampler, nodeModel, path);
        return channel;
    }

    /**
     * Initialize the {@link BufferModel} instances
     */
    private void initBufferModels() {
        final List<Buffer> buffers = Optionals.of(this.gltf.getBuffers());

        if (buffers.isEmpty() && this.binaryData != null) {
            GltfModelV2.logger.warning("Binary data was given, but no buffers");
            return;
        }

        for (int i = 0; i < buffers.size(); i++) {
            final Buffer buffer = buffers.get(i);
            final DefaultBufferModel bufferModel = this.bufferModels.get(i);
            bufferModel.setName(buffer.getName());
            if (i == 0 && this.binaryData != null) {
                bufferModel.setBufferData(this.binaryData);
            } else {
                final String uri = buffer.getUri();
                if (IO.isDataUriString(uri)) {
                    final byte data[] = IO.readDataUri(uri);
                    final ByteBuffer bufferData = Buffers.create(data);
                    bufferModel.setBufferData(bufferData);
                } else {
                    if (uri == null) {
                        GltfModelV2.logger.warning("Buffer " + i + " does not have "
                                + "a uri. Binary chunks that are not the main GLB "
                                + "buffer are not supported.");
                    } else {
                        final ByteBuffer bufferData = this.gltfAsset.getReferenceData(uri);
                        bufferModel.setBufferData(bufferData);
                    }
                }
            }
        }
    }

    /**
     * Initialize the {@link BufferViewModel} instances
     */
    private void initBufferViewModels() {
        final List<BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());
        for (int i = 0; i < bufferViews.size(); i++) {
            final BufferView bufferView = bufferViews.get(i);

            final DefaultBufferViewModel bufferViewModel = this.bufferViewModels.get(i);
            bufferViewModel.setName(bufferView.getName());

            final int bufferIndex = bufferView.getBuffer();
            final BufferModel bufferModel = this.bufferModels.get(bufferIndex);
            bufferViewModel.setBufferModel(bufferModel);
        }
    }

    /**
     * Initialize the {@link MeshModel} instances
     */
    private void initMeshModels() {
        final List<Mesh> meshes = Optionals.of(this.gltf.getMeshes());
        for (int i = 0; i < meshes.size(); i++) {
            final Mesh mesh = meshes.get(i);
            final DefaultMeshModel meshModel = this.meshModels.get(i);
            meshModel.setName(mesh.getName());

            final List<MeshPrimitive> primitives = Optionals.of(mesh.getPrimitives());
            for (final MeshPrimitive meshPrimitive : primitives) {
                final MeshPrimitiveModel meshPrimitiveModel =
                        this.createMeshPrimitiveModel(meshPrimitive);
                meshModel.addMeshPrimitiveModel(meshPrimitiveModel);
            }
        }
    }

    /**
     * Create a {@link MeshPrimitiveModel} for the given
     * {@link MeshPrimitive}.<br>
     * <br>
     * Note: The resulting {@link MeshPrimitiveModel} will not have any
     * {@link MaterialModel} assigned. The material model may have to
     * be instantiated multiple times, with different {@link TechniqueModel}
     * instances. This is done in {@link #instantiateMaterialModels()}
     * 
     * @param meshPrimitive The {@link MeshPrimitive}
     * @return The {@link MeshPrimitiveModel}
     */
    private DefaultMeshPrimitiveModel createMeshPrimitiveModel(final MeshPrimitive meshPrimitive) {
        final Integer mode = Optionals.of(meshPrimitive.getMode(), meshPrimitive.defaultMode());
        final DefaultMeshPrimitiveModel meshPrimitiveModel = new DefaultMeshPrimitiveModel(mode);

        final Integer indicesIndex = meshPrimitive.getIndices();
        if (indicesIndex != null) {
            final AccessorModel indices = this.accessorModels.get(indicesIndex);
            meshPrimitiveModel.setIndices(indices);
        }
        final Map<String, Integer> attributes = Optionals.of(meshPrimitive.getAttributes());
        for (final Entry<String, Integer> entry : attributes.entrySet()) {
            final String attributeName = entry.getKey();
            final int attributeIndex = entry.getValue();
            final AccessorModel attribute = this.accessorModels.get(attributeIndex);
            meshPrimitiveModel.putAttribute(attributeName, attribute);
        }

        final List<Map<String, Integer>> morphTargets = Optionals.of(meshPrimitive.getTargets());
        for (final Map<String, Integer> morphTarget : morphTargets) {
            final Map<String, AccessorModel> morphTargetModel =
                    new LinkedHashMap<String, AccessorModel>();
            for (final Entry<String, Integer> entry : morphTarget.entrySet()) {
                final String attribute = entry.getKey();
                final Integer accessorIndex = entry.getValue();
                final DefaultAccessorModel accessorModel = this.accessorModels.get(accessorIndex);
                morphTargetModel.put(attribute, accessorModel);
            }
            meshPrimitiveModel.addTarget(Collections.unmodifiableMap(morphTargetModel));
        }

        return meshPrimitiveModel;
    }

    /**
     * Initialize the {@link NodeModel} instances
     */
    private void initNodeModels() {
        final List<Node> nodes = Optionals.of(this.gltf.getNodes());
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);

            final DefaultNodeModel nodeModel = this.nodeModels.get(i);
            nodeModel.setName(node.getName());

            final List<Integer> childIndices = Optionals.of(node.getChildren());
            for (final Integer childIndex : childIndices) {
                final DefaultNodeModel child = this.nodeModels.get(childIndex);
                nodeModel.addChild(child);
            }

            final Integer meshIndex = node.getMesh();
            if (meshIndex != null) {
                final MeshModel meshModel = this.meshModels.get(meshIndex);
                nodeModel.addMeshModel(meshModel);
            }

            final Integer skinIndex = node.getSkin();
            if (skinIndex != null) {
                final SkinModel skinModel = this.skinModels.get(skinIndex);
                nodeModel.setSkinModel(skinModel);
            }

            final float matrix[] = node.getMatrix();
            final float translation[] = node.getTranslation();
            final float rotation[] = node.getRotation();
            final float scale[] = node.getScale();
            nodeModel.setMatrix(Optionals.clone(matrix));
            nodeModel.setTranslation(Optionals.clone(translation));
            nodeModel.setRotation(Optionals.clone(rotation));
            nodeModel.setScale(Optionals.clone(scale));

            final List<Float> weights = node.getWeights();
            if (weights != null) {
                final float weightsArray[] = new float[weights.size()];
                for (int j = 0; j < weights.size(); j++) {
                    weightsArray[j] = weights.get(j);
                }
                nodeModel.setWeights(weightsArray);
            }
        }
    }

    /**
     * Initialize the {@link SceneModel} instances
     */
    private void initSceneModels() {
        final List<Scene> scenes = Optionals.of(this.gltf.getScenes());
        for (int i = 0; i < scenes.size(); i++) {
            final Scene scene = scenes.get(i);

            final DefaultSceneModel sceneModel = this.sceneModels.get(i);
            sceneModel.setName(scene.getName());

            final List<Integer> nodeIndices = Optionals.of(scene.getNodes());
            for (final Integer nodeIndex : nodeIndices) {
                final NodeModel nodeModel = this.nodeModels.get(nodeIndex);
                sceneModel.addNode(nodeModel);
            }
        }
    }

    /**
     * Initialize the {@link SkinModel} instances
     */
    private void initSkinModels() {
        final List<Skin> skins = Optionals.of(this.gltf.getSkins());
        for (int i = 0; i < skins.size(); i++) {
            final Skin skin = skins.get(i);
            final DefaultSkinModel skinModel = this.skinModels.get(i);
            skinModel.setName(skin.getName());

            final List<Integer> jointIndices = skin.getJoints();
            for (final Integer jointIndex : jointIndices) {
                final NodeModel jointNodeModel = this.nodeModels.get(jointIndex);
                skinModel.addJoint(jointNodeModel);
            }

            final Integer inverseBindMatricesIndex = skin.getInverseBindMatrices();
            final DefaultAccessorModel inverseBindMatrices =
                    this.accessorModels.get(inverseBindMatricesIndex);
            skinModel.setInverseBindMatrices(inverseBindMatrices);
        }
    }

    /**
     * Initialize the {@link TextureModel} instances
     */
    private void initTextureModels() {
        final List<Texture> textures = Optionals.of(this.gltf.getTextures());
        for (int i = 0; i < textures.size(); i++) {
            final Texture texture = textures.get(i);
            final DefaultTextureModel textureModel = this.textureModels.get(i);
            textureModel.setName(texture.getName());

            final Integer imageIndex = texture.getSource();
            final DefaultImageModel imageModel = this.imageModels.get(imageIndex);
            textureModel.setImageModel(imageModel);
        }
    }

    /**
     * Initialize the {@link ImageModel} instances
     */
    private void initImageModels() {
        final List<Image> images = Optionals.of(this.gltf.getImages());
        for (int i = 0; i < images.size(); i++) {
            final Image image = images.get(i);
            final DefaultImageModel imageModel = this.imageModels.get(i);
            imageModel.setName(image.getName());

            final Integer bufferViewIndex = image.getBufferView();
            if (bufferViewIndex != null) {
                final BufferViewModel bufferViewModel = this.bufferViewModels.get(bufferViewIndex);
                imageModel.setBufferViewModel(bufferViewModel);
            } else {
                final String uri = image.getUri();
                if (IO.isDataUriString(uri)) {
                    final byte data[] = IO.readDataUri(uri);
                    final ByteBuffer imageData = Buffers.create(data);
                    imageModel.setImageData(imageData);
                } else {
                    final ByteBuffer imageData = this.gltfAsset.getReferenceData(uri);
                    imageModel.setImageData(imageData);
                }
            }
        }
    }

    /**
     * Create the {@link CameraModel} instances. This has to be be called
     * <b>after</b> the {@link #nodeModels} have been created: Each time
     * that a node refers to a camera, a new instance of this camera
     * has to be created.
     */
    private void instantiateCameraModels() {
        final List<Node> nodes = Optionals.of(this.gltf.getNodes());
        final List<Camera> cameras = Optionals.of(this.gltf.getCameras());
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);

            final Integer cameraIndex = node.getCamera();
            if (cameraIndex != null) {
                final Camera camera = cameras.get(cameraIndex);
                final NodeModel nodeModel = this.nodeModels.get(i);

                final Function<float[], float[]> viewMatrixComputer = result -> {
                    final float localResult[] = Utils.validate(result, 16);
                    nodeModel.computeGlobalTransform(localResult);
                    MathUtils.invert4x4(localResult, localResult);
                    return localResult;
                };
                final BiFunction<float[], Float, float[]> projectionMatrixComputer =
                        (result, aspectRatio) -> {
                            final float localResult[] = Utils.validate(result, 16);
                            CamerasV2.computeProjectionMatrix(camera, aspectRatio, localResult);
                            return localResult;
                        };
                final DefaultCameraModel cameraModel =
                        new DefaultCameraModel(viewMatrixComputer, projectionMatrixComputer);
                cameraModel.setName(camera.getName());

                cameraModel.setNodeModel(nodeModel);

                final String nodeName = Optionals.of(node.getName(), "node" + i);
                final String cameraName = Optionals.of(camera.getName(), "camera" + cameraIndex);
                final String instanceName = nodeName + "." + cameraName;
                cameraModel.setInstanceName(instanceName);

                this.cameraModels.add(cameraModel);
            }
        }
    }

    /**
     * For each mesh that is instantiated in a node, call
     * {@link #instantiateMaterialModels(Mesh, MeshModel, int)}
     */
    private void instantiateMaterialModels() {
        final List<Node> nodes = Optionals.of(this.gltf.getNodes());
        final List<Mesh> meshes = Optionals.of(this.gltf.getMeshes());
        for (int i = 0; i < nodes.size(); i++) {
            final Node node = nodes.get(i);

            final Integer meshIndex = node.getMesh();

            if (meshIndex != null) {
                final MeshModel meshModel = this.meshModels.get(meshIndex);

                int numJoints = 0;
                final Integer skinIndex = node.getSkin();
                if (skinIndex != null) {
                    final SkinModel skinModel = this.skinModels.get(skinIndex);
                    numJoints = skinModel.getJoints().size();
                }
                final Mesh mesh = meshes.get(meshIndex);
                this.instantiateMaterialModels(mesh, meshModel, numJoints);
            }
        }
    }

    /**
     * Create the {@link MaterialModel} instances that are required for
     * rendering the {@link MeshPrimitiveModel} instances of the given
     * {@link MeshModel}, based on the corresponding {@link MeshPrimitive}
     * and the given number of joints.
     * 
     * @param mesh      The {@link Mesh}
     * @param meshModel The {@link MeshModel}
     * @param numJoints The number of joints
     */
    private void instantiateMaterialModels(final Mesh mesh, final MeshModel meshModel,
            final int numJoints) {
        final List<MeshPrimitive> meshPrimitives = mesh.getPrimitives();
        final List<MeshPrimitiveModel> meshPrimitiveModels = meshModel.getMeshPrimitiveModels();

        for (int i = 0; i < meshPrimitives.size(); i++) {
            final MeshPrimitive meshPrimitive = meshPrimitives.get(i);
            final DefaultMeshPrimitiveModel meshPrimitiveModel =
                    (DefaultMeshPrimitiveModel) meshPrimitiveModels.get(i);

            Material material = null;
            final Integer materialIndex = meshPrimitive.getMaterial();
            if (materialIndex == null) {
                material = Materials.createDefaultMaterial();
            } else {
                material = this.gltf.getMaterials().get(materialIndex);
            }
            final DefaultMaterialModel materialModel =
                    this.materialModelHandler.createMaterialModel(material, numJoints);
            materialModel.setName(material.getName());

            meshPrimitiveModel.setMaterialModel(materialModel);
            this.materialModels.add(materialModel);
        }
    }

    @Override
    public List<AccessorModel> getAccessorModels() {
        return Collections.unmodifiableList(this.accessorModels);
    }

    @Override
    public List<AnimationModel> getAnimationModels() {
        return Collections.unmodifiableList(this.animationModels);
    }

    @Override
    public List<BufferModel> getBufferModels() {
        return Collections.unmodifiableList(this.bufferModels);
    }

    @Override
    public List<BufferViewModel> getBufferViewModels() {
        return Collections.unmodifiableList(this.bufferViewModels);
    }

    @Override
    public List<CameraModel> getCameraModels() {
        return Collections.unmodifiableList(this.cameraModels);
    }

    @Override
    public List<ImageModel> getImageModels() {
        return Collections.unmodifiableList(this.imageModels);
    }

    @Override
    public List<MaterialModel> getMaterialModels() {
        return Collections.unmodifiableList(this.materialModels);
    }

    @Override
    public List<NodeModel> getNodeModels() {
        return Collections.unmodifiableList(this.nodeModels);
    }

    @Override
    public List<SceneModel> getSceneModels() {
        return Collections.unmodifiableList(this.sceneModels);
    }

    @Override
    public List<TextureModel> getTextureModels() {
        return Collections.unmodifiableList(this.textureModels);
    }

    /**
     * Returns the raw glTF object, which is a
     * {@link de.javagl.jgltf.impl.v1.GlTF version 2.0 glTF}.<br>
     * <br>
     * This method should usually not be called by clients. It may be
     * omitted in future versions.
     * 
     * @return The glTF object
     */
    public GlTF getGltf() {
        return this.gltf;
    }

}
