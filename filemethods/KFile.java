/*
KurzFiler
A Soundfile Editor for Kurzweil Samplers

Copyright (c) 2003 Marc Halbruegge
  
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


Contact the author:
Marc Halbruegge
Auf Torf 21
49328 Melle
Germany
eMail: marc.halbruegge@uni-osnabrueck.de

*/

package filemethods;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

import kfcore.History;
import kfcore.StateListener;
import kfcore.fillmodes.FillMode;
import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.KObject;
import kurzobjects.keymaps.Entry;
import kurzobjects.keymaps.KKeymap;
import kurzobjects.keymaps.KeymapEntry;
import kurzobjects.samples.KSample;
import filemethods.kurzweil.K2x00FileMethod;
import filemethods.kurzweil.LoadK2x00Method;
import filemethods.kurzweil.SaveK2000Method;
import filemethods.kurzweil.SaveK2500noKDFXMethod;
import filemethods.kurzweil.SaveK2500withKDFXMethod;
import filemethods.wav.LoadWaveMethod;
import filemethods.wav.SaveWaveMethod;
import resources.Messages;

public class KFile extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 373269838149213692L;
	protected String name;
	private boolean dirty = false;
	protected FillMode fillmode;

	protected KHashtable kobjlist = new KHashtable();
	protected ArrayList<Integer> kobjIDs = new ArrayList<Integer>();

	protected History history = new History();

	protected Vector<FileMethod> importRoutines = new Vector<FileMethod>();
	protected Vector<FileMethodInterface> exportRoutines = new Vector<FileMethodInterface>();
	protected FileMethodInterface waveExport = new SaveWaveMethod();

	public KFile() {
		fillmode = new kfcore.fillmodes.FillModeFill();
		importRoutines.add(new LoadK2x00Method());
		importRoutines.add(new LoadWaveMethod());
		exportRoutines.add(new SaveK2000Method());
		exportRoutines.add(new SaveK2500noKDFXMethod());
		exportRoutines.add(new SaveK2500withKDFXMethod());
		exportRoutines.add(waveExport);
	}

	public FileFilter getKurzFileFilter() {
		return new K2x00FileMethod().getFileFilter();
	}

	public boolean save(String name) {
		boolean res;
		if (name == null) {
			if (this.name == null) {
				return false;
			} else {
				res = new K2x00FileMethod().Save(kobjlist, this.name);
			}
		} else {
			res = new K2x00FileMethod().Save(kobjlist, name);
		}
		setDirty(false);
		return res;
	}

	public boolean exportData(String name, FileMethodInterface m) {
		if (m.onlyAsACopy()) {
			return m.Save(kobjlist, name);
		} else {
			name = m.processFileName(name);
			setName(name);
			if (m.Save(kobjlist, name))
				setDirty(false);
			return !isDirty();
		}
	}

	public boolean importData(String name, FileMethodInterface m) {
		KHashtable tmp = new KHashtable();
		m.Load(tmp, name);
		importData(tmp);
		return true;
	}

	public Vector<FileMethod> getImportRoutines() {
		return importRoutines;
	}

	public Vector<FileMethodInterface> getExportRoutines() {
		return exportRoutines;
	}

	/******************
	Tablemodel - Routinen
	*/

	private Integer ids[];
	private String types[];
	private String names[];
	private String anzeige[];
	private Integer size[];

	private int stringzahl;

	private String[] columnNames = { Messages.getString("KFile.ID"), Messages.getString("KFile.Type"), Messages.getString("KFile.Name"), Messages.getString("KFile.Info"), Messages.getString("KFile.Size") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	public int getColumnCount() {
		return columnNames.length;
	}
	public int getRowCount() {
		return stringzahl;
	}
	public String getColumnName(int col) {
		return columnNames[col];
	}
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0 :
				return ids[row];
			case 1 :
				return types[row];
			case 2 :
				return names[row];
			case 3 :
				return anzeige[row];
			case 4 :
				return size[row];
			default :
				return ""; //$NON-NLS-1$
		}
	}
	public Class<?> getColumnClass(int c) {
		switch (c) {
			case 0 :
				return Integer.class;
			case 1 :
				return String.class;
			case 2 :
				return String.class;
			case 3 :
				return String.class;
			case 4 :
				return Integer.class;
			default :
				return getValueAt(0, c).getClass();
		}
	}
	public boolean isCellEditable(int row, int col) {
		return col == 2;
	}
	public Object getLongestColumnValue(int col) {
		switch (col) {
			case 0 :
				return "1000"; //$NON-NLS-1$
			case 1 :
				return "Program "; //$NON-NLS-1$
			case 2 :
				return "01234567890123456"; //$NON-NLS-1$
			case 3 :
				return "0123456789012345678901234567890"; //$NON-NLS-1$
			case 4 :
				return "65536"; //$NON-NLS-1$
			default :
				return ""; //$NON-NLS-1$
		}
		/*	    Object longest = null;
			    for (int i = 0; i < getRowCount(); i++) {
				Object val = getValueAt(i, col);
				if ((longest == null)
				    || (val.toString().length()
					> longest.toString().length())) {
				    longest = val;
				}
			    }
			    return longest;*/
	}
	public void setValueAt(Object value, int row, int col) {
		startAction();
		KObject ko = getKObjectAt(row);
		KObject newko = ko.deepCopy();
		removeKObject(ko);
		try {
			newko.setName((String) value);
		} catch (Exception e) {
			//Nothing
		}
		addKObject(newko);
		names[row] = newko.getName();
		endAction();
		fireTableCellUpdated(row, col);
	}

	public void updateList() {
		Integer id;
		int oldsize = getSize();
		kobjIDs.removeAll(kobjIDs);

		anzeige = new String[kobjlist.size()];
		ids = new Integer[kobjlist.size()];
		types = new String[kobjlist.size()];
		names = new String[kobjlist.size()];
		size = new Integer[kobjlist.size()];

		Iterator<Integer> keyiter = kobjlist.getIterator();
		stringzahl = 0;
		while (keyiter.hasNext()) {
			id = keyiter.next();
			kobjIDs.add(id);
			ids[stringzahl] =
				new Integer(KHash.getID((id)));
			types[stringzahl] = KHash.getName((id));
			names[stringzahl] = getKObject(id).getName();
			anzeige[stringzahl] = getKObject(id).getDescription();
			size[stringzahl] = new Integer(getKObject(id).getSize());
			stringzahl++;
		}
		if ((oldsize == 0) && (stringzahl == 0))
			return;

		fireTableRowsDeleted(0, oldsize);
		fireTableRowsInserted(0, getSize());
	}

	public int getSize() {
		//return kobjIDs.size();
		return stringzahl;
	}

	public short getObjectType(int index) {
		return KHash.getType(( kobjIDs.get(index)));
	}

	public KObject getKObjectAt(int index) {
		return kobjlist.getKObject(kobjIDs.get(index));
	}

	public Integer getIDAt(int index) {
		return kobjIDs.get(index);
	}

	public int getIndex(Object o) {
		for (int i = 0; i < kobjIDs.size(); i++)
			if (o.equals(kobjIDs.get(i)))
				return i;
		return 0;
	}

	public TreeSet<Integer> getIDs() {
		return kobjlist.getIDs();
	}

	public Iterator<Integer> getIterator() {
		return kobjlist.getIterator();
	}

	public Iterator<Integer> getSampleIterator() {
		return kobjlist.getSampleIterator();
	}

	private Vector<StateListener> stateListener = new Vector<StateListener>();

	public void addStateListener(StateListener s) {
		stateListener.add(s);
		s.update();
	}

	public void removeStateListener(StateListener s) {
		stateListener.remove(s);
	}

	private void fireUpdate() {
		Iterator<StateListener> iter = stateListener.iterator();
		while (iter.hasNext())
			 iter.next().update();
	}

	public boolean isEmpty() {
		if (kobjlist==null) return true;
		return kobjlist.isEmpty();
	}
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean b) {
		if (b==false) history.setSafePoint();
		
		dirty = b;
		fireUpdate();
		//System.out.println("dirty=" + b);
	}
	/************************
	Undo/Redo-Funktionen
	*/

	public void addUndoRedoListener(StateListener s) {
		history.addStateListener(s);
	}

	public void removeUndoRedoListener(StateListener s) {
		history.removeStateListener(s);
	}

	public void clearHistory() {
		history.clear();
	}

	public boolean canUndo() {
		return history.isUndoPossible();
	}

	public void undo() {
		history.undoAction(kobjlist);
		//schlechte Heuristik!
		if (isDirty() && (!canUndo())) setDirty(false);
		else if(!isDirty()) setDirty(true);
	}

	public boolean canRedo() {
		return history.isRedoPossible();
	}

	public void redo() {
		history.redoAction(kobjlist);
		
		//schlechte Heuristik!
		if (!isDirty())	{
			setDirty(true);
		} else if (isDirty() && history.isAtSafePoint()) {
			setDirty(false);
		}
		//fireUpdate();
	}

	public void startAction() {
		history.startAddingActions();
	}

	public void endAction() {
		history.endAddingActions();
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getName() {
		return name;
	}

	public void reinit() {
		name = null;
		kobjlist = null;
		setDirty(false);
		history.clear();
		System.gc();
		kobjlist = new KHashtable();
	}

	public KObject getKObject(Integer o) {
		return kobjlist.getKObject(o);
	}

	public void addKObject(KObject ko) {
		history.doAction(History.ADD, ko);
		kobjlist.put(new Integer(ko.getHash()), ko);
		setDirty(true);
	}

	public void removeKObject(KObject ko) {
		removeKObject(new Integer(ko.getHash()));
	}

	public void removeKObject(Integer o) {
		KObject ko = getKObject(o);
		if (ko == null)
			throw new RuntimeException(Messages.getString("KFile.RemovedNonexistentObject")); //$NON-NLS-1$
		history.doAction(History.REMOVE, ko);
		kobjlist.remove(o);
		setDirty(true);
	}

	public void removeKObject(Integer o, boolean deleteDependants) {
		if (deleteDependants) {
			TreeSet<Integer> dependants = getDirectDependants(o);
			removeKObject(o);
			if (dependants.size() > 0) {
				Iterator<Integer> depIter = dependants.iterator();
				while (depIter.hasNext())
					removeKObject(depIter.next(), true);
			}
		} else {
			removeKObject(o);
		}
		setDirty(true);
	}

	public TreeSet<Integer> getUsers(Integer o) {
		TreeSet<Integer> users = new TreeSet<Integer>();
		Integer hash;
		Iterator<Integer> iter = kobjlist.getIterator();
		while (iter.hasNext()) {
			hash = iter.next();
			if (getKObject(hash).isUsing(( o)))
				users.add(hash);
		}
		return users;
	}

	public TreeSet<Integer> getDirectDependants(Integer o) {
		TreeSet<Integer> dependants = getKObject(o).getDependants();
		Iterator<Integer> depIter = dependants.iterator();
		while (depIter.hasNext())
			if (!kobjlist.containsKey(depIter.next()))
				depIter.remove();
		return dependants;
	}

	public TreeSet<Integer> getDependantsRecursive(Integer o) {
		Integer hash;
		TreeSet<Integer> dependants = getDirectDependants(o);
		TreeSet<Integer> secondOrder = new TreeSet<Integer>();
		TreeSet<Integer> thirdOrder = new TreeSet<Integer>();
		Iterator<Integer> depIter = dependants.iterator();
		while (depIter.hasNext()) {
			hash = depIter.next();
			if (getKObject(hash).hasDependants())
				secondOrder.addAll(getDirectDependants(hash));
		}
		depIter = secondOrder.iterator();
		while (depIter.hasNext()) {
			hash = depIter.next();
			if (getKObject(hash).hasDependants())
				thirdOrder.addAll(getDirectDependants(hash));
		}
		dependants.addAll(secondOrder);
		dependants.addAll(thirdOrder);
		return dependants;
	}

	public void remapIDs() {
		KHashtable newTable = new KHashtable();
		Integer id;

		Iterator<Integer> iter = kobjlist.getIterator();
		while (iter.hasNext()) {
			id = iter.next();
			newTable.put(id, getKObject(id).deepCopy());
		}

		iter = kobjlist.getIterator();
		while (iter.hasNext()) {
			removeKObject(iter.next());
		}

		importData(newTable);
	}

	public void importData(KFile theother) {
		importData(theother.kobjlist);
	}

	public void importData(KHashtable theother) {
		Integer ID, newID;
		Hashtable<Integer, Integer> newIDsTbl = new Hashtable<Integer, Integer>();

		//nur Samples
		Iterator<Integer> IDsIter = theother.getSampleIterator();
		while (IDsIter.hasNext()) {
			ID = IDsIter.next();
			newID = fillmode.getNewHash(ID, kobjlist);
			if (newID != ID) {
				theother.getKObject(ID).setHash(newID);
				newIDsTbl.put(ID, newID);
			}
			addKObject(theother.getKObject(ID));
			theother.remove(ID);
		}
		theother.updateLinks(newIDsTbl);

		newIDsTbl.clear();
		//nur Keymaps
		IDsIter = theother.getKeymapIterator();
		while (IDsIter.hasNext()) {
			ID = IDsIter.next();
			newID = fillmode.getNewHash(ID, kobjlist);
			if (newID != ID) {
				theother.getKObject(ID).setHash(newID);
				newIDsTbl.put(ID, newID);
			}
			addKObject(theother.getKObject(ID));
			theother.remove(ID);
		}
		theother.updateLinks(newIDsTbl);

		//der Rest
		IDsIter = theother.getIterator();
		while (IDsIter.hasNext()) {
			ID =  IDsIter.next();
			newID = fillmode.getNewHash(ID, kobjlist);
			if (newID != ID) {
				theother.getKObject(ID).setHash(newID);
			}
			addKObject(theother.getKObject(ID));
			theother.remove(ID);
		}

	}

	public void setFillMode(FillMode f) {
		fillmode = f;
	}

	public FillMode getFillMode() {
		return fillmode;
	}

	public void compactKeymap(Integer o) {
		KObject ko;
		KKeymap kk;
		KSample ks;

		KHashtable newObjects = new KHashtable();
		Hashtable<KeymapEntry, KeymapEntry> oldhead2newhead;
		HashSet<Integer> programs, keymaps, samples;

		Integer ID;
		short subsample;

		if (o == null)
			return;
		try {
			ko = getKObject(o);
			if (ko instanceof KKeymap) {
				kk = (KKeymap) ko;
				if (kk.isCompactable()) {
					//BigEntries			

					ks = new KSample();
					ks.setHash(KHash.generate(1000, KHash.T_SAMPLE));
					ks.setName(kk.getName());
					ks.baseID = 1;
					ks.copyID = 0;
					ks.ks1 = 0;
					ks.ks2 = 0;
					ks.flags = 0; //mono
					ks.generateEnvelopes();

					samples = new HashSet<Integer>();
					samples.addAll(kobjlist.getDependants(o));
					if (samples.size() > 255) {
						//soviel geht leider nicht!!
						return;
					}

					oldhead2newhead = new Hashtable<KeymapEntry, KeymapEntry>();
					KeymapEntry entry, newentry;
					Iterator<Integer> depiter = samples.iterator();
					while (depiter.hasNext()) {
						ID =  depiter.next();
						if (((KSample) kobjlist.get(ID)).isMultiRoot()) {
							ks = null;
							break;
						}
						
						//multirootsamples gehen nicht!!
						entry = new Entry();
						entry.setSampleID(KHash.getID(ID));
						entry.setSSNr((short) 1);

						if (((KSample) kobjlist.get(ID)).isStereo()) {
							subsample =
								ks.insertHeader(
									((KSample) kobjlist.get(ID)).getheader(0));
							ks.insertHeader(
								((KSample) kobjlist.get(ID)).getheader(1));
							ks.flags = 1; //Stereo
						} else {
							subsample =
								ks.insertHeader(
									((KSample) kobjlist.get(ID)).getheader(0));
						}
						newentry = new Entry();
						newentry.setSampleID((short) 1000);
						newentry.setSSNr((short) subsample);

						oldhead2newhead.put(entry, newentry);
					}

					keymaps = new HashSet<Integer>();
					for (Iterator<Integer> sIter = samples.iterator();
						sIter.hasNext();
						) {
						ID = sIter.next();
						keymaps.addAll(getUsers(ID));
					}
					//alle Keymaps, die die Samples benutzen!

					programs = new HashSet<Integer>();
					Iterator<Integer> userIter = keymaps.iterator();
					while (userIter.hasNext()) {
						ID = userIter.next();
						programs.addAll(getUsers(ID));
						kk = (KKeymap) getKObject(ID).deepCopy();
						kk.exchangeSamples(oldhead2newhead);
						kk.compact();
						//hier werden die Keymaps kleiner!
						newObjects.put(ID, kk);
						removeKObject(ID);
					}
					//Keymaps updaten

					userIter = programs.iterator();
					while (userIter.hasNext()) {
						ID = userIter.next();
						newObjects.put(ID, getKObject(ID).deepCopy());
						removeKObject(ID);
					}
					//Programs mitkopieren, damit die Keymaps verlinkt bleiben

					if (ks != null) {
						newObjects.put(new Integer(ks.getHash()), ks);
						for (Iterator<Integer> weg = samples.iterator();
							weg.hasNext();
							) {
							ID = weg.next();
							removeKObject(ID);

						}
					}

					importData(newObjects);
				}
			}

		} catch (Exception e) {
			System.out.println(Messages.getString("KFile.ErrorOccured")); //$NON-NLS-1$
			System.out.println(e.toString());
			return;
		}

	}

}
