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

public class MusicalKMCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public MusicalKMCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4956241940450256181L;
	public String getName() {
		return Messages.getString("KurzFiler.New_Instrument_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.New_Instrument_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		try {
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
			try {
				kk.setName(Messages.getString("KurzFiler.chromatic_Keymap_default_Name")); //$NON-NLS-1$
			} catch(Exception e) {};
			kk.sampleId = 0;
			kk.method = 3;
			kk.basePitch = 0;
			kk.centsPerEntry = 100;
			kk.entriesPerVel = 127;
			kk.entrySize = 3;

			vl = kk.newLevel();

			Iterator headerIter;
			short header;
			sampleIter = samples.iterator();
			while (sampleIter.hasNext()) {
				ko = this.filer.getFileObject().getKObject(sampleIter.next());
				headerIter = ((KSample) ko).getIterator();
				while (headerIter.hasNext()) {
					header = ((Short) headerIter.next()).shortValue();
					j = ((KSample) ko).getheader(header).rootkey - 12;
					vl.setSample((KSample) ko, header, j);
				}
			}
			vl.fillSpacesBetweenSamples();

			for (j = 0; j < 8; j++)
				kk.velomapping[j] = vl;

			KProgram kp = new KProgram();
			kp.setHash(KHash.generate(200, KHash.T_PROGRAM));
			kp.setName(Messages.getString("KurzFiler.Instrument_Program_default_Name")); //$NON-NLS-1$
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
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public boolean isPossible() {
		return this.filer.getSelSampleNum() > 0;
	}
}