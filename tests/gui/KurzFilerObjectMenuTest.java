/*
 * Created on 07.03.2004
 *
 */
package tests.gui;

import java.io.File;

/**
 * @author mahal
 *
 */
public class KurzFilerObjectMenuTest extends TestcaseWithRobot {
	

	public KurzFilerObjectMenuTest(String arg0) {
		super(arg0);
	}


	private void resetObject() {
		resetObj();

		assertEquals(0, getObj().getSize());
		assertTrue(getObj().isEmpty());
	}

	private void loadFile() {
		//typeMetaChar(resources.Messages.getString("KurzFiler.FileMenuMnem"));
		//typeString(resources.Messages.getString("KurzFiler.Import_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Import_Command"));

		typeString("tests");
		typeEnter();
		typeBackspace(5);
		typeString("test.wav");
		typeEnter();

		//wait(300);
		waitForLoadCompletion();
		
		//Datei laden
		
		assertTrue(getObj().isDirty());

		// Ein Objekt sollte vorhanden sein
		assertEquals(1, getObj().getSize()); 
	}
	
	private void selectAll() {
		//typeMetaChar(resources.Messages.getString("KurzFiler.EditMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.Select_all_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Select_All_Command"));
	}
	
	private void setRootKeyToC6() {
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.SampleMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.Set_Rootkey_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Set_Rootkey_Command"));
		
		typeString("C6");
		typeEnter();

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
	}
	
	private void saveAndCompareTo(String filename) {
		//typeMetaChar(resources.Messages.getString("KurzFiler.FileMenuMnem"));
		//typeString(resources.Messages.getString("KurzFiler.Save_As_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Save_As_Command"));

		//Modus
		typeEnter();

		typeString(TMP_GUI_KRZ_NAME);
		typeEnter();
		
		assertTrue(! getObj().isDirty());
		
		assertEqualFiles(filename, TMP_KRZ_NAME);

		assertTrue(new File(TMP_KRZ_NAME).delete());
	}
	
	public void testSampleRootKey() {
		resetObject();
		loadFile();
		selectAll();
		setRootKeyToC6();
		// Ergebnis prüfen 
		saveAndCompareTo("tests/TestRootKey.krz");
	}	


	public void testDrumKeymap() {
		resetObject();
		loadFile();
		selectAll();
		setRootKeyToC6();

		//Drumset
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.ProgramMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.New_Drumset_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.New_Drumset_Command"));

		//Namen ändern 	
		typeString("km");
		typeTab();	
		typeString("drums");
		typeTab();	
		typeSpace();

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		// Anzahl Objekte 
		assertEquals(3, getObj().getSize()); 
	
		// Ergebnis prüfen 
		saveAndCompareTo("tests/TestDrumProgram.krz");
	}	


	public void testChromaKeymap() {
		resetObject();
		loadFile();
		selectAll();
		setRootKeyToC6();

		//Chroma
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.ProgramMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.New_Instrument_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.New_Instrument_Command"));

		//Namen ändern 	
		typeString("km");
		typeTab();	
		typeString("chromatisch");
		typeTab();	
		typeSpace();

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		// Anzahl Objekte 
		assertEquals(3, getObj().getSize()); 
	
		// Ergebnis prüfen 
		saveAndCompareTo("tests/TestChromaticalProgram.krz");
	}	


	public void testCompactKeymap() {
		resetObject();
		loadFile();
		selectAll();
		setRootKeyToC6();

		//Chroma
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.ProgramMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.New_Instrument_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.New_Instrument_Command"));

		//Namen ändern 	
		typeString("km");
		typeTab();	
		typeString("chromatisch");
		typeTab();	
		typeSpace();

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		// Anzahl Objekte 
		assertEquals(3, getObj().getSize()); 
	
		// Kompaktieren
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.KeymapMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.Compact_Keymap_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Compact_Keymap_Command"));

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		assertEquals(3, getObj().getSize()); 

		// Ergebnis prüfen 
		saveAndCompareTo("tests/TestCompactedKeymap.krz");
	}	

	public void testNewKeymap() {
		resetObject();
		loadFile();
		selectAll();
		setRootKeyToC6();

		//Chroma
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.ProgramMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.New_Instrument_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.New_Instrument_Command"));

		//Namen ändern 	
		typeString("km");
		typeTab();	
		typeString("chromatisch");
		typeTab();	
		typeSpace();

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		// Anzahl Objekte 
		assertEquals(3, getObj().getSize()); 
	
		// Kompaktieren
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.KeymapMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.Compact_Keymap_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.Compact_Keymap_Command"));

		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		assertEquals(3, getObj().getSize()); 

		// Neue Keymap
		selectAll();
		
		//typeMetaChar(resources.Messages.getString("KurzFiler.ObjectMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.KeymapMenuMnem"));
		//typeChar(resources.Messages.getString("KurzFiler.New_Keymap_Mnem"));
		RunCommand(resources.Messages.getString("KurzFiler.New_Keymap_Command"));

		// Für verschiedene Velocities wählen
		typeTab();	
		typeTab();	
		typeTab();	
		typeTab();	
		typeTab();	
		typeTab();	
		typeTab();	
		typeTab();	
		typeSpace();

		//Namen ändern 	
		typeString("velosplit");
		typeTab();	
		typeString("prg");
		typeTab();	
		typeSpace();
		
		assertTrue(! getObj().isEmpty());
		assertTrue(getObj().canUndo());
		assertTrue(! getObj().canRedo());
		assertEquals(5, getObj().getSize()); 

		// Ergebnis prüfen 
		saveAndCompareTo("tests/TestNewKeymap.krz");
	}	
}
