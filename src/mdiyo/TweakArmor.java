package mdiyo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import tconstruct.library.armor.ArmorCore;

public class TweakArmor extends ItemArmor implements ISpecialArmor
{
    public TweakArmor(int id, EnumArmorMaterial mat, int renderIndex, int armorType)
    {
        super(id, mat, renderIndex, armorType);
    }

    @Override
    public ArmorProperties getProperties (EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot)
    {
        if (source.isUnblockable())
            return new ArmorProperties(0, 0, Integer.MAX_VALUE);

        float current = armor.getMaxDamage() - armor.getItemDamage();
        float max = armor.getMaxDamage();
        float amount = current / max;
        return new ArmorProperties(0, Math.max(0.05, amount * 0.2), (int) (current + 1));
    }

    /*@Override
    public int getArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        float current = armor.getMaxDamage() - armor.getItemDamage();
        float max = armor.getMaxDamage();
        float amount = current / max * 5 + 0.09F;
        if (slot == 2 && amount < 1)
            amount = 1;
        return (int) amount;
    }*/

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        if (!source.isUnblockable())
            stack.damageItem(damage, entity);
    }

    @Override
    public int getArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        if (slot != 1)
        {
            ItemStack stack = player.getCurrentArmor(1);
            if (stack != null && stack.getItem() instanceof TweakArmor)
                return 0;
            return disconnectedArmorDisplay(player, armor, slot);
        }

        return combinedArmorDisplay(player, armor);
    }

    protected int disconnectedArmorDisplay (EntityPlayer player, ItemStack armor, int slot)
    {
        float current = armor.getMaxDamage() - armor.getItemDamage();
        float max = armor.getMaxDamage();
        float amount = current / max * 5 + 0.09F;
        if (slot == 2 && amount < 1)
            amount = 1;
        return (int) amount;
    }

    protected int combinedArmorDisplay (EntityPlayer player, ItemStack legs)
    {
        ItemStack[] armors = new ItemStack[] { player.getCurrentArmor(3), player.getCurrentArmor(2), legs, player.getCurrentArmor(0) };
        int types = 0;
        int max = 0;
        int damage = 0;
        boolean anyAlive = false;
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = armors[i];
            if (stack != null && stack.getItem() instanceof TweakArmor)
            {
                types++;
                max += stack.getMaxDamage();
                damage += stack.getItemDamage();

            }
        }
        float ratio = ((float) max - (float) damage) / (float) max * (types * 5) + 0.1f;
        int minimum = anyAlive ? 1 : 0;
        if (ratio < minimum)
            ratio = minimum;
        return (int) ratio;
    }

}
