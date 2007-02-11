/**
 * 
 */
package kfcore.commands;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.LookAndFeelDialog;

public class LookAndFeelCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public LookAndFeelCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4194256393636250545L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Look_and_Feel_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Look_and_Feel_Command"); //$NON-NLS-1$
	}
	public void actionPerformed(ActionEvent event) {
		LookAndFeelDialog dia = new LookAndFeelDialog(this.filer);
		dia.updateState();
		dia.setVisible(true);
		if (dia.getResult()) {
			dia.setLooknFeel();
			SwingUtilities.updateComponentTreeUI(this.filer);
			SwingUtilities.updateComponentTreeUI(this.filer.getFileChooser());
		}
	}
	public boolean isPossible() {
		return true;
	}
	public void Execute() {
	}
}