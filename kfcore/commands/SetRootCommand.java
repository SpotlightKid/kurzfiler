/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import kurzobjects.KObject;
import kurzobjects.samples.KSample;
import resources.Messages;
import dialogs.MessageDialog;
import dialogs.RootKeyDialog;

public class SetRootCommand extends KMultipleCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SetRootCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -697936382875215316L;
	public String getName() {
		return  Messages.getString("KurzFiler.Set_Rootkey_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Set_Rootkey_Mnem")).charAt(0); //$NON-NLS-1$
	}
	void SingleCommand(Integer o) {
		KObject ko = this.filer.getFileObject().getKObject(o);
		if (ko instanceof KSample) {
			if (!((KSample) ko).isMultiRoot()) {
				KObject newko = ko.deepCopy();
				this.filer.getFileObject().removeKObject(o);
				RootKeyDialog Dia = new RootKeyDialog(this.filer);
				Dia.setName(newko.getLongName());
				Dia.setRootKey(((KSample) newko).getRootKey());
				Dia.setVisible(true);
				if (Dia.getResult())
					try {
						((KSample) newko).setRootKey(Dia.getRootKey());
					} catch (Exception e) {
						MessageDialog dia =
							new MessageDialog(this.filer);
						dia.setMessage(
							Messages.getString("KurzFiler.The_argument_you_typed_in_was_not_valid_and_has_been_ignored")); //$NON-NLS-1$
						dia.setVisible(true);
					}
				this.filer.getFileObject().addKObject(newko);
			} else {
				MessageDialog dia = new MessageDialog(this.filer);
				dia.setMessage(
					Messages.getString("KurzFiler.This_is_not_supported_for_MultiRoot_Samples")); //$NON-NLS-1$
				dia.setVisible(true);
			}
		}
	}
	public boolean isPossible() {
		return this.filer.getSelMonoRootSampleNum() > 0;
	}
}