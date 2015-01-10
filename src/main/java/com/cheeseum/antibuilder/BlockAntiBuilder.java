package com.cheeseum.antibuilder;

import java.util.Random;

import com.cheeseum.antibuilder.client.BlockAntiBuilderRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAntiBuilder extends BlockContainer {
	protected BlockAntiBuilder() {
		super(Material.rock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityAntiBuilder();
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hx, float hy, float hz) {
	
		if (player.capabilities.isCreativeMode) {
			TileEntityAntiBuilder te = (TileEntityAntiBuilder) world.getTileEntity(x, y, z);
			ItemStack heldItem = player.getHeldItem();
			if (heldItem == null) {
				if (te != null) {
					if (player.isSneaking()) {
						te.facadeBlock = null;
						te.facadeMeta = 0;
						world.markBlockForUpdate(x,y,z);
					} else {
						player.openGui(AntiBuilder.instance, 0, world, x, y, z);
					}
				}
			} else {
				Block heldBlock = Block.getBlockFromItem(heldItem.getItem());
				if (heldBlock != null && !(heldBlock instanceof BlockAntiBuilder)) { //&& heldBlock.isBlockNormalCube()) {
					if (te != null) {
						te.facadeBlock = heldBlock;
						te.facadeMeta = heldItem.getItemDamage();
						world.markBlockForUpdate(x, y, z);
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockAntiBuilderRenderer.antibuilderModel;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		double dx = 0.5 + x + random.nextFloat() - 0.5;
		double dy = 0.5 + y + random.nextFloat() - 0.5;
		double dz = 0.5 + z + random.nextFloat() - 0.5;
		double v = random.nextFloat() * 0.2 - 0.1;
		
		world.spawnParticle("largesmoke", dx, dy, dz, v, v, v);
		world.spawnParticle("witchMagic", dx, dy, dz, v, v, v);
		world.spawnParticle("witchMagic", dx, dy, dz, v, v, v);
	}
}
