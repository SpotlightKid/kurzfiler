/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;

public class SelectAllCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SelectAllCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8699752054151840863L;
	public String getName() {
		return  Messages.getString("KurzFiler.Select_All_Command"); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Select_all_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Select_all_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.getObjectTable().setRowSelectionInterval(0, this.filer.getFileObject().getSize() - 1);
	}
	public boolean isPossible() {
		return (!this.filer.getFileObject().isEmpty() && (this.filer.getSelNum() < this.filer.getFileObject().getSize()));
	}
}