package org.daisy.common.transform;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import org.daisy.common.base.Provider;

public class LazySaxResultProvider implements Provider<Result>{

	private String systemId;


	/**
	 * Constructs a new instance.
	 *
	 * @param systemId The systemId for this instance.
	 */
	public LazySaxResultProvider(String systemId) {
		this.systemId=systemId;
	}


	@Override
	public Result provide() {
		SAXResult src=new ProxiedSAXResult();
		src.setSystemId(this.systemId);
		return src;
	}
	private class ProxiedSAXResult extends SAXResult{

		@Override
		public void setSystemId(String systemId) {
			super.setSystemId(systemId);
			LazySaxResultProvider.this.systemId=systemId;
		}

	}
}
