package mdiyo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

public class GuiIngameForgeFix extends GuiIngameForge
{
    public GuiIngameForgeFix(Minecraft mc)
    {
        super(mc);
    }

    private FontRenderer fontrenderer = mc.fontRenderer;

    @Override
    protected void renderToolHightlight (int width, int height)
    {
        if (this.mc.gameSettings.heldItemTooltips)
        {
            mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                String name = this.highlightingItemStack.getDisplayName();

                int opacity = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);
                if (opacity > 255)
                    opacity = 255;

                if (opacity > 0)
                {
                    int y = height - 52;
                    if (!mc.playerController.shouldDrawHUD())
                        y += 7;

                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    FontRenderer font = highlightingItemStack.getItem().getFontRenderer(highlightingItemStack);
                    if (font != null)
                    {
                        int x = (width - font.getStringWidth(name)) / 2;
                        font.drawStringWithShadow(name, x, y, 0xFFFFFF | (opacity << 24));
                    }
                    else
                    {
                        int x = (width - fontrenderer.getStringWidth(name)) / 2;
                        fontrenderer.drawStringWithShadow(name, x, y, 0xFFFFFF | (opacity << 24));
                    }
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }

            mc.mcProfiler.endSection();
        }
    }

    @ForgeSubscribe
    public void openGui (GuiOpenEvent event)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
        if (DiyoTweaks.disableExpBar)
            mc.ingameGUI = this;
    }
}
