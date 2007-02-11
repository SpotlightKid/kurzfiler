/**
 * 
 */
package kfcore.commands;

import java.io.File;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFileChooser;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.MessageDialog;
import dialogs.SaveDependantsDialog;
import dialogs.SaveModeDialog;
import filemethods.FileMethodInterface;
import filemethods.KFile;

public class SaveSelectedCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SaveSelectedCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7774541149090782067L;
	public String getName() {
		return Messages.getString("KurzFiler.Save_Selected_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Save_Selected_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		KFile theother = new KFile();
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		TreeSet<Integer> dependants = new TreeSet<Integer>();
		TreeSet<Integer> selection = new TreeSet<Integer>();
		Integer o;
		int i;
		for (i = 0; i < indexes.length; i++) {
			o = this.filer.getFileObject().getIDAt(indexes[i]);
			if (o == null)
				continue;
			selection.add(o);
			dependants.addAll(this.filer.getFileObject().getDependantsRecursive(o));
		}

		if (dependants.size() > 0) {
			SaveDependantsDialog depdia =
				new SaveDependantsDialog(this.filer);
			depdia.setVisible(true);
			if (depdia.getResult()) {
				if (depdia.hasConfirmed())
					selection.addAll(dependants);
			} else
				return;
			//User canceled
		}

		Iterator<Integer> iter = selection.iterator();
		while (iter.hasNext()) {
			theother.addKObject(this.filer.getFileObject().getKObject(iter.next()));
		}

		File filename;
		FileMethodInterface savemode;

		Vector<FileMethodInterface> modes = theother.getExportRoutines();
		SaveModeDialog dia =
			new SaveModeDialog(this.filer, modes.size());
		Iterator<FileMethodInterface> iter2 = modes.iterator();
		while (iter2.hasNext()) {
			dia.addMode(iter2.next());
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
				SplashScreen Splash =
					new SplashScreen(this.filer, SplashScreen.SAVE_MSG);
				Splash.showSplash();
				filename = this.filer.getFileChooser().getSelectedFile();

				Splash.setCurFile(filename.getName());
				try {
					theother.exportData(filename.getPath(), savemode);
				} catch (Exception e) {
					MessageDialog msgdia =
						new MessageDialog(this.filer);
					msgdia.setMessage(
						Messages.getString("KurzFiler.An_error_occured_while_saving_the_file_<br><font_size_+1>") //$NON-NLS-1$
							+ e.getMessage());
					msgdia.setVisible(true);
				}
				this.filer.clearSel();

				Splash.hideSplash();
			}
		}
	}
	public boolean isPossible() {
		return this.filer.getSelNum() > 0;
	}
}