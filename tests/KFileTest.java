/*
 * Created on 07.03.2004
 *
 */
package tests;

import java.io.File;
import java.util.Iterator;

import kurzobjects.samples.KSample;
import filemethods.KFile;

/**
 * @author mahal
 *
 */
public class KFileTest extends TestCaseFileCompare {
	private KFile testObject;
	
	/**
	 * Constructor for KFileTest.
	 * @param arg0
	 */
	public KFileTest(String arg0) {
		super(arg0);
	}

	public void testGetSize() {
		assertTrue(testObject.getSize()==0);
	}

	public void testGetSetName() {
		testObject.setName(null);
		assertNull(testObject.getName());
		
		String testName = "Name";
		testObject.setName(testName);
		assertEquals(testObject.getName(),testName);
		
		testObject.setName(null);
	}


	public void testWavNoLoopImport() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/Tralla16BitMonoNoLoop.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
		// 72 Bytes im PRAM
		Iterator<Integer> iter = testObject.getIterator();
		int numBytes = 0;
		while (iter.hasNext()) {
			numBytes += testObject.getKObject(iter.next()).getSize();
		}
		assertEquals(78, numBytes);
		
		// 8780 Bytes im RAM
		iter = testObject.getSampleIterator();
		numBytes = 0;
		while (iter.hasNext()) {
			numBytes += ((KSample) testObject.getKObject(iter.next())).getRamSize();
		}
		assertEquals(8780, numBytes);
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/Tralla16BitMonoNoLoop.wav.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}

	public void testWavWithLoopImport() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/Tralla16BitMonoLoop.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
		// Bytes im PRAM
		Iterator<Integer> iter = testObject.getIterator();
		int numBytes = 0;
		while (iter.hasNext()) {
			numBytes += testObject.getKObject(iter.next()).getSize();
		}
		assertEquals(78, numBytes);
		
		// Bytes im RAM
		iter = testObject.getSampleIterator();
		numBytes = 0;
		while (iter.hasNext()) {
			numBytes += ((KSample) testObject.getKObject(iter.next())).getRamSize();
		}
		assertEquals(8672, numBytes);
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/Tralla16BitMonoLoop.wav.krz");
		
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
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

		testObject=null;		
	}

}
