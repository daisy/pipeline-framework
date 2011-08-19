package org.daisy.pipeline.modules.converter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.xproc.NamedValue;



public class ExtendedXprocDefinition {

		public enum Direction {
			INPUT, OUTPUT,NA
		}	
		protected HashMap< String, ExtendedPort > mInputPorts = new HashMap<String, ExtendedPort>();
		protected  HashMap< String, ExtendedPort> mOutputPorts = new HashMap<String, ExtendedPort>();
		protected HashMap< String, ExtendedPort > mParameterPorts = new HashMap<String, ExtendedPort>();
		protected HashMap<String , ExtendedOption> mOptions= new HashMap<String, ExtendedOption>();
		protected URI mXprocUri;
		public Map<String,ExtendedPort> getInputPorts(){
			return new HashMap<String, ExtendedPort>(mInputPorts);
		}
		public Map<String,ExtendedPort> getOutputPorts(){
			return new HashMap<String, ExtendedPort>(mOutputPorts);
		} 
		public Map<String,ExtendedPort> getParameterPorts(){
			return new HashMap<String, ExtendedPort>(mParameterPorts);
		} 
		
		public Map<String,ExtendedOption> getOptions(){
			return new HashMap<String, ExtendedOption>(mOptions);
		} 
	/**
		 * @param xprocUri the xprocUri to set
		 */
		public void setXprocUri(URI xprocUri) {
			mXprocUri = xprocUri;
		}
		/**
		 * @return the xprocUri
		 */
		public URI getXprocUri() {
			return mXprocUri;
		}
	static class ExtendedPort{
		String mName;
		String mDescription;
		
		public ExtendedPort(String name, String description) {
			mName = name;
			mDescription = description;
		}
		public String getName() {
			return mName;
		}
		public void setName(String name) {
			mName = name;
		}
		public String getDescription() {
			return mDescription;
		}
		public void setDescription(String description) {
			mDescription = description;
		}
	}
		
	static class ExtendedOption{
		
		String mDataType;
		String mDefinition;
		Direction mDirection;
		public ExtendedOption( String dataType,
				String definition, Direction direction) {
			
			mDataType = dataType;
			mDefinition = definition;
			mDirection = direction;
		}
		public String getDataType() {
			return mDataType;
		}
		public void setDataType(String dataType) {
			mDataType = dataType;
		}
		public String getDefinition() {
			return mDefinition;
		}
		public void setDefinition(String definition) {
			mDefinition = definition;
		}
		public Direction getDirection() {
			return mDirection;
		}
		public void setDirection(Direction direction) {
			mDirection = direction;
		}
		 
	}
}
