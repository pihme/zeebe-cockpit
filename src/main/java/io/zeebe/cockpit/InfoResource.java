package io.zeebe.cockpit;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/info")
public class InfoResource {
	
	@Inject
	Config config;

	@GET
	@Path("/config")
	@Produces(MediaType.APPLICATION_JSON)
	public Config config() {
		return config;
	}
}
