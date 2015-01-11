package com.cheeseum.antibuilder;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;

public class TileEntityAntiBuilder extends TileEntity {
	protected static int DEFAULT_RANGE = 16; 
	protected static int MAX_RANGE = 128; 
	
	public static class OffsetData {
		public int xNeg = DEFAULT_RANGE;
		public int xPos = DEFAULT_RANGE;
		public int yNeg = DEFAULT_RANGE;
		public int yPos = DEFAULT_RANGE;
		public int zNeg = DEFAULT_RANGE;
		public int zPos = DEFAULT_RANGE;
		
		public OffsetData() {};
		public OffsetData(OffsetData d) {
			this.xNeg = d.xNeg;
			this.xPos = d.xPos;
			this.yNeg = d.yNeg;
			this.yPos = d.yPos;
			this.zNeg = d.zNeg;
			this.zPos = d.zPos;
		};
	}
	
	public OffsetData offsets;

	public Block facadeBlock;
	public int facadeMeta;
	
	public TileEntityAntiBuilder() {
		MinecraftForge.EVENT_BUS.register(this);
		this.offsets = new OffsetData();
	}
	
	protected boolean areCoordsInRange(double x, double y, double z) {
		return Math.abs(x - this.xCoord) <= MAX_RANGE && 
				Math.abs(z - this.zCoord) <= MAX_RANGE &&
				Math.abs(y - this.yCoord) <= MAX_RANGE;
	}
	
	protected boolean isEntityInRange(EntityLivingBase p) {
		return p != null && areCoordsInRange(p.posX, p.posY, p.posZ);
	}
	
	protected boolean isBlockInRange(int x, int y, int z) {
		return this.xCoord - offsets.xNeg <= x && this.xCoord + offsets.xPos >= x
				&& this.zCoord - offsets.zNeg <= z && this.zCoord + offsets.zPos >= z
				&& this.yCoord - offsets.yNeg <= y && this.yCoord + offsets.yPos >= y;
	}
	
	@SubscribeEvent
	public boolean onBlockBreakEvent(BlockEvent.BreakEvent e) {
		if (e.block instanceof BlockAntiBuilder) {
			return true;
		} else {
			EntityPlayer p = e.getPlayer();
			if (isEntityInRange(p) && isBlockInRange(e.x, e.y, e.z)
					&& !AntiBuilder.breakWhitelist.isBlockWhitelisted(e.block, e.blockMetadata)) {
				if (p != null && !p.capabilities.isCreativeMode) {
					e.setCanceled(true);
				}
			}
		}
			
		return true;
	}
	
	@SubscribeEvent
	public boolean onBlockPlaceEvent(BlockEvent.PlaceEvent e) {
		if (e.placedBlock instanceof BlockAntiBuilder) {
			return true;
		} else {
			EntityPlayer p = e.player;
			if (isEntityInRange(p) && isBlockInRange(e.x, e.y, e.z)
					&& !AntiBuilder.placeWhitelist.isBlockWhitelisted(e.block, e.blockMetadata)) {
				if (p != null && !p.capabilities.isCreativeMode) {
					e.setCanceled(true);
				}
			}
		}
			
		return true;
		
	}

	@SubscribeEvent
	public boolean onBlockMultiPlaceEvent(BlockEvent.MultiPlaceEvent e) {
		if (e.placedBlock instanceof BlockAntiBuilder) {
			return true;
		} else {
			EntityPlayer p = e.player;
			if (isEntityInRange(p) && isBlockInRange(e.x, e.y, e.z)
					&& !AntiBuilder.placeWhitelist.isBlockWhitelisted(e.block, e.blockMetadata)) {
				if (p != null && !p.capabilities.isCreativeMode) {
					e.setCanceled(true);
				}
			}
		}
			
		return true;
		
	}
	
	@SubscribeEvent
	public boolean onExplosionEvent(ExplosionEvent.Detonate e) {
		if (areCoordsInRange(e.explosion.explosionX, e.explosion.explosionY, e.explosion.explosionZ)) {
			List<ChunkPosition> affectedBlocks = e.getAffectedBlocks();
			Iterator<ChunkPosition> it = affectedBlocks.iterator();
			while (it.hasNext()) {
				ChunkPosition cp = it.next();
				if (isBlockInRange(cp.chunkPosX, cp.chunkPosY, cp.chunkPosZ)
					&& !AntiBuilder.breakWhitelist.isBlockWhitelisted(
							e.world.getBlock(cp.chunkPosX, cp.chunkPosY, cp.chunkPosZ),
							e.world.getBlockMetadata(cp.chunkPosX, cp.chunkPosY, cp.chunkPosZ))) {
					// FIXME: add block whitelist
					it.remove();
				}
			}
		}
		//affectedBlocks.clear();
		return true; 
	}

	@Override
	public void invalidate() {
		// unregister from our check events, NOT SURE if this is reliably called
		// not unregistering results in ghost listeners still preventing block breakage
		MinecraftForge.EVENT_BUS.unregister(this);
		super.invalidate();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		this.writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		
		NBTTagCompound offsetsTag = tagCompound.getCompoundTag("offsets");
		this.offsets.xNeg = offsetsTag.getByte("xNeg");
		this.offsets.xPos = offsetsTag.getByte("xPos");
		this.offsets.yNeg = offsetsTag.getByte("yNeg");
		this.offsets.yPos = offsetsTag.getByte("yPos");
		this.offsets.zNeg = offsetsTag.getByte("zNeg");
		this.offsets.zPos = offsetsTag.getByte("zPos");
		
		int blockId = tagCompound.getInteger("facadeBlockId");
		if (blockId > 0) {
			this.facadeBlock = Block.getBlockById(blockId);
			this.facadeMeta = tagCompound.getInteger("facadeMeta");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		
		NBTTagCompound offsetsTag = new NBTTagCompound();
		offsetsTag.setByte("xNeg", (byte) this.offsets.xNeg);
		offsetsTag.setByte("xPos", (byte) this.offsets.xPos);
		offsetsTag.setByte("yNeg", (byte) this.offsets.yNeg);
		offsetsTag.setByte("yPos", (byte) this.offsets.yPos);
		offsetsTag.setByte("zNeg", (byte) this.offsets.zNeg);
		offsetsTag.setByte("zPos", (byte) this.offsets.zPos);
		
		tagCompound.setTag("offsets", offsetsTag);
		tagCompound.setInteger("facadeBlockId", Block.getIdFromBlock(this.facadeBlock));
		tagCompound.setInteger("facadeMeta", this.facadeMeta);
	}

}
