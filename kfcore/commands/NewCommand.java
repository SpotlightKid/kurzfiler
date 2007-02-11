/**
 * 
 */
package kfcore.commands;

import javax.swing.Action;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.SaveOnReinitDialog;

public class NewCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5781110579948349816L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.New_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.New_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return  Messages.getString("KurzFiler.New_Command"); //$NON-NLS-1$
	}
	public NewCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		putValue(
			Action.SMALL_ICON,
			resources.Images.getImage(resources.Images.NEW_ICON)); 
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("KurzFiler.New_short_Descr")); //$NON-NLS-1$
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
					//in dieser Form SingleThreaded
				}
			} else
				return;
			//cancel new
		}

		this.filer.getFileObject().reinit();
		this.filer.setTitle(KurzFiler.myName);
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return !((this.filer.getFileObject().isEmpty()) && (this.filer.getFileObject().getName() == null));
	}
}