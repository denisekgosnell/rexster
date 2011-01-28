package com.tinkerpop.rexster.servlet.gremlin;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of currently running gremlin sessions. Each one is associated
 * with a web client and a particular graph hosted within Rexster.
 * 
 * Credit to Neo Technology (http://neotechnology.com/) for most of the code related to the 
 * Gremlin Terminal in Rexster.  Specifically, this code was borrowed from 
 * https://github.com/neo4j/webadmin and re-purposed for Rexster's needs.
 * 
 * Original author Jacob Hansson <jacob@voltvoodoo.com>
 * 
 */
public class ConsoleSessions {

	protected static ConcurrentHashMap<String, ConsoleSession> sessions = new ConcurrentHashMap<String, ConsoleSession>();

	/**
	 * Gets a GremlinSession for a given session identifier and graph name, 
	 * creating a GremlinSession if one does not exist.  
	 */
	public static ConsoleSession getSession(String sessionId, String graphName) {
		ensureSessionExists(sessionId, graphName);
		return sessions.get(sessionId + graphName);
	}
	
	public static ConsoleSession findSessionByKey(String sessionKey) {
		return sessions.get(sessionKey);
	}

	/**
	 * Destroy a session
	 * @param sessionId The unique identifier for the session.
	 * @param graphName The name of the graph the Gremlin session is connected to.
	 */
	public static void destroySession(String sessionId, String graphName) {
		destroySession(sessionId + graphName);
	}

	/**
	 * Destroy a session using the session key.
	 * 
	 * @param sessionKey The session identifier concatenated with the graph name.
	 */
	public static void destroySession(String sessionKey) {
		sessions.get(sessionKey).die();
		sessions.remove(sessionKey);
	}
	
	public static void destroyAllSessions() {
		Iterator<String> keys = sessions.keySet().iterator();
		while (keys.hasNext()) {
			destroySession(keys.next());
		}
	}

	public static boolean hasSession(String sessionId, String graphName) {
		return hasSessionKey(sessionId + graphName);
	}
	
	public static boolean hasSessionKey(String sessionKey) {
		return sessions.containsKey(sessionKey);
	}

	public static Collection<String> getSessionKeys() {
		return sessions.keySet();
	}

	protected static void ensureSessionExists(String sessionId, String graphName) {
		String key = sessionId + graphName;
		if (!sessions.containsKey(key)) {
			sessions.put(key, new ConsoleSession(graphName));
		}
	}

}