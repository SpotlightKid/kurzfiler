/**
 * 
 */
package kfcore.commands;

import javax.swing.Action;

import kfcore.KurzFiler;


import resources.Messages;

public class UndoCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6495838495508140273L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Undo_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Undo_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Undo_Command"); //$NON-NLS-1$
	}
	public UndoCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		this.filer.getFileObject().addUndoRedoListener(this);
		//	    putValue(Action.SMALL_ICON, new ImageIcon("undo.gif"));
		putValue(
			Action.SMALL_ICON,
			resources.Images.getImage(resources.Images.UNDO_ICON)); 
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("KurzFiler.Undo_short_Descr")); //$NON-NLS-1$
	}
	public void Execute() {
		this.filer.getFileObject().undo();
		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return this.filer.getFileObject().canUndo();
	}
}