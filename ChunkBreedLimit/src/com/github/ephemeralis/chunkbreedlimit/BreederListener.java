package com.github.ephemeralis.chunkbreedlimit;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class BreederListener implements Listener {
	
	private ChunkBreedLimit basePlugin;
	private int spawnCap;
	private List<EntityType> allowedEntities;
	private Hashtable<EntityType, Integer> individualCapData;
	private String breedingFailMessage;
	private boolean individualEntityCap;
	
	public BreederListener(ChunkBreedLimit plugin, int cap, List<EntityType> allowedEnts, String bmsg)
	{
		//no individual entity counting
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		spawnCap = cap;
		allowedEntities = allowedEnts;
		breedingFailMessage = bmsg;
		individualEntityCap = false;
	}
	
	public BreederListener(ChunkBreedLimit plugin, int cap, Hashtable<EntityType, Integer> capdata, String bmsg)
	{
		//individual entity counting
		basePlugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		individualCapData = capdata;
		breedingFailMessage = bmsg;
		allowedEntities = new ArrayList<EntityType>();
		individualEntityCap = true;
		
		Enumeration<EntityType> entityTypes = capdata.keys();
		
		while (entityTypes.hasMoreElements())
		{
			EntityType temp = entityTypes.nextElement();
			allowedEntities.add(temp);
		}
		
		if (allowedEntities.size() == 0)
		{
			plugin.getLogger().info("Entity list is length 0!");
		}

	}
	
	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING 
				|| event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG
				|| event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
		{
			
			if (individualEntityCap && allowedEntities.contains(event.getEntityType()))
			{
				event.setCancelled(checkAndApplyEntityWithCap(event.getEntityType(), 
						event.getLocation().getChunk(),
						individualCapData.get(event.getEntityType())));
			}
			else
			{
				//otherwise, just use standard checking
				event.setCancelled(checkAndApplyEntity(event.getEntityType(), 
						event.getLocation().getChunk()));
			}
			
		} 
	}
	
	public boolean checkAndApplyEntityWithCap(EntityType ent, Chunk chunk, Integer cap)
	{
		/*
		 * Checks and applies entity cap using individual cap limits
		 */
		Entity[] entitiesInEventChunk = chunk.getEntities();
		
		Integer entcount = 0;
		for (Entity entt : entitiesInEventChunk)
		{
			//we have entity list, so iterate through it
			//to determine just how many of the same type
			//we have on the same chunk
			if (entt.getType() == ent)
			{
				//same type detected, so increase
				entcount++;
			}
		}
		
		if (entcount >= cap)
		{
			return true;
		} 
		else
		{
			return false;
		}
	}
	
	public boolean checkAndApplyEntity(EntityType ent, Chunk chunk)
	{
		/*
		 * Checks and applies entity cap using global cap limit
		 */
		Entity[] entitiesInEventChunk = chunk.getEntities();
		
		int entcount = 0;
		for (Entity entt : entitiesInEventChunk)
		{
			//we have entity list, so iterate through it
			//to determine just how many of the same type
			//we have on the same chunk
			if (entt.getType() == ent)
			{
				//same type detected, so increase
				entcount++;
			}
		}
		
		if (entcount >= spawnCap)
		{
			return true;
		} 
		else
		{
			return false;
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event)
	{
		Player p = event.getPlayer();
		
		if ((p.getItemInHand().getType() == Material.WHEAT ||
				p.getItemInHand().getType() == Material.RAW_BEEF ||
				p.getItemInHand().getType() == Material.PORK ||
				p.getItemInHand().getType() == Material.COOKED_BEEF ||
				p.getItemInHand().getType() == Material.RAW_FISH)
				&& allowedEntities.contains(event.getRightClicked().getType()))
		{
			int entcount = 0;
			//player is holding wheat and the entity is allowed, check
			for (Entity ent : event.getRightClicked().getLocation().getChunk().getEntities())
			{
				if (allowedEntities.contains(ent.getType()))
					entcount++;
			}
			
			if (entcount >= spawnCap)
			{
				p.sendMessage(ChatColor.RED + breedingFailMessage);
				event.setCancelled(true);	
			}
		}
		
	}
	
}


