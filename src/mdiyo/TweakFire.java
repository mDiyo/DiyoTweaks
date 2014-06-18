package mdiyo;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;

public class TweakFire extends BlockFire
{

    public TweakFire(int par1)
    {
        super(par1);
        disableStats();
    }

    public void registerIcons (IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        Block.fire.registerIcons(par1IconRegister);
    }
    
    @Override
    public void onBlockAdded (World par1World, int par2, int par3, int par4)
    {
        if (par1World.provider.dimensionId > 0 || par1World.getBlockId(par2, par3 - 1, par4) != Block.obsidian.blockID || !((BlockPortal) Block.blocksList[90]).tryToCreatePortal(par1World, par2, par3, par4))
        {
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4))
            {
                par1World.setBlockToAir(par2, par3, par4);
            }
            else
            {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par1World.rand.nextInt(10));
            }
        }
    }
    
    private boolean canNeighborBurn (World par1World, int par2, int par3, int par4)
    {
        return canBlockCatchFire(par1World, par2 + 1, par3, par4, WEST) || canBlockCatchFire(par1World, par2 - 1, par3, par4, EAST) || canBlockCatchFire(par1World, par2, par3 - 1, par4, UP)
                || canBlockCatchFire(par1World, par2, par3 + 1, par4, DOWN) || canBlockCatchFire(par1World, par2, par3, par4 - 1, SOUTH) || canBlockCatchFire(par1World, par2, par3, par4 + 1, NORTH);
    }
}
