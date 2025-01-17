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

import java.util.Objects;

import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.v2.gl.Materials;

/**
 * A simple (package-private!) class describing the structure of a material.
 * It is mainly used as a key for map lookups, to decide whether for a
 * certain structure of a material a new technique is required.
 */
class MaterialStructure {
    /**
     * The base color texture coordinates semantic string
     */
    private final String baseColorTexCoordSemantic;

    /**
     * The metallic-roughness texture coordinates semantic string
     */
    private final String metallicRoughnessTexCoordSemantic;

    /**
     * The normal texture coordinates semantic string
     */
    private final String normalTexCoordSemantic;

    /**
     * The occlusion texture coordinates semantic string
     */
    private final String occlusionTexCoordSemantic;

    /**
     * The emissive texture coordinates semantic string
     */
    private final String emissiveTexCoordSemantic;

    /**
     * The number of joints
     */
    private final int numJoints;

    /**
     * Default constructor
     * 
     * @param material  The {@link Material}
     * @param numJoints The number of joints
     */
    MaterialStructure(final Material material, final int numJoints) {
        MaterialPbrMetallicRoughness pbrMetallicRoughness = material.getPbrMetallicRoughness();
        if (pbrMetallicRoughness == null) {
            pbrMetallicRoughness = Materials.createDefaultMaterialPbrMetallicRoughness();
        }

        this.baseColorTexCoordSemantic =
                MaterialStructure.getTexCoordSemantic(pbrMetallicRoughness.getBaseColorTexture());
        this.metallicRoughnessTexCoordSemantic = MaterialStructure
                .getTexCoordSemantic(pbrMetallicRoughness.getMetallicRoughnessTexture());
        this.normalTexCoordSemantic =
                MaterialStructure.getTexCoordSemantic(material.getNormalTexture());
        this.occlusionTexCoordSemantic =
                MaterialStructure.getTexCoordSemantic(material.getOcclusionTexture());
        this.emissiveTexCoordSemantic =
                MaterialStructure.getTexCoordSemantic(material.getEmissiveTexture());

        this.numJoints = numJoints;
    }

    /**
     * Obtain the <code>TEXCOORD_n</code> semantic string based on the given
     * texture info, defaulting to <code>TEXCOORD_0</code>
     * 
     * @param textureInfo The optional texture info
     * @return The string
     */
    private static String getTexCoordSemantic(final TextureInfo textureInfo) {
        if (textureInfo == null) {
            return "TEXCOORD_0";
        }
        final int texCoord = Optionals.of(textureInfo.getTexCoord(), textureInfo.defaultTexCoord());
        return "TEXCOORD_" + texCoord;
    }

    /**
     * Returns the texture coordinates semantic string
     * 
     * @return The semantic string
     */
    String getBaseColorTexCoordSemantic() {
        return this.baseColorTexCoordSemantic;
    }

    /**
     * Returns the texture coordinates semantic string
     * 
     * @return The semantic string
     */
    String getMetallicRoughnessTexCoordSemantic() {
        return this.metallicRoughnessTexCoordSemantic;
    }

    /**
     * Returns the texture coordinates semantic string
     * 
     * @return The semantic string
     */
    String getNormalTexCoordSemantic() {
        return this.normalTexCoordSemantic;
    }

    /**
     * Returns the texture coordinates semantic string
     * 
     * @return The semantic string
     */
    String getOcclusionTexCoordSemantic() {
        return this.occlusionTexCoordSemantic;
    }

    /**
     * Returns the texture coordinates semantic string
     * 
     * @return The semantic string
     */
    String getEmissiveTexCoordSemantic() {
        return this.emissiveTexCoordSemantic;
    }

    /**
     * Returns the number of joints
     * 
     * @return The number of joints
     */
    int getNumJoints() {
        return this.numJoints;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.baseColorTexCoordSemantic, this.metallicRoughnessTexCoordSemantic,
                this.normalTexCoordSemantic, this.occlusionTexCoordSemantic,
                this.emissiveTexCoordSemantic, this.numJoints);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        final MaterialStructure other = (MaterialStructure) object;
        if (!Objects.equals(this.baseColorTexCoordSemantic, other.baseColorTexCoordSemantic)) {
            return false;
        }
        if (!Objects.equals(this.metallicRoughnessTexCoordSemantic,
                other.metallicRoughnessTexCoordSemantic)) {
            return false;
        }
        if (!Objects.equals(this.normalTexCoordSemantic, other.normalTexCoordSemantic)) {
            return false;
        }
        if (!Objects.equals(this.occlusionTexCoordSemantic, other.occlusionTexCoordSemantic)) {
            return false;
        }
        if (!Objects.equals(this.emissiveTexCoordSemantic, other.emissiveTexCoordSemantic)) {
            return false;
        }
        if (this.numJoints != other.numJoints) {
            return false;
        }
        return true;
    }

}
