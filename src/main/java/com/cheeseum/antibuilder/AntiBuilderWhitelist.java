package com.cheeseum.antibuilder;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;

public class AntiBuilderWhitelist {
	private List<WhitelistData> blockWhitelist = new ArrayList<WhitelistData>();
	
    protected static class WhitelistData {
    	Block block;
    	int meta;
    	
    	public WhitelistData (Block block, int meta) {
    		this.block = block;
    		this.meta = meta;
    	}
    	@Override
    	public boolean equals (Object o) {
    		if (o instanceof WhitelistData) {
    			WhitelistData d = (WhitelistData)o;
    			return (d.block.equals(this.block) && d.meta == this.meta);
    		}
    		return false;
    	}
    	@Override
    	public int hashCode () {
    		return block.hashCode() + meta;
    	}
    }
    
    public boolean isBlockWhitelisted (Block block, int meta) {
    	return this.blockWhitelist.contains(new WhitelistData(block, -1)) ||
    			this.blockWhitelist.contains(new WhitelistData(block, meta));
    }
    
    public void addBlockFromString (String entry) {
		entry = entry.replace("\"", "");
		entry = entry.replace("'", "");
		String entryData[] = entry.split(":");
		if (entryData.length < 2) {
			AntiBuilder.logger.warn("Invalid filter entry %s", entry);
		} else {
			Block block = GameRegistry.findBlock(entryData[0], entryData[1]);
			int meta = entryData.length > 2 ? Integer.parseInt(entryData[2]) : -1;
			if (block == null) {
				AntiBuilder.logger.warn("No such block %s:%s found for filter!", entryData[0], entryData[1]);
			} else {
				this.addBlock(block, meta);
			}
		}
	}
    
    public void addBlock (Block block, int meta) {
    	this.blockWhitelist.add(new WhitelistData(block, meta));
    }
}
