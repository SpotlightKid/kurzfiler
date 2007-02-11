/**
 * 
 */
package kfcore.commands;

import java.awt.event.KeyEvent;
import java.util.TreeSet;

import javax.swing.KeyStroke;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.DeleteDependantsDialog;

public class DeleteCommand extends KMultipleCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public DeleteCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4015868412019869734L;
	public String getName() {
		return Messages.getString("KurzFiler.Delete_Command"); //$NON-NLS-1$
	}
	public char getShortCut() {
		return 1;
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Delete_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public KeyStroke getKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	}
	void SingleCommand(Integer o) {
		if (this.filer.getFileObject().getKObject(o) == null)
			return;
		TreeSet dependants = this.filer.getFileObject().getDirectDependants(o);
		if (dependants.size() > 0) {
			DeleteDependantsDialog Dia =
				new DeleteDependantsDialog(this.filer);
			Dia.setName(this.filer.getFileObject().getKObject(o).getLongName());
			Dia.setVisible(true);
			if (Dia.getResult()) {
				this.filer.getFileObject().removeKObject(o, Dia.hasConfirmed());
			}
		} else {
			this.filer.getFileObject().removeKObject(o);
		}
	}
	public boolean isPossible() {
		return this.filer.getSelNum() > 0;
	}
}