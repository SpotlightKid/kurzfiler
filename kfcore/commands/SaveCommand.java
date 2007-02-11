/**
 * 
 */
package kfcore.commands;

import javax.swing.Action;

import kfcore.KurzFiler;


import resources.Messages;
import dialogs.MessageDialog;

public class SaveCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5293033237036276792L;
	public String getName() {
		return Messages.getString("KurzFiler.Save_Command"); //$NON-NLS-1$
	}
	public char getShortCut() {
		return new String(Messages.getString("KurzFiler.Save_Shortcut")).charAt(0); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Save_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public SaveCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		//putValue(Action.SMALL_ICON, new ImageIcon("save.gif"));
		putValue(
			Action.SMALL_ICON,
			resources.Images.getImage(resources.Images.SAVE_ICON)); 
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("KurzFiler.Save_short_Descr")); //$NON-NLS-1$
		this.filer.getFileObject().addStateListener(this);
	}
	public void Execute() {
		if (this.filer.getFileObject().getName() == null) {
			this.filer.getSaveasCmd().Execute();
			return;
		}
		//if (fileObject.isDirty()) {
		SplashScreen Splash = new SplashScreen(this.filer, SplashScreen.SAVE_MSG);
		Splash.showSplash();
		try {
			this.filer.getFileObject().save(null);
		} catch (Exception e) {
			MessageDialog msgdia = new MessageDialog(this.filer);
			msgdia.setMessage(
				Messages.getString("KurzFiler.An_error_occured_while_saving_the_file_<br><font_size_+1>") //$NON-NLS-1$
					+ e.getMessage());
			msgdia.setVisible(true);
		}
		Splash.hideSplash();
		//}
		this.filer.clearSel();
	}
	public boolean isPossible() {
		//return (fileObject.isDirty()) && (!fileObject.isEmpty()) && (!(fileObject.getName() == null));
		return !this.filer.getFileObject().isEmpty();
	}
}