package com.tac.guns.init;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.block.FlashLightBlock;
import com.tac.guns.block.WorkbenchBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> WORKBENCH = ModBlocks.register("workbench",
            () -> new WorkbenchBlock(Block.Properties.of(Material.METAL).strength(1.5F)) {
                /*
                 * @Override
                 * public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
                 * {
                 * Block.spawnAsEntity((World)
                 * worldIn,pos,this.getBlock().getItem(worldIn,pos,state));
                 * }
                 */
            }, true);
    /*
     * public static final RegistryObject<UpgradeBenchBlock> UPGRADE_BENCH =
     * register("upgrade_bench", () -> new
     * UpgradeBenchBlock(Block.Properties.of(Material.METAL).strength(3F))
     * {
     *//*
                       * @Override
                       * public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
                       * {
                       * Block.spawnAsEntity((World) worldIn,pos,
                       * this.getBlock().getItem(worldIn,pos,state));
                       * }
                       *//*
                                         * },true);
                                         */
    public static final RegistryObject<Block> FLASHLIGHT_BLOCK =
            ModBlocks.register("flashlight", FlashLightBlock::new, null);

    private static <T extends Block> RegistryObject<T> register(final String id,
            final Supplier<T> blockSupplier, final boolean grouped) {
        return ModBlocks.register(id, blockSupplier,
                block1 -> new BlockItem(block1, grouped ? new Item.Properties().tab(GunMod.GENERAL)
                        : new Item.Properties().tab(CreativeModeTab.TAB_SEARCH)));
    }

    public static void init() {
    };

    private static <T extends Block> RegistryObject<T> register(final String id,
            final Supplier<T> blockSupplier, @Nullable final Function<T, BlockItem> supplier) {
        final RegistryObject<T> registryObject = ModBlocks.REGISTER.register(id, blockSupplier);
        if (supplier != null) {
            ModItems.REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }
}
