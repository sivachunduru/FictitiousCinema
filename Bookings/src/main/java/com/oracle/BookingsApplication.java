package com.oracle;

import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.db.BookingsDAO;
import com.oracle.resources.BookingsResource;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BookingsApplication extends Application<BookingsConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BookingsApplication.class);
	
    public static void main(final String[] args) throws Exception {
        new BookingsApplication().run(args);
    }

    @Override
    public String getName() {
        return "Bookings";
    }

    @Override
    public void initialize(final Bootstrap<BookingsConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final BookingsConfiguration configuration,
                    final Environment environment) {
    	LOGGER.info("Registering BookingsResource...");
    	final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
        final BookingsDAO dao = jdbi.onDemand(BookingsDAO.class);
    	environment.jersey().register(new BookingsResource(dao));
//    	environment.jersey().register(new BookingsResource(environment.getValidator()));
    }

}
