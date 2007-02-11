/**
 * 
 */
package kfcore.commands;

import java.util.Locale;

import kfcore.KurzFiler;


import dialogs.SaveOnExitDialog;

public class SetLangDECommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SetLangDECommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6206877554543294557L;
	public String getName() {
		return Locale.GERMAN.getDisplayName();
	}
	public char getMnemonic() {
		return new String(getName()).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		if (this.filer.getFileObject().isDirty()) {
			SaveOnExitDialog Dia = new SaveOnExitDialog(this.filer);
			Dia.setName(this.filer.getFileObject().getName());
			Dia.setVisible(true);
			if (Dia.getResult()) {
				if (Dia.hasConfirmed()) {
					if (this.filer.getFileObject().getName() == null)
						this.filer.getSaveasCmd().Execute();
					else
						this.filer.getSaveCmd().Execute();
				}
				KurzFiler.restart(this.filer, Locale.GERMAN);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(this.filer, Locale.GERMAN);			
	}
	public boolean isPossible() {
		return true;
	}
}