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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialNormalTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialOcclusionTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.ShaderModel.ShaderType;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.gl.impl.DefaultProgramModel;
import de.javagl.jgltf.model.gl.impl.DefaultShaderModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueParametersModel;
import de.javagl.jgltf.model.gl.impl.TechniqueStatesModels;
import de.javagl.jgltf.model.impl.DefaultMaterialModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v2.gl.Materials;

/**
 * A class for creating the {@link MaterialModel} instances that are required
 * in a {@link GltfModelV2}. <br>
 * <br>
 * It will lazily create the internal {@link TechniqueModel},
 * {@link ProgramModel} and {@link ShaderModel} instances that
 * are required for rendering.
 */
class MaterialModelHandler {
    /**
     * The logger used in this class
     */
    private static final Logger logger = Logger.getLogger(MaterialModelHandler.class.getName());

    /**
     * The mapping from joint counts to vertex {@link ShaderModel} instances
     */
    private final Map<Integer, ShaderModel> vertexShaderModels;

    /**
     * The fragment {@link ShaderModel}
     */
    private ShaderModel fragmentShaderModel;

    /**
     * The mapping from joint count to {@link ProgramModel} instances
     */
    private final Map<Integer, ProgramModel> programModels;

    /**
     * The mapping from {@link MaterialStructure} descriptions to the
     * matching {@link TechniqueModel} instances
     */
    private final Map<MaterialStructure, TechniqueModel> techniqueModels;

    /**
     * Default constructor
     */
    MaterialModelHandler() {
        this.vertexShaderModels = new LinkedHashMap<Integer, ShaderModel>();
        this.programModels = new LinkedHashMap<Integer, ProgramModel>();
        this.techniqueModels = new LinkedHashMap<MaterialStructure, TechniqueModel>();
    }

    /**
     * Obtain the vertex {@link ShaderModel} for the given number of joints,
     * creating it if necessary
     * 
     * @param numJoints The number of joints
     * @return The {@link ShaderModel}
     */
    private ShaderModel obtainVertexShaderModel(final int numJoints) {
        ShaderModel shaderModel = this.vertexShaderModels.get(numJoints);
        if (shaderModel == null) {
            shaderModel = this.createVertexShaderModel(numJoints);
            this.vertexShaderModels.put(numJoints, shaderModel);
        }
        return shaderModel;
    }

    /**
     * Create the vertex {@link ShaderModel} for the given number of joints
     * 
     * @param numJoints The number of joints
     * @return The {@link ShaderModel}
     */
    private ShaderModel createVertexShaderModel(final int numJoints) {
        String vertexShaderDefines = "";
        if (numJoints > 0) {
            vertexShaderDefines += "#define NUM_JOINTS " + numJoints + "\n";
        }
        final ShaderModel vertexShaderModel =
                MaterialModelHandler.createDefaultShaderModel("pbr.vert",
                        "pbr" + numJoints + ".vert", ShaderType.VERTEX_SHADER, vertexShaderDefines);
        return vertexShaderModel;
    }

    /**
     * Obtain the fragment {@link ShaderModel}, creating it if necessary
     * 
     * @return The {@link ShaderModel}
     */
    private ShaderModel obtainFragmentShaderModel() {
        if (this.fragmentShaderModel == null) {
            this.fragmentShaderModel = MaterialModelHandler.createDefaultShaderModel("pbr.frag",
                    "pbr.frag", ShaderType.FRAGMENT_SHADER, null);
        }
        return this.fragmentShaderModel;
    }

    /**
     * Obtain the {@link ProgramModel} for the given number of joints,
     * creating it if necessary
     * 
     * @param numJoints The number of joints
     * @return The {@link ProgramModel}
     */
    private ProgramModel obtainProgramModel(final int numJoints) {
        ProgramModel programModel = this.programModels.get(numJoints);
        if (programModel == null) {
            programModel = this.createProgramModel(numJoints);
            this.programModels.put(numJoints, programModel);
        }
        return programModel;
    }

    /**
     * Create the vertex {@link ProgramModel} for the given number of joints
     * 
     * @param numJoints The number of joints
     * @return The {@link ProgramModel}
     */
    private ProgramModel createProgramModel(final int numJoints) {
        final ShaderModel vertexShaderModel = this.obtainVertexShaderModel(numJoints);
        final ShaderModel fragmentShaderModel = this.obtainFragmentShaderModel();

        final DefaultProgramModel programModel = new DefaultProgramModel();
        programModel.setVertexShaderModel(vertexShaderModel);
        programModel.setFragmentShaderModel(fragmentShaderModel);

        return programModel;
    }

    /**
     * Obtain the {@link TechniqueModel} for the given
     * {@link MaterialStructure}, creating it if necessary
     * 
     * @param materialStructure The {@link MaterialStructure}
     * @return The {@link TechniqueModel}
     */
    private TechniqueModel obtainTechniqueModel(final MaterialStructure materialStructure) {
        TechniqueModel techniqueModel = this.techniqueModels.get(materialStructure);
        if (techniqueModel == null) {
            techniqueModel = this.createTechniqueModel(materialStructure);
            this.techniqueModels.put(materialStructure, techniqueModel);
        }
        return techniqueModel;
    }

    /**
     * Create the {@link TechniqueModel} for the given
     * {@link MaterialStructure}
     * 
     * @param materialStructure The {@link MaterialStructure}
     * @return The {@link TechniqueModel}
     */
    private TechniqueModel createTechniqueModel(final MaterialStructure materialStructure) {
        final ProgramModel programModel = this.obtainProgramModel(materialStructure.getNumJoints());

        final DefaultTechniqueModel techniqueModel = new DefaultTechniqueModel();
        techniqueModel.setProgramModel(programModel);

        MaterialModelHandler.addParametersForPbrTechnique(techniqueModel, materialStructure);

        final TechniqueStatesModel techniqueStatesModel = TechniqueStatesModels.createDefault();
        techniqueModel.setTechniqueStatesModel(techniqueStatesModel);

        return techniqueModel;
    }

    /**
     * Create a {@link MaterialModel} instance for the given {@link Material}
     * 
     * @param material  The {@link Material}
     * @param numJoints The number of joints
     * @return The {@link MaterialModel}
     */
    DefaultMaterialModel createMaterialModel(final Material material, final int numJoints) {
        final DefaultMaterialModel materialModel = new DefaultMaterialModel();

        final MaterialStructure materialStructure = new MaterialStructure(material, numJoints);
        final TechniqueModel techniqueModel = this.obtainTechniqueModel(materialStructure);
        materialModel.setTechniqueModel(techniqueModel);

        MaterialPbrMetallicRoughness pbrMetallicRoughness = material.getPbrMetallicRoughness();
        if (pbrMetallicRoughness == null) {
            pbrMetallicRoughness = Materials.createDefaultMaterialPbrMetallicRoughness();
        }

        final Map<String, Object> values = new LinkedHashMap<String, Object>();

        if (Boolean.TRUE.equals(material.isDoubleSided())) {
            values.put("isDoubleSided", 1);
        } else {
            values.put("isDoubleSided", 0);
        }

        final TextureInfo baseColorTextureInfo = pbrMetallicRoughness.getBaseColorTexture();
        if (baseColorTextureInfo != null) {
            values.put("hasBaseColorTexture", 1);
            values.put("baseColorTexCoord", materialStructure.getBaseColorTexCoordSemantic());
            values.put("baseColorTexture", baseColorTextureInfo.getIndex());
        } else {
            values.put("hasBaseColorTexture", 0);
        }
        final float[] baseColorFactor = Optionals.of(pbrMetallicRoughness.getBaseColorFactor(),
                pbrMetallicRoughness.defaultBaseColorFactor());
        values.put("baseColorFactor", baseColorFactor);

        final TextureInfo metallicRoughnessTextureInfo =
                pbrMetallicRoughness.getMetallicRoughnessTexture();
        if (metallicRoughnessTextureInfo != null) {
            values.put("hasMetallicRoughnessTexture", 1);
            values.put("metallicRoughnessTexCoord",
                    materialStructure.getMetallicRoughnessTexCoordSemantic());
            values.put("metallicRoughnessTexture", metallicRoughnessTextureInfo.getIndex());
        } else {
            values.put("hasMetallicRoughnessTexture", 0);
        }
        final float metallicFactor = Optionals.of(pbrMetallicRoughness.getMetallicFactor(),
                pbrMetallicRoughness.defaultMetallicFactor());
        values.put("metallicFactor", metallicFactor);

        final float roughnessFactor = Optionals.of(pbrMetallicRoughness.getRoughnessFactor(),
                pbrMetallicRoughness.defaultRoughnessFactor());
        values.put("roughnessFactor", roughnessFactor);

        final MaterialNormalTextureInfo normalTextureInfo = material.getNormalTexture();
        if (normalTextureInfo != null) {
            values.put("hasNormalTexture", 1);
            values.put("normalTexCoord", materialStructure.getNormalTexCoordSemantic());
            values.put("normalTexture", normalTextureInfo.getIndex());

            final float normalScale =
                    Optionals.of(normalTextureInfo.getScale(), normalTextureInfo.defaultScale());
            values.put("normalScale", normalScale);
        } else {
            values.put("hasNormalTexture", 0);
            values.put("normalScale", 1.0);
        }

        final MaterialOcclusionTextureInfo occlusionTextureInfo = material.getOcclusionTexture();
        if (occlusionTextureInfo != null) {
            values.put("hasOcclusionTexture", 1);
            values.put("occlusionTexCoord", materialStructure.getOcclusionTexCoordSemantic());
            values.put("occlusionTexture", occlusionTextureInfo.getIndex());

            final float occlusionStrength = Optionals.of(occlusionTextureInfo.getStrength(),
                    occlusionTextureInfo.defaultStrength());
            values.put("occlusionStrength", occlusionStrength);
        } else {
            values.put("hasOcclusionTexture", 0);

            // TODO Should this really be 1.0?
            values.put("occlusionStrength", 0.0);
        }

        final TextureInfo emissiveTextureInfo = material.getEmissiveTexture();
        if (emissiveTextureInfo != null) {
            values.put("hasEmissiveTexture", 1);
            values.put("emissiveTexCoord", materialStructure.getEmissiveTexCoordSemantic());
            values.put("emissiveTexture", emissiveTextureInfo.getIndex());
        } else {
            values.put("hasEmissiveTexture", 0);
        }

        final float[] emissiveFactor =
                Optionals.of(material.getEmissiveFactor(), material.defaultEmissiveFactor());
        values.put("emissiveFactor", emissiveFactor);

        final float lightPosition[] = {-800, 500, 500};
        values.put("lightPosition", lightPosition);

        materialModel.setValues(values);

        return materialModel;
    }

    /**
     * Create a default {@link ShaderModel} instance with the given URI
     * string and type, by reading the resource that is identified with
     * the given name. If the specified resource cannot be read, then
     * an error message will be printed and the returned shader model
     * will not contain any data. This method is only intended for
     * internal use!
     * 
     * @param resourceName The name of the resource to read the source code from
     * @param uriString    The URI string
     * @param shaderType   The shader type
     * @param defines      An optional string containing lines of code that
     *                     will be prefixed to the shader code, and which will
     *                     usually
     *                     contain preprocessor definitions
     * @return The {@link ShaderModel}
     */
    private static DefaultShaderModel createDefaultShaderModel(final String resourceName,
            final String uriString, final ShaderType shaderType, final String defines) {
        final DefaultShaderModel shaderModel = new DefaultShaderModel(uriString, shaderType);
        try (InputStream inputStream =
                MaterialModelHandler.class.getResourceAsStream("/" + resourceName)) {
            final byte[] data = IO.readStream(inputStream);
            final String basicShaderString = new String(data);
            String fullShaderString = basicShaderString;
            if (defines != null) {
                fullShaderString = defines + "\n" + basicShaderString;
            }
            final ByteBuffer shaderData = Buffers.create(fullShaderString.getBytes());
            shaderModel.setShaderData(shaderData);
        } catch (final IOException e) {
            MaterialModelHandler.logger.log(Level.SEVERE, "Could not read shader source code", e);
        }
        return shaderModel;
    }

    /**
     * Add all {@link TechniqueParametersModel} instances for PBR techniques
     * to the given {@link TechniqueModel}
     * 
     * @param techniqueModel    The {@link TechniqueModel}
     * @param materialStructure The {@link MaterialStructure} of the material
     *                          for which the {@link TechniqueModel} is intended
     */
    private static void addParametersForPbrTechnique(final DefaultTechniqueModel techniqueModel,
            final MaterialStructure materialStructure) {
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_position", "position",
                GltfConstants.GL_FLOAT_VEC4, 1, "POSITION");
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_normal", "normal",
                GltfConstants.GL_FLOAT_VEC4, 1, "NORMAL");
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_tangent", "tangent",
                GltfConstants.GL_FLOAT_VEC4, 1, "TANGENT");

        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_baseColorTexCoord",
                "baseColorTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getBaseColorTexCoordSemantic());
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_metallicRoughnessTexCoord",
                "metallicRoughnessTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getMetallicRoughnessTexCoordSemantic());
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_normalTexCoord",
                "normalTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getNormalTexCoordSemantic());
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_occlusionTexCoord",
                "occlusionTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getOcclusionTexCoordSemantic());
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_emissiveTexCoord",
                "emissiveTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getEmissiveTexCoordSemantic());

        MaterialModelHandler.addUniformParameters(techniqueModel, "u_modelViewMatrix",
                "modelViewMatrix", GltfConstants.GL_FLOAT_MAT4, 1, "MODELVIEW");
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_projectionMatrix",
                "projectionMatrix", GltfConstants.GL_FLOAT_MAT4, 1, "PROJECTION");
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_normalMatrix", "normalMatrix",
                GltfConstants.GL_FLOAT_MAT3, 1, "MODELVIEWINVERSETRANSPOSE");

        MaterialModelHandler.addUniformParameters(techniqueModel, "u_isDoubleSided",
                "isDoubleSided", GltfConstants.GL_INT, 1, null);

        MaterialModelHandler.addUniformParameters(techniqueModel, "u_baseColorTexture",
                "baseColorTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_metallicRoughnessTexture",
                "metallicRoughnessTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_normalTexture",
                "normalTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_occlusionTexture",
                "occlusionTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_emissiveTexture",
                "emissiveTexture", GltfConstants.GL_SAMPLER_2D, 1, null);

        MaterialModelHandler.addUniformParameters(techniqueModel, "u_hasBaseColorTexture",
                "hasBaseColorTexture", GltfConstants.GL_INT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_hasMetallicRoughnessTexture",
                "hasMetallicRoughnessTexture", GltfConstants.GL_INT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_hasNormalTexture",
                "hasNormalTexture", GltfConstants.GL_INT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_hasOcclusionTexture",
                "hasOcclusionTexture", GltfConstants.GL_INT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_hasEmissiveTexture",
                "hasEmissiveTexture", GltfConstants.GL_INT, 1, null);

        MaterialModelHandler.addUniformParameters(techniqueModel, "u_baseColorFactor",
                "baseColorFactor", GltfConstants.GL_FLOAT_VEC4, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_metallicFactor",
                "metallicFactor", GltfConstants.GL_FLOAT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_roughnessFactor",
                "roughnessFactor", GltfConstants.GL_FLOAT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_normalScale", "normalScale",
                GltfConstants.GL_FLOAT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_occlusionStrength",
                "occlusionStrength", GltfConstants.GL_FLOAT, 1, null);
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_emissiveFactor",
                "emissiveFactor", GltfConstants.GL_FLOAT_VEC3, 1, null);

        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_joint", "joint",
                GltfConstants.GL_FLOAT_VEC4, 1, "JOINTS_0");
        MaterialModelHandler.addAttributeParameters(techniqueModel, "a_weight", "weight",
                GltfConstants.GL_FLOAT_VEC4, 1, "WEIGHTS_0");

        if (materialStructure.getNumJoints() > 0) {
            MaterialModelHandler.addUniformParameters(techniqueModel, "u_jointMat", "jointMat",
                    GltfConstants.GL_FLOAT_MAT4, materialStructure.getNumJoints(), "JOINTMATRIX");
        }

        // TODO Preliminary uniform for a single point light
        MaterialModelHandler.addUniformParameters(techniqueModel, "u_lightPosition",
                "lightPosition", GltfConstants.GL_FLOAT_VEC3, 1, null);

    }

    /**
     * Add the specified attribute to the given model
     * 
     * @param techniqueModel The {@link TechniqueModel}
     * @param attributeName  The attribute name
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addAttributeParameters(final DefaultTechniqueModel techniqueModel,
            final String attributeName, final String parameterName, final int type, final int count,
            final String semantic) {
        techniqueModel.addAttribute(attributeName, parameterName);
        MaterialModelHandler.addParameters(techniqueModel, parameterName, type, count, semantic);
    }

    /**
     * Add the specified uniform to the given model
     * 
     * @param techniqueModel The {@link TechniqueModel}
     * @param uniformName    The uniform name
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addUniformParameters(final DefaultTechniqueModel techniqueModel,
            final String uniformName, final String parameterName, final int type, final int count,
            final String semantic) {
        techniqueModel.addUniform(uniformName, parameterName);
        MaterialModelHandler.addParameters(techniqueModel, parameterName, type, count, semantic);
    }

    /**
     * Add a {@link TechniqueParametersModel} with the given parameters to
     * the given {@link TechniqueModel}
     * 
     * @param techniqueModel The {@link TechniqueModel}
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addParameters(final DefaultTechniqueModel techniqueModel,
            final String parameterName, final int type, final int count, final String semantic) {
        final Object value = null;
        final NodeModel nodeModel = null;
        final TechniqueParametersModel techniqueParametersModel =
                new DefaultTechniqueParametersModel(type, count, semantic, value, nodeModel);
        techniqueModel.addParameter(parameterName, techniqueParametersModel);
    }

}
