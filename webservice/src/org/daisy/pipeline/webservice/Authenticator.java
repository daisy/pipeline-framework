package org.daisy.pipeline.webservice;

import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Authenticator {
	
	// 10 minutes in ms
	// TODO make configurable
	private static final long MAX_REQUEST_TIME = 600000;
	
	// just for testing
    private static final Map<String, String> clients = new HashMap<String, String>();
    static {
        clients.put("clientkey", "supersecret");
    }
	
	public static boolean authenticate(String key, String hash, String timestamp, String URI) {
		// rules for hashing: use the whole URL string, minus the hash param & value
		// e.g. 
		// http://localhost:8182/ws/script?id=theID&key=mykey&timestamp=12345
		// becomes
		// theHash = hash(http://localhost:8182/ws/script?id=theID&key=mykey&timestamp=12345, clientSecret)
		// then submit:
		// http://localhost:8182/ws/script?id=theID&key=mykey&timestamp=12345&hash=theHash
		// important!  put the hash last so we can easily strip it out
		
		int idx = URI.indexOf("&hash=", 0);
		if (idx > 1) {
			String hashuri = URI.substring(0, idx);
			String clientSecret = clients.get(key);
			String serverHash = "";
			try {
				serverHash = Authenticator.calculateRFC2104HMAC(hashuri, clientSecret);
				
				SimpleDateFormat UTC_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				UTC_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
				
				Date serverTimestamp = new Date(System.currentTimeMillis());
				Date clientTimestamp;
				try {
					clientTimestamp = UTC_FORMATTER.parse(timestamp);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				if(hash.equals(serverHash) && 
						serverTimestamp.getTime() - clientTimestamp.getTime() < MAX_REQUEST_TIME) {
					return true;
				}
				else {
					return false;
				}
			} catch (SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		else return false;
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
	public static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException {
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