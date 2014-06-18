package mdiyo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TNTTweak extends BlockTNT
{

    public TNTTweak(int par1)
    {
        super(par1);
    }

    @Override
    public void onBlockDestroyedByPlayer (World par1World, int par2, int par3, int par4, int par5)
    {
        //this.primeTnt(par1World, par2, par3, par4, par5, (EntityLivingBase) null);
    }

    @Override
    public boolean removeBlockByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        ItemStack stack = player != null ? player.getCurrentItemOrArmor(0) : null;
        if (player == null || !player.capabilities.isCreativeMode && (stack == null || player == null || (!(stack.getItem() instanceof ItemShears) && !EnchantmentHelper.getSilkTouchModifier(player))))
        {
            this.primeTnt(world, x, y, z, world.getBlockMetadata(x, y, z), player);
            world.setBlockToAir(x, y, z);
            return false;
        }
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public void primeTnt (World par1World, int par2, int par3, int par4, int par5, EntityLivingBase par6EntityLivingBase)
    {
        if (!par1World.isRemote)
        {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(par1World, (double) ((float) par2 + 0.5F), (double) ((float) par3 + 0.5F), (double) ((float) par4 + 0.5F), par6EntityLivingBase);
            par1World.spawnEntityInWorld(entitytntprimed);
            par1World.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
        }
    }

    @Override
    public void onNeighborBlockChange (World par1World, int par2, int par3, int par4, int par5)
    {
        if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
        {
            this.removeBlockByPlayer(par1World, null, par2, par3, par4);
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        Block.tnt.registerIcons(iconRegister);
    }
}
