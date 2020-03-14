package io.zeebe.cockpit;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.Topology;

@Path("/status")
public class StatusResource {

	@Inject
	CockpitApp cockpitApp;

	@GET
	@Path("/connection")
	@Produces(MediaType.TEXT_PLAIN)
	public Response connection() {
		ZeebeClient client = cockpitApp.getZeebeClient();
		
		
		try {
			client.newTopologyRequest().send().get();
			return Response.ok().entity("Connected").build();
		} catch (Exception e) {
			return Response.ok().entity("Not connected: " + e.getMessage())
					.build();
		}
	}
	
	@GET
	@Path("/topology")
	@Produces(MediaType.APPLICATION_JSON)
	public Topology toplogy() throws InterruptedException, ExecutionException {
		ZeebeClient client = cockpitApp.getZeebeClient();
		return client.newTopologyRequest().send().get();	
	}
	
}
