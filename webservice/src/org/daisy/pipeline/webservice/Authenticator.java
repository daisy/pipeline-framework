package org.daisy.pipeline.webservice;

import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.daisy.pipeline.webservice.db.WSClient;
import org.daisy.pipeline.webservice.db.WSRequestLogEntry;
import org.daisy.pipeline.webservice.db.WSDatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authenticator {
	
	private static Logger logger = LoggerFactory.getLogger(PipelineWebService.class.getName());
	
	public static boolean authenticate(String key, String hash, String timestamp, String nonce, String URI, long maxRequestTime, String dbPath) {
		// rules for hashing: use the whole URL string, minus the hash part (&sign=<some value>)
		// important!  put the sign param last so we can easily strip it out
		
		int idx = URI.indexOf("&sign=", 0);
		
		if (idx > 1) {
			String hashuri = URI.substring(0, idx);
			String clientSecret = getClientSecret(key, dbPath);
			String serverHash = "";
			try {
				serverHash = calculateRFC2104HMAC(hashuri, clientSecret);
				
				SimpleDateFormat UTC_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				UTC_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				Date serverTimestamp = new Date(System.currentTimeMillis());
				Date clientTimestamp;
				try {
					clientTimestamp = UTC_FORMATTER.parse(timestamp);
				} catch (ParseException e) {
					logger.warn("Could not parse timestamp: " + timestamp);
					e.printStackTrace();
					return false;
				}                                                                                                                                                                                                                                                                               
				if(!hash.equals(serverHash)) {
					logger.warn("Hash values do not match");
					return false;
				}
				if (serverTimestamp.getTime() - clientTimestamp.getTime() > maxRequestTime) {
					logger.warn("Request expired");
					return false;
				}
				if (!checkValidNonce(key, nonce, timestamp, dbPath)) {
					logger.warn("Invalid nonce");
					return false;
				}
				return true;
				
			} catch (SignatureException e) {
				logger.warn("Could not generate hash");
				e.printStackTrace();
				return false;
			}
		}
		else return false;
	}
	
	
	// nonces, along with timestamps, protect against replay attacks
	private static boolean checkValidNonce(String key, String nonce, String timestamp, String dbPath) {
		WSDatabaseManager manager = new WSDatabaseManager(dbPath);
		WSClient client = manager.getClientByKey(key);
		if (client == null) {
			//TODO: log error
			return false;
		}
		
		WSRequestLogEntry entry = new WSRequestLogEntry(client.getId(), nonce, timestamp);
		
		// if this nonce was already used with this timestamp, don't accept it again
		boolean isDuplicate = manager.isDuplicate(entry);
		if (isDuplicate) {
			//TODO: log error
			return false;
		}
		
		// else, it is unique and therefore ok
		manager.addObject(entry);
		return true;
			
	}
	
	private static String getClientSecret(String key, String dbPath) {
		
		WSDatabaseManager manager = new WSDatabaseManager(dbPath);
		WSClient client = manager.getClientByKey(key);
		if (client != null) {
			return client.getSecret();
		}
		return "";		
	}
	
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";


	// adapted slightly from 
	// http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/index.html?AuthJavaSampleHMACSignature.html
	/**
	* Computes RFC 2104-compliant HMAC signature.
	* * @param data
	* The data to be signed.
	* @param key
	* The signing key.
	* @return
	* The Base64-encoded RFC 2104-compliant HMAC signature.
	* @throws
	* java.security.SignatureException when signature generation fails
	*/
	private static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException {
		String result;
		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
		
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());
		
			// base64-encode the hmac
			result = Base64.encodeBase64String(rawHmac);
		
			} catch (Exception e) {
				throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}
	
	
	
}