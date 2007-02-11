/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;


import dialogs.SaveOnExitDialog;

public class SetLangPLCommand extends KCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = -186012208020090451L;
	/**
	 * 
	 */
	public SetLangPLCommand(KurzFiler filer) {
		super(filer);
	}
	/**
	 * 
	 */
	public String getName() {
		return KurzFiler.POLEN.getDisplayName();
	}
	public char getMnemonic() {
		return 0;
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
				KurzFiler.restart(getFiler(), KurzFiler.POLEN);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(getFiler(), KurzFiler.POLEN);
	}
	public boolean isPossible() {
		return true;
	}
}