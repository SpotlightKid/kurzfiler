/*
 * Created on 07.03.2004
 *
 */
package tests.gui;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;

import filemethods.KFile;
import kfcore.KurzFiler;
import kfcore.commands.KCommand;

/**
 * @author mahal
 *
 */
public class TestFiler extends KurzFiler {


	/**
	 * 
	 */
	private static final long serialVersionUID = -926321260793639876L;
	public TestFiler() {
		super();
	}
	
	public KFile getFileObject() {
		return super.getFileObject();
	}
	
	public void resetFileChooser() {
		setFileChooser(new JFileChooser("."));		
	}
	public void reInit() {
		getNewCmd().Execute();
	}
	

	private boolean isLoadingFlag;	 
	public void loadStartedNotification() {
		super.loadStartedNotification();
		isLoadingFlag=true;
	}

	public void loadCompletedNotification() {
		super.loadCompletedNotification();
		isLoadingFlag=false;
	}
	
	public boolean isLoading() {
		return isLoadingFlag;
	}

	protected Vector<KCommand> testCommands;

	@Override
	protected void addMenuItem(KCommand command, JMenu menu) {
		super.addMenuItem(command, menu);
		if (testCommands==null) testCommands=new Vector<KCommand>();
		testCommands.add(command);
	}
	
	public void RunCommand(String s) {
		if (testCommands==null) {
			System.err.println("no commands here!");
			testCommands=new Vector<KCommand>();
		}
		Iterator<KCommand> i = testCommands.iterator();
		while (i.hasNext()) {
			KCommand command = i.next();
			if (command.getName().equals(s)) {
				command.actionPerformed(null);
				return;
			}
		}
	}
}
