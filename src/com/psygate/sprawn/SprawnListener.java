package com.psygate.sprawn;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.psygate.sprawn.settings.Shape;
import com.psygate.sprawn.settings.WorldSetting;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;

public class SprawnListener implements Listener {
	private WorldBorder wb;
	private HashMap<String, WorldSetting> worlds = new HashMap<String, WorldSetting>();

	public SprawnListener() {
		wb = null;
	}

	public SprawnListener(WorldBorder wb) {
		this.wb = wb;
	}

	@EventHandler
	public void spawn(PlayerJoinEvent ev) {
		if (!ev.getPlayer().hasPlayedBefore()) {
			if (worlds.containsKey(ev.getPlayer().getWorld().getName())) {
				WorldSetting s = worlds.get(ev.getPlayer().getWorld().getName());
				long time = System.currentTimeMillis();
				long seed = time - (time % s.getChange());
				Random rand = new Random(seed);
				if (s.isOverrideWB() || wb == null) {
					setSpawn(ev.getPlayer(), s, rand);
				} else if (wb != null) {
					setSpawnWB(ev.getPlayer(), rand);
				}
			}
		}
	}

	private void setSpawnWB(Player player, Random rand) {
		BorderData bd = wb.GetWorldBorder(player.getWorld().getName());
		if (bd == null) {
			return;
		} else {
			if (bd.getShape() == null || bd.getShape() == false) {
				ellipticalSpawn(player, rand, (int) bd.getX(), (int) bd.getZ(), bd.getRadiusX(), bd.getRadiusZ());
			} else {
				rectangularSpawn(player, rand, (int) bd.getX(), (int) bd.getZ(), bd.getRadiusX(), bd.getRadiusZ());
			}
		}
	}

	private void setSpawn(Player player, WorldSetting s, Random rand) {
		if (s.getShape() == Shape.ELLIPTICAL) {
			ellipticalSpawn(player, rand, s.getX(), s.getZ(), s.getXradius(), s.getZradius());
		} else if (s.getShape() == Shape.RECTANGULAR) {
			rectangularSpawn(player, rand, s.getX(), s.getZ(), s.getXradius(), s.getZradius());
		}

	}

	private void rectangularSpawn(Player player, Random rand, int x, int z, int xradius, int zradius) {
		int diffx = rand.nextInt(xradius) * ((rand.nextBoolean()) ? -1 : 1);
		int diffz = rand.nextInt(zradius) * ((rand.nextBoolean()) ? -1 : 1);

		portTo(player, x + diffx, z + diffz);
	}

	private void ellipticalSpawn(Player player, Random rand, int x, int z, int xrad, int zrad) {
		double angle = rand.nextDouble() * Math.PI * 2;

		int xpos = (int) (x + (Math.cos(angle) * xrad));
		int zpos = (int) (z + (Math.sin(angle) * zrad));
		portTo(player, xpos, zpos);
	}

	private void portTo(Player player, int xpos, int zpos) {
		player.teleport(player.getWorld().getHighestBlockAt(xpos, zpos).getLocation(), TeleportCause.PLUGIN);
	}

	public void setSettings(List<WorldSetting> settings) {
		for (WorldSetting setting : settings) {
			worlds.put(setting.getWorld(), setting);
		}
	}
}
