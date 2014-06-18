package mdiyo;

import net.minecraft.potion.Potion;

public class TweakPoisonStatus extends Potion
{
    public TweakPoisonStatus(int id, boolean badEffect, int color)
    {
        super(id, badEffect, color);
        this.setEffectiveness(0.25D);
    }

    @Override
    public boolean isReady (int par1, int par2)
    {
        int k;

        if (this.id == poison.id)
        {
            k = DiyoTweaks.poisonTime >> par2;
            return k > 0 ? par1 % k == 0 : true;
        }
        
        return false;
    }
}
