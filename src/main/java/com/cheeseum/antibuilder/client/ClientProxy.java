package com.cheeseum.antibuilder.client;

import com.cheeseum.antibuilder.client.BlockAntiBuilderRenderer;
import com.cheeseum.antibuilder.common.CommonProxy;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerBlockHandler(new BlockAntiBuilderRenderer());
	}
}
