/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.TreeSet;

import kfcore.KurzFiler;
import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.KObject;
import kurzobjects.KProgram;
import kurzobjects.keymaps.KKeymap;
import kurzobjects.keymaps.VeloLevel;
import kurzobjects.samples.KSample;
import resources.Messages;

public class SimpleDrumKMCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SimpleDrumKMCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3859649208445677075L;
	public String getName() {
		return Messages.getString("KurzFiler.New_Drumset_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.New_Drumset_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		TreeSet<Integer> samples = new TreeSet<Integer>();
		Integer o;
		int i;
		for (i = 0; i < indexes.length; i++) {
			o = this.filer.getFileObject().getIDAt(indexes[i]);
			if (o == null)
				continue;
			samples.add(o);
		}

		Iterator headerIter;
		Iterator<Integer> sampleIter = samples.iterator();
		KObject ko;
		KKeymap kk;
		VeloLevel vl;
		int j;
		while (sampleIter.hasNext()) {
			ko = this.filer.getFileObject().getKObject(sampleIter.next());
			if (!(ko instanceof KSample))
				sampleIter.remove();
		}
		if (samples.size() < 1)
			return;

		kk = new KKeymap();
		kk.setHash(KHash.generate(200, KHash.T_KEYMAP));
		kk.setName(Messages.getString("KurzFiler.Drum_Keymap_Default_Name")); //$NON-NLS-1$
		kk.sampleId = 0;
		kk.method = 0x13;
		kk.basePitch = 0;
		kk.centsPerEntry = 100;
		kk.entriesPerVel = 127;
		kk.entrySize = 5;

		vl = kk.newLevel();

		sampleIter = samples.iterator();
		j = 24;
		while (sampleIter.hasNext()) {
			ko = this.filer.getFileObject().getKObject(sampleIter.next());
			headerIter = ((KSample) ko).getIterator();
			while (headerIter.hasNext()) {
				vl.setSample(
					(KSample) ko,
					((Short) headerIter.next()).shortValue(),
					j);
				j++;
			}
		}

		for (j = 0; j < 8; j++)
			kk.velomapping[j] = vl;

		KProgram kp = new KProgram();
		kp.setHash(KHash.generate(200, KHash.T_PROGRAM));
		kp.setName(Messages.getString("KurzFiler.Drumset_Program_Default_Name")); //$NON-NLS-1$
		kp.makePGMblock();
		kp.addLayer(
			kk,
			((KSample) this.filer.getFileObject().getKObject(samples.first())).isStereo());

		KHashtable newObjects = new KHashtable();
		newObjects.put(new Integer(kk.getHash()), kk);
		newObjects.put(new Integer(kp.getHash()), kp);

		if (this.filer.nameNewObjects(kk, kp)) {
			this.filer.getFileObject().importData(newObjects);
		}

		this.filer.getFileObject().updateList();
		this.filer.clearSel();
	}
	public boolean isPossible() {
		return this.filer.getSelSampleNum() > 0;
	}
}