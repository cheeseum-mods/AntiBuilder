package com.cheeseum.antibuilder.gui;

import com.cheeseum.antibuilder.TileEntityAntiBuilder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class AntiBuilderGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if (ID == 0) {
			return new GuiAntiBuilderBlock((TileEntityAntiBuilder) world.getTileEntity(x, y, z));
		}
		
		return null;
	}

}
