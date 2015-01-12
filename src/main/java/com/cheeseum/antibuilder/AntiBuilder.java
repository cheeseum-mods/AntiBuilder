
package com.cheeseum.antibuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cheeseum.antibuilder.client.BlockAntiBuilderRenderer;
import com.cheeseum.antibuilder.common.CommonProxy;
import com.cheeseum.antibuilder.gui.AntiBuilderGuiHandler;
import com.cheeseum.antibuilder.network.AntiBuilderMsgPreventBuild;
import com.cheeseum.antibuilder.network.AntiBuilderMsgUpdate;

import scala.reflect.internal.Trees.This;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

@Mod( 
	name="AntiBuilder Block",
	modid=AntiBuilder.MODID,
	version=AntiBuilder.VERSION,
	dependencies="required-after:Forge@[10.13.2.1277,]"
)
public class AntiBuilder
{
	public static final String MODID = "antibuilder";
	public static final String VERSION = "@VERSION@";
	
	@Mod.Instance
	public static AntiBuilder instance;
	public static final Logger logger = LogManager.getFormatterLogger("AntiBuilder");
	public static SimpleNetworkWrapper network;
	
	@SidedProxy(clientSide="com.cheeseum.antibuilder.client.ClientProxy", serverSide="com.cheeseum.antibuilder.common.CommonProxy")
	public static CommonProxy proxy;
	
	// Blocks
	public static BlockAntiBuilder blockAntiBuilder;
	
	public static AntiBuilderWhitelist breakWhitelist = new AntiBuilderWhitelist();	
	public static AntiBuilderWhitelist placeWhitelist = new AntiBuilderWhitelist();	
    private Configuration config;
    private boolean populateConfig = false;
    
    public AntiBuilder() {
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	// Init Config
    	File configFile = event.getSuggestedConfigurationFile();
        this.config = new Configuration(configFile);
        this.config.load();
        this.config.addCustomCategoryComment(config.CATEGORY_GENERAL, "General Config");

        // Init block(s)
        blockAntiBuilder = (BlockAntiBuilder)new BlockAntiBuilder().setBlockName("antibuilder").setBlockTextureName("antibuilder:antibuilder");
        GameRegistry.registerBlock(this.blockAntiBuilder, "antibuilder");
        GameRegistry.registerTileEntity(TileEntityAntiBuilder.class, "antibuilderte");
       
        proxy.registerRenderers();
       
        // Network Handlers
        network = NetworkRegistry.INSTANCE.newSimpleChannel("AntiBuilder");
        network.registerMessage(AntiBuilderMsgUpdate.Handler.class, AntiBuilderMsgUpdate.class, 0, Side.SERVER);
        network.registerMessage(AntiBuilderMsgPreventBuild.Handler.class, AntiBuilderMsgPreventBuild.class, 1, Side.CLIENT);
        
        // Register GUI(s)
        NetworkRegistry.INSTANCE.registerGuiHandler(this.instance, new AntiBuilderGuiHandler());
       
        if (this.config.hasChanged()) {
        	this.config.save();
        }
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	// Load the whitelists, done in post-init after mods register blocks
    	
        Property breakWhitelistConfig = this.config.get(config.CATEGORY_GENERAL, "breakWhitelist", new String[0],
        		"whitelist for breaking, one entry per line, format is 'modid:blockid:meta'" +
        		" or just 'modid:blockid' to match any metadata, vanilla modid is 'minecraft'");
        Property placeWhitelistConfig = this.config.get(config.CATEGORY_GENERAL, "placeWhitelist", new String[0],
        		"whitelist for placing, one entry per line, format is 'modid:blockid:meta'" +
        		" or just 'modid:blockid' to match any metadata, vanilla modid is 'minecraft'");
        
        if (breakWhitelistConfig.isList()) {
        	for (String entry : breakWhitelistConfig.getStringList()) {
        		breakWhitelist.addBlockFromString(entry);
        	}
        }
        
        if (placeWhitelistConfig.isList()) {
        	for (String entry : placeWhitelistConfig.getStringList()) {
        		placeWhitelist.addBlockFromString(entry);
        	}
        }
    }
}
