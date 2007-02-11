/**
 * 
 */
package kfcore.commands;

import kfcore.KurzFiler;
import resources.Messages;

public class CompactCommand extends KMultipleCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public CompactCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2823227012019585054L;
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Compact_Keymap_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public String getName() {
		return Messages.getString("KurzFiler.Compact_Keymap_Command"); //$NON-NLS-1$
	}
	void SingleCommand(Integer o) {
		this.filer.getFileObject().compactKeymap(o);
	}
	public boolean isPossible() {
		return this.filer.getSelCompactableKeymapNum() > 0;
	}
}