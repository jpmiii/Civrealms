package com.jpmiii.Civrealms;

//import java.util.Iterator;

//import org.bukkit.Material;
import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.jpmiii.Civrealms.CivrealmsTask;

import net.milkbowl.vault.permission.Permission;

public class Civrealms extends JavaPlugin implements Listener {
	public Permission perms = null;

	public void onEnable() {
		// getLogger().info("onEnable has been invoked!");

		this.saveDefaultConfig();

		getServer().getPluginManager().registerEvents(
				this, this);
		setupPermissions();
		BukkitTask t = new CivrealmsTask(this).runTaskTimer(this, 6000, 6000);

/*

		final Iterator<Recipe> recipes = getServer().recipeIterator();
		Recipe recipe;
		ItemStack result;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) == null)
				continue;

			if ((result = recipe.getResult()) == null)
				continue;

			if (result.getType() == Material.ENCHANTMENT_TABLE)
				recipes.remove();
		}*/


	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public void onDisable() {

		getLogger().info("onDisable has been invoked!");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void jail(EntityDamageByEntityEvent event) {
		if ((event.getEntityType() == EntityType.PLAYER)
				&& (event.getDamager().getType() == EntityType.PLAYER)
				&& ((((Damageable) event.getEntity()).getHealth() - event
						.getDamage()) < 0)) {

			ItemStack inhand = ((Player) event.getDamager()).getItemInHand();
			if (inhand.hasItemMeta()) {
				String iname = inhand.getItemMeta().getDisplayName();
				Location jloc = null;
				if (iname.equalsIgnoreCase("freetown_jail")) jloc = new Location(this.getServer().getWorld("world"), -902.0, 65.0, 1570.0);
				if (iname.equalsIgnoreCase("otherplace")) jloc = new Location(this.getServer().getWorld("world_nether"), 6000.0, 33.0, 0.0);
	
				if (jloc != null) {
					event.setCancelled(true);
					((Damageable) event.getEntity()).setHealth(20.0);
					for (ItemStack is : ((Player) event.getEntity()).getInventory().getContents()){
						if (is != null) {
							if (is.getType() != Material.AIR) event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), is);
						}
					}
					for (ItemStack is2 : ((Player) event.getEntity()).getInventory().getArmorContents()){
						if (is2 != null) {
							if (is2.getType() != Material.AIR) event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), is2);
						}
					}
					((Player) event.getEntity()).getInventory().clear();
					((Player) event.getEntity()).getInventory().setArmorContents(null);
					((Player) event.getEntity()).setBedSpawnLocation(jloc, true);
					
					
					
					event.getEntity().teleport(jloc);
					
				}
			}

		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void spawnmove(PlayerRespawnEvent event) {
		if (!event.isBedSpawn()) {
			Location curloc = event.getRespawnLocation();
			boolean look =true;
			while (look) {
				World wld = getServer().getWorld(
						this.getConfig().getString("worldName"));
				Integer xloc = ((int) (Math.random() * 200 - 100 + curloc
						.getX()));
				Integer zloc = ((int) (Math.random() * 200 - 100 + curloc
						.getZ()));
				Integer yloc = wld.getHighestBlockYAt(xloc, zloc);
				if (wld.getBlockAt(xloc, yloc-1, zloc).getType() == Material.GRASS) {
					look = false;
					wld.setSpawnLocation(xloc, yloc, zloc);
					this.getLogger().info("spawn moved");
				}
			}

		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {


		if (cmd.getName().equalsIgnoreCase("civ")) {
			// doSomething

			if (!(sender instanceof Player)) {
				this.reloadConfig();

				getLogger().info("config reloaded");
				return true;
			}

			Player player = (Player) sender;
			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("end")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig()
								.getString("worldName") + "_the_end");
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("nether")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig()
								.getString("worldName") + "_nether");
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("world")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig()
								.getString("worldName"));
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}

			}

		}

		return false;
	}

}
