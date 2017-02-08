package com.oracle.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.oracle.core.Booking;

@RegisterMapper(BookingMapper.class)
public interface BookingsDAO {
	@SqlQuery("select user_id, show_date, cinema_id from orders order by show_date, cinema_id")
	List<Booking> getAllBookings();
	
	@SqlQuery("select user_id, show_date, cinema_id from orders where user_id=:userid order by show_date, cinema_id")
	List<Booking> getBookingsByUserId(@Bind("userid") String userid);
	
	@SqlQuery("select user_id, show_date, cinema_id from orders where DATE_FORMAT(show_date, '%Y-%m-%d')=:showdate order by user_id, cinema_id")
	List<Booking> getBookingsByShowDate(@Bind("showdate") String showdate);
	
	@SqlQuery("select user_id, show_date, cinema_id from orders where cinema_id=:cinemaid order by show_date, user_id")
	List<Booking> getBookingsByCinemaId(@Bind("cinemaid") String cinemaid);
	
	@SqlUpdate("insert into orders (user_id, show_date, cinema_id) values (:userid, DATE_FORMAT(:showdate, '%Y-%m-%d'), :cinemaid)")
	int insert(@Bind("userid") String userid, @Bind("showdate") String showdate, @Bind("cinemaid") String cinemaid);

	@SqlUpdate("update orders set user_id=:userid, show_date=DATE_FORMAT(:showdate, '%Y-%m-%d'), cinema_id=:cinemaid where user_id=:uid and DATE_FORMAT(show_date, '%Y-%m-%d')=:sdate")
	int update(@Bind("userid") String userid, @Bind("showdate") String showdate, @Bind("cinemaid") String cinemaid, @Bind("uid") String uid, @Bind("sdate") String sdate);

	@SqlUpdate("delete from orders where user_id=:uid and DATE_FORMAT(show_date, '%Y-%m-%d')=:sdate")
	int delete(@Bind("uid") String uid, @Bind("sdate") String sdate);

	void close();
}
