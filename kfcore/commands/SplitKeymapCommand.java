/**
 * 
 */
package kfcore.commands;

import java.util.Iterator;
import java.util.Vector;

import kfcore.KurzFiler;
import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.KObject;
import kurzobjects.KProgram;
import kurzobjects.keymaps.KKeymap;
import kurzobjects.keymaps.VeloLevel;
import resources.Messages;
import dialogs.SplitKeymapDialog;

public class SplitKeymapCommand extends KCommand {
	/**
	 * 
	 */
	private final KurzFiler filer;
	/**
	 * @param filer
	 */
	public SplitKeymapCommand(KurzFiler filer) {
		super(filer);
		this.filer = filer;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5217703347392465157L;
	public String getName() {
		return Messages.getString("KurzFiler.New_Keymap_Command"); //$NON-NLS-1$
	}
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.New_Keymap_Mnem")).charAt(0); //$NON-NLS-1$
	}
	public void Execute() {
		int[] indexes = this.filer.getObjectTable().getSelectedRows();
		Vector<KKeymap> selection = new Vector<KKeymap>();
		KObject ko;
		KKeymap kk;
		int i;
		for (i = 0; i < indexes.length; i++) {
			ko = this.filer.getFileObject().getKObjectAt(indexes[i]);
			if (ko instanceof KKeymap) {
				kk = new KKeymap((KKeymap) ko);
				if (!kk.isCompactable())
					kk.setMethod((short) (kk.getMethod() | 2));
				selection.add(kk);
			}
		}
		SplitKeymapDialog dia = new SplitKeymapDialog(this.filer);

		Iterator iter = selection.iterator();
		String s;
		VeloLevel vl;
		Vector<VeloLevel> levels = new Vector<VeloLevel>();
		while (iter.hasNext()) {
			kk = (KKeymap) iter.next();
			//				if (kk.Method!=3) continue;
			for (i = 0; i < kk.numLevels(); i++) {
				vl = kk.getLevel(i);
				levels.add(new VeloLevel(vl));
				s = String.valueOf(KHash.getID(kk.getHash()));
				s += " " //$NON-NLS-1$
					+ kk.getName()
					+ " Level " //$NON-NLS-1$
					+ String.valueOf(i)
					+ " "; //$NON-NLS-1$
				s += KKeymap.LEVEL_NAME[kk.minVelocity(vl)] + Messages.getString("KurzFiler.to_bis"); //$NON-NLS-1$
				s += KKeymap.LEVEL_NAME[kk.maxVelocity(vl)];
				dia.addMode(s);
			}
		}
		int[] wahl = new int[KKeymap.NUM_LEVELS];
		/*iter=levels.iterator();
		  while (iter.hasNext()) {
		
		  }*/
		for (i = 0; i < wahl.length; i++)
			wahl[i] = (int) (levels.size() * i) / (wahl.length);
		dia.setAuswahl(wahl);
		//Dia.setName (f.getKObject(o).nameOf());
		dia.setVisible(true);
		if (dia.getResult()) {
			wahl = dia.getAuswahl();
			kk = new KKeymap();

			short method = 3;

			kk.setHash(KHash.generate(200, KHash.T_KEYMAP));
			kk.setName(Messages.getString("KurzFiler.Velosplit_Keymap_Default_Name")); //$NON-NLS-1$
			kk.sampleId = 0;
			kk.method = 3;
			kk.basePitch = 0;
			kk.centsPerEntry = 100;
			kk.entriesPerVel = 127;
			kk.entrySize = 3;

			vl = (VeloLevel) levels.elementAt(wahl[0]);
			method |= vl.getMethod();
			kk.insertMap(vl);
			kk.velomapping[0] = vl;
			vl.setRang(0);
			for (i = 1; i < wahl.length; i++) {
				vl = (VeloLevel) levels.elementAt(wahl[i]);
				if (vl == kk.velomapping[i - 1]) {
					//keine neue Stufe
					kk.velomapping[i] = kk.velomapping[i - 1];
					continue;
				}
				method |= vl.getMethod();
				kk.insertMap(vl);
				kk.velomapping[i] = vl;
				vl.setRang(kk.velomapping[i - 1].getRang() + 1);
			}

			kk.setMethod(method);

			KProgram kp = new KProgram();
			kp.setHash(KHash.generate(200, KHash.T_PROGRAM));
			kp.setName(Messages.getString("KurzFiler.VeloSplit_Program_Default_Name")); //$NON-NLS-1$
			kp.makePGMblock();
			kp.addLayer(kk, false); //erstmal mono

			KHashtable newObjects = new KHashtable();

			newObjects.put(new Integer(kk.getHash()), kk);
			newObjects.put(new Integer(kp.getHash()), kp);

			if (this.filer.nameNewObjects(kk, kp)) {
				this.filer.getFileObject().importData(newObjects);
			}

			this.filer.getFileObject().updateList();
			this.filer.clearSel();
		}
	}
	public boolean isPossible() {
		return this.filer.getSelKeymapNum() > 0;
	}
}