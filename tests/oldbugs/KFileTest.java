/*
 * Created on 07.03.2004
 *
 */
package tests.oldbugs;

import java.io.File;
import java.util.Iterator;

import kurzobjects.samples.KSample;
import filemethods.KFile;

/**
 * @author mahal
 *
 */
public class KFileTest extends tests.TestCaseFileCompare {
	private KFile testObject;
	
	/**
	 * Constructor for KFileTest.
	 * @param arg0
	 */
	public KFileTest(String arg0) {
		super(arg0);
	}


	public void testKorgPadImport() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/oldbugs/KORG_PAD.WAV", new filemethods.wav.LoadWaveMethod());
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
		assertEquals(74, numBytes);
		
		// Bytes im RAM
		iter = testObject.getSampleIterator();
		numBytes = 0;
		while (iter.hasNext()) {
			numBytes += ((KSample) testObject.getKObject(iter.next())).getRamSize();
		}
		assertEquals(264602, numBytes);
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/oldbugs/KORG_PAD.WAV.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}

	public void testVLA6Import() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/oldbugs/VLAC6.wav", new filemethods.wav.LoadWaveMethod());
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
		assertEquals(70, numBytes);
		
		// Bytes im RAM
		iter = testObject.getSampleIterator();
		numBytes = 0;
		while (iter.hasNext()) {
			numBytes += ((KSample) testObject.getKObject(iter.next())).getRamSize();
		}
		assertEquals(72846, numBytes);
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/oldbugs/VLAC6.wav.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}	
	
	public void testNiko1Import() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/oldbugs/HOUK2-021.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/oldbugs/HOUK2-021.wav.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}		
	
	public void testNiko2Import() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/oldbugs/HOUK2-021_SF-Test.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
		testObject.save(TMP_KRZ_NAME);
		assertEqualFiles(TMP_KRZ_NAME, "tests/oldbugs/HOUK2-021_SF-Test.wav.krz");
		
		assertTrue(new File(TMP_KRZ_NAME).delete());
		
		testObject.reinit();
		assertTrue(testObject.isEmpty());
	}			

	public void testErtanImport() {
		//Keine Ahnung, was sein Problem war
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/oldbugs/Ertan_L_G076.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 
		
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
