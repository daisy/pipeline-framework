package org.daisy.pipeline.webserviceutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Routes.class.getName());
	
	public static final String SCRIPTS_ROUTE = "/scripts";
	public static final String SCRIPT_ROUTE = "/scripts/{id}";
	public static final String JOBS_ROUTE = "/jobs";
	public static final String JOB_ROUTE = "/jobs/{id}";
	public static final String LOG_ROUTE = "/jobs/{id}/log";
	public static final String RESULT_ROUTE = "/jobs/{id}/result";
	public static final String HALT_ROUTE = "/admin/halt";
	public static final String CLIENTS_ROUTE = "/admin/clients";
	public static final String CLIENT_ROUTE = "/admin/clients/{id}";
	
	public static final String PORT_PROPERTY = "org.daisy.pipeline.ws.port";
	public static final String PATH_PROPERTY = "org.daisy.pipeline.ws.path";
	public static final String HOST_PROPERTY = "org.daisy.pipeline.ws.host";
	public static final String LOCAL_MODE_PROPERTY = "org.daisy.pipeline.ws.local";
	
	private String path = "/ws";
	private static final int LOCAL_PORT_DEF=8181;
	private static final int REMOTE_PORT_DEF=8182;
	private int portNumber = 0;
	private String host = "http://localhost";
	
	public Routes() {
		readOptions();
	}
	
	public String getHost() {
		return host;
	}
	public String getPath() {
		return path;
	}
	public int getPort() {
		return portNumber;
	}
	public String getBaseUri() {
		return host + ":" + String.valueOf(portNumber) + path;
	}
	
	private void readOptions() {
		String path = System.getProperty(PATH_PROPERTY);
		if (path != null) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			this.path = path;
		}
		
		String hostname = System.getProperty(HOST_PROPERTY);
		if (hostname != null) {
			host = hostname;
		}
		
		String port = System.getProperty(PORT_PROPERTY);
		if (port != null) {
			try {
				int portnum = Integer.parseInt(port);
				if (portnum >= 0 && portnum <= 65535) {
					portNumber = portnum;
				}
				else {
					logger.error(String.format(
							"Value specified in option %s (%d) is not valid. Using default value of %d.",
							PORT_PROPERTY, portnum, portNumber));
				}
			} catch (NumberFormatException e) {
				logger.error(String.format(
						"Value specified in option %s (%s) is not a valid numeric value. Using default value of %d.",
						PORT_PROPERTY, port, portNumber));
			}
		} else {
			portNumber = isLocal() ? LOCAL_PORT_DEF : REMOTE_PORT_DEF;
		}

	}
	
	public boolean isLocal() {
		return Boolean.valueOf(System.getProperty(LOCAL_MODE_PROPERTY));
	}	
	
}