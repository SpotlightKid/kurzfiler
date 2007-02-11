/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;

public class RemapCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public RemapCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1574351402256626750L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Remap_IDs_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Remap_IDs_Command"); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.getFileObject().remapIDs();
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return !this.filer.getFileObject().isEmpty();
	}
}