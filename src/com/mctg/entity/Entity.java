package com.mctg.entity;

public class Entity {

	private String id;
	private double x;
	private double y;
	private double z;

	public Entity(String id, double x, double y, double z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}
