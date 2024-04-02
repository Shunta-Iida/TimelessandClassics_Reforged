package com.tac.guns.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Material.Builder;
import net.minecraft.world.level.material.MaterialColor;

public class FlashLightBlock extends AirBlock {
    public static final Material flashLightBlock;

    public FlashLightBlock() {
        super(Properties.of(FlashLightBlock.flashLightBlock).noCollission().noDrops().air()
                .instabreak().lightLevel(p_235470_0_ -> 15));
    }

    public int getLightValue(final BlockState state, final BlockGetter world, final BlockPos pos) {
        return 15;
    }

    public boolean hasTileEntity(final BlockState state) {
        return true;
    }

    static {
        flashLightBlock = (new Builder(MaterialColor.NONE)).noCollider().nonSolid().build();
    }
}
