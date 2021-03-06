package com.jpmiii.Civrealms;
//

//import java.util.Iterator;

//import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import com.jpmiii.Civrealms.CivrealmsTask;

















import net.milkbowl.vault.permission.Permission;

public class Civrealms extends JavaPlugin implements Listener {
	public Permission perms = null;
	public Map<String, Object> jailList = new HashMap<String, Object>();
	public Map<String, Object> jailPlayers = new HashMap<String, Object>();
	BukkitTask gentask = null;
	Random rand = new Random();


	public void onEnable() {
		// getLogger().info("onEnable has been invoked!");

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		jailList = this.getConfig().getConfigurationSection("jailList").getValues(false);
		jailPlayers = this.getConfig().getConfigurationSection("jailPlayers").getValues(false);
		getServer().getPluginManager().registerEvents(this, this);
		setupPermissions();
		BukkitTask t = new CivrealmsTask(this).runTaskTimer(this, 2400, 2400);

		/*
		final Iterator<Recipe> recipes = getServer().recipeIterator();
		Recipe recipe;
		ItemStack result;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) == null)
				continue;

			if ((result = recipe.getResult()) == null)
				continue;

			if (result.getType() == Material.GOLDEN_CARROT)
				recipes.remove();
		}
		*/

		ShapedRecipe recipe1 = new ShapedRecipe(new ItemStack(Material.GOLDEN_CARROT,
				1));
		recipe1.shape("BBB", "BAB", "BBB");
		recipe1.setIngredient('A', Material.GOLD_NUGGET);
		recipe1.setIngredient('B', Material.COBBLESTONE);

		getServer().addRecipe(recipe1);
		


	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}


	public void onDisable() {

		getLogger().info("onDisable has been invoked!");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void jail(EntityDamageByEntityEvent event) {
		if ((event.getEntityType() == EntityType.PLAYER) && (event.getDamager().getType() == EntityType.PLAYER)
				&& ((((Damageable) event.getEntity()).getHealth() - event.getDamage()) < 0)) {
			// this.getLogger().info("deathblow");
			if ((((Player) event.getDamager()).hasPermission("civ.jail") && !event.getDamager().getWorld().getName()
					.equalsIgnoreCase("world_the_end"))) {
				// this.getLogger().info("ptest");

				ItemStack inhand = ((Player) event.getDamager()).getItemInHand();
				if (inhand.hasItemMeta() && !inhand.getItemMeta().getDisplayName().isEmpty()) {
					String iname = inhand.getItemMeta().getDisplayName().toLowerCase();
					// this.getLogger().info(iname);
					Location jloc = null;
					if (jailList.containsKey(iname)) {
						// this.getLogger().info(iname + ": found");
						String[] locl = ((String) jailList.get(iname)).split(",");
						if (Integer.parseInt(locl[4]) >= this.getConfig().getInt("votes")) {
							this.getLogger().info(((Player) event.getEntity()).getName() + "  Jailed in  " + iname);

							jloc = new Location(this.getServer().getWorld(locl[0]), Float.parseFloat(locl[1]),
									Float.parseFloat(locl[2]), Float.parseFloat(locl[3]));
							event.setCancelled(true);
							((Damageable) event.getEntity()).setHealth(20.0);
							for (ItemStack is : ((Player) event.getEntity()).getInventory().getContents()) {
								if (is != null) {
									if (is.getType() != Material.AIR)
										event.getEntity().getWorld()
												.dropItemNaturally(event.getEntity().getLocation(), is);
								}
							}
							for (ItemStack is2 : ((Player) event.getEntity()).getInventory().getArmorContents()) {
								if (is2 != null) {
									if (is2.getType() != Material.AIR)
										event.getEntity().getWorld()
												.dropItemNaturally(event.getEntity().getLocation(), is2);
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
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void arrowDamage(EntityDamageEvent event) {

		if (event.isCancelled() || (event.getDamage() == 0)) {
			return;
		}

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

			if (e.getEntityType() == EntityType.PLAYER) {
				Player target = (Player) e.getEntity();
				Entity dmgr = e.getDamager();
				double dmg = event.getDamage();

				
				if (dmgr instanceof Player) {
					if (!((Player)dmgr).hasPermission("civ.fulldamage")) {
						event.setDamage(dmg*.1);
					}
						
				}

				if (dmgr instanceof Projectile) {

					ProjectileSource dmgrplayer = ((Projectile) dmgr).getShooter();
					if (dmgrplayer instanceof Player) {
					double heightdiff = ((Player) dmgrplayer).getLocation()
								.getY() - target.getLocation().getY();

						if (heightdiff > 3) {

							dmg = dmg + heightdiff
									- 3.0;

						}
						
						if (!((Player)dmgrplayer).hasPermission("civ.fulldamage")) {
							dmg = dmg*.1;
							
						}


						event.setDamage(dmg);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void changeBlockDropEvent(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.PACKED_ICE) {
			event.getBlock().setType(Material.AIR);
			event.getBlock()
					.getWorld()
					.dropItem(event.getBlock().getLocation(),
							new ItemStack(Material.PACKED_ICE, 1));
			event.setCancelled(true);
		}
		if (event.getBlock().getType() == Material.ICE) {
			event.getBlock().setType(Material.AIR);
			event.getBlock()
					.getWorld()
					.dropItem(event.getBlock().getLocation(),
							new ItemStack(Material.ICE, 1));
			event.setCancelled(true);
		}
		

	}
	
	
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void firstjoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) {
			event.getPlayer().getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
			event.getPlayer().getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
			event.getPlayer().getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
			event.getPlayer().getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
			this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "kit start ".concat(event.getPlayer().getName()) );
			this.getServer().broadcastMessage("new player: ".concat(event.getPlayer().getName()) );
			//event.getPlayer().teleport(new Location(this.getServer().getWorld("world"),2517.0,74.0,2637.0));
		}
		
		
	}
	/*
	@EventHandler(priority = EventPriority.MONITOR)
	public void quit(PlayerQuitEvent event) {
		combatApi.tagPlayer(event.getPlayer());
	}
*/	
	@EventHandler(priority = EventPriority.HIGH)
	public void magigpotion(PlayerItemConsumeEvent event) {
		ItemStack itst = event.getItem();
		
		if (itst.getType() == Material.POTION && itst.hasItemMeta()) {
			if (itst.getItemMeta().hasDisplayName()) {
				//event.setItem(new ItemStack(Material.GLASS_BOTTLE, 1));
				this.getLogger().info("named potion drank");
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,1200,1));
			}
		}
		
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void spawnmove(PlayerRespawnEvent event) {
		
		if (!event.isBedSpawn()) {
			Location curloc = event.getRespawnLocation();
			boolean look = true;
			int lookcount = 0;
			World wld = getServer().getWorld(this.getConfig().getString("worldName"));
			while (look) {
				
				Integer xloc = ((int) (rand.nextInt(230) - 100 + curloc.getX()));
				if (xloc > 2200){
					xloc = xloc - 3400;
				}
				if (xloc < -1200){
					xloc = xloc + 3400;
				}
				Integer zloc = ((int) (rand.nextInt(300) - 100 + curloc.getZ()));
				if (zloc < -2400){
					zloc = zloc + 6000;
				}
				if (zloc > 3600){
					zloc = zloc - 6000;
				}
				Integer yloc = wld.getHighestBlockYAt(xloc, zloc);
				Material mat = wld.getBlockAt(xloc, (yloc - 1), zloc).getType();
				
				if ((mat == Material.GRASS)	|| (mat == Material.SAND) || (mat == Material.HARD_CLAY)) {
					look = false;
					wld.setSpawnLocation(xloc, yloc, zloc);
					this.getLogger().info(
							"spawn moved " + xloc.toString() + ":" + yloc.toString() + ":" + zloc.toString());
				}
				lookcount++;
				if (lookcount > 100) {
					this.getLogger().info("spawn not moved");
					look = false;
				}
			}

		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemUse(PlayerInteractEvent event)
	{

	    Block block = event.getClickedBlock();
	    if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && block.getType() == Material.JUKEBOX)
	    {
	        Material material = event.getItem().getType();
	        if(material == Material.GOLD_RECORD)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2256);
	        else if(material == Material.GREEN_RECORD)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2257);
	        else if(material == Material.RECORD_3)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2258);
	        else if(material == Material.RECORD_4)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2259);
	        else if(material == Material.RECORD_5)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2260);
	        else if(material == Material.RECORD_6)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2261);
	        else if(material == Material.RECORD_7)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2262);
	        else if(material == Material.RECORD_8)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2263);
	        else if(material == Material.RECORD_9)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2264);
	        else if(material == Material.RECORD_10)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2265);
	        else if(material == Material.RECORD_11)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2266);
	        else if(material == Material.RECORD_12)
	            block.getWorld().playEffect(block.getLocation(), Effect.RECORD_PLAY, 2267);
	    }
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("jjail")) {
			if (!(sender instanceof Player)) {

				return true;
			}
			Player player = (Player) sender;
			if (args.length > 0) {
				if (jailList.containsKey(args[0].toLowerCase())) {
					jailPlayers.put(player.getName(), args[0].toLowerCase());

					String Rem = "no";

					for (String jailname : jailList.keySet()) {
						int vcount = 0;
						for (Object plvote : jailPlayers.values()) {
							if (((String) plvote).equalsIgnoreCase(jailname))
								vcount = vcount + 1;

							this.getLogger().info(jailname + ": " + plvote + ": " + vcount);

						}
						if (vcount > 0) {
							String[] locl = ((String) jailList.get(jailname)).split(",");
							jailList.put(jailname, locl[0] + "," + locl[1] + "," + locl[2] + "," + locl[3] + ","
									+ vcount);

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
					// this.getLogger().info(args[0]);
					player.sendMessage("voted for jail: " + args[0].toLowerCase());
					return true;

				} else {
					jailPlayers.put(player.getName(), args[0].toLowerCase());
					jailList.put(args[0].toLowerCase(), player.getWorld().getName() + ","
							+ player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + ","
							+ player.getLocation().getBlockZ() + ",1");

					for (String jailname : jailList.keySet()) {
						int vcount = 0;
						for (Object plvote : jailPlayers.values()) {
							if (((String) plvote).equalsIgnoreCase(jailname))
								vcount = vcount + 1;

						}
						if (vcount > 0) {
							String[] locl = ((String) jailList.get(jailname)).split(",");
							jailList.put(jailname, locl[0] + "," + locl[1] + "," + locl[2] + "," + locl[3] + ","
									+ vcount);

						} else {
							jailList.remove(jailname);
							player.sendMessage("removed jail: " + jailname);
						}
					}
					this.getConfig().createSection("jailList", jailList);
					this.getConfig().createSection("jailPlayers", jailPlayers);
					this.saveConfig();
					this.getLogger().info("added jail " + args[0].toLowerCase());
					player.sendMessage("added jail " + args[0].toLowerCase());
					return true;

				}
			}

		}
		if (cmd.getName().equalsIgnoreCase("biol")) {
			for (Biome b: Biome.values()) {
				this.getLogger().info(b.name());
			}
		}
		if (cmd.getName().equalsIgnoreCase("bad")) {
			if (!(sender instanceof Player)) {

				return true;
			}
			Player player = (Player) sender;
			if (player.hasPermission("civ.ban")) {
				if (args.length > 0) {
					Player targ = this.getServer().getPlayer(
							args[0]);
					if (!targ.hasPermission("civ.unban")) {
						targ.setBanned(true);
						if (targ.isOnline()) {
							targ.getPlayer().kickPlayer("banned");
							
						}
						player.sendMessage(args[0] + " banned");

					} else {
						player.sendMessage(args[0] + " is registered");
					}
					return true;
					
				}
			} else {
				player.sendMessage("Not enough permission");
				return true;
			}

			
		}

		if (cmd.getName().equalsIgnoreCase("civ")) {
			// doSomething

			if (!(sender instanceof Player)) {
				this.reloadConfig();
				jailList = this.getConfig().getConfigurationSection("jailList").getValues(false);
				jailPlayers = this.getConfig().getConfigurationSection("jailPlayers").getValues(false);

				getLogger().info("config reloaded");
				return true;
			}

			Player player = (Player) sender;
			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("end")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig().getString("worldName") + "_the_end");
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("nether")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig().getString("worldName") + "_nether");
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("world")) {
					if (player.isOp()) {
						World nether = Bukkit.getWorld(this.getConfig().getString("worldName"));
						Location loc = nether.getSpawnLocation();
						((Player) sender).teleport(loc);
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("setspawn")) {
					if (player.isOp()) {
						World wld = player.getWorld();
						wld.setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player
								.getLocation().getBlockZ());
						return true;
					}
					return true;
				}

			}

		}

		return false;
	}

}
