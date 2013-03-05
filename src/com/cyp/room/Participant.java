package com.cyp.room;

public class Participant {
	
	private String jid;
	
	private int slot;
	
	public Participant(String jid,int slot){
		this.jid = jid;
		this.slot = slot;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	@Override
	public String toString() {
		return "Participant [jid=" + jid + ", slot=" + slot + "]";
	}		
}
