/**
 * 
 */
package kfcore.commands;

import java.awt.Event;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import kfcore.KurzFiler;
import kfcore.StateListener;

public abstract class KCommand extends AbstractAction implements StateListener {
	/**
	 * 
	 */
	private final KurzFiler filer;

	public KCommand(KurzFiler filer) {
		this.filer = filer;
		putValue(Action.NAME, getName());
		if (getMnemonic()!=0 && getMnemonic()!='!') //! ist Zeichen für nicht vorhandenes!
			putValue(Action.MNEMONIC_KEY, new Integer(getMnemonic()));
		putValue(Action.ACCELERATOR_KEY, getKeyStroke());
	}

	public abstract String getName();

	public abstract void Execute();

	public abstract boolean isPossible();

	public char getShortCut() {
		return 0;
	}

	public char getMnemonic() {
		return 0;
	}

	public KeyStroke getKeyStroke() {
		if (getShortCut()!=0 && getShortCut()!='!')
			return KeyStroke.getKeyStroke(getShortCut(), Event.CTRL_MASK);
		return null;
	};



	public void update() {
		setEnabled(isPossible());
	}

	public void actionPerformed(ActionEvent event) {
		this.filer.getFileObject().startAction();
		Execute();
		this.filer.getFileObject().endAction();
	}

	protected KurzFiler getFiler() {
		return filer;
	}
}