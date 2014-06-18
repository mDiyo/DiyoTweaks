package mdiyo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class TweakFlesh extends ItemFood
{

    public TweakFlesh(int par1, int par2, float par3, boolean par4)
    {
        super(par1, par2, par3, par4);
    }


    protected void onFoodEaten (ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par2World.isRemote)
        {
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 30 * 20, 1));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.confusion.id, 15 * 20, 1));
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 5 * 20, 1));
        }
    }
}
