package org.daisy.properties;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PropertyTrackerTest {

	private static String BUNDLE_NAME="name";	
	private static String BUNDLE_NAME2="name2";	
	private static long BUNDLE_ID=1L;	
	private static String PROPERTY_NAME="property1";	
	private static String PROPERTY_NAME2="property2";	
	private static String PROPERTY_VALUE="value";	

	PropertyTracker propTracker;
	Property prop1;
	Property prop2;
	@Before
	public void setUp(){
		this.propTracker=new PropertyTracker();
		this.prop1=new Property.Builder().withValue(PROPERTY_VALUE).withPropertyName(PROPERTY_NAME).withBundleId(BUNDLE_ID).withBundleName(BUNDLE_NAME).build();
		this.prop2=new Property.Builder().withValue(PROPERTY_VALUE).withPropertyName(PROPERTY_NAME2).withBundleId(BUNDLE_ID).withBundleName(BUNDLE_NAME2).build();
		//check handle propery is tricky but we can consider that's been enough tested if the rest of cases work
		this.propTracker.handlePropery(this.prop1);
		this.propTracker.handlePropery(this.prop2);

	}

	/**
	 * Tests 'getProperties'.
	 *
	 * @see org.daisy.properties.PropertyTracker#getProperties()
	 * @see org.daisy.properties.PropertyTracker#getProperties(String)
	 */
	@Test
	public void getProperties() throws Exception {
		Collection<Property> ps=this.propTracker.getProperties();
		Assert.assertEquals(ps.size(),2);
		ArrayList<Property> ap=new ArrayList<Property>(ps);
		Assert.assertEquals(ap.get(0),this.prop1);
		Assert.assertEquals(ap.get(1),this.prop2);
	}

	/**
	 * Tests 'getProperty'.
	 *
	 * @see org.daisy.properties.PropertyTracker#getProperty(String,String)
	 */
	@Test
	public void getProperty() throws Exception {
		Property p=this.propTracker.getProperty(this.prop1.getPropertyName(),this.prop1.getBundleName());
		Assert.assertNotNull(p);
		Assert.assertEquals(p,this.prop1);
		p=this.propTracker.getProperty(this.prop2.getPropertyName(),this.prop2.getBundleName());
		Assert.assertNotNull(p);
		Assert.assertEquals(p,this.prop2);
		//and error
		p=this.propTracker.getProperty(this.prop1.getPropertyName(),this.prop2.getBundleName());
		Assert.assertNull(p);
	}


	/**
	 * Tests 'handleProperyDeletionEvent'.
	 *
	 * @see org.daisy.properties.PropertyTracker#handleProperyDeletionEvent(PropertyDeletionEvent)
	 */
	@Test
	public void handleProperyDeletionEvent() throws Exception {
	}

}
