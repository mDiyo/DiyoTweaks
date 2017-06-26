package mdiyo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "DiyoTweaks", name = "DiyoTweaks", version = "1.6.4_1.0", dependencies = "after:MineFactoryReloaded")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "DiyoTweaks" }, packetHandler = mdiyo.PacketHandler.class)
public class DiyoTweaks
{
    @SidedProxy(clientSide = "mdiyo.ClientProxy", serverSide = "mdiyo.CommonProxy")
    public static mdiyo.CommonProxy proxy;

    /* TODO:
     * 
     * Good:
     * Kill all achievements
     * 2x2 trees
     * Right-click crops to harvest
     * 
     * Decent:
     * Skeleton strafing
     * Skeleton Panic AI change to pulling out sword
     * Zombies with bows, skeletons with swords
     * Activate jukebox with redstone
     */
    Random random = new Random();

    public static boolean overrideHungerHud = false; //Should be false until set

    //Crosshair
    public static boolean[] crosshairBlacklist = new boolean[32000];
    public static boolean[] rangedCrosshair = new boolean[32000];

    //Armor
    public static int[] woodArmor;
    public static int[] stoneArmor;
    public static int[] ironArmor;
    public static int[] diamondArmor;
    public static int[] goldArmor;

    //Hunger
    public static boolean disableHunger = false;
    public static int maxHungerHeal = 8;
    public static boolean overrideArmorHud = true;

    public static boolean tweakHunger = false;
    public static int maxFoodLevel = 20;
    public static int regenerationThreshold = 18;
    public static boolean alwaysHungerRegenerate = true;
    public static float foodExhaustion = 1.0f;

    //Gameplay
    public static boolean alterStackSizes = true;
    public static boolean addNametagRecipe = true;
    public static boolean disableExp = false;
    public static boolean nastyFlesh = true;
    public static boolean alwaysDropExp = false;
    public static boolean nerfFoodStackSize = false;
    public static boolean stackableSoup = true;
    public static int sugarCaneHeight = 3;
    public static int poisonTime = 50;

    //Mobs
    public static boolean animalBones = true;
    public static boolean leather = true;
    public static boolean creeperBehavior = true;
    public static boolean mounts = true;
    public static boolean endermenDontPickUpBlocks = true;
    public static boolean spawnZombieReinforcements = false;
    public static boolean keepBabyZombies = false;
    public static boolean disableZombieFire = true;

    //Classic
    public static boolean makeGuudFire = true;
    public static boolean feathers = true;
    public static boolean revertTNT = false;
    public static boolean fleshToFeathers = false;
    public static boolean changeArmorCalculations = false;

    //Render
    public static boolean fancyGrass = true;
    public static boolean disableExpBar = false;
    public static boolean removeVoidParticles = true;
    public static boolean removeVoidFog = true;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        File file = event.getSuggestedConfigurationFile();
        Configuration config = new Configuration(file);
        disableHunger = true;//config.get("Hunger Disable", "Disable Hunger", false, "Completely disables by locking it at 7 hunger and redirects food to HP").getBoolean(false);
        maxHungerHeal = config.get("Hunger Disable", "Maximum food heal amount", 8, "Only works if hunger is disabled").getInt(8);

        tweakHunger = config.get("Hunger Tweak", "Tweak Hunger", true, "Incompatible with disabled hunger, allows for other tweaks").getBoolean(true);
        maxFoodLevel = config.get("Hunger Tweak", "Maximum food level", 20, "Vanilla default: 20").getInt(20);
        regenerationThreshold = config.get("Hunger Tweak", "Hunger regeneration threshold", 18, "Incompatible with always regeneration. Vanilla default: 18").getInt(18);
        alwaysHungerRegenerate = config.get("Hunger Tweak", "Always regenerate from hunger", true, "Player always regenerates from hunger. Goes slower at lower food levels").getBoolean(true);
        foodExhaustion = (float) config.get("Hunger Tweak", "Food Depletion", 0, "How much hunger should be drained per heart healed. Vanilla default: 3.0").getDouble(0);

        overrideArmorHud = config.get("Render Tweak", "Rearrange Armor HUD", true, "Moves the armor HUD if hunger or exp is disabled").getBoolean(true);
        fancyGrass = config.get("Render Tweak", "Force fancy grass", true).getBoolean(true);
        /*removeVoidParticles = config.get("Render Tweak", "Remove Void Particles", true).getBoolean(true);
        removeVoidFog = config.get("Render Tweak", "Remove Void Fog", true).getBoolean(true);*/

        alterStackSizes = config.get("Gameplay Tweak", "More stackable items", true, "Doors, boats, minecarts, and cake").getBoolean(true);
        addNametagRecipe = config.get("Gameplay Tweak", "Add Nametag Recipe", true, "String, slimeball, paper").getBoolean(true);
        animalBones = config.get("Gameplay Tweak", "Animal Bones", true, "Passive mobs drop bones on peaceful mode").getBoolean(true);
        feathers = config.get("Gameplay Tweak", "More Feathers", true, "Chickens drop many more feathers").getBoolean(true);
        leather = config.get("Gameplay Tweak", "Minimum Leather", true, "Cows drop at least one leather").getBoolean(true);
        nastyFlesh = config.get("Gameplay Tweak", "Nasty Flesh", true, "Rotten flesh turns absolutely nasty").getBoolean(true);
        nerfFoodStackSize = config.get("Gameplay Tweak", "Food Shrinkage", false, "Reduces maximum stack size on many foods").getBoolean(false);
        stackableSoup = config.get("Gameplay Tweak", "Stackable Soup", true, "Overrides mushroom soup to make it stackable").getBoolean(true);
        alwaysDropExp = config.get("Gameplay Tweak", "Everywhere Exp", false, "Experience orbs drop from more than player kills").getBoolean(false);
        sugarCaneHeight = config.get("Gameplay Tweak", "Sugar Cane Height", 3).getInt(3);
        poisonTime = config.get("Gameplay Tweak", "Poison Time", 50, "Ticks between poison damage. Vanilla Default: 25").getInt(50);

        creeperBehavior = config.get("Mob Behavior", "Slightly Smarter Creepers", true, "Creepers change targets towards the closer aggressor when hit").getBoolean(true);
        mounts = config.get("Mob Behavior", "Jockeyfication", true, "Certain mobs may spawn with extra mobs attached").getBoolean(true);
        endermenDontPickUpBlocks = config.get("Mob Behavior", "Ender Nender", true, "Prevent endermen from moving blocks around").getBoolean(true);
        spawnZombieReinforcements = !config.get("Mob Behavior", "Disable Zombie Reinforcements", true, "Overrides the vanilla zombie entity").getBoolean(true);
        keepBabyZombies = !config.get("Mob Behavior", "Disable Zombie Babies", true, "Overrides the vanilla zombie entity").getBoolean(true);
        disableZombieFire = config.get("Mob Behavior", "Disable Zombie Fire", true, "Overrides the vanilla zombie entity").getBoolean(true);
        //nicerWitches = config.get("Mob Behavior", "Nicer Witches", true, "Witches throw less powerful potions. Overrides the vanilla witch entity").getBoolean(true);

        changeArmorCalculations = config.get("Classic Mechanics", "Classic Armor", false, "Overrides vanilla armor to give alpha armor feel").getBoolean(false);
        revertTNT = config.get("Classic Mechanics", "TNT Puncher", true, "TNT can only be harvested with shears or silk touch").getBoolean(true);
        makeGuudFire = config.get("Classic Mechanics", "Hungry Fire", true, "Fire will burn down entire forests").getBoolean(true);
        disableExpBar = config.get("Classic Mechanics", "Disable XP bar", false).getBoolean(false);
        disableExp = config.get("Classic Mechanics", "Disable XP orbs", false).getBoolean(false);
        fleshToFeathers = config.get("Classic Mechanics", "Flesh to Feathers", false, "Zombies drop feathers instead of rotten flesh").getBoolean(false);

        int[] blacklist = config.get("Crosshair Tweaks", "Blacklist", new int[] { Item.map.itemID }, "Add block or item IDs that should not show the crosshair").getIntList();
        int[] rangeTarget = config.get("Crosshair Tweaks", "Crosshair", new int[] { Item.bow.itemID, Item.snowball.itemID, Item.egg.itemID, Item.fishingRod.itemID, Item.enderPearl.itemID },
                "Add block or item IDs that should render a target crosshair").getIntList();

        woodArmor = config.get("Armor Durability", "Leather Armor", new int[] { Item.helmetLeather.itemID, Item.plateLeather.itemID, Item.legsLeather.itemID, Item.bootsLeather.itemID },
                "Items that get a durability boost equal to wooden pickaxes").getIntList();
        stoneArmor = config.get("Armor Durability", "Chain Armor", new int[] { Item.helmetChain.itemID, Item.plateChain.itemID, Item.legsChain.itemID, Item.bootsChain.itemID },
                "Items that get a durability boost equal to stone pickaxes").getIntList();
        ironArmor = config.get("Armor Durability", "Iron Armor", new int[] { Item.helmetIron.itemID, Item.plateIron.itemID, Item.legsIron.itemID, Item.bootsIron.itemID },
                "Items that get a durability boost equal to iron pickaxes").getIntList();
        diamondArmor = config.get("Armor Durability", "Diamond Armor", new int[] { Item.helmetDiamond.itemID, Item.plateDiamond.itemID, Item.legsDiamond.itemID, Item.bootsDiamond.itemID },
                "Items that get a durability boost equal to diamond pickaxes").getIntList();
        goldArmor = config.get("Armor Durability", "Gold Armor", new int[] { Item.helmetGold.itemID, Item.plateGold.itemID, Item.legsGold.itemID, Item.bootsGold.itemID },
                "Items that get a durability boost equal to gold pickaxes").getIntList();

        for (int i = 0; i < blacklist.length; i++)
            this.crosshairBlacklist[blacklist[i]] = true;
        for (int i = 0; i < rangeTarget.length; i++)
            this.rangedCrosshair[rangeTarget[i]] = true;
        config.save();

        GameRegistry.registerPlayerTracker(new TweakPlayerTracker());
        MinecraftForge.EVENT_BUS.register(proxy);
        if (disableExp)
            MinecraftForge.EVENT_BUS.register(new ExpOrbListener());
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event)
    {
        proxy.postInit();
        if (alterStackSizes)
        {
            Item.doorWood.setMaxStackSize(16);
            Item.doorIron.setMaxStackSize(16);
            Item.boat.setMaxStackSize(16);
            Item.minecartEmpty.setMaxStackSize(3);
            Item.minecartCrate.setMaxStackSize(3);
            Item.minecartPowered.setMaxStackSize(3);
            Item.itemsList[Block.cake.blockID].setMaxStackSize(16);
        }

        if (makeGuudFire)
        {
            Block.setBurnProperties(Block.planks.blockID, 25, 20);
            Block.setBurnProperties(Block.woodDoubleSlab.blockID, 25, 20);
            Block.setBurnProperties(Block.woodSingleSlab.blockID, 25, 20);
            Block.setBurnProperties(Block.fence.blockID, 25, 20);
            Block.setBurnProperties(Block.stairsWoodOak.blockID, 25, 20);
            Block.setBurnProperties(Block.stairsWoodBirch.blockID, 25, 20);
            Block.setBurnProperties(Block.stairsWoodSpruce.blockID, 25, 20);
            Block.setBurnProperties(Block.stairsWoodJungle.blockID, 25, 20);
            Block.setBurnProperties(Block.wood.blockID, 25, 5);
            Block.setBurnProperties(Block.leaves.blockID, 90, 60);
            Block.setBurnProperties(Block.bookShelf.blockID, 90, 20);
            Block.setBurnProperties(Block.tnt.blockID, 45, 100);
            Block.setBurnProperties(Block.tallGrass.blockID, 180, 100);
            Block.setBurnProperties(Block.cloth.blockID, 90, 60);
            Block.setBurnProperties(Block.vine.blockID, 45, 100);
            Block.setBurnProperties(Block.coalBlock.blockID, 25, 5);
            Block.setBurnProperties(Block.hay.blockID, 180, 20);
        }

        if (addNametagRecipe)
        {
            OreDictionary.registerOre("slimeball", new ItemStack(Item.slimeBall));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.nameTag), "P~ ", "~O ", "  ~", '~', Item.silk, 'P', Item.paper, 'O', "slimeball"));
        }

        if (endermenDontPickUpBlocks)
        {
            EntityEnderman.carriableBlocks = new boolean[Block.blocksList.length];
        }

        if (changeArmorCalculations)
        {
            for (int i = 42; i <= 61; i++)
                Item.itemsList[i + 256] = null;
            Item.helmetLeather = (ItemArmor) (new TweakArmor(42, EnumArmorMaterial.CLOTH, 0, 0)).setUnlocalizedName("helmetCloth").setTextureName("leather_helmet");
            Item.plateLeather = (ItemArmor) (new TweakArmor(43, EnumArmorMaterial.CLOTH, 0, 1)).setUnlocalizedName("chestplateCloth").setTextureName("leather_chestplate");
            Item.legsLeather = (ItemArmor) (new TweakArmor(44, EnumArmorMaterial.CLOTH, 0, 2)).setUnlocalizedName("leggingsCloth").setTextureName("leather_leggings");
            Item.bootsLeather = (ItemArmor) (new TweakArmor(45, EnumArmorMaterial.CLOTH, 0, 3)).setUnlocalizedName("bootsCloth").setTextureName("leather_boots");
            Item.helmetChain = (ItemArmor) (new TweakArmor(46, EnumArmorMaterial.CHAIN, 1, 0)).setUnlocalizedName("helmetChain").setTextureName("chainmail_helmet");
            Item.plateChain = (ItemArmor) (new TweakArmor(47, EnumArmorMaterial.CHAIN, 1, 1)).setUnlocalizedName("chestplateChain").setTextureName("chainmail_chestplate");
            Item.legsChain = (ItemArmor) (new TweakArmor(48, EnumArmorMaterial.CHAIN, 1, 2)).setUnlocalizedName("leggingsChain").setTextureName("chainmail_leggings");
            Item.bootsChain = (ItemArmor) (new TweakArmor(49, EnumArmorMaterial.CHAIN, 1, 3)).setUnlocalizedName("bootsChain").setTextureName("chainmail_boots");
            Item.helmetIron = (ItemArmor) (new TweakArmor(50, EnumArmorMaterial.IRON, 2, 0)).setUnlocalizedName("helmetIron").setTextureName("iron_helmet");
            Item.plateIron = (ItemArmor) (new TweakArmor(51, EnumArmorMaterial.IRON, 2, 1)).setUnlocalizedName("chestplateIron").setTextureName("iron_chestplate");
            Item.legsIron = (ItemArmor) (new TweakArmor(52, EnumArmorMaterial.IRON, 2, 2)).setUnlocalizedName("leggingsIron").setTextureName("iron_leggings");
            Item.bootsIron = (ItemArmor) (new TweakArmor(53, EnumArmorMaterial.IRON, 2, 3)).setUnlocalizedName("bootsIron").setTextureName("iron_boots");
            Item.helmetDiamond = (ItemArmor) (new TweakArmor(54, EnumArmorMaterial.DIAMOND, 3, 0)).setUnlocalizedName("helmetDiamond").setTextureName("diamond_helmet");
            Item.plateDiamond = (ItemArmor) (new TweakArmor(55, EnumArmorMaterial.DIAMOND, 3, 1)).setUnlocalizedName("chestplateDiamond").setTextureName("diamond_chestplate");
            Item.legsDiamond = (ItemArmor) (new TweakArmor(56, EnumArmorMaterial.DIAMOND, 3, 2)).setUnlocalizedName("leggingsDiamond").setTextureName("diamond_leggings");
            Item.bootsDiamond = (ItemArmor) (new TweakArmor(57, EnumArmorMaterial.DIAMOND, 3, 3)).setUnlocalizedName("bootsDiamond").setTextureName("diamond_boots");
            Item.helmetGold = (ItemArmor) (new TweakArmor(58, EnumArmorMaterial.GOLD, 4, 0)).setUnlocalizedName("helmetGold").setTextureName("gold_helmet");
            Item.plateGold = (ItemArmor) (new TweakArmor(59, EnumArmorMaterial.GOLD, 4, 1)).setUnlocalizedName("chestplateGold").setTextureName("gold_chestplate");
            Item.legsGold = (ItemArmor) (new TweakArmor(60, EnumArmorMaterial.GOLD, 4, 2)).setUnlocalizedName("leggingsGold").setTextureName("gold_leggings");
            Item.bootsGold = (ItemArmor) (new TweakArmor(61, EnumArmorMaterial.GOLD, 4, 3)).setUnlocalizedName("bootsGold").setTextureName("gold_boots");
        }

        if (revertTNT)
        {
            int id = Block.tnt.blockID;
            Block.blocksList[id] = null;
            Block.blocksList[id] = new TNTTweak(id).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("tnt").setTextureName("tnt");
        }

        if (nastyFlesh)
        {
            Item.rottenFlesh = (new TweakFlesh(111, 1, 0.1F, true)).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh");
        }
        else if (disableHunger)
        {
            Item.rottenFlesh = (new ItemFood(111, 1, 0.1F, true)).setPotionEffect(Potion.poison.id, 5, 0, 0.8F).setUnlocalizedName("rottenFlesh").setTextureName("rotten_flesh");
        }

        if (nerfFoodStackSize)
        {
            Item[] nerfs = new Item[] { Item.appleRed, Item.bread, Item.porkRaw, Item.porkCooked, Item.appleGold, Item.fishRaw, Item.fishCooked, Item.cookie, Item.beefRaw, Item.beefCooked,
                    Item.chickenRaw, Item.chickenCooked, Item.rottenFlesh, Item.carrot, Item.potato, Item.bakedPotato, Item.goldenCarrot, Item.pumpkinPie };
            Item[] bigNerfs = new Item[] { Item.cookie, Item.melon };

            for (int i = 0; i < nerfs.length; i++)
                nerfs[i].setMaxStackSize(4);

            for (int i = 0; i < bigNerfs.length; i++)
                bigNerfs[i].setMaxStackSize(8);

            if (Loader.isModLoaded("TConstruct"))
            {
                Object o = getStaticItem("strangeFood", "tconstruct.common.TContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("jerky", "tconstruct.common.TContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);
            }

            if (Loader.isModLoaded("Natura"))
            {
                Object o = getStaticItem("waterDrop", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(8);

                o = getStaticItem("netherBerryItem", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("berryItem", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("berryMedley", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("seedFood", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("impMeat", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);

                o = getStaticItem("bowlStew", "mods.natura.common.NContent");
                if (o != null && o instanceof Item)
                    ((Item) o).setMaxStackSize(4);
            }
        }

        if (stackableSoup)
        {
            Item.itemsList[26 + 256] = null;
            Item.bowlSoup = new TweakStew(26, 6).setMaxStackSize(nerfFoodStackSize ? 4 : 64).setUnlocalizedName("mushroomStew").setTextureName("mushroom_stew");
        }

        if (!Loader.isModLoaded("ZAMod") && !spawnZombieReinforcements || !keepBabyZombies || disableZombieFire)
        {
            EntityList.addMapping(TweakZombie.class, "Zombie", 54, 44975, 7969893);
        }

        if (sugarCaneHeight != 3)
        {
            int id = Block.reed.blockID;
            Block.blocksList[id] = null;
            Block.blocksList[id] = new TweakSugarCane(id).setHardness(0.0F).setStepSound(Block.soundGrassFootstep).setUnlocalizedName("reeds").setTextureName("reeds");
        }

        if (poisonTime != 25)
        {
            Potion.potionTypes[Potion.poison.id] = new TweakPoisonStatus(19, true, 5149489).setPotionName("potion.poison").setIconIndex(6, 0);
        }

        for (int id = 0; id < woodArmor.length; id++)
            Item.itemsList[woodArmor[id]].setMaxDamage(Item.itemsList[woodArmor[id]].getMaxDamage() + Item.pickaxeWood.getMaxDamage());
        for (int id = 0; id < stoneArmor.length; id++)
            Item.itemsList[stoneArmor[id]].setMaxDamage(Item.itemsList[stoneArmor[id]].getMaxDamage() + Item.pickaxeStone.getMaxDamage());
        for (int id = 0; id < ironArmor.length; id++)
            Item.itemsList[ironArmor[id]].setMaxDamage(Item.itemsList[ironArmor[id]].getMaxDamage() + Item.pickaxeIron.getMaxDamage());
        for (int id = 0; id < diamondArmor.length; id++)
            Item.itemsList[diamondArmor[id]].setMaxDamage(Item.itemsList[diamondArmor[id]].getMaxDamage() + Item.pickaxeDiamond.getMaxDamage());
        for (int id = 0; id < goldArmor.length; id++)
            Item.itemsList[goldArmor[id]].setMaxDamage(Item.itemsList[goldArmor[id]].getMaxDamage() + Item.pickaxeGold.getMaxDamage());

        /*if (overridePortal)
        {
            int id = Block.portal.blockID;
            Block.blocksList[id] = null;
            Block.blocksList[id] = new TweakPortal(id).setHardness(-1.0F).setStepSound(Block.soundGlassFootstep).setLightValue(0.75F).setUnlocalizedName("portal").setTextureName("portal");
            id = Block.fire.blockID;
            Block.blocksList[id] = null;
            Block.blocksList[id] = new TweakFire(id).setHardness(0.0F).setLightValue(1.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("fire").setTextureName("fire");
        }*/

        //Doesn't work for some reason
        /*if (nicerWitches)
        {
            EntityList.addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);            
        }*/
    }

    public static void overrideFoodStats (EntityPlayer player)
    {
        overrideHungerHud = disableHunger;
        player.foodStats = new FoodStatsTweak(player);
    }

    public static Object getStaticItem (String name, String classPackage)
    {
        try
        {
            Class clazz = Class.forName(classPackage);
            Field field = clazz.getDeclaredField(name);
            Object ret = field.get(null);
            if (ret != null && (ret instanceof ItemStack || ret instanceof Item))
                return ret;
            return null;
        }
        catch (Exception e)
        {
            System.out.println("Could not find " + name);
            return null;
        }
    }
}
