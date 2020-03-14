package io.zeebe.cockpit;

import java.time.Duration;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.Topology;

@ApplicationScoped
public class CockpitApp {

	private static final Logger LOGGER = Logger.getLogger("io.zeebe.cockpit");

	@Inject
	Config config;
	
	@Inject
	TopologyFeed topologyFeed;

	ZeebeClient zeebeClient;

	void onStart(@Observes StartupEvent ev) {
		LOGGER.info("The application is starting...");
		String contactPoint = config.getContactPoint();

		LOGGER.info("Configured to talk to " + contactPoint);

		zeebeClient = ZeebeClient.newClientBuilder().defaultRequestTimeout(Duration.ofSeconds(3))
				.brokerContactPoint(contactPoint).usePlaintext().build();
	}
	
	 @Scheduled(every="6s")     
	 void tick() {
		 
	    LOGGER.info("tick"); 
	    Topology topology = zeebeClient.newTopologyRequest().send().join();	
	    
	    topologyFeed.broadcast(topology);
	    
	    LOGGER.fine(topology.toString());
	 }


	void onStop(@Observes ShutdownEvent ev) {
		zeebeClient.close();
		LOGGER.info("The application is stopping...");
	}

	public ZeebeClient getZeebeClient() {
		return zeebeClient;
	}

}
