/**
 * 
 */
package kfcore.commands;

import java.io.File;

import kfcore.KurzFiler;


import resources.Messages;
import filemethods.FileMethodInterface;
import filemethods.kurzweil.LoadK2x00Method;

public class RevertCommand extends BackGroundLoaderCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public RevertCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
		loader = new Loader(this.filer, this);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5533338116141232536L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Revert_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return  Messages.getString("KurzFiler.Revert_Command"); //$NON-NLS-1$
	}
	private Loader loader=null;
	private LoadK2x00Method filemethod = new LoadK2x00Method();
	protected FileMethodInterface getFileMethod() {
		return filemethod;
	}
	protected boolean appendToCurrentObject() {
		return false;
	}
	protected Loader getLoader() {
		return loader;
	}
	public void Execute() {
		String name = this.filer.getFileObject().getName();
		if (name != null) {
			this.filer.getFileObject().reinit();
			this.filer.getFileObject().setName(name);
			setFiles(new File[1]);
			getFiles()[0] = new File(name);
			index = -1;
			loadIteration();
		}
	}
	public boolean isPossible() {
		return !(this.filer.getFileObject().getName() == null);
	}
}