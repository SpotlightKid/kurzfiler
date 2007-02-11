/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.TreeSet;

import kfcore.KurzFiler;
import kurzobjects.KObject;
import kurzobjects.samples.KSample;
import resources.Messages;

public class LoopOnlyCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	LoopOnlyCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2539618424176622509L;
	public String getName() {
		return  Messages.getString("KurzFiler.Loop_Only_(experimental)_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.Loop_Only_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		try {
			int[] indexes = this.filer.getObjectTable().getSelectedRows();
			TreeSet<Integer> samples = new TreeSet<Integer>();
			Integer id;
			int i;
			for (i = 0; i < indexes.length; i++) {
				id = this.filer.getFileObject().getIDAt(indexes[i]);
				if (id == null)
					continue;
				samples.add(id);
			}

			Iterator<Integer> sampleIter = samples.iterator();
			KObject ko;
			while (sampleIter.hasNext()) {
				ko = this.filer.getFileObject().getKObject(sampleIter.next());
				if (!(ko instanceof KSample))
					sampleIter.remove();
			}
			if (samples.size() < 1)
				return;

			sampleIter = samples.iterator();
			while (sampleIter.hasNext()) {
				ko = this.filer.getFileObject().getKObject(sampleIter.next());
				((KSample) ko).setLoopStartToSampleStart();
			}

			this.filer.getFileObject().updateList();
			this.filer.clearSel();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public boolean isPossible() {
		return this.filer.getSelSampleNum() > 0;
	}
}