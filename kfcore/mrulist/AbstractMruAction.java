/**
 * 
 */
package kfcore.mrulist;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

abstract public class AbstractMruAction extends AbstractAction implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private MRUList mru;
	private int index;
	public AbstractMruAction(MRUList mruList, int index) {
		this.mru=mruList;
		this.index=index;
		mru.addPropertyChangeListener(MRUList.MRU_LIST_ITEM, this);

		putValue(Action.NAME, String.valueOf(index+1)+" "+mru.getFiles().get(index).getName());
		putValue(Action.MNEMONIC_KEY, new Integer(String.valueOf(index+1).charAt(0)));
	}
	protected File GetFile() {
		return mru.getFiles().get(index);
	}
	public void propertyChange(PropertyChangeEvent evt) {
		// Hässlich, aber tut!
		if ((Integer)evt.getNewValue()==index)
			putValue(Action.NAME, String.valueOf(index+1)+" "+mru.getFiles().get(index).getName());
	}
	
}