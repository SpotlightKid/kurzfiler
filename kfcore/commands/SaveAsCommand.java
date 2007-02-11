/**
 * 
 */
package kfcore.commands;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.MessageDialog;
import dialogs.SaveModeDialog;
import filemethods.FileMethodInterface;

public class SaveAsCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SaveAsCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -613414457500992348L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Save_As_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Save_As_Command"); //$NON-NLS-1$
	}
	/*
	  SaveAsCommand(){
	  f.addStateListener(this);
	  }*/
	public void Execute() {
		File filename;
		FileMethodInterface savemode;

		Vector modes = this.filer.getFileObject().getExportRoutines();
		SaveModeDialog dia =
			new SaveModeDialog(this.filer, modes.size());
		Iterator iter = modes.iterator();
		while (iter.hasNext()) {
			dia.addMode((FileMethodInterface) iter.next());
		}
		dia.pack();
		dia.setVisible(true);

		if (dia.getResult()) {
			savemode = dia.getAuswahl();
			if (savemode == null)
				return;

			this.filer.getFileChooser().resetChoosableFileFilters();
			this.filer.getFileChooser().addChoosableFileFilter(savemode.getFileFilter());

			int retval = this.filer.getFileChooser().showSaveDialog(this.filer);
			if (retval == JFileChooser.APPROVE_OPTION) {
				SplashScreen splash =
					new SplashScreen(this.filer, SplashScreen.SAVE_MSG);
				splash.showSplash();
				filename = this.filer.getFileChooser().getSelectedFile();
				splash.setCurFile(filename.getName());
				try {
					this.filer.getFileObject().exportData(filename.getPath(), savemode);
				} catch (Exception e) {
					MessageDialog msgdia =
						new MessageDialog(this.filer);
					msgdia.setMessage(
						Messages.getString("KurzFiler.An_error_occured_while_saving_the_file_<br><font_size_+1>") //$NON-NLS-1$
							+ e.getMessage());
					msgdia.setVisible(true);
				}
				this.filer.setTitle(KurzFiler.myName + this.filer.getFileObject().getName());
				this.filer.getFileObject().updateList();
				this.filer.clearSel();

				if (this.filer.getFileObject().getName()!=null) {
					this.filer.getMruList().AddFile(new File(this.filer.getFileObject().getName()));
				}

				splash.hideSplash();
			}
		}
	}
	public boolean isPossible() {
		return !this.filer.getFileObject().isEmpty();
	}
}