package com.oracle.resources;

import java.util.List;

import javax.validation.Validator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.core.Booking;
import com.oracle.db.BookingsDAO;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
public class BookingsResource {

	Logger log = LoggerFactory.getLogger(BookingsResource.class);

	BookingsDAO dao;
	
    public BookingsResource(BookingsDAO dao) {
        this.dao = dao;
    }
    
	@GET
    public Response getAllBookings() {
		List<Booking> list = dao.getAllBookings();
		dao.close();
		if (list.size() == 0) {
			String msg = "{'status':'No records found...'}";
			return Response.ok(msg).build();
		}
		return Response.ok(list).build();
    }

	@GET
	@Path("/userid/{userid}")
    public Response getBookingsByUserId(@PathParam("userid") String userid) {
		List<Booking> list = dao.getBookingsByUserId(userid);
		dao.close();
		if (list.size() == 0) {
			String msg = "{'status':'No records found...'}";
			return Response.ok(msg).build();
		}
		return Response.ok(list).build();
    }

	@GET
	@Path("/cinemaid/{cinemaid}")
    public Response getBookingsByCinemaId(@PathParam("cinemaid") String cinemaid) {
		List<Booking> list = dao.getBookingsByCinemaId(cinemaid);
		dao.close();
		if (list.size() == 0) {
			String msg = "{'status':'No records found...'}";
			return Response.ok(msg).build();
		}
		return Response.ok(list).build();
    }

	@GET
	@Path("/showdate/{showdate}")
    public Response getBookingsByShowDate(@PathParam("showdate") String showdate) {
		List<Booking> list = dao.getBookingsByShowDate(showdate);
		dao.close();
		if (list.size() == 0) {
			String msg = "{'status':'No records found...'}";
			return Response.ok(msg).build();
		}
		return Response.ok(list).build();
    }

	@POST
	public Response insertBooking(Booking booking) {
		int result = dao.insert(booking.getUser_id(), booking.getShow_date(), booking.getCinema_id());
		dao.close();
		String msg = (result == 1) ? "{\"status\":\"Successfully inserted...\"}" : "{\"status\":\"Failed to insert...\"}";
		return Response.ok(msg).build();
	}

	@PUT
	@Path("/{userid}/{showdate}")
	public Response updateBooking(Booking booking, @PathParam("userid") String userid, @PathParam("showdate") String showdate) {
		int result = dao.update(booking.getUser_id(), booking.getShow_date(), booking.getCinema_id(), userid, showdate);
		dao.close();
		String msg = (result == 1) ? "{\"status\":\"Successfully updated...\"}" : "{\"status\":\"Failed to update...\"}";
		return Response.ok(msg).build();
	}

	@DELETE
	@Path("/{userid}/{showdate}")
	public Response deleteBooking(@PathParam("userid") String userid, @PathParam("showdate") String showdate) {
		int result = dao.delete(userid, showdate);
		dao.close();
		String msg = (result == 1) ? "{\"status\":\"Successfully deleted...\"}" : "{\"status\":\"Failed to delete...\"}";
		return Response.ok(msg).build();
	}
}
