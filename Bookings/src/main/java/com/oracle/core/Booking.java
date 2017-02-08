package com.oracle.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {
	@JsonProperty
	private String user_id;
	@JsonProperty
	private String show_date;
	@JsonProperty
	private String cinema_id;
	
	public Booking() {
		this.user_id = "";
		this.show_date = "";
		this.cinema_id = "";
	}

	public Booking(String user_id, String show_date, String cinema_id) {
		this.user_id = user_id;
		this.show_date = show_date;
		this.cinema_id = cinema_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getShow_date() {
		return show_date;
	}

	public String getCinema_id() {
		return cinema_id;
	}

	@Override
	public String toString() {
		return "Booking [user_id=" + user_id + ", show_date=" + show_date + ", cinema_id=" + cinema_id + "]";
	}

}
