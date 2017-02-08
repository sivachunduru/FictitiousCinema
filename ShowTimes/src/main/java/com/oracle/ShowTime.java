package com.oracle;

public class ShowTime {
	private String show_date;
	private String show_time;
	private String cinema_id;

	public String getShow_date() {
		return show_date;
	}

	public String getShow_time() {
		return show_time;
	}

	public String getCinema_id() {
		return cinema_id;
	}

	public ShowTime() {
		show_date = "";
		show_time = "";
		cinema_id = "";
	}
	
	public ShowTime(String show_date, String show_time, String cinema_id) {
		super();
		this.show_date = show_date;
		this.show_time = show_time;
		this.cinema_id = cinema_id;
	}

	@Override
	public String toString() {
		return "ShowTime [show_date=" + show_date + ", show_time=" + show_time + ", cinema_id=" + cinema_id + "]";
	}
	

}
