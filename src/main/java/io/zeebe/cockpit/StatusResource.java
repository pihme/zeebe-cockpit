package io.zeebe.cockpit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
public class StatusResource {

	@Inject
	CockpitApp cockpitApp;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Status get() {
		return cockpitApp.getStatus();
	}
	
}
