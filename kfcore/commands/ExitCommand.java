/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;
import dialogs.SaveOnExitDialog;

public class ExitCommand extends KCommand {

	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public ExitCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4259046454636775108L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Exit_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Exit_Command"); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Exit_Shortcut")).charAt(0); //$NON-NLS-1$
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
				this.filer.setVisible(false);
				this.filer.dispose();
				System.exit(0);
			}
			return;
			//Cancel exit
		}
		this.filer.WriteMRUList();
		this.filer.setVisible(false);
		this.filer.dispose();
		System.exit(0);
	}
	public boolean isPossible() {
		return true;
	}
}