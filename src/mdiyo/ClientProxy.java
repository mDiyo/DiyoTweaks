package mdiyo;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{
    Minecraft mc = Minecraft.getMinecraft();
    GameSettings gs = Minecraft.getMinecraft().gameSettings;

    public ClientProxy()
    {
        if (DiyoTweaks.fancyGrass)
            TickRegistry.registerTickHandler(new TweakTicker(), Side.CLIENT);
        if (DiyoTweaks.disableExpBar)
            MinecraftForge.EVENT_BUS.register(new GuiIngameForgeFix(mc));
    }

    @Override
    public void postInit ()
    {
        //Neither of these work.
        /*if (DiyoTweaks.removeVoidParticles)
            mc.renderGlobal = new RenderGlobalTweak(mc);
        if (DiyoTweaks.removeVoidFog)
            mc.entityRenderer = new EntityRendererTweak(mc);*/
    }

    boolean tukmc = Loader.isModLoaded("tukmc_Vz");
    boolean TConstruct = Loader.isModLoaded("TConstruct");
    private ScaledResolution res = null;
    public static final ResourceLocation icons = new ResourceLocation("mdiyo", "textures/gui/icons.png");
    boolean updateResolution = false;
    int scaledWidth;
    int scaledHeight;
    int updateCounter = 0;
    int foodUpdateConter = 0;
    Random rand = new Random();

    @ForgeSubscribe
    public void renderHud (RenderGameOverlayEvent.Pre event)
    {
        if (tukmc)
            return;

        if (event.type == ElementType.ALL)
        {
            if (updateResolution)
            {
                res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
                scaledWidth = res.getScaledWidth();
                scaledHeight = res.getScaledHeight();
                updateResolution = false;
            }
        }

        //GuiIngameForge.renderExperiance = true;

        else if (event.type == ElementType.CROSSHAIRS) //Crosshairs
        {
            if (gs.thirdPersonView != 0)
                event.setCanceled(true);
            else
            {
                ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
                if (stack != null)
                {
                    if (DiyoTweaks.crosshairBlacklist[stack.itemID])
                        event.setCanceled(true);
                    else if (DiyoTweaks.rangedCrosshair[stack.itemID])
                    {
                        mc.getTextureManager().bindTexture(icons);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
                        drawTexturedModalRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0, 0, 16, 16);
                        GL11.glDisable(GL11.GL_BLEND);
                        mc.getTextureManager().bindTexture(Gui.icons);
                        event.setCanceled(true);
                        updateResolution = true;
                    }
                }
            }
        }

        else if (!TConstruct && event.type == ElementType.HEALTH && DiyoTweaks.disableExpBar)
        {
            if (event.type == ElementType.HEALTH)
            {
                updateCounter++;

                ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                scaledWidth = scaledresolution.getScaledWidth();
                scaledHeight = scaledresolution.getScaledHeight();
                int xBasePos = scaledWidth / 2 - 91;
                int yBasePos = scaledHeight - 39;

                boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

                if (mc.thePlayer.hurtResistantTime < 10)
                {
                    highlight = false;
                }

                AttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
                int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
                float healthMax = (float) attrMaxHealth.getAttributeValue();
                if (healthMax > 20)
                    healthMax = 20;
                float absorb = this.mc.thePlayer.getAbsorptionAmount();

                int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
                int rowHeight = Math.max(10 - (healthRows - 2), 3);

                this.rand.setSeed((long) (updateCounter * 312871));

                int left = scaledWidth / 2 - 91;
                int top = scaledHeight - GuiIngameForge.left_height;
                if (GuiIngameForge.renderExperiance == false)
                {
                    top += 7;
                    yBasePos += 7;
                }

                int regen = -1;
                if (mc.thePlayer.isPotionActive(Potion.regeneration))
                {
                    regen = updateCounter % 25;
                }

                final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
                final int BACKGROUND = (highlight ? 25 : 16);
                int MARGIN = 16;
                if (mc.thePlayer.isPotionActive(Potion.poison))
                    MARGIN += 36;
                else if (mc.thePlayer.isPotionActive(Potion.wither))
                    MARGIN += 72;
                float absorbRemaining = absorb;

                for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i)
                {
                    int b0 = (highlight ? 1 : 0);
                    int row = MathHelper.ceiling_float_int((float) (i + 1) / 10.0F) - 1;
                    int x = left + i % 10 * 8;
                    int y = top - row * rowHeight;

                    if (health <= 4)
                        y += rand.nextInt(2);
                    if (i == regen)
                        y -= 2;

                    drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

                    if (highlight)
                    {
                        if (i * 2 + 1 < healthLast)
                            drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
                        else if (i * 2 + 1 == healthLast)
                            drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
                    }

                    if (absorbRemaining > 0.0F)
                    {
                        if (absorbRemaining == absorb && absorb % 2.0F == 1.0F)
                            drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
                        else
                            drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
                        absorbRemaining -= 2.0F;
                    }
                    else
                    {
                        if (i * 2 + 1 < health)
                            drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
                        else if (i * 2 + 1 == health)
                            drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
                    }
                }

                int potionOffset = 0;
                PotionEffect potion = mc.thePlayer.getActivePotionEffect(Potion.wither);
                if (potion != null)
                    potionOffset = 18;
                potion = mc.thePlayer.getActivePotionEffect(Potion.poison);
                if (potion != null)
                    potionOffset = 9;

                GuiIngameForge.left_height += 10;
                if (absorb > 0)
                    GuiIngameForge.left_height += 10;

                event.setCanceled(true);
            }
        }

        else if (event.type == ElementType.EXPERIENCE && DiyoTweaks.disableExpBar)
        {
            GuiIngameForge.renderExperiance = false;
            event.setCanceled(true);
        }

        else if (DiyoTweaks.overrideHungerHud) //Hunger
        {
            if (event.type == ElementType.FOOD)
            {
                event.setCanceled(true);
            }

            if (DiyoTweaks.overrideArmorHud && event.type == ElementType.ARMOR)
            {
                mc.mcProfiler.startSection("armor");
                if (DiyoTweaks.disableExpBar)
                    GuiIngameForge.right_height -= 7;
                int left = scaledWidth / 2 + 11;
                int top = scaledHeight - GuiIngameForge.right_height;

                int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
                for (int i = 1; level > 0 && i < 20; i += 2)
                {
                    if (i < level)
                    {
                        drawTexturedModalRect(left, top, 34, 9, 9, 9);
                    }
                    else if (i == level)
                    {
                        drawTexturedModalRect(left, top, 25, 9, 9, 9);
                    }
                    else if (i > level)
                    {
                        drawTexturedModalRect(left, top, 16, 9, 9, 9);
                    }
                    left += 8;
                }
                GuiIngameForge.right_height += 10;

                mc.mcProfiler.endSection();
                updateResolution = true;
                event.setCanceled(true);
            }
        }
        else if (DiyoTweaks.tweakHunger)
        {
            if (event.type == ElementType.FOOD)
            {
                mc.mcProfiler.startSection("food");
                foodUpdateConter++;
                if (DiyoTweaks.disableExpBar)
                    GuiIngameForge.right_height -= 7;

                updateResolution = true;
                int left = scaledWidth / 2 + 91;
                int top = scaledHeight - GuiIngameForge.right_height;
                GuiIngameForge.right_height += 10;
                boolean unused = false;// Unused flag in vanilla, seems to be part of a 'fade out' mechanic

                FoodStats stats = mc.thePlayer.getFoodStats();
                int level = stats.getFoodLevel();
                int levelLast = stats.getPrevFoodLevel();

                for (int i = 0; i < 10; ++i) //Default food
                {
                    int idx = i * 2 + 1;
                    int x = left - i * 8 - 9;
                    int y = top;
                    int icon = 16;
                    byte backgound = 0;

                    if (mc.thePlayer.isPotionActive(Potion.hunger))
                    {
                        icon += 36;
                        backgound = 13;
                    }
                    if (unused)
                        backgound = 1; //Probably should be a += 1 but vanilla never uses this

                    /*if (mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (level * 10 + 1) == 0)
                    {
                        y = top + (rand.nextInt(3) - 1);
                    }*/

                    drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);

                    if (idx < level)
                        drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                    else if (idx == level)
                        drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);

                }

                //Extra food
                this.mc.getTextureManager().bindTexture(icons);
                left += 88;

                for (int iter = 0; iter < level / 20; iter++)
                {
                    int renderHearts = (level - 20 * (iter + 1)) / 2;
                    if (renderHearts > 10)
                        renderHearts = 10;
                    for (int i = 0; i < renderHearts; i++)
                    //for (int i = iter * 10; i < 10 + iter * 10; i++)
                    {
                        int idx = i * 2 + 1;
                        int x = left - i * 8 - 17 - 80;
                        int y = top;
                        int icon = 0 + iter * 18;
                        int background = 0;

                        if (mc.thePlayer.isPotionActive(Potion.hunger))
                        {
                            background += 9;
                        }

                        if (mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && foodUpdateConter % (level * 10 + 1) == 0)
                        {
                            y = top + (rand.nextInt(3) - 1);
                        }

                        //drawTexturedModalRect(x, y, 16 + backgound * 9, 27, 9, 9);

                        if (idx < level)
                            drawTexturedModalRect(x, y, icon + 0, background + 27, 9, 9);
                        else if (idx == level)
                            drawTexturedModalRect(x, y, icon + 9, background + 27, 9, 9);
                    }
                }
                this.mc.getTextureManager().bindTexture(icons);
                mc.mcProfiler.endSection();
                event.setCanceled(true);
            }
        }

        if (!DiyoTweaks.overrideHungerHud && DiyoTweaks.disableExpBar && event.type == ElementType.ARMOR)
        {
            mc.mcProfiler.startSection("armor");
            if (DiyoTweaks.disableExpBar)
                GuiIngameForge.left_height -= 7;
            int left = scaledWidth / 2 - 91;
            int top = scaledHeight - GuiIngameForge.left_height;

            int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
            for (int i = 1; level > 0 && i < 20; i += 2)
            {
                if (i < level)
                {
                    drawTexturedModalRect(left, top, 34, 9, 9, 9);
                }
                else if (i == level)
                {
                    drawTexturedModalRect(left, top, 25, 9, 9, 9);
                }
                else if (i > level)
                {
                    drawTexturedModalRect(left, top, 16, 9, 9, 9);
                }
                left += 8;
            }
            GuiIngameForge.left_height += 10;

            mc.mcProfiler.endSection();
            updateResolution = true;
            event.setCanceled(true);
        }
    }

    @ForgeSubscribe
    public void renderHudPost (RenderGameOverlayEvent.Post event)
    {
        if (event.type == ElementType.EXPERIENCE && DiyoTweaks.disableExpBar)
        {

        }
    }

    int zLevel = 0;

    public void drawTexturedModalRect (int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
        tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + 0) * f1));
        tessellator.draw();
    }
}
