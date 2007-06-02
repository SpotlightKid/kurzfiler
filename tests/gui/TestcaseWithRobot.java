/*
 * Created on 07.03.2004
 *
 */
package tests.gui;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import filemethods.KFile;
/**
 * @author mahal
 *
 */
public class TestcaseWithRobot extends tests.TestCaseFileCompare {
	private TestFiler testObject;
	private java.awt.Robot robot;
	
	protected KFile getObj() {
		return testObject.getFileObject();
	}
	
	protected void resetObj() {
		if (! getObj().isEmpty()) {
			getObj().setDirty(false);
			testObject.reInit();		
		} 

		assertEquals(0, getObj().getSize());
		assertTrue(getObj().isEmpty());
		assertTrue(! getObj().isDirty());
		
		testObject.resetFileChooser();
	}
	
	/**
	 * Constructor for TestcaseWithRobot.
	 * @param arg0
	 */
	public TestcaseWithRobot(String arg0) {
		super(arg0);
	}

	private class CmdRunner implements Runnable {
		String s;
		
		public CmdRunner(String cmd) {
			s=cmd;
		}
		public void run() {
			testObject.RunCommand(s);
		}
		
	}
	protected void RunCommand(String s) {
		SwingUtilities.invokeLater(new CmdRunner(s));
		wait(300);
	}
	
	protected void typeString(String s) {
		typeBackspace(2);
		for (int i=0;i<s.length();i++) {
			wait(30);
			robot.keyPress(Character.toUpperCase(s.charAt(i)));
			wait(30);
			robot.keyRelease(Character.toUpperCase(s.charAt(i)));
			//System.out.print(s.charAt(i));
		}
	}
	
	protected void typeChar(String s) {
		robot.keyPress(Character.toUpperCase(s.charAt(0)));
		robot.keyRelease(Character.toUpperCase(s.charAt(0)));
	}
	
	protected void typeMetaChar(String s){
		wait(30);
		robot.keyPress(KeyEvent.VK_ALT);
		wait(30);
		robot.keyPress(Character.toUpperCase(s.charAt(0)));
		wait(30);
		robot.keyRelease(Character.toUpperCase(s.charAt(0)));
		wait(30);
		robot.keyRelease(KeyEvent.VK_ALT);
	}

	protected void typeEnter() {
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	protected void typeTab() {
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
	}

	protected void typeBackspace(int num) {
		for (int i=0;i<num;i++) {
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
		}
	}
	
	protected void typeSpace() {
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);
	}
	
	protected void wait(int mseconds) {
		robot.delay(mseconds);
	}
	
	protected void waitForLoadCompletion()  {
		try {
			do {
				Thread.sleep(500);
			} while (testObject.isLoading());
		} catch(Exception e) {
			throw new RuntimeException("waitForLoadCompletion");
		}
	}
		
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
 
	protected void setUp() throws Exception {
		super.setUp();
	
		robot = new Robot();
		robot.setAutoWaitForIdle(true);
		//robot.setAutoDelay(100);
		
		testObject=new TestFiler();		
		testObject.setSize(700, 500);
		testObject.setVisible(true);
		testObject.requestFocus();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		robot=null;
		resetObj();
		testObject.setVisible(false);
		testObject.dispose();
		testObject=null;		

		super.tearDown();
	}

}
