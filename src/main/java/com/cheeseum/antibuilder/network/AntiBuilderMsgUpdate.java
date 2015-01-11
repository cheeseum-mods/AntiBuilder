package com.cheeseum.antibuilder.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.cheeseum.antibuilder.TileEntityAntiBuilder;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class AntiBuilderMsgUpdate implements IMessage {
	public TileEntityAntiBuilder tileEntity;
	
	public AntiBuilderMsgUpdate() {
	}
	
	public AntiBuilderMsgUpdate(TileEntityAntiBuilder tileEntity) {
		this.tileEntity = tileEntity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int dimensionId = buf.readInt();
		World world = DimensionManager.getWorld(dimensionId);
		
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityAntiBuilder) {
			TileEntityAntiBuilder teAB = (TileEntityAntiBuilder)te;
			teAB.offsets.xNeg = buf.readByte();
			teAB.offsets.yNeg = buf.readByte();
			teAB.offsets.zNeg = buf.readByte();
			teAB.offsets.xPos = buf.readByte();
			teAB.offsets.yPos = buf.readByte();
			teAB.offsets.zPos = buf.readByte();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.tileEntity.getWorldObj().provider.dimensionId);
		buf.writeInt(this.tileEntity.xCoord);
		buf.writeInt(this.tileEntity.yCoord);
		buf.writeInt(this.tileEntity.zCoord);
		buf.writeByte(this.tileEntity.offsets.xNeg);
		buf.writeByte(this.tileEntity.offsets.yNeg);
		buf.writeByte(this.tileEntity.offsets.zNeg);
		buf.writeByte(this.tileEntity.offsets.xPos);
		buf.writeByte(this.tileEntity.offsets.yPos);
		buf.writeByte(this.tileEntity.offsets.zPos);
	}
	
	public static class Handler implements IMessageHandler<AntiBuilderMsgUpdate, IMessage> {
		@Override
		public IMessage onMessage(AntiBuilderMsgUpdate message,	MessageContext ctx) {
			return null;
		}
		
	}

}
