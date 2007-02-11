/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import dialogs.SaveOnExitDialog;

public class SetLangESCommand extends KCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3126595672856466312L;
	/**
	 * 
	 */
	public SetLangESCommand(KurzFiler filer) {
		super(filer);
	}
	/**
	 * 
	 */
	public String getName() {
		String s = KurzFiler.SPANIEN.getDisplayName();
		try {
			int leer = s.indexOf(" ");
			return s.substring(0,leer);
		} catch (Exception e) {
			return s;
		}
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
				KurzFiler.restart(getFiler(), KurzFiler.SPANIEN);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(getFiler(), KurzFiler.SPANIEN);
	}
	public boolean isPossible() {
		return true;
	}
}