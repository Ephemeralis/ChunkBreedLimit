package com.github.ephemeralis.chunkbreedlimit;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkBreedLimit extends JavaPlugin {
	
	public int entitySpawnCap;
	public List<EntityType> allowedEntityList = new ArrayList<EntityType>();
	public Hashtable<EntityType, Integer> individCapData = new Hashtable<EntityType, Integer>();
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		entitySpawnCap = this.getConfig().getInt("spawn-cap", 100);
		boolean individualCap = this.getConfig().getBoolean("use-individual-cap", false);
		String breedingFailMessage = this.getConfig().getString("breeding-fail-msg", "The animal cannot breed - it is too crowded!");
		
		if (individualCap)
		{
			List<String> entityListLoad = this.getConfig().getStringList("entity-list-individual");
			if (entityListLoad.size() == 0)
			{
				getLogger().info("Entity list is invalid for individual cap loading! Reverting to defaults..");
				
				individCapData.put(EntityType.COW, 100);
				individCapData.put(EntityType.CHICKEN, 100);
				individCapData.put(EntityType.PIG, 100);
				individCapData.put(EntityType.SHEEP, 100);
			}
			else
			{
				for (String entry : entityListLoad)
				{
					String[] temp = entry.split(",");
					int tempint = Integer.parseInt(temp[1].trim());
					EntityType temptype = EntityType.fromName(temp[0].toUpperCase());
					getLogger().info(String.format("%s, %s", temptype, tempint));
					individCapData.put(temptype, tempint);
				}
			}
			
			new BreederListener(this, entitySpawnCap, individCapData, breedingFailMessage);
			getLogger().info("Watching individual entity limits");
		}
		else
		{
			List<String> entityListLoad = this.getConfig().getStringList("entity-list");
			if (entityListLoad.size() == 0)
			{
				getLogger().info("Entity list appears invalid! Reverting to defaults..");
				
				allowedEntityList.add(EntityType.COW);
				allowedEntityList.add(EntityType.CHICKEN);
				allowedEntityList.add(EntityType.PIG);
				allowedEntityList.add(EntityType.SHEEP);
			} else {
				//load list from config
				
				getLogger().info("Loading breeding deny list from config..");
				
				for (String ent : entityListLoad)
				{
						EntityType temp = EntityType.fromName(ent);
						allowedEntityList.add(temp);
				}
			}
			
			new BreederListener(this, entitySpawnCap, allowedEntityList, breedingFailMessage);
			getLogger().info(String.format("Now watching: %s when entity chunk count above %s", allowedEntityList.toString(), entitySpawnCap));
		}
		
	}
	
	@Override
	public void onDisable()
	{
		CreatureSpawnEvent.getHandlerList().unregister(this);
		PlayerInteractEntityEvent.getHandlerList().unregister(this);
		getLogger().info("ChunkBreedLimit unloaded!");
	}
}
