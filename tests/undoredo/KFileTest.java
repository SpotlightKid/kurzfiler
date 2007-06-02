/*
 * Created on 07.03.2004
 *
 */
package tests.undoredo;

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

	public void testUndoRedo() {
		assertTrue(testObject.isEmpty());
		testObject.importData("tests/Tralla16BitMonoNoLoop.wav", new filemethods.wav.LoadWaveMethod());
		testObject.updateList();
		assertTrue(testObject.isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, testObject.getSize()); 

		assertTrue(testObject.canUndo());
		assertTrue(! testObject.canRedo());
		
		testObject.undo();
		testObject.updateList();
				
		// Kein Objekt sollte vorhanden sein
		assertEquals(0, testObject.getSize()); 
		assertTrue(testObject.isEmpty());

		assertTrue(! testObject.canUndo());
		assertTrue(testObject.canRedo());

		testObject.redo();
		testObject.updateList();
		
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
