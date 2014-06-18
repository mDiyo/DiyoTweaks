package mdiyo;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class CommonProxy
{
    public CommonProxy()
    {
    }

    public void postInit ()
    {        
    }

    //Events

    /*@ForgeSubscribe
    public void creepSplosion (LivingDeathEvent event)
    {
        if (!event.entityLiving.worldObj.isRemote)// && event.entityLiving instanceof EntityCreeper)
        {
            EntityLivingBase living = event.entityLiving;
            ItemStack firework = new ItemStack(Item.firework);
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound tags = new NBTTagCompound("Fireworks");
            NBTTagCompound explosion = new NBTTagCompound();
            explosion.setByte("Type", (byte) 3);
            explosion.setByte("Flight", (byte) 3);
            tags.setTag("Explosion", explosion);
            compound.setTag("Fireworks", tags);
            firework.setTagCompound(compound);
            EntityFireworkRocket rocket = new EntityFireworkRocket(living.worldObj, living.posX, living.posY+1, living.posZ, firework);
            living.worldObj.spawnEntityInWorld(rocket);
        }
    }*/

    Random random = new Random();

    @ForgeSubscribe
    public void onLivingDrop (LivingDropsEvent event)
    {
        if (event.entityLiving == null)
            return;

        EntityLivingBase living = event.entityLiving;
        if (DiyoTweaks.leather && living instanceof EntityCow)
        {
            addDrops(event, new ItemStack(Item.leather));
        }

        else if (DiyoTweaks.feathers && living.getClass() == EntityChicken.class)
        {
            int amount = random.nextInt(5) + random.nextInt(1 + event.lootingLevel) + random.nextInt(1 + event.lootingLevel) + 1;

            for (int iter = 0; iter < amount; ++iter)
            {
                addDrops(event, new ItemStack(Item.feather, 1));
            }
        }

        else if (living instanceof EntityEnderman)
        {
            int block = ((EntityEnderman) living).getCarried();
            if (block != 0)
                addDrops(event, new ItemStack(block, 1, ((EntityEnderman) living).getCarryingData()));
        }

        else if (DiyoTweaks.animalBones && living instanceof EntityAnimal)
        {
            if (living.worldObj.difficultySetting == 0)
            {
                int amount = random.nextInt(3) + random.nextInt(1 + event.lootingLevel) + 1;
                for (int i = 0; i < amount; i++)
                {
                    addDrops(event, new ItemStack(Item.bone));
                }
            }
        }

        else if (DiyoTweaks.fleshToFeathers && event.entityLiving instanceof EntityZombie)
        {
            Iterator iter = event.drops.iterator();
            while (iter.hasNext())
            {
                EntityItem entity = (EntityItem) iter.next();
                if (entity.getEntityItem().getItem() == Item.rottenFlesh)
                    iter.remove();
            }

            if (random.nextInt(3) == 0)
            {
                int amount = random.nextInt(3) + random.nextInt(event.lootingLevel + 1);
                if (amount > 0)
                {
                    EntityItem item = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(Item.feather, amount));
                    event.drops.add(item);
                }
            }
        }
    }

    void addDrops (LivingDropsEvent event, ItemStack dropStack)
    {
        EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
        entityitem.delayBeforeCanPickup = 10;
        event.drops.add(entityitem);
    }

    @ForgeSubscribe
    public void onHurt (LivingHurtEvent event)
    {
        if (DiyoTweaks.alwaysDropExp && validSourceTypes(event.source))
            event.entityLiving.recentlyHit += 50;
        
        if (DiyoTweaks.creeperBehavior)
        {
            EntityLivingBase reciever = event.entityLiving;
            if (reciever instanceof EntityCreeper)
            {
                Entity attacker = event.source.getEntity();
                if (attacker instanceof EntityLivingBase)
                {
                    Entity target = ((EntityCreeper) reciever).getAttackTarget();
                    if (target != null)
                    {
                        float d1 = reciever.getDistanceToEntity(((EntityCreeper) reciever).getAttackTarget());
                        float d2 = reciever.getDistanceToEntity(attacker);
                        if (d2 < d1)
                        {
                            ((EntityCreeper) event.entityLiving).setAttackTarget((EntityLivingBase) event.source.getEntity());
                        }
                    }
                }
            }
        }
    }
    
    boolean validSourceTypes(DamageSource source)
    {
        if (source == DamageSource.lava || source == DamageSource.drown)
            return false;
        return true;
    }

    @ForgeSubscribe
    public void onLivingSpawn (LivingSpawnEvent.SpecialSpawn event)
    {
        if (event.world.isRemote)
            return;

        if (DiyoTweaks.mounts)
        {
            EntityLivingBase living = event.entityLiving;
            if (living.getClass() == EntitySpider.class && random.nextInt(100) == 0)
            {
                EntityCreeper creeper = new EntityCreeper(living.worldObj);
                spawnEntityLiving(living.posX, living.posY + 1, living.posZ, creeper, living.worldObj);
                if (living.riddenByEntity != null)
                    creeper.mountEntity(living.riddenByEntity);
                else
                    creeper.mountEntity(living);

                EntityXPOrb orb = new EntityXPOrb(living.worldObj, living.posX, living.posY, living.posZ, random.nextInt(20) + 20);
                orb.mountEntity(creeper);
            }

            else if (living.getClass() == EntitySlime.class)
            {
                if (random.nextInt(10) == 0)
                    attachSlime(living, ((EntitySlime) living).getSlimeSize() - 1);
            }
        }
    }

    private void attachSlime (EntityLivingBase living, int size)
    {
        if (random.nextBoolean())
        {
            EntitySlime slime = new EntitySlime(living.worldObj);
            slime.setSlimeSize(size);

            spawnEntityLiving(living.posX, living.posY + 1, living.posZ, slime, living.worldObj);
            if (living.riddenByEntity != null)
                slime.mountEntity(living.riddenByEntity);
            else
                slime.mountEntity(living);

            if (size > 1)
                attachSlime(slime, size - 1);
        }
    }

    public static void spawnEntityLiving (double x, double y, double z, EntityLiving entity, World world)
    {
        if (!world.isRemote)
        {
            entity.setPosition(x, y, z);
            entity.onSpawnWithEgg((EntityLivingData) null);
            world.spawnEntityInWorld(entity);
        }
    }
    
    private void func_71013_b (int par1, EntityPlayer player)
    {
        player.field_71079_bU = 0.0F;
        player.field_71089_bV = 0.0F;

        switch (par1)
        {
        case 0:
            player.field_71089_bV = -1.8F;
            break;
        case 1:
            player.field_71079_bU = 1.8F;
            break;
        case 2:
            player.field_71089_bV = 1.8F;
            break;
        case 3:
            player.field_71079_bU = -1.8F;
        }
    }
}
