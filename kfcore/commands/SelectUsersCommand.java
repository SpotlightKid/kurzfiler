/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.TreeSet;

import kfcore.KurzFiler;


import resources.Messages;

public class SelectUsersCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SelectUsersCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5989844553710376596L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Select_Users_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Select_Users_Command"); //$NON-NLS-1$
	}
	public void Execute() {
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		TreeSet<Integer> users = new TreeSet<Integer>();
		Integer id;
		int i;
		for (i = 0; i < indexes.length; i++) {
			id= this.filer.getFileObject().getIDAt(indexes[i]);
			if (id == null)
				continue;
			users.addAll(this.filer.getFileObject().getUsers(id));
		}
		this.filer.getObjectTable().clearSelection();

		Iterator iter = users.iterator();

		while (iter.hasNext()) {
			i = this.filer.getFileObject().getIndex(iter.next());
			this.filer.getObjectTable().addRowSelectionInterval(i, i);
		}
	}
	public boolean isPossible() {
		return (this.filer.getSelNum() > 0);
	}
}