package org.daisy.common.file;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class URIsTest {

	@Test
	public void testRelativize() {
		assertEquals("x86_64/liblouis.dylib",
		             URIs.relativize("jar:file:/liblouis-native.jar!/native/macosx/",
		                             "jar:file:/liblouis-native.jar!/native/macosx/x86_64/liblouis.dylib")
		             .toString());
	}
}
