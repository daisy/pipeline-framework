package org.daisy.pipeline.job;

import org.daisy.common.base.Provider;
import javax.xml.transform.Result;

/**
 * This class is not thread-safe
 */
public class DynamicResultProvider implements Provider<Result>{

	private final String prefix;
	private final String suffix;
	private int count=0;

	public DynamicResultProvider(String prefix,String suffix){
		this.prefix=prefix;
		this.suffix=suffix;
	};

	/**
	 * The results returned by this method will not support setting the systemId and a expcetion will be thrown
	 */
	@Override
	public Result provide() {
		String sysId=null;
		if ( count==0){
			sysId=String.format("%s%s",prefix,suffix);
		}else{
			sysId=String.format("%s-%d%s",prefix,count,suffix);
		}
		count++;
		return new DynamicResult(sysId);
			
	}

	private static class DynamicResult implements Result{
		private final String systemId;

		/**
		 * Constructs a new instance.
		 *
		 * @param systemId The systemId for this instance.
		 */
		public DynamicResult(String systemId) {
			this.systemId = systemId;
		}

		@Override
		public String getSystemId() {
			return systemId;
		}

		@Override
		public void setSystemId(String arg0) {
			throw new UnsupportedOperationException(String.format("%s does not support modifying the systemId",DynamicResult.class));
		}

		

	}

	
}
