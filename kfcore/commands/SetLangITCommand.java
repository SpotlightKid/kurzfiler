/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;


import dialogs.SaveOnExitDialog;

public class SetLangITCommand extends KCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1191272908612329100L;
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SetLangITCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	public String getName() {
		return KurzFiler.ITALIEN.getDisplayName();
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
				KurzFiler.restart(this.filer, KurzFiler.ITALIEN);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(this.filer, KurzFiler.ITALIEN);			
	}
	public boolean isPossible() {
		return true;
	}
}