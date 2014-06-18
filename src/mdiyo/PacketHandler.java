package mdiyo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler
{

    @Override
    public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (packet.channel.equals("DiyoTweaks"))
        {
            if (side == Side.SERVER)
                handleServerPacket(packet, (EntityPlayerMP) player);
            else
                handleClientPacket(packet, (EntityPlayer) player);
        }
    }

    void handleClientPacket (Packet250CustomPayload packet, EntityPlayer player)
    {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte packetID;

        try
        {
            packetID = inputStream.readByte();

            switch (packetID)
            {
            case 1:
                DiyoTweaks.overrideFoodStats(player);   break;
            }
        }
        catch (Exception e)
        {
            System.out.println("Failed at reading client packet for DiyoTweaks.");
            e.printStackTrace();
        }
    }

    void handleServerPacket (Packet250CustomPayload packet, EntityPlayerMP player)
    {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte packetID;

        try
        {
            packetID = inputStream.readByte();

            switch (packetID)
            {
            case 1:
                break;
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed at reading server packet for DiyoTweaks.");
            e.printStackTrace();
        }
    }

    Entity getEntity (World world, int id)
    {
        for (Object o : world.loadedEntityList)
        {
            if (((Entity) o).entityId == id)
                return (Entity) o;
        }
        return null;
    }
}
