package mdiyo;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;

public class TweakSugarCane extends BlockReed
{
    public TweakSugarCane(int id)
    {
        super(id);
        disableStats();
    }

    @Override
    public void updateTick (World world, int x, int y, int z, Random random)
    {
        if (world.isAirBlock(x, y + 1, z))
        {
            int l;

            for (l = 1; world.getBlockId(x, y - l, z) == this.blockID; ++l)
            {
                ;
            }

            if (l < DiyoTweaks.sugarCaneHeight)
            {
                int i1 = world.getBlockMetadata(x, y, z);

                if (i1 == 15)
                {
                    world.setBlock(x, y + 1, z, this.blockID);
                    world.setBlockMetadataWithNotify(x, y, z, 0, 4);
                }
                else
                {
                    world.setBlockMetadataWithNotify(x, y, z, i1 + 1, 4);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        Block.reed.registerIcons(iconRegister);
    }
}
