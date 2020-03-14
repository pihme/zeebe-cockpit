package io.zeebe.cockpit;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.vertx.core.impl.ConcurrentHashSet;
import io.zeebe.client.api.response.Topology;

@ServerEndpoint("/status/topology/feed")
@ApplicationScoped
public class TopologyFeed {

	private static final Logger LOGGER = Logger.getLogger("io.zeebe.cockpit");

	Set<Session> sessions = new ConcurrentHashSet<>();

	ObjectWriter objectWriter = new ObjectMapper().writer();

	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);

		LOGGER.info("Session opened " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);

		LOGGER.info("Session closed " + session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		sessions.remove(session);

		LOGGER.error("Session erred " + session.getId() + throwable.getMessage(), throwable);
	}

	@OnMessage
	public void onMessage(String message) {

	}

	public void broadcast(Topology topology) {
		try {
			String data = objectWriter.writeValueAsString(topology);
			sessions.forEach(s -> {
				s.getAsyncRemote().sendObject(data, result -> {
					if (result.getException() != null) {
						System.out.println("Unable to send message: " + result.getException());
					}
				});
			});

		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}
}
