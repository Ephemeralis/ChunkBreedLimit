package com.github.ephemeralis.chunkbreedlimit;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class ChunkBreedLimit extends JavaPlugin {
	@Override
	public void onEnable()
	{
		getLogger().info("ChunkBreedLimit loaded!");
	}
	
	@Override
	public void onDisable()
	{
		getLogger().info("ChunkBreedLimit unloaded!");
	}
}
