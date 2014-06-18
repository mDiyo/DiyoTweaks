package mdiyo;

import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TweakZombie extends EntityZombie
{
    public TweakZombie(World par1World)
    {
        super(par1World);
    }

    @Override
    public void setChild (boolean flag)
    {
        if (DiyoTweaks.keepBabyZombies)
            super.setChild(flag);
    }

    @Override
    protected void applyEntityAttributes ()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0D);
        if (DiyoTweaks.spawnZombieReinforcements)
            this.getAttributeMap().func_111150_b(field_110186_bp).setAttribute(this.rand.nextDouble() * 0.10000000149011612D);
    }

    @Override
    public boolean attackEntityFrom (DamageSource par1DamageSource, float par2)
    {
        if (!super.attackEntityFrom(par1DamageSource, par2))
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.getAttackTarget();

            if (entitylivingbase == null && this.getEntityToAttack() instanceof EntityLivingBase)
            {
                entitylivingbase = (EntityLivingBase) this.getEntityToAttack();
            }

            if (entitylivingbase == null && par1DamageSource.getEntity() instanceof EntityLivingBase)
            {
                entitylivingbase = (EntityLivingBase) par1DamageSource.getEntity();
            }

            if (DiyoTweaks.spawnZombieReinforcements && entitylivingbase != null && this.worldObj.difficultySetting >= 3
                    && (double) this.rand.nextFloat() < this.getEntityAttribute(field_110186_bp).getAttributeValue())
            {
                int i = MathHelper.floor_double(this.posX);
                int j = MathHelper.floor_double(this.posY);
                int k = MathHelper.floor_double(this.posZ);
                EntityZombie entityzombie = new EntityZombie(this.worldObj);

                for (int l = 0; l < 50; ++l)
                {
                    int i1 = i + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
                    int j1 = j + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
                    int k1 = k + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);

                    if (this.worldObj.doesBlockHaveSolidTopSurface(i1, j1 - 1, k1) && this.worldObj.getBlockLightValue(i1, j1, k1) < 10)
                    {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);

                        if (this.worldObj.checkNoEntityCollision(entityzombie.boundingBox) && this.worldObj.getCollidingBoundingBoxes(entityzombie, entityzombie.boundingBox).isEmpty()
                                && !this.worldObj.isAnyLiquid(entityzombie.boundingBox))
                        {
                            this.worldObj.spawnEntityInWorld(entityzombie);
                            entityzombie.setAttackTarget(entitylivingbase);
                            entityzombie.onSpawnWithEgg((EntityLivingData) null);
                            this.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
                            entityzombie.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    @Override
    public EntityLivingData onSpawnWithEgg (EntityLivingData par1EntityLivingData)
    {
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
        Object par1EntityLivingData1 = par1EntityLivingData;//super.onSpawnWithEgg(par1EntityLivingData);
        float f = this.worldObj.getLocationTensionFactor(this.posX, this.posY, this.posZ);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * f);

        if (par1EntityLivingData1 == null)
        {
            par1EntityLivingData1 = new ZombieGroupData(this, this.worldObj.rand.nextFloat() < 0.05F, this.worldObj.rand.nextFloat() < 0.05F, (EmptyZombieInner) null);
        }

        if (par1EntityLivingData1 instanceof ZombieGroupData)
        {
            ZombieGroupData entityzombiegroupdata = (ZombieGroupData) par1EntityLivingData1;

            if (entityzombiegroupdata.field_142046_b)
            {
                this.setVillager(true);
            }

            if (entityzombiegroupdata.field_142048_a)
            {
                this.setChild(true);
            }
        }

        this.addRandomArmor();
        this.enchantEquipment();

        if (this.getCurrentItemOrArmor(4) == null)
        {
            Calendar calendar = this.worldObj.getCurrentDate();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.rand.nextFloat() < 0.25F)
            {
                this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Block.pumpkinLantern : Block.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05000000074505806D, 0));
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", this.rand.nextDouble() * 1.5D, 2));

        if (this.rand.nextFloat() < f * 0.05F)
        {
            if (DiyoTweaks.spawnZombieReinforcements)
                this.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 0.25D + 0.5D, 0));
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("Leader zombie bonus", this.rand.nextDouble() * 3.0D + 1.0D, 2));
        }

        return (EntityLivingData) par1EntityLivingData1;
    }

    class ZombieGroupData implements EntityLivingData
    {
        public boolean field_142048_a;
        public boolean field_142046_b;

        final EntityZombie field_142047_c;

        private ZombieGroupData(EntityZombie par1EntityZombie, boolean par2, boolean par3)
        {
            this.field_142047_c = par1EntityZombie;
            this.field_142048_a = false;
            this.field_142046_b = false;
            this.field_142048_a = par2;
            this.field_142046_b = par3;
        }

        ZombieGroupData(EntityZombie par1EntityZombie, boolean par2, boolean par3, EmptyZombieInner par4EntityZombieINNER1)
        {
            this(par1EntityZombie, par2, par3);
        }
    }

    class EmptyZombieInner
    {
    }
    
    public boolean attackEntityAsMob (Entity par1Entity)
    {
        if (DiyoTweaks.disableZombieFire)
        {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (par1Entity instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) par1Entity);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) par1Entity);
        }

        boolean flag = par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0)
            {
                par1Entity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F), 0.1D,
                        (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                par1Entity.setFire(j * 4);
            }

            if (par1Entity instanceof EntityLivingBase)
            {
                EnchantmentThorns.func_92096_a(this, (EntityLivingBase) par1Entity, this.rand);
            }
        }

        return flag;
        }
        else
            return super.attackEntityAsMob(par1Entity);
    }
}
