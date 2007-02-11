/**
 * 
 */
package kfcore.commands;

import java.util.Locale;

import kfcore.KurzFiler;


import dialogs.SaveOnExitDialog;

public class SetLangFRCommand extends KCommand {
	/**
	 * 
	 */
	public SetLangFRCommand(KurzFiler filer) {
		super(filer);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7946762707579348286L;
	public String getName() {
		return Locale.FRENCH.getDisplayName();
	}
	public char getMnemonic() {
		return new String(getName()).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		if (getFiler().getFileObject().isDirty()) {
			SaveOnExitDialog Dia = new SaveOnExitDialog(getFiler());
			Dia.setName(getFiler().getFileObject().getName());
			Dia.setVisible(true);
			if (Dia.getResult()) {
				if (Dia.hasConfirmed()) {
					if (getFiler().getFileObject().getName() == null)
						getFiler().getSaveasCmd().Execute();
					else
						getFiler().getSaveCmd().Execute();
				}
				KurzFiler.restart(getFiler(), Locale.FRENCH);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(getFiler(), Locale.FRENCH);
	}
	public boolean isPossible() {
		return true;
	}
}