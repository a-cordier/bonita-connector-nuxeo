/**
 * 
 */
package fr.univlille2.bpm;

import junit.framework.TestCase;

/**
 * @author acordier Jul 24, 2014
 * NuxeoConnectorUtilsTest.java
 */
public class NuxeoConnectorUtilsTest extends TestCase {

	/**
	 * Test method for {@link fr.univlille2.bpm.NuxeoConnectorUtils#trail(java.lang.String)}.
	 */
	public void testTrail() {
		assertEquals("/foo/bar/", NuxeoConnectorUtils.trail("/foo/bar"));
		assertEquals("/foo/bar/", NuxeoConnectorUtils.trail("/foo/bar/"));
	}

	/**
	 * Test method for {@link fr.univlille2.bpm.NuxeoConnectorUtils#unTrail(java.lang.String)}.
	 */
	public void testUnTrail() {
		assertEquals("/foo/bar", NuxeoConnectorUtils.unTrail("/foo/bar"));
		assertEquals("/foo/bar", NuxeoConnectorUtils.unTrail("/foo/bar/"));
	}

}
