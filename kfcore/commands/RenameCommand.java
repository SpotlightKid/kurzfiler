/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import kurzobjects.KObject;
import resources.Messages;
import dialogs.MessageDialog;
import dialogs.ObjectNameDialog;

public class RenameCommand extends KMultipleCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public RenameCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	private static final long serialVersionUID = -730466540122084531L;
	public String getName() {
		return Messages.getString("KurzFiler.Rename_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Rename_Mnem")).charAt(0); //$NON-NLS-1$
	}
	//	char getShortCut () {return 1;}
	//	KeyStroke getKeyStroke () {	
	//	    return KeyStroke.getKeyStroke(KeyEvent.VK_F2,0);
	//	}
	void SingleCommand(Integer o) {
		KObject ko = this.filer.getFileObject().getKObject(o);

		ObjectNameDialog Dia = new ObjectNameDialog(this.filer);
		Dia.setObjectDescription(ko.getLongName());
		Dia.setName(ko.getName());
		Dia.setVisible(true);
		if (Dia.getResult()) {
			KObject newko = ko.deepCopy();
			this.filer.getFileObject().removeKObject(o);
			try {
				newko.setName(Dia.getName());
			} catch (Exception e) {
				MessageDialog dia = new MessageDialog(this.filer);
				dia.setMessage(
					Messages.getString("KurzFiler.The_name_you_typed_in_was_too_long_and_has_been_truncated")); //$NON-NLS-1$
				dia.setVisible(true);
			}
			this.filer.getFileObject().addKObject(newko);
			//				f.setDirty(true);
		}

	}
	public boolean isPossible() {
		return this.filer.getSelNum() > 0;
	}
}