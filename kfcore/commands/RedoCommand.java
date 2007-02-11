/**
 * 
 */
package kfcore.commands;

import javax.swing.Action;

import kfcore.KurzFiler;


import resources.Messages;

public class RedoCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5799118712970639290L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Redo_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Redo_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return  Messages.getString("KurzFiler.Redo_Command"); //$NON-NLS-1$
	}
	public RedoCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		this.filer.getFileObject().addUndoRedoListener(this);
		//	    putValue(Action.SMALL_ICON, new ImageIcon("redo.gif"));
		putValue(
			Action.SMALL_ICON,
			resources.Images.getImage(resources.Images.REDO_ICON)); 
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("KurzFiler.Redo_short_Descr")); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.getFileObject().redo();
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return this.filer.getFileObject().canRedo();
	}
}