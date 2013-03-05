package com.cyp.room;


public class Contact {
	
	private String username;
	
	private int rating;
			
	public Contact(String username){
		this.username = username;
		this.rating = 1500;;				
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}		
}
