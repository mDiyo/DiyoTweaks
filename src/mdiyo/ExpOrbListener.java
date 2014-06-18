package mdiyo;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class ExpOrbListener
{
    @ForgeSubscribe
    public void killExpOrbs (EntityJoinWorldEvent event)
    {
        //if (DiyoTweaks.disableExp)
        //{
            if (event.entity instanceof EntityXPOrb)
                event.setCanceled(true);
        //}
    }
}
