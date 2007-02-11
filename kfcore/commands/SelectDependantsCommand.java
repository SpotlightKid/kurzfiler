/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.TreeSet;

import kfcore.KurzFiler;


import resources.Messages;

public class SelectDependantsCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SelectDependantsCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4620241587089936471L;
	public String getName() {
		return  Messages.getString("KurzFiler.Select_Dependants_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Select_Dependants_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		TreeSet<Integer> dependants = new TreeSet<Integer>();
		Integer o;
		int i;
		for (i = 0; i < indexes.length; i++) {
			o = this.filer.getFileObject().getIDAt(indexes[i]);
			if (o == null)
				continue;
			dependants.addAll(this.filer.getFileObject().getDependantsRecursive(o));
		}
		this.filer.getObjectTable().clearSelection();

		Iterator<Integer> depIter = dependants.iterator();

		while (depIter.hasNext()) {
			i = this.filer.getFileObject().getIndex(depIter.next());
			this.filer.getObjectTable().addRowSelectionInterval(i, i);
		}
	}
	public boolean isPossible() {
		return (this.filer.getSelNum() > 0);
	}
}