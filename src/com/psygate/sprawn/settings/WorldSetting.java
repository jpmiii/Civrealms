package com.psygate.sprawn.settings;

import org.bukkit.World;

public class WorldSetting {
	private boolean overrideWB = false;
	private Shape shape = Shape.ELLIPTICAL;
	private String world = null;
	private int xradius = 10, zradius = 10, x = 0, z = 0;
	private long change = 0;

	public WorldSetting(boolean overrideWB, Shape shape, String world2, int xradius, int zradius, int x, int z,
			long change) {
		super();
		this.overrideWB = overrideWB;
		this.shape = shape;
		this.world = world2;
		this.xradius = xradius;
		this.zradius = zradius;
		this.x = x;
		this.z = z;
		this.change = change;
	}

	public boolean isOverrideWB() {
		return overrideWB;
	}

	public void setOverrideWB(boolean overrideWB) {
		this.overrideWB = overrideWB;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getXradius() {
		return xradius;
	}

	public void setXradius(int xradius) {
		this.xradius = xradius;
	}

	public int getZradius() {
		return zradius;
	}

	public void setZradius(int zradius) {
		this.zradius = zradius;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public long getChange() {
		return change;
	}

	public void setChange(long change) {
		this.change = change;
	}
}
