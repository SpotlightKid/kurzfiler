/**
 * 
 */
package kfcore.commands;

import java.util.Locale;

import kfcore.KurzFiler;


import dialogs.SaveOnExitDialog;

public class SetLangENCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SetLangENCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8932944394709818602L;
	public String getName() {
		return Locale.ENGLISH.getDisplayName();
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
				KurzFiler.restart(this.filer, Locale.ENGLISH);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(this.filer, Locale.ENGLISH);
	}
	public boolean isPossible() {
		return true;
	}
}