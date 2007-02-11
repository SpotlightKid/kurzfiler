/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;
import dialogs.FillModeDialog;

public class FillmodeCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public FillmodeCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1941196701218463545L;
	public String getName() {
		return Messages.getString("KurzFiler.Fillmode_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.FillMode_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		FillModeDialog fmDia = new FillModeDialog(this.filer);
		fmDia.setAuswahl(this.filer.getFileObject().getFillMode());
		fmDia.setVisible(true);
		if (fmDia.getResult())
			this.filer.getFileObject().setFillMode(fmDia.getAuswahl());
	}
	public boolean isPossible() {
		return true;
	}
}