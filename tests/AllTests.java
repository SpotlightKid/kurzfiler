/*
 * Created on 07.03.2004
 *
 */
package tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author mahal
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for KurzFiler");
		//$JUnit-BEGIN$
		for (int i=0;i<1; i++) {
			suite.addTest(new TestSuite(KFileTest.class));
			suite.addTest(new TestSuite(tests.undoredo.KFileTest.class));
			suite.addTest(new TestSuite(tests.oldbugs.KFileTest.class));
			suite.addTest(new TestSuite(tests.bug061029.SamplePeriodTest.class));
		}
		//$JUnit-END$
		return suite;
	}

}
