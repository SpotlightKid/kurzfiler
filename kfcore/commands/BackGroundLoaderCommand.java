/**
 * 
 */
package kfcore.commands;

import java.io.File;

import kfcore.KurzFiler;


import filemethods.FileMethodInterface;

abstract class BackGroundLoaderCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;

	/**
	 * @param filer
	 */
	protected BackGroundLoaderCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	protected SplashScreen splashScreen;
	
	public int index;
	private File[] files;
	
	protected abstract Loader getLoader();
	protected abstract FileMethodInterface getFileMethod();
	/* loadIndirect == true
	 * means that the new data shall be
	 * added to the current file
	 */
	protected abstract boolean appendToCurrentObject();
	
	protected void loadAborted() {
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
		splashScreen.hideSplash();
		this.filer.loadCompletedNotification();
	}
	protected void loadIteration() {
		index++;
		if (index == 0) {
			loadPreStart();
		}
		if (index < files.length) {
			splashScreen.setCurFile(files[index].getName());
			Thread thread = new Thread(getLoader());
			thread.start();
		} else {
			this.filer.getFileObject().updateList();
			this.filer.clearSel();
			loadCompleted();
		}
	}
	protected void loadPreStart() {
		this.filer.loadStartedNotification();
		if (splashScreen == null)
			splashScreen = new SplashScreen(this.filer, SplashScreen.READ_MSG);
		splashScreen.showSplash();
	}
	protected void loadCompleted() {
		splashScreen.hideSplash();
		this.filer.loadCompletedNotification();
	}
	public void setFiles(File[] files) {
		this.files = files;
	}
	public File[] getFiles() {
		return files;
	}
}