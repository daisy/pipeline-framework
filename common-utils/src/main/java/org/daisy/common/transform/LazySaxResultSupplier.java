package org.daisy.common.transform;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import com.google.common.base.Supplier;

public class LazySaxResultSupplier implements Supplier<Result>{

	private String systemId;


	/**
	 * Constructs a new instance.
	 *
	 * @param systemId The systemId for this instance.
	 */
	public LazySaxResultSupplier(String systemId) {
		this.systemId=systemId;
	}


	@Override
	public Result get() {
		SAXResult src=new ProxiedSAXResult();
		src.setSystemId(this.systemId);
		return src;
	}
	private class ProxiedSAXResult extends SAXResult{

		@Override
		public void setSystemId(String systemId) {
			super.setSystemId(systemId);
			LazySaxResultSupplier.this.systemId=systemId;
		}

	}
}
