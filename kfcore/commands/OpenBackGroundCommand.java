/**
 * 
 */
package kfcore.commands;

import javax.swing.Action;
import javax.swing.JFileChooser;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.SaveOnReinitDialog;
import filemethods.FileMethodInterface;
import filemethods.kurzweil.LoadK2x00Method;

public class OpenBackGroundCommand extends BackGroundLoaderCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3021437620078962207L;
	public String getName() {
		return Messages.getString("KurzFiler.Open_Command"); //$NON-NLS-1$ 
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Open_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Open_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public OpenBackGroundCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		loader = new Loader(this.filer, this);
		//putValue(Action.SMALL_ICON, new ImageIcon("open.gif"));
		/*putValue(
			Action.SMALL_ICON,
			new ImageIcon(getClass().getResource("open.gif"))); //$NON-NLS-1$*/
		putValue(
			Action.SMALL_ICON,
			resources.Images.getImage(resources.Images.OPEN_ICON)); 
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("KurzFiler.Open_short_Descr")); //$NON-NLS-1$
	}
	
	private Loader loader; 

	private LoadK2x00Method filemethod = new LoadK2x00Method();

	protected FileMethodInterface getFileMethod() {
		return filemethod;
	}
	protected boolean appendToCurrentObject() {
		return index > 0;
	}
	protected Loader getLoader() {
		return loader;
	}
	public void Execute() {
		if (this.filer.getFileObject().isDirty()) {
			SaveOnReinitDialog dia = new SaveOnReinitDialog(this.filer);
			dia.setName(this.filer.getFileObject().getName());
			dia.setVisible(true);
			if (dia.getResult()) {
				if (dia.hasConfirmed()) {
					if (this.filer.getFileObject().getName() == null)
						this.filer.getSaveasCmd().Execute();
					else
						this.filer.getSaveCmd().Execute();
				}
			} else
				return;
			//cancel new
		}

		this.filer.getFileChooser().resetChoosableFileFilters();
		this.filer.getFileChooser().addChoosableFileFilter(this.filer.getFileObject().getKurzFileFilter());
		this.filer.getFileChooser().setMultiSelectionEnabled(true);

		int retval = this.filer.getFileChooser().showOpenDialog(this.filer);
		if (retval == JFileChooser.APPROVE_OPTION) {
			this.filer.getFileObject().reinit();
			setFiles(this.filer.getFileChooser().getSelectedFiles());

			if (getFiles().length > 0) {
				index = -1;
				loadIteration();
			}
		}
	}
	protected void loadCompleted() {
		super.loadCompleted();
		this.filer.setTitle(KurzFiler.myName + getFiles()[0].getPath());
		this.filer.getFileObject().setName(getFiles()[0].getPath());
		this.filer.getFileObject().clearHistory();
		for (int i=0;i<getFiles().length;i++)
			this.filer.getMruList().AddFile(getFiles()[i]);
	}
	public boolean isPossible() {
		return true;
	}
}