package com.oracle;

import static spark.Spark.*;

import java.util.List;
import java.util.Optional;
//import java.util.Properties;

import com.google.gson.Gson;

public class App 
{
    public static final Optional<String> host;
    public static final Optional<String> port;
//    public static final Properties myProps = new Properties();
    private static Gson gson = new Gson();
//    return gson.toJson(model);
    
    static {
        host = Optional.ofNullable(System.getenv("HOSTNAME"));
        port = Optional.ofNullable(System.getenv("PORT"));
    }

    public static void main( String[] args )
    {
        ipAddress(host.orElse("localhost"));
    	port(Integer.parseInt(port.orElse("8126"))); 
    	
    	ShowTimesDAO edao = new ShowTimesListDAO();

    	get("/showtimes", (request, response) -> {
    		String result = gson.toJson(edao.getAllShowTimes().toArray(new ShowTime[0]));
    		return result;
    	}); //, new JsonTransformer()

    	get("/showtimes/showdate/:showdate", (request, response) -> {
    		String show_date = request.params(":showdate");
    		List matchList = edao.getShowsByDate(show_date);
    		if (matchList.size() > 0) {
    			response.status(200);
    			String result = gson.toJson((ShowTime[])matchList.toArray(new ShowTime[0]));
    			return result;
    		} else {
    			response.status(400);
    			return null;
    		}
    	});

    	get("/showtimes/showtime/:showtime", (request, response) -> {
    		String show_time = request.params(":showtime");
    		List matchList = edao.getShowsByTimes(show_time);
    		if (matchList.size() > 0) {
    			response.status(200);
    			String result = gson.toJson((ShowTime[])matchList.toArray(new ShowTime[0]));
    			return result;
    		} else {
    			response.status(400);
    			return null;
    		}
    	});

    	get("/showtimes/cinemaid/:cinemaid", (request, response) -> {
    		String cinema_id = request.params(":cinemaid");
    		List matchList = edao.getShowsByCinemaId(cinema_id);
    		if (matchList.size() > 0) {
    			response.status(200);
    			String result = gson.toJson((ShowTime[])matchList.toArray(new ShowTime[0]));
    			return result;
    		} else {
    			response.status(400);
    			return null;
    		}
    	});

    	post("/showtimes", (request, response) -> {
    		ShowTime showtime = gson.fromJson(request.body(), ShowTime.class);
    		if (edao.add(showtime)) {
    			response.status(200);
    			return "{'status':'Successfully inserted...'}";
    		} else {
    			response.status(400);
    			return "{'status':'Failed to insert the record...'}";
    		}
    	});

    	put("/showtimes/:sdate/:stime", (request, response) -> {
    		String sdate = request.params(":sdate");
    		String stime = request.params(":stime");
    		ShowTime showtime = gson.fromJson(request.body(), ShowTime.class);
    		if (edao.update(sdate, stime, showtime)) {
    			response.status(200);
    			return "{'status':'Successfully updated...'}";
    		} else {
    			response.status(400);
    			return "{'status':'Failed to update the record...'}";
    		}
    	});

    	delete("/showtimes/:sdate/:stime", (request, response) -> {
    		String sdate = request.params(":sdate");
    		String stime = request.params(":stime");
    		boolean result = edao.delete(sdate, stime);
    		if (result) {
    			response.status(200);
    			return "{'status':'Successfully deleted...'}";
    		} else {
    			response.status(400);
    			return "{'status':'Failed to delete the record...'}";
    		}
    	});

    }
}
