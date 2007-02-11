/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;

import kfcore.KurzFiler;
import kurzobjects.samples.KSample;
import resources.Messages;
import dialogs.MessageDialog;

public class FileInfoCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public FileInfoCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5863394401426846844L;
	public String getName() {
		return Messages.getString("KurzFiler.File_Information_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.File_Information_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		int i;
		Iterator<Integer> iter;
		String info = Messages.getString("KurzFiler.<font_size_+1>File_Information</font><br>"); //$NON-NLS-1$
		info += Messages.getString("KurzFiler.Number_of_Objects") + String.valueOf(this.filer.getFileObject().getSize()); //$NON-NLS-1$
		iter = this.filer.getFileObject().getIterator();
		i = 0;
		while (iter.hasNext()) {
			i += this.filer.getFileObject().getKObject(iter.next()).getSize();
		}
		info += Messages.getString("KurzFiler.<br>PRAM_Space_needed") + String.valueOf(i) + " Bytes"; //$NON-NLS-1$ //$NON-NLS-2$

		iter = this.filer.getFileObject().getSampleIterator();
		i = 0;
		while (iter.hasNext()) {
			i += ((KSample) this.filer.getFileObject().getKObject(iter.next())).getRamSize();
		}
		i /= 1024;
		info += Messages.getString("KurzFiler.<br>RAM_Space_needed") + String.valueOf(i) + " KiloBytes"; //$NON-NLS-1$ //$NON-NLS-2$
		MessageDialog msgdia = new MessageDialog(this.filer);
		msgdia.setMessage(info);
		msgdia.setVisible(true);
	}
	public boolean isPossible() {
		return !this.filer.getFileObject().isEmpty();
	}
}