/**
 * 
 */
package kfcore.commands;

import javax.swing.SwingUtilities;

import kfcore.KurzFiler;
import resources.Messages;
import dialogs.runtimemsg.DisplayFactory;
import dialogs.runtimemsg.MsgDisplay;
import filemethods.KFile;

class Loader implements Runnable {
	/**
	 * 
	 */
	private final KurzFiler filer;
	private final BackGroundLoaderCommand owner;
	protected Loader(KurzFiler filer, BackGroundLoaderCommand c) {
		this.filer = filer;
		owner = c;
	}
	public void run() {
		try {
			if (owner.getFiles() == null)
				return;
			if (owner.index >= owner.getFiles().length)
				return;
			if (!owner.appendToCurrentObject()) {
				//ins eigene KFile Object laden 
				this.filer.getFileObject().importData(
					owner.getFiles()[owner.index].getPath(),
					owner.getFileMethod());
				this.filer.getFileObject().setDirty(false);

			} else {
				//in ein temporäres KFile Object laden
				KFile theother = new KFile();
				theother.importData(
					owner.getFiles()[owner.index].getPath(),
					owner.getFileMethod());
				this.filer.getFileObject().importData(theother);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			MsgDisplay display=DisplayFactory.GetDisplay();
			display.ShowErrorMessage(e.getMessage(), 
					Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					owner.loadAborted();
				}
			});
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				owner.loadIteration();
			}
		});
	}
}