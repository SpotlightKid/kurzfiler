/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;

public class DeSelectCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public DeSelectCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3393356027191982798L;
	public String getName() {
		return Messages.getString("KurzFiler.Deselect_Command"); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Deselect_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Deselect_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return this.filer.getSelNum() > 0;
	}
}