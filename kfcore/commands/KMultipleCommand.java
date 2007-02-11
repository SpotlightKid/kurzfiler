/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;



public abstract class KMultipleCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	KMultipleCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	public void Execute() {
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		for (int i = 0; i < indexes.length; i++) {
			SingleCommand(this.filer.getFileObject().getIDAt(indexes[i]));
		}
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	abstract void SingleCommand(Integer o);
}