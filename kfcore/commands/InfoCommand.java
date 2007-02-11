/**
 * 
 */
package kfcore.commands;

import java.awt.event.ActionEvent;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.AboutDialog;

public class InfoCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public InfoCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -685479857698662830L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.About_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.About_Command"); //$NON-NLS-1$
	}

	public void actionPerformed(ActionEvent event) {
		AboutDialog dia = new AboutDialog(this.filer);
		dia.setVisible(true);
	}
	public void Execute() {
	}
	public boolean isPossible() {
		return true;
	}
}