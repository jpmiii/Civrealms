package com.psygate.sprawn.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.jpmiii.Civrealms.Civrealms;

public class SettingsLoader {
	private static LinkedList<Character> numeric = new LinkedList<Character>();
	static {
		numeric.add('0');
		numeric.add('1');
		numeric.add('2');
		numeric.add('3');
		numeric.add('4');
		numeric.add('5');
		numeric.add('6');
		numeric.add('7');
		numeric.add('8');
		numeric.add('9');
	}

	public static List<WorldSetting> loadConfiguration(Civrealms sp) {
		LinkedList<WorldSetting> settings = new LinkedList<WorldSetting>();

		for (World world : sp.getServer().getWorlds()) {
			boolean load = getBoolean("worlds." + world.getName() + ".enabled", sp, false);
			if (load) {
				boolean overrideWB = getBoolean("worlds." + world.getName() + ".overrideWorldBorderSettings", sp, false);
				Shape shape = getShape("worlds." + world.getName() + ".shape", sp, Shape.ELLIPTICAL);
				int xradius = getInt("worlds." + world.getName() + ".x-radius", sp, 1000);
				int zradius = getInt("worlds." + world.getName() + ".z-radius", sp, 1000);
				int x = getInt("worlds." + world.getName() + ".center-x", sp, 1000);
				int z = getInt("worlds." + world.getName() + ".center-z", sp, 1000);
				long change = getTime("worlds." + world.getName() + ".spawnrotation", sp);

				WorldSetting s = new WorldSetting(overrideWB, shape, world.getName(), xradius, zradius, x, z, change);
				settings.add(s);
			}
		}

		return settings;
	}

	private static long getTime(String string, Civrealms sp) {
		String defset = "1h30m30s";
		if (!sp.getConfig().isString(string)) {
			sp.getConfig().set(string, defset);
			sp.saveConfig();
		} else {
			try {
				defset = sp.getConfig().getString(string);
			} catch (IllegalArgumentException e) {
				sp.getConfig().set(string, defset);
				sp.saveConfig();
			}
		}
		try {
			return parseTime(defset);
		} catch (IllegalArgumentException e) {
			Civrealms.errlog("[!]Illegal time format: " + defset+" -> Reverting to default of 1h30m30s.");
			return parseTime("1h30m30s");
		}
	}

	private static long parseTime(String timestr) throws IllegalArgumentException {
		long time = 1;
		boolean hset = timestr.indexOf("h") != timestr.lastIndexOf("h");
		boolean mset = timestr.indexOf("m") != timestr.lastIndexOf("m");
		boolean sset = timestr.indexOf("s") != timestr.lastIndexOf("s");
		if (hset || mset || sset) {
			throw new IllegalArgumentException();
		}

		String bfr = "";

		for (int i = 0; i < timestr.length(); i++) {
			Character chr = timestr.charAt(i);
			if (numeric.contains(chr)) {
				bfr += chr;
			} else {
				if (chr.equals('h')) {
					int parsed = Integer.parseInt(bfr);
					bfr = "";
					time += TimeUnit.HOURS.toMillis(parsed);
				} else if (chr.equals('m')) {
					int parsed = Integer.parseInt(bfr);
					bfr = "";
					time += TimeUnit.MINUTES.toMillis(parsed);
				} else if (chr.equals('s')) {
					int parsed = Integer.parseInt(bfr);
					bfr = "";
					time += TimeUnit.SECONDS.toMillis(parsed);
				} else {
					throw new IllegalArgumentException();
				}
			}
		}

		return time;
	}

	private static int getInt(String string, Civrealms sp, int i) {
		if (!sp.getConfig().isInt(string)) {
			sp.getConfig().set(string, i);
			sp.saveConfig();
			return i;
		} else {
			return sp.getConfig().getInt(string);
		}
	}

	private static Shape getShape(String string, Civrealms sp, Shape shape) {
		try {
			return Shape.valueOf(sp.getConfig().getString(string).toUpperCase());
		} catch (Exception e) {
			sp.getConfig().set(string, shape.name());
			sp.saveConfig();
			return shape;
		}
	}

	private static boolean getBoolean(String string, Civrealms sp, boolean b) {
		if (!sp.getConfig().isBoolean(string)) {
			sp.getConfig().set(string, b);
			sp.saveConfig();
			return b;
		} else {
			return sp.getConfig().getBoolean(string);
		}
	}
}
