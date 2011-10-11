package org.daisy.pipeline.webservice;

import java.util.HashMap;
import java.util.Map;

public class ClientStore {
	
	public static final ClientStore INSTANCE = new ClientStore();
	
	private ClientStore() {
		// singleton
	}
 
	
	// TODO: replace with DB
	private static final Map<String, String> clients = new HashMap<String, String>();
    static {
        clients.put("clientkey", "supersecret");
    }
	
    // map client key to a map of nonces and timestamps
    // TODO: put in a DB
    // TODO: clear this out periodically
	private Map<String, Map<String, String>> nonces = new HashMap<String, Map<String, String>>();
    
	public String getSecret(String key) {
		return clients.get(key);
	}
	
	// nonces, along with timestamps, protect against replay attacks
	public boolean checkValidNonce(String key, String nonce, String timestamp) {
		
		// if this client has no nonce entries, then this nonce is ok
		if (nonces.get(key) == null) {
			addNonce(key, nonce, timestamp);
			return true;
		}
		
		Map<String, String> alreadyUsed = nonces.get(key);
		
		// if this nonce was already used with this timestamp, don't accept it again
		if (alreadyUsed.get(nonce) == timestamp) {
			return false;
		}
		// else, it is unique and therefore ok
		else {
			addNonce(key, nonce, timestamp);
			return true;
		}
			
	}
	
	private void addNonce(String key, String nonce, String timestamp) {
		if (nonces.get(key) == null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(nonce, timestamp);
			nonces.put(key, map);
		}
		else {
			nonces.get(key).put(key, timestamp);
		}
	}
	
}