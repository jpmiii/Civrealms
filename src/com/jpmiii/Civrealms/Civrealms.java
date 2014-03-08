package com.jpmiii.Civrealms;

//import java.util.Iterator;

//import org.bukkit.Material;

import java.util.HashMap;

import java.util.Map;

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
	public Map<String, Object> jailList = new HashMap<String, Object>();
	public Map<String, Object> jailPlayers = new HashMap<String, Object>();
	public void onEnable() {
		// getLogger().info("onEnable has been invoked!");

		this.saveDefaultConfig();
		jailList =  this.getConfig().getConfigurationSection("jailList").getValues(false);
		jailPlayers =  this.getConfig().getConfigurationSection("jailPlayers").getValues(false);
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
			if (((Player)event.getDamager()).hasPermission("civ.jail")) {

				ItemStack inhand = ((Player) event.getDamager()).getItemInHand();
				if (inhand.hasItemMeta()) {
					String iname = inhand.getItemMeta().getDisplayName();
					Location jloc = null;
					if (jailList.containsKey(iname)) {
						String[] locl = ((String)jailList.get(iname)).split(",");
						if (Integer.parseInt(locl[4]) >= this.getConfig().getInt("votes")) {


						jloc = new Location(this.getServer().getWorld(locl[0]),Float.parseFloat(locl[1]), Float.parseFloat(locl[2]), Float.parseFloat(locl[3]));
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


					}}
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
		if (cmd.getName().equalsIgnoreCase("jjail")) {
			if (!(sender instanceof Player)) {

				return true;
			}	
			Player player = (Player) sender;
			if (args.length > 0) {
				if (jailList.containsKey(args[0])) {
					jailPlayers.put(player.getName(), args[0]);
					
					String Rem = "no";

					for (String jailname : jailList.keySet()) {
						int vcount = 0;
						for (Object plvote : jailPlayers.values()) {
							if (((String)plvote).equalsIgnoreCase(jailname)) vcount = vcount + 1;
								
							
							this.getLogger().info(jailname + ": " + plvote +": "+ vcount);

						}
						if (vcount > 0){
							String[] locl = ((String)jailList.get(jailname)).split(",");
							jailList.put(jailname, locl[0] + "," + locl[1] + "," + locl[2] + "," + locl[3] + "," + vcount);
							
						} else {
							Rem = jailname;
							
						}
					}
					if (!Rem.equalsIgnoreCase("no")) {
						jailList.remove(Rem);
							player.sendMessage("removed jail: " + Rem);
					}
					this.getConfig().createSection("jailList", jailList);
					this.getConfig().createSection("jailPlayers", jailPlayers);
					this.saveConfig();
					//this.getLogger().info(args[0]);
					player.sendMessage("voted for jail: " + args[0]);
					return true;
					


				} else {
					jailPlayers.put(player.getName(), args[0]);
					jailList.put(args[0], player.getWorld().getName() +"," + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() +","+ player.getLocation().getBlockZ() + ",1");

					for (String jailname : jailList.keySet()) {
						int vcount = 0;
						for (Object plvote : jailPlayers.values()) {
							if (((String)plvote).equalsIgnoreCase(jailname)) vcount = vcount + 1;

						}
						if (vcount > 0){
							String[] locl = ((String)jailList.get(jailname)).split(",");
							jailList.put(jailname, locl[0] + "," + locl[1] + "," + locl[2] + "," + locl[3] + "," + vcount);
							
						} else {
							jailList.remove(jailname);
							player.sendMessage("removed jail: " + jailname);
						}
					}
					this.getConfig().createSection("jailList", jailList);
					this.getConfig().createSection("jailPlayers", jailPlayers);
					this.saveConfig();
					this.getLogger().info(args[0]);
					player.sendMessage("added jail " + args[0]);
					return true;

				}
			}

		}

		if (cmd.getName().equalsIgnoreCase("civ")) {
			// doSomething

			if (!(sender instanceof Player)) {
				this.reloadConfig();
				jailList =  this.getConfig().getConfigurationSection("jailList").getValues(false);
				jailPlayers =  this.getConfig().getConfigurationSection("jailPlayers").getValues(false);


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
