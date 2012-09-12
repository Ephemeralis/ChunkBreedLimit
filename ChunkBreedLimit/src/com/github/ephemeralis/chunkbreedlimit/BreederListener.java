package com.github.ephemeralis.chunkbreedlimit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BreederListener implements Listener {
	
	private Logger outputLogger; 
	
	public BreederListener(ChunkBreedLimit plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		outputLogger = plugin.getLogger();
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BREEDING)
		{
			Entity[] entitiesInEventChunk = event.getLocation().getChunk().getEntities();
			
			int entcount = 0;
			for (Entity ent : entitiesInEventChunk)
			{
				//we have entity list, so iterate through it
				//to determine just how many of the same type
				//we have on the same chunk
				if (ent.getType() == event.getEntityType())
				{
					//same type detected, so increase
					entcount++;
				}
			}
			
			if (entcount > 5) //replace this with configuratio0n reader
			{
				outputLogger.log(Level.FINE, "Entity count exceeded, cancelling entity!!");
				event.setCancelled(true); //must be a better way to can the event
			}
		}
	
	}
}

