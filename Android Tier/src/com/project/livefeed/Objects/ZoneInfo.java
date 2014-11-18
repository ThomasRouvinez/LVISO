package com.project.livefeed.Objects;

public class ZoneInfo{

	// Variables.
	private int zoneID;
	private double x;
	private double y;
	private double radius;
	
	// Constuctor.
	public ZoneInfo(){};
	
	public ZoneInfo(int zoneID, double x, double y, double radius){
		this.setZoneID(zoneID);
		this.setX(x);
		this.setY(y);
		this.setRadius(radius);
	}

	// Getters-Setters.
	public int getZoneID() {
		return zoneID;
	}
	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
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

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
}