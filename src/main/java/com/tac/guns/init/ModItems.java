package com.tac.guns.init;

import com.tac.guns.GunMod;
import com.tac.guns.Reference;
import com.tac.guns.common.GunModifiers;
import com.tac.guns.item.AmmoItem;
import com.tac.guns.item.ArmorPlateItem;
import com.tac.guns.item.BarrelItem;
import com.tac.guns.item.ExtendedMagItem;
import com.tac.guns.item.GunSkinItem;
import com.tac.guns.item.IrDeviceItem;
import com.tac.guns.item.PistolBarrelItem;
import com.tac.guns.item.PistolScopeItem;
import com.tac.guns.item.ScopeItem;
import com.tac.guns.item.SideRailItem;
import com.tac.guns.item.StockItem;
import com.tac.guns.item.UnderBarrelItem;
import com.tac.guns.item.attachment.impl.Barrel;
import com.tac.guns.item.attachment.impl.ExtendedMag;
import com.tac.guns.item.attachment.impl.GunSkin;
import com.tac.guns.item.attachment.impl.IrDevice;
import com.tac.guns.item.attachment.impl.Scope;
import com.tac.guns.item.attachment.impl.ScopeZoomData;
import com.tac.guns.item.attachment.impl.SideRail;
import com.tac.guns.item.attachment.impl.Stock;
import com.tac.guns.item.attachment.impl.UnderBarrel;
import com.tac.guns.item.transition.GunItem;
import com.tac.guns.item.transition.TimelessAmmoItem;
import com.tac.guns.item.transition.TimelessLRFAmmoItem;
import com.tac.guns.item.transition.TimelessPistolGunItem;
import com.tac.guns.item.transition.TimelessRFAmmoItem;
import com.tac.guns.item.transition.TimelessSGAmmoItem;
import com.tac.guns.item.transition.grenades.BaseballGrenadeItem;
import com.tac.guns.item.transition.grenades.LightGrenadeItem;
import com.tac.guns.item.transition.wearables.ArmorRigItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);


    /* Guns */
    public static final RegistryObject<GunItem> M1911 = ModItems.REGISTER.register("m1911",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.M1911_MOD));

    public static final RegistryObject<GunItem> AK47 = ModItems.REGISTER.register("ak47",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.AK47_MOD));

    public static final RegistryObject<GunItem> M60 = ModItems.REGISTER.register("m60",
            () -> new GunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL),
                    GunModifiers.M60_MOD));

    public static final RegistryObject<Item> GLOCK_17 = ModItems.REGISTER.register("glock_17",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.GLOCK17_MOD));

    public static final RegistryObject<Item> DP28 = ModItems.REGISTER.register("dp28",
            () -> new GunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL),
                    GunModifiers.DP28_MOD));

    public static final RegistryObject<Item> STI2011 = ModItems.REGISTER.register("sti2011",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.STI2011_MOD));

    public static final RegistryObject<Item> M92FS = ModItems.REGISTER.register("m92fs",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    public static final RegistryObject<GunItem> VECTOR45 = ModItems.REGISTER.register("vector45",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.VECTOR_MOD));

    public static final RegistryObject<Item> MICRO_UZI = ModItems.REGISTER.register("micro_uzi",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG)));

    public static final RegistryObject<Item> M4 = ModItems.REGISTER.register("m4",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.M4A1_MOD));

    public static final RegistryObject<Item> DB_SHORT = ModItems.REGISTER.register("db_short",
            () -> new GunItem(properties -> properties.tab(GunMod.SHOTGUN)));

    public static final RegistryObject<GunItem> M24 = ModItems.REGISTER.register("m24",
            () -> new GunItem(properties -> properties.tab(GunMod.SNIPER)));

    public static final RegistryObject<Item> QBZ_95 = ModItems.REGISTER.register("qbz_95",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE)));

    public static final RegistryObject<Item> AA_12 = ModItems.REGISTER.register("aa_12",
            () -> new GunItem(properties -> properties.tab(GunMod.SHOTGUN),
                    GunModifiers.AA_12_MOD));

    public static final RegistryObject<Item> M870_CLASSIC = ModItems.REGISTER.register(
            "m870_classic",
            () -> new GunItem(properties -> properties.tab(GunMod.SHOTGUN), GunModifiers.M870_MOD));// properties.tab(GunMod.SHOTGUN)));

    public static final RegistryObject<Item> M1A1_SMG = ModItems.REGISTER.register("m1a1_smg",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG)));

    public static final RegistryObject<Item> MK14 = ModItems.REGISTER.register("mk14",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE)));

    public static final RegistryObject<Item> DEAGLE_357 = ModItems.REGISTER.register("deagle_357",
            () -> new GunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.DEAGLE50_MOD));

    public static final RegistryObject<Item> HK_MP5A5 = ModItems.REGISTER.register("hk_mp5a5",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.MP5A5_MOD));

    public static final RegistryObject<Item> GLOCK_18 = ModItems.REGISTER.register("glock_18",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    public static final RegistryObject<Item> CZ75 = ModItems.REGISTER.register("cz75",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    public static final RegistryObject<Item> CZ75_AUTO = ModItems.REGISTER.register("cz75_auto",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    public static final RegistryObject<Item> HK416_A5 = ModItems.REGISTER.register("hk416_a5",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.HK416_MOD));

    public static final RegistryObject<Item> TYPE81_X = ModItems.REGISTER.register("type81_x",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE),
                    GunModifiers.TYPE81x_MOD));

    public static final RegistryObject<Item> MP7 = ModItems.REGISTER.register("mp7",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.MP7_MOD));

    public static final RegistryObject<Item> M82A2 = ModItems.REGISTER.register("m82a2",
            () -> new GunItem(properties -> properties.tab(GunMod.SNIPER), GunModifiers.M82A2_MOD));

    public static final RegistryObject<Item> AI_AWP = ModItems.REGISTER.register("ai_awp",
            () -> new GunItem(properties -> properties.tab(GunMod.SNIPER), GunModifiers.AIAWP_MOD));

    public static final RegistryObject<GunItem> RPG7 = ModItems.REGISTER.register("rpg7",
            () -> new GunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL),
                    GunModifiers.RPG7_MOD));

    public static final RegistryObject<Item> RPK = ModItems.REGISTER.register("rpk",
            () -> new GunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL)));

    public static final RegistryObject<Item> FN_FAL = ModItems.REGISTER.register("fn_fal",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE)));

    public static final RegistryObject<Item> SIG_MCX_SPEAR = ModItems.REGISTER
            .register("sig_mcx_spear", () -> new GunItem(properties -> properties.tab(GunMod.RIFLE),
                    GunModifiers.SIG_MCX_SPEAR_MOD));

    public static final RegistryObject<Item> MP9 = ModItems.REGISTER.register("mp9",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.MP9_MOD));

    public static final RegistryObject<Item> SKS_TACTICAL = ModItems.REGISTER
            .register("sks_tactical", () -> new GunItem(properties -> properties.tab(GunMod.RIFLE),
                    GunModifiers.SKS_TAC_MOD));

    public static final RegistryObject<Item> M1014 = ModItems.REGISTER.register("m1014",
            () -> new GunItem(properties -> properties.tab(GunMod.SHOTGUN),
                    GunModifiers.M1014_MOD));

    public static final RegistryObject<Item> M249 = ModItems.REGISTER.register("m249",
            () -> new GunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL),
                    GunModifiers.M249_MOD));

    public static final RegistryObject<Item> MK23 = ModItems.REGISTER.register("mk23",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    public static final RegistryObject<Item> QBZ_191 = ModItems.REGISTER.register("qbz_191",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE),
                    GunModifiers.QBZ_191_MOD));

    public static final RegistryObject<Item> M16A4 = ModItems.REGISTER.register("m16a4",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.M16A4_MOD));

    public static final RegistryObject<Item> SCAR_H = ModItems.REGISTER.register("scar_h",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.SCAR_H_MOD));

    public static final RegistryObject<Item> SCAR_L = ModItems.REGISTER.register("scar_l",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.SCAR_L_MOD));

    public static final RegistryObject<Item> MK47 = ModItems.REGISTER.register("mk47",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.MK47_MOD));

    public static final RegistryObject<Item> SPR_15 = ModItems.REGISTER.register("spr15",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.SPR_15_MOD));

    public static final RegistryObject<Item> TTI_G34 = ModItems.REGISTER.register("tti_g34",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.TTI34_MOD));

    public static final RegistryObject<Item> MK18_MOD1 = ModItems.REGISTER.register("mk18_mod1",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE),
                    GunModifiers.MK18_MOD1_MOD));

    public static final RegistryObject<Item> UDP_9 = ModItems.REGISTER.register("udp_9",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.UDP_9_MOD));

    public static final RegistryObject<Item> SCAR_MK20 = ModItems.REGISTER.register("scar_mk20",
            () -> new GunItem(properties -> properties.tab(GunMod.SNIPER),
                    GunModifiers.SCAR_MK20_MOD));

    public static final RegistryObject<Item> HK_G3 = ModItems.REGISTER.register("hk_g3",
            () -> new GunItem(properties -> properties.tab(GunMod.RIFLE), GunModifiers.HK_G3_MOD));

    public static final RegistryObject<Item> TIMELESS_50 = ModItems.REGISTER.register("timeless_50",
            () -> new GunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.STI2011_MOD));

    public static final RegistryObject<Item> COLT_PYTHON = ModItems.REGISTER.register("colt_python",
            () -> new GunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.COLT_PYTHON_MOD));

    public static final RegistryObject<Item> P90 = ModItems.REGISTER.register("p90",
            () -> new GunItem(properties -> properties.tab(GunMod.SMG), GunModifiers.P90_MOD));

    public static final RegistryObject<Item> TEC_9 = ModItems.REGISTER.register("tec_9",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL),
                    GunModifiers.TEC_9_MOD));

    public static final RegistryObject<Item> UZI = ModItems.REGISTER.register("uzi",
            () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.SMG),
                    GunModifiers.UZI_MOD));

    public static final RegistryObject<Item> MRAD = ModItems.REGISTER.register("mrad",
            () -> new GunItem(properties -> properties.tab(GunMod.SNIPER), GunModifiers.MRAD_MOD));

    public static final RegistryObject<Item> DEVELOPMENT_SLOW_BULLET = ModItems.REGISTER
            .register("devgun1", () -> new GunItem(properties -> properties.tab(GunMod.RIFLE)));

    // public static final RegistryObject<TimelessGunItem> M1894 = REGISTER.register("m1894",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SNIPER)));

    // public static final RegistryObject<Item> M1928 = REGISTER.register("m1928",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SMG)));

    // public static final RegistryObject<Item> MOSIN = REGISTER.register("mosin",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SNIPER)));

    // public static final RegistryObject<Item> M16A1 = REGISTER.register("m16a1",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.RIFLE)));

    // public static final RegistryObject<GunItem> AR_15_HELLMOUTH =
    //         REGISTER.register("ar_15_hellmouth",
    //                 () -> new TimelessGunItem(properties -> properties.tab(GunMod.RIFLE)));

    // public static final RegistryObject<GunItem> MOSBERG590 = REGISTER.register("mosberg590",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SHOTGUN)));

    // public static final RegistryObject<Item> DB_LONG = REGISTER.register("db_long",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SHOTGUN)));

    // public static final RegistryObject<Item> WALTHER_PPK = REGISTER.register("walther_ppk",
    //         () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    // public static final RegistryObject<Item> PPSH_41 = REGISTER.register("ppsh_41",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SMG)));

    // public static final RegistryObject<Item> X95R = REGISTER.register("x95r",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.RIFLE)));

    // public static final RegistryObject<Item> FR_F2 = REGISTER.register("fr_f2",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SNIPER)));

    // public static final RegistryObject<Item> MG3 = REGISTER.register("mg3",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL)));

    // public static final RegistryObject<Item> MG42 = REGISTER.register("mg42",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL)));

    // public static final RegistryObject<Item> AR_10 = REGISTER.register("ar_10",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.RIFLE)));

    // public static final RegistryObject<Item> SPAS_12 = REGISTER.register("spas_12",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SHOTGUN)));

    // public static final RegistryObject<Item> STEN_MK_II = REGISTER.register("sten_mk_ii",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SMG)));

    // public static final RegistryObject<Item> M1873 = REGISTER.register("m1873",
    //         () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    // public static final RegistryObject<Item> VZ61 = REGISTER.register("vz61",
    //         () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    // public static final RegistryObject<Item> QSZ92G1 = REGISTER.register("qsz92g1",
    //         () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    // public static final RegistryObject<Item> PKP_PENCHENBERG = REGISTER.register("pkp_penchenberg",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.HEAVY_MATERIAL)));

    // public static final RegistryObject<Item> SKS = REGISTER.register("sks",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.RIFLE),
    //                 GunModifiers.SKS_MOD));

    // public static final RegistryObject<GunItem> M79 = REGISTER.register("m79",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.EXPLOSIVES),
    //                 GunModifiers.M79_MOD));

    // public static final RegistryObject<GunItem> MGL_40MM = REGISTER.register("mgl_40mm",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.EXPLOSIVES), true,
    //                 GunModifiers.MGL_40MM_MOD));

    // public static final RegistryObject<Item> C96 = REGISTER.register("c96",
    //         () -> new TimelessPistolGunItem(properties -> properties.tab(GunMod.PISTOL)));

    // public static final RegistryObject<Item> STEN_OSS = REGISTER.register("sten_mk_ii_oss",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SMG),
    //                 GunModifiers.STEN_OSS_MOD));

    // public static final RegistryObject<Item> ESPADON = REGISTER.register("espadon",
    //         () -> new TimelessGunItem(properties -> properties.tab(GunMod.SNIPER),
    //                 GunModifiers.ESPADON_MOD));


    /* Ammunition */
    public static final RegistryObject<Item> BULLET_MAGNUM =
            ModItems.REGISTER.register("b_magnum", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_45 =
            ModItems.REGISTER.register("round45", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_46 =
            ModItems.REGISTER.register("46x30", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_50AE =
            ModItems.REGISTER.register("ae50", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_30_WIN =
            ModItems.REGISTER.register("win_30-30", TimelessLRFAmmoItem::new);

    public static final RegistryObject<Item> BULLET_308 =
            ModItems.REGISTER.register("bullet_308", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_556 =
            ModItems.REGISTER.register("nato_556_bullet", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_9 =
            ModItems.REGISTER.register("9mm_round", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_10g =
            ModItems.REGISTER.register("10_gauge_round", TimelessSGAmmoItem::new);

    public static final RegistryObject<Item> BULLET_58x42 =
            ModItems.REGISTER.register("58x42", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_762x25 =
            ModItems.REGISTER.register("762x25", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_762x54 =
            ModItems.REGISTER.register("762x54", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_762x39 =
            ModItems.REGISTER.register("762x39", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_50_BMG =
            ModItems.REGISTER.register("50bmg", TimelessRFAmmoItem::new);

    public static final RegistryObject<Item> BULLET_LAPUA338 =
            ModItems.REGISTER.register("lapua338", TimelessRFAmmoItem::new);

    public static final RegistryObject<Item> BULLET_68 =
            ModItems.REGISTER.register("bullet68", TimelessAmmoItem::new);

    public static final RegistryObject<Item> BULLET_57 =
            ModItems.REGISTER.register("57x28", TimelessAmmoItem::new);

    // public static final RegistryObject<Item> MAGNUM_BULLET =
    //         REGISTER.register("magnumround", TimelessAmmoItem::new);

    // public static final RegistryObject<Item> BULLET_300MAG =
    //         REGISTER.register("762x39", TimelessAmmoItem::new);

    // public static final RegistryObject<Item> GRENADE_40MM =
    //         REGISTER.register("grenade40mm", Timeless40AmmoItem::new);


    /* Explosives */
    public static final RegistryObject<Item> RPG7_MISSILE = ModItems.REGISTER.register(
            "rpg7_missile", () -> new AmmoItem(new Item.Properties().stacksTo(6).tab(GunMod.AMMO)));

    public static final RegistryObject<Item> LIGHT_GRENADE =
            ModItems.REGISTER.register("light_grenade",
                    () -> new LightGrenadeItem(
                            new Item.Properties().stacksTo(8).tab(GunMod.EXPLOSIVES), 25 * 4, 0.95f,
                            1.35f));

    public static final RegistryObject<Item> BASEBALL_GRENADE =
            ModItems.REGISTER.register("baseball_grenade",
                    () -> new BaseballGrenadeItem(
                            new Item.Properties().stacksTo(4).tab(GunMod.EXPLOSIVES), 20 * 7,
                            1.425f, 1.135f));

    /* Scope Attachments */
    public static final RegistryObject<Item> COYOTE_SIGHT =
            ModItems.REGISTER.register("coyote_sight",
                    () -> new ScopeItem(
                            Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)},
                                    2.15F, 0.325, "coyote", GunModifiers.COYOTE_SIGHT_ADS)
                                    .viewFinderOffset(0.415).viewFinderOffsetSpecial(0.415),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> AIMPOINT_T2_SIGHT =
            ModItems.REGISTER.register("aimpoint_t2",
                    () -> new ScopeItem(
                            Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)},
                                    2.635F, 0.325, "aimpoint2", GunModifiers.AIMPOINT_T1_SIGHT_ADS)
                                    .viewFinderOffset(0.39).viewFinderOffsetSpecial(0.39),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> AIMPOINT_T1_SIGHT =
            ModItems.REGISTER.register("aimpoint_t1",
                    () -> new ScopeItem(
                            Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 1.3F,
                                    0.325, "aimpoint1", GunModifiers.AIMPOINT_T1_SIGHT_ADS)
                                    .viewFinderOffset(0.45).viewFinderOffsetSpecial(0.45),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> EOTECH_N_SIGHT = ModItems.REGISTER.register("eotech_n",
            () -> new ScopeItem(
                    Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 2.225F,
                            0.325, "eotechn", GunModifiers.EOTECH_N_SIGHT_ADS)
                            .viewFinderOffset(0.415).viewFinderOffsetSpecial(0.415),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> VORTEX_UH_1 = ModItems.REGISTER.register("vortex_uh_1",
            () -> new ScopeItem(
                    Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 2.525F,
                            0.325, "vortex1", GunModifiers.VORTEX_UH_1_ADS).viewFinderOffset(0.3725)
                            .viewFinderOffsetSpecial(0.3725),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> EOTECH_SHORT_SIGHT = ModItems.REGISTER.register(
            "eotech_short",
            () -> new ScopeItem(
                    Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 2.71F, 0.325,
                            "eotechshort", GunModifiers.EOTECH_SHORT_SIGHT_ADS)
                            .viewFinderOffset(0.455).viewFinderOffsetSpecial(0.455),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> SRS_RED_DOT_SIGHT =
            ModItems.REGISTER.register("srs_red_dot",
                    () -> new ScopeItem(
                            Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)},
                                    2.2675F, 0.325, "srsdot", GunModifiers.SRS_RED_DOT_SIGHT_ADS)
                                    .viewFinderOffset(0.355).viewFinderOffsetSpecial(0.355),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> ACOG_4 = ModItems.REGISTER.register("acog_4x_scope",
            () -> new ScopeItem(
                    Scope.create(
                            new ScopeZoomData[] {new ScopeZoomData(4f, 0.4F, 1.5f),
                                    new ScopeZoomData(2f, 0.4F, 1.5f)},
                            2.325F, 0.21, "acog4x", true, GunModifiers.ACOG_4_ADS)
                            .viewFinderOffset(0.45).viewFinderOffsetDR(0.40)
                            .viewFinderOffsetSpecial(0.425).viewFinderOffsetSpecialDR(0.35),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> QMK152 =
            ModItems.REGISTER
                    .register("qmk152",
                            () -> new ScopeItem(
                                    Scope.create(
                                            new ScopeZoomData[] {
                                                    new ScopeZoomData(3F, 0.4F, -0.4f)},
                                            2.39F, 0.11, "qmk152", true, GunModifiers.QMK152_ADS)
                                            .viewFinderOffset(0.45).viewFinderOffsetDR(0.315)
                                            .viewFinderOffsetSpecial(0.34)
                                            .viewFinderOffsetSpecialDR(0.238),
                                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));
    public static final RegistryObject<Item> ELCAN_DR_14X = ModItems.REGISTER.register("elcan_14x",
            () -> new ScopeItem(
                    Scope.create(
                            new ScopeZoomData[] {new ScopeZoomData(4f, 0.4225F, 2.0f),
                                    new ScopeZoomData(1.2f, 0.333F + 0.016F, 2.0f)},
                            2.45F, 0.23, "elcan14x", true, GunModifiers.ELCAN_DR_14X_ADS)
                            .viewFinderOffset(0.515).viewFinderOffsetDR(0.46)
                            .viewFinderOffsetSpecial(0.435).viewFinderOffsetSpecialDR(0.38),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> VORTEX_LPVO_3_6 = ModItems.REGISTER.register(
            "lpvo_1_6",
            () -> new ScopeItem(
                    Scope.create(
                            new ScopeZoomData[] {new ScopeZoomData(6f, 0.4175F, 2.6f),
                                    new ScopeZoomData(3f, 0.365F, 2.6f)},
                            2.2625F, 0.1725, "vlpvo6", true, GunModifiers.VORTEX_LPVO_1_6_ADS)
                            .viewFinderOffset(0.475).viewFinderOffsetDR(0.375)
                            .viewFinderOffsetSpecial(0.505).viewFinderOffsetSpecialDR(0.355),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));// .viewFinderOffset(0.475),

    // public static final RegistryObject<Item> SLX_2X = REGISTER.register("slx_2x",
    //         () -> new ScopeItem(
    //                 Scope.create(new ScopeZoomData[] {new ScopeZoomData(0.0165F, 0.4185F)}, 2.25F,
    //                         0.1725, "slx2x", GunModifiers.ACOG_4_ADS).viewFinderOffset(0.425)
    //                         .viewFinderOffsetDR(0.4).viewFinderOffsetSpecial(0.375)
    //                         .viewFinderOffsetSpecialDR(0.35),
    //                 new Item.Properties().stacksTo(1).tab(GunMod.GENERAL) // .viewFinderOffset(0.475),
    //         ));


    public static final RegistryObject<Item> STANDARD_6_10x_SCOPE = ModItems.REGISTER.register(
            "8x_scope",
            () -> new ScopeItem(
                    Scope.create(
                            new ScopeZoomData[] {new ScopeZoomData(10f, 0.395F, 4.2f),
                                    new ScopeZoomData(6f, 0.4225F, 4.2f)},
                            2.45F, 0.2125, "gener8x", true, GunModifiers.LONGRANGE_6_10_SCOPE_ADS)
                            .viewFinderOffset(0.595).viewFinderOffsetDR(0.3925)
                            .viewFinderOffsetSpecial(0.465).viewFinderOffsetSpecialDR(0.415),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    /* Pistol-Scopes */
    public static final RegistryObject<Item> MINI_DOT = ModItems.REGISTER.register("mini_dot",
            () -> new PistolScopeItem(
                    Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 1.475F,
                            0.325, "minidot", GunModifiers.MINI_DOT_ADS).viewFinderOffset(0.685)
                            .viewFinderOffsetSpecial(0.685),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> SRO_DOT = ModItems.REGISTER.register("sro_dot",
            () -> new PistolScopeItem(
                    Scope.create(new ScopeZoomData[] {new ScopeZoomData(1.0F, 0.00F)}, 1.615F,
                            0.325, "sro_dot", GunModifiers.MINI_DOT_ADS).viewFinderOffset(0.685)
                            .viewFinderOffsetSpecial(0.685),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> MICRO_HOLO_SIGHT =
    //         REGISTER.register("micro_holo_sight",
    //                 () -> new PistolScopeItem(
    //                         Scope.create(new ScopeZoomData[] {new ScopeZoomData(0.00F, 0.00F)},
    //                                 1.645F, 0.325, "microholo", GunModifiers.MICRO_HOLO_SIGHT_ADS)
    //                                 .viewFinderOffset(0.685).viewFinderOffsetSpecial(0.685),
    //                         new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Barrel Attachments */
    public static final RegistryObject<Item> SILENCER = ModItems.REGISTER.register("silencer",
            () -> new BarrelItem(Barrel.create(8.0F, GunModifiers.TACTICAL_SILENCER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> MUZZLE_BRAKE =
            ModItems.REGISTER.register("muzzle_brake",
                    () -> new BarrelItem(Barrel.create(2.0F, GunModifiers.MUZZLE_BRAKE_MODIFIER),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> MUZZLE_COMPENSATOR =
            ModItems.REGISTER.register("muzzle_compensator",
                    () -> new BarrelItem(
                            Barrel.create(2.0F, GunModifiers.MUZZLE_COMPENSATOR_MODIFIER),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Pistol-Barrel Attachments */
    public static final RegistryObject<Item> PISTOL_SILENCER =
            ModItems.REGISTER.register("pistol_silencer",
                    () -> new PistolBarrelItem(Barrel.create(8.0F, GunModifiers.PISTOL_SILENCER),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Stock Attachments */
    public static final RegistryObject<Item> LIGHT_STOCK = ModItems.REGISTER.register("light_stock",
            () -> new StockItem(Stock.create(GunModifiers.LIGHT_STOCK_MODIFIER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL), false));

    public static final RegistryObject<Item> TACTICAL_STOCK =
            ModItems.REGISTER.register("tactical_stock",
                    () -> new StockItem(Stock.create(GunModifiers.TACTICAL_STOCK_MODIFIER),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL), false));

    public static final RegistryObject<Item> WEIGHTED_STOCK = ModItems.REGISTER.register(
            "weighted_stock", () -> new StockItem(Stock.create(GunModifiers.HEAVY_STOCK_MODIFIER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Under Barrel Attachments */
    public static final RegistryObject<Item> LIGHT_GRIP = ModItems.REGISTER.register("light_grip",
            () -> new UnderBarrelItem(UnderBarrel.create(GunModifiers.LIGHT_GRIP_MODIFIER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> SPECIALISED_GRIP =
            ModItems.REGISTER.register("specialised_grip",
                    () -> new UnderBarrelItem(
                            UnderBarrel.create(GunModifiers.TACTICAL_GRIP_MODIFIER),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Side rail Attachments */
    public static final RegistryObject<Item> BASIC_LASER = ModItems.REGISTER.register("basic_laser",
            () -> new SideRailItem(SideRail.create(GunModifiers.BASIC_LASER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> IR_LASER = ModItems.REGISTER.register("ir_laser",
            () -> new IrDeviceItem(IrDevice.create(GunModifiers.IR_LASER),
                    new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> SMALL_EXTENDED_MAG =
            ModItems.REGISTER.register("small_extended_mag",
                    () -> new ExtendedMagItem(ExtendedMag.create(GunModifiers.SMALL_EXTENDED_MAG),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> MEDIUM_EXTENDED_MAG =
            ModItems.REGISTER.register("medium_extended_mag",
                    () -> new ExtendedMagItem(ExtendedMag.create(GunModifiers.MEDIUM_EXTENDED_MAG),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> LARGE_EXTENDED_MAG =
            ModItems.REGISTER.register("large_extended_mag",
                    () -> new ExtendedMagItem(ExtendedMag.create(GunModifiers.LARGE_EXTENDED_MAG),
                            new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));


    /* Rigs and Armors */
    public static final RegistryObject<Item> ARMOR_RIG = ModItems.REGISTER.register("armor_rig",
            () -> (new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)))
                    .setRigRows(3).setDamageRate(0.6F));

    // public static final RegistryObject<Item> ARMOR_R1 = ModItems.REGISTER.register("armor_r1",
    //         () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> ARMOR_R2 = ModItems.REGISTER.register("armor_r2",
    //         () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> ARMOR_R3 = ModItems.REGISTER.register("armor_r3",
    //         () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> ARMOR_R4 = ModItems.REGISTER.register("armor_r4",
    //         () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> ARMOR_R5 = ModItems.REGISTER.register("armor_r5",
    //         () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> ARMOR_REPAIR_PLATE = ModItems.REGISTER.register(
            "armor_plate_test",
            () -> new ArmorPlateItem(new Item.Properties().stacksTo(4).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> LIGHT_ARMOR = ModItems.REGISTER.register("light_armor",
            () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL))
                    .setRigRows(1).setDamageRate(0.4F));

    public static final RegistryObject<Item> MEDIUM_STEEL_ARMOR =
            ModItems.REGISTER.register("medium_steel_armor",
                    () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL))
                            .setRigRows(1).setDamageRate(0.2F));

    // public static final RegistryObject<Item> CARDBOARD_ARMOR =
    //         REGISTER.register("cardboard_armor",
    //                 () -> new ArmorRigItem(new Item.Properties().stacksTo(1).tab(GunMod.GENERAL))
    //                         .setRigRows(2).setDamageRate(0.8F));

    public static final RegistryObject<Item> LIGHT_ARMOR_REPAIR_PLATE = ModItems.REGISTER.register(
            "light_armor_plate",
            () -> new ArmorPlateItem(new Item.Properties().stacksTo(12).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> MODULE = ModItems.REGISTER.register("module_item",
            () -> new Item(new Item.Properties().stacksTo(3).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> BLANK_SKIN_MOD_LVL1 =
            ModItems.REGISTER.register("blank_skin_mod_lvl1",
                    () -> new Item(new Item.Properties().stacksTo(64).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> UNCOMMON_MATERIAL =
            ModItems.REGISTER.register("uncommon_material",
                    () -> new Item(new Item.Properties().stacksTo(64).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> RARE_MATERIAL =
            ModItems.REGISTER.register("rare_material",
                    () -> new Item(new Item.Properties().stacksTo(48).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> EPIC_MATERIAL =
            ModItems.REGISTER.register("epic_material",
                    () -> new Item(new Item.Properties().stacksTo(32).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> LEGENDARY_MATERIAL =
            ModItems.REGISTER.register("legendary_material",
                    () -> new Item(new Item.Properties().stacksTo(24).tab(GunMod.GENERAL)));

    public static final RegistryObject<Item> ULTIMATE_MATERIAL =
            ModItems.REGISTER.register("ultimate_material",
                    () -> new Item(new Item.Properties().stacksTo(16).tab(GunMod.GENERAL)));

    // public static final RegistryObject<Item> LEGENDARY_CERTIFICATE =
    //         REGISTER.register("legendary_certificate",
    //                 () -> new Item(new Item.Properties().stacksTo(32).tab(GunMod.GENERAL)));
    // public static final RegistryObject<Item> ULTIMATE_CERTIFICATE =
    //         REGISTER.register("ultimate_certificate",
    //                 () -> new Item(new Item.Properties().stacksTo(16).tab(GunMod.GENERAL)));


    /* Skin */
    public static final RegistryObject<Item> SKIN_AK_SPENT_BULLET =
            ModItems.REGISTER.register("skin_ak_spent_bullet",
                    () -> new GunSkinItem(
                            GunSkin.create(new ResourceLocation("skin_ak_spent_bullet")),
                            new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));
    public static final RegistryObject<Item> SKIN_MP9_THUNDER =
            ModItems.REGISTER.register("skin_mp9_thunder",
                    () -> new GunSkinItem(GunSkin.create(new ResourceLocation("skin_mp9_thunder")),
                            new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    // public static final RegistryObject<Item> SKIN_CUSTOM = REGISTER.register("skin_custom",
    //         () -> new GunSkinItem(GunSkin.create((ResourceLocation) null),
    //                 new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));


    /* Common Skin */
    public static final RegistryObject<Item> SKIN_BLACK =
            ModItems.REGISTER.register("skin_black", () -> new GunSkinItem(GunSkin.create("black"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_BLUE =
            ModItems.REGISTER.register("skin_blue", () -> new GunSkinItem(GunSkin.create("blue"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_BROWN =
            ModItems.REGISTER.register("skin_brown", () -> new GunSkinItem(GunSkin.create("brown"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_DARK_BLUE = ModItems.REGISTER
            .register("skin_dark_blue", () -> new GunSkinItem(GunSkin.create("dark_blue"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_DARK_GREEN = ModItems.REGISTER
            .register("skin_dark_green", () -> new GunSkinItem(GunSkin.create("dark_green"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_GRAY =
            ModItems.REGISTER.register("skin_gray", () -> new GunSkinItem(GunSkin.create("gray"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_GREEN =
            ModItems.REGISTER.register("skin_green", () -> new GunSkinItem(GunSkin.create("green"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_JADE =
            ModItems.REGISTER.register("skin_jade", () -> new GunSkinItem(GunSkin.create("jade"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_LIGHT_GRAY = ModItems.REGISTER
            .register("skin_light_gray", () -> new GunSkinItem(GunSkin.create("light_gray"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_MAGENTA = ModItems.REGISTER
            .register("skin_magenta", () -> new GunSkinItem(GunSkin.create("magenta"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_ORANGE = ModItems.REGISTER.register("skin_orange",
            () -> new GunSkinItem(GunSkin.create("orange"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_PINK =
            ModItems.REGISTER.register("skin_pink", () -> new GunSkinItem(GunSkin.create("pink"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_PURPLE = ModItems.REGISTER.register("skin_purple",
            () -> new GunSkinItem(GunSkin.create("purple"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_RAD =
            ModItems.REGISTER.register("skin_red", () -> new GunSkinItem(GunSkin.create("red"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_SAND =
            ModItems.REGISTER.register("skin_sand", () -> new GunSkinItem(GunSkin.create("sand"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

    public static final RegistryObject<Item> SKIN_WHITE =
            ModItems.REGISTER.register("skin_white", () -> new GunSkinItem(GunSkin.create("white"),
                    new Item.Properties().stacksTo(1).tab(GunMod.SKINS)));

}
