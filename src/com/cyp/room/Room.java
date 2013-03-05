package com.cyp.room;

public class Room {
	
	private String name;
	
	private int size;
	
	private int freeSlots;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getFreeSlots() {
		return freeSlots;
	}
	public void setFreeSlots(int freeSlots) {
		this.freeSlots = freeSlots;
	}
}
