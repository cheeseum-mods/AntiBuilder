package com.cheeseum.antibuilder.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.cheeseum.antibuilder.BlockAntiBuilder;
import com.cheeseum.antibuilder.TileEntityAntiBuilder;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class AntiBuilderMsgPreventBuild implements IMessage {
	public int x;
	public int y;
	public int z;
	
	public AntiBuilderMsgPreventBuild() {
	}
	
	public AntiBuilderMsgPreventBuild(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	public static class Handler implements IMessageHandler<AntiBuilderMsgPreventBuild, IMessage> {
		@Override
		public IMessage onMessage(AntiBuilderMsgPreventBuild message,	MessageContext ctx) {
			World world = Minecraft.getMinecraft().theWorld;
			for (int i=0; i < 10; i++) {
				BlockAntiBuilder.spawnRandomParticles(world, message.x, message.y, message.z, world.rand);
			}
			return null;
		}
		
	}

}
