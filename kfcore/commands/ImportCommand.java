/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;

import kfcore.KurzFiler;


import resources.Messages;
import filemethods.FileMethodInterface;

public class ImportCommand extends BackGroundLoaderCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public ImportCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		loader = new Loader(this.filer, this);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8882170448990322710L;
	public String getName() {
		return Messages.getString("KurzFiler.Import_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Import_Mnem")).charAt(0); //$NON-NLS-1$
	}
	private Loader loader=null;
	private Vector modes;
	protected boolean appendToCurrentObject() {
		return true;
	}
	protected Loader getLoader() {
		return loader;
	}
	protected FileMethodInterface getFileMethod() {
		Iterator iter = modes.iterator();
		FileMethodInterface importmode;
		while (iter.hasNext()) {
			importmode = (FileMethodInterface) iter.next();
			if (importmode.matchesExtensions(getFiles()[index].getName())) {
				return importmode;
			}
		}
		throw new RuntimeException(Messages.getString("KurzFiler.Don__t_know_how_to_load_it")); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.getFileChooser().resetChoosableFileFilters();

		modes = this.filer.getFileObject().getImportRoutines();
		Iterator iter = modes.iterator();
		while (iter.hasNext()) {
			this.filer.getFileChooser().addChoosableFileFilter(
				((FileMethodInterface) iter.next()).getFileFilter());
		}

		this.filer.getFileChooser().setMultiSelectionEnabled(true);

		int retval = this.filer.getFileChooser().showOpenDialog(this.filer);
		if (retval == JFileChooser.APPROVE_OPTION) {
			setFiles(this.filer.getFileChooser().getSelectedFiles());
			if (getFiles().length > 0) {
				index = -1;
				loadIteration();
			}
		}
	}
	public boolean isPossible() {
		return true;
	}
}