/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import dialogs.SaveOnExitDialog;

public class SetLangNLCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SetLangNLCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6479603927112932930L;
	public String getName() {
		String s = KurzFiler.NIEDERLANDE.getDisplayName();
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
				KurzFiler.restart(this.filer, KurzFiler.NIEDERLANDE);			
			}
			return;
			//Cancel 
		}
		KurzFiler.restart(this.filer, KurzFiler.NIEDERLANDE);			
	}
	public boolean isPossible() {
		return true;
	}
}