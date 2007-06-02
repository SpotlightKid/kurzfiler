/*
 * Created on 30.10.2006
 *
 */
package tests.bug061029;

import java.io.File;

import dialogs.runtimemsg.DisplayFactory;
import filemethods.KFile;

/**
 * @author mahal
 *
 */
public class SamplePeriodTest extends tests.TestCaseFileCompare {
	private KFile testObject;
	
	/**
	 * Constructor for KFileTest.
	 * @param arg0
	 */
	public SamplePeriodTest(String arg0) {
		super(arg0);
	}


	public void testBadWavImport() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/bug061029/bad wav file C1.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
		testObject.exportData("tests/bug061029/test", new filemethods.wav.SaveWaveMethod());
		assertEqualFiles("tests/bug061029/test 1 C1.wav", "tests/bug061029/fixed file 1 C1.wav");
		
		assertTrue(new File("tests/bug061029/test 1 C1.wav").delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}

	public void testBadKrzLoad() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/bug061029/kurz1.krz", new filemethods.kurzweil.LoadK2x00Method());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(11, testObject.getSize()); 
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/bug061029/kurz1 fixed.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		testObject=new KFile();
		
		DisplayFactory.SetDisplayMode(DisplayFactory.mode.console);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		testObject=null;		
	}

}
