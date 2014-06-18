package mdiyo;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TweakWitch extends EntityWitch
{

    public TweakWitch(World par1World)
    {
        super(par1World);
    }
    
    @Override
    public void attackEntityWithRangedAttack (EntityLivingBase par1EntityLivingBase, float par2)
    {
        if (!this.getAggressive())
        {
            ItemStack stack = new ItemStack(Item.potion, 1, 16384);
            double d0 = par1EntityLivingBase.posX + par1EntityLivingBase.motionX - this.posX;
            double d1 = par1EntityLivingBase.posY + (double) par1EntityLivingBase.getEyeHeight() - 1.100000023841858D - this.posY;
            double d2 = par1EntityLivingBase.posZ + par1EntityLivingBase.motionZ - this.posZ;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);

            if (f1 >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.moveSlowdown))
            {
                stack.setItemDamage(16392);
                
                PotionEffect pe = new PotionEffect(Potion.moveSlowdown.id, 20*20);
                NBTTagCompound potionTag = new NBTTagCompound();
                pe.writeCustomPotionEffectToNBT(potionTag);
                
                NBTTagCompound base = new NBTTagCompound();
                base.setCompoundTag("CustomPotionEffects", potionTag);
                stack.setTagCompound(base);
            }
            else if (par1EntityLivingBase.getHealth() >= 8.0F && !par1EntityLivingBase.isPotionActive(Potion.poison))
            {
                stack.setItemDamage(16388);
                
                PotionEffect pe = new PotionEffect(Potion.poison.id, 20*20, 10);
                NBTTagCompound potionTag = new NBTTagCompound();
                pe.writeCustomPotionEffectToNBT(potionTag);
                
                NBTTagCompound base = new NBTTagCompound();
                base.setCompoundTag("CustomPotionEffects", potionTag);
                stack.setTagCompound(base);
            }
            else if (f1 <= 3.0F && !par1EntityLivingBase.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F)
            {
                stack.setItemDamage(16392);
                
                PotionEffect pe = new PotionEffect(Potion.weakness.id, 20*20);
                NBTTagCompound potionTag = new NBTTagCompound();
                pe.writeCustomPotionEffectToNBT(potionTag);
                
                NBTTagCompound base = new NBTTagCompound();
                base.setCompoundTag("CustomPotionEffects", potionTag);
                stack.setTagCompound(base);
            }
            EntityPotion entitypotion = new EntityPotion(this.worldObj, this, stack);
            entitypotion.rotationPitch -= -20.0F;

            entitypotion.setThrowableHeading(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
            this.worldObj.spawnEntityInWorld(entitypotion);
        }
    }
}
