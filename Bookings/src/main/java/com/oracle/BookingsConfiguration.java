package com.oracle;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;

public class BookingsConfiguration extends Configuration {

	public BookingsConfiguration() {
		Optional<String> port = Optional.ofNullable(System.getenv("PORT"));
    	DefaultServerFactory factory = (DefaultServerFactory) getServerFactory();
    	HttpConnectorFactory cfactory = (HttpConnectorFactory) factory.getApplicationConnectors().get(0);
    	cfactory.setPort(Integer.parseInt(port.orElse("8128")));
	}

	@NotNull
    @Valid
    private DataSourceFactory database = new DataSourceFactory();

	@JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory database) {
        Optional<String> ipAddress = Optional.ofNullable(System.getenv("BOOKINGS_DATABASE_HOST"));
        String URL = "jdbc:mysql://"+ipAddress.orElse("192.168.99.100")+":3307/cinema";
        database.setUrl(URL);
		this.database = database;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

}
