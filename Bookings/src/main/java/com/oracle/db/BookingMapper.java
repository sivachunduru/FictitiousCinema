package com.oracle.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.oracle.core.Booking;

public class BookingMapper implements ResultSetMapper<Booking> {

	@Override
	public Booking map(int arg0, ResultSet arg1, StatementContext arg2) throws SQLException {
		Booking booking = new Booking(arg1.getString("user_id"), arg1.getString("show_date"), arg1.getString("cinema_id"));
		return booking;
	}

}