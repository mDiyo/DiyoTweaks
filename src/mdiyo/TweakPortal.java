package mdiyo;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TweakPortal extends BlockPortal
{
    public TweakPortal(int id)
    {
        super(id);
    }

    public void registerIcons (IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        Block.portal.registerIcons(par1IconRegister);
    }

    @Override
    public boolean tryToCreatePortal (World world, int x, int y, int z)
    {
        return false;
    }
}