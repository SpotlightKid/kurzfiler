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

package kurzobjects.keymaps;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import kurzobjects.KHash;
import kurzobjects.KObject;
import resources.Messages;


public class KKeymap extends KObject {
	public static final int NUM_LEVELS = 8;
	public static final String[] LEVEL_NAME =
		{ "ppp", "pp", "p", "mp", "mf", "f", "ff", "fff" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

	public short sampleId;
	public short method;
	public short basePitch;
	public short centsPerEntry;
	public short entriesPerVel; //-1
	public short entrySize;
	public short[] Level = new short[8];

	public Vector<VeloLevel> maps;
	public VeloLevel[] velomapping = new VeloLevel[8];

	private byte[] data;

	static public int Method2Size(int method) {
		int res = 0;
		if ((method & 0x10) > 0)
			res += 2;
		else if ((method & 0x8) > 0)
			res += 1;
		if ((method & 0x04) > 0)
			res += 1;
		if ((method & 0x02) > 0)
			res += 2;
		if ((method & 0x01) > 0)
			res += 1;
		return res;
	}

	public KKeymap() {
		sampleId = 0;
		method = 0;
		basePitch = 0;
		centsPerEntry = 0;
		entriesPerVel = 0;
		entrySize = 0;
		for (int i = 0; i < 8; i++)
			Level[i] = 0;
		data = null;
	}

	public int getSize() {
		int h;
		h = 28;
		h += maps.size() * entrySize * (entriesPerVel + 1);
		if (data != null)
			h += data.length;
		return h + super.getSize();
	}

	public void setMethod(short m) {
		Iterator<VeloLevel> iter;
		if (!isCompactable()) {
			iter = maps.iterator();
			VeloLevel vl;
			while (iter.hasNext()) {
				vl = iter.next();
				for (int i = 0; i <= entriesPerVel; i++)
					vl.map[i].setSampleID(sampleId);
				//falls 2 gesetzt wird!!
			}
		}
		iter = maps.iterator();
		while (iter.hasNext()) {
			iter.next().setMethod(m);
		}
		method = m;
		entrySize = (short) Method2Size(m);
	}

	public short getMethod() {
		return method;
	}

	public KKeymap(KKeymap kk) {
		super(kk);
		VeloLevel vl, vlnew;

		sampleId = kk.sampleId;
		method = kk.method;
		basePitch = kk.basePitch;
		centsPerEntry = kk.centsPerEntry;
		entriesPerVel = kk.entriesPerVel;
		entrySize = kk.entrySize;
		for (int i = 0; i < 8; i++) {
			Level[i] = kk.Level[i];
		}
		data = kk.data;
		maps = new Vector<VeloLevel>();
		Iterator<VeloLevel> iter = kk.maps.iterator();
		while (iter.hasNext()) {
			vl = iter.next();
			vlnew = new VeloLevel(vl);
			maps.add(vlnew);
			for (int i = 0; i < 8; i++) {
				if (kk.velomapping[i] == vl)
					velomapping[i] = vlnew;
			}
		}
	}

	public KObject deepCopy() {
		KKeymap kk = new KKeymap(this);
		kk.copyInfo(this);
		return kk;
	}

	public KKeymap(int i, RandomAccessFile f) throws IOException {
		//int size;
		int ofs;

		byte[] b;
		int count;
		int j, k;
		VeloLevel vl;

		setHash (i);
		f.readUnsignedShort(); //size
		ofs = f.readUnsignedShort();

		b = new byte[ofs - 2];
		f.readFully(b);

		String name = new String();
		for (j = 0; b[j] != 0; j++) {
			name += (char) b[j];
		}
		try {
			// some .k26 files contain names that are too long for a K2000
			setName (name);
		} catch (Exception e) {
			// do nothing
		}
		
		b = null;

		sampleId = f.readShort();
		method = f.readShort();
		basePitch = f.readShort();
		centsPerEntry = f.readShort();
		entriesPerVel = f.readShort();
		entrySize = f.readShort();
		for (j = 0; j < 8; j++)
			Level[j] = f.readShort();

		for (j = 0; j < 8; j++)
			Level[j] -= (short) ((8 - j) * 2);

		count = 1;

		for (j = 1; j < 8; j++)
			if (Level[j] != Level[j - 1])
				count++;

		maps = new Vector<VeloLevel>();
		for (j = 0; j < count; j++) {
			vl = new VeloLevel(method, entriesPerVel + 1, f);
			vl.setRang(j);
			maps.addElement(vl);
			for (k = 0; k < 8; k++) {
				if (Level[k] == 0)
					velomapping[k] = vl;
				Level[k] -= vl.getSize();
			}
		}
	}

	public void write(RandomAccessFile f) {
		long l;
		l = writestart(f);
		int j;
		try {
			f.writeShort(sampleId);
			f.writeShort(method);
			f.writeShort(basePitch);
			f.writeShort(centsPerEntry);
			f.writeShort(entriesPerVel);
			f.writeShort(entrySize);

			for (j = 0; j < 8; j++)
				Level[j] = (short) ((8 - j) * 2);
			for (j = 0; j < 8; j++)
				Level[j]
					+= (short) (velomapping[j].getRang() * velomapping[j].getSize());

			for (j = 0; j < 8; j++)
				f.writeShort(Level[j]);

			for (j = 0; j < maps.size(); j++)
				 (maps.elementAt(j)).write(f);
			//			f.write(data);
		} catch (IOException e) {
			throw new RuntimeException(
				Messages.getString("KKeymap.Error_while_writing_Keymap") + getName()); //$NON-NLS-1$
		}

		writefinish(f, l);
	}

	public void insertMap(VeloLevel vl) {
		if (maps == null)
			maps = new Vector<VeloLevel>();
		maps.addElement(vl);
		vl.setRang(maps.size() - 1);
	}

	public int numLevels() {
		return maps.size();
	}

	public VeloLevel getLevel(int i) {
		return (maps.elementAt(i));
	}

	public VeloLevel newLevel() {
		if (maps == null)
			maps = new Vector<VeloLevel>();
		VeloLevel vl = new VeloLevel(method, entriesPerVel + 1);
		maps.addElement(vl);
		vl.setRang(maps.size() - 1);
		return vl;
	}

	public int minVelocity(VeloLevel vl) {
		for (int j = 0; j < 8; j++)
			if (velomapping[j] == vl)
				return j;
		throw new RuntimeException(Messages.getString("KKeymap.Keymap__Velocity_Level_not_found")); //$NON-NLS-1$
	}

	public int maxVelocity(VeloLevel vl) {
		for (int j = 7; j >= 0; j--)
			if (velomapping[j] == vl)
				return j;
		throw new RuntimeException(Messages.getString("KKeymap.Keymap__Velocity_Level_not_found")); //$NON-NLS-1$
	}

	public String getLongName() {
		String s = super.getLongName();
		s += "  " + String.valueOf(numLevels()) + " Levels "; //$NON-NLS-1$ //$NON-NLS-2$
		s += " Mode " + String.valueOf(method); //$NON-NLS-1$
		return s;
	}

	public String getDescription() {
		return String.valueOf(numLevels()) + " Velocity Levels"; //$NON-NLS-1$
	}

	public boolean isCompactable() {
		return (method & 2) == 2;
	}

	protected void updateLink(Hashtable<Integer, Integer> newIDsTbl) {
		if (isCompactable()) {
			//			System.out.println("updateLink "+k_name+" "+alt+" "+neu);
			for (Enumeration<VeloLevel> mapEnum = maps.elements();
				mapEnum.hasMoreElements();
				)
				 mapEnum.nextElement().updateLink(newIDsTbl);
		} else {
			Integer i = new Integer(KHash.generate(sampleId, KHash.T_SAMPLE));
			if (newIDsTbl.get(i) != null)
				sampleId = KHash.getID(newIDsTbl.get(i));
		}
	}

	public void exchangeSamples(Hashtable<KeymapEntry,KeymapEntry> excTbl) {
		if (isCompactable()) {
			for (Enumeration<VeloLevel> mapEnum = maps.elements();
				mapEnum.hasMoreElements();
				)
				 mapEnum.nextElement().exchangeSamples(excTbl);
		} else {
			setMethod((short) (method | 2));
			for (Enumeration<VeloLevel> mapEnum = maps.elements();
				mapEnum.hasMoreElements();
				)
				 mapEnum.nextElement().exchangeSamples(excTbl);
		}
	}

	public void compact() {
		short id = 0;
		VeloLevel vl;
		int i;
		if (!isCompactable())
			return;

		for (Enumeration<VeloLevel> mapEnum = maps.elements();
			mapEnum.hasMoreElements();
			) {
			vl = mapEnum.nextElement();
			for (i = 0; i <= entriesPerVel; i++) {
				if (vl.map[i].isUsed()) {
					if (id == 0)
						id = vl.map[i].getSampleID();
					else if (id != vl.map[i].getSampleID())
						return;
					//nix zu machen
				}
			}
		}
		sampleId = id;
		setMethod((short) (method & 253));
	}

	public boolean hasDependants() {
		return true;
	}

	public TreeSet<Integer> getDependants() {
		TreeSet<Integer> dependants = new TreeSet<Integer>();
		if ((method & 2) != 2) {
			dependants.add(new Integer(KHash.generate(sampleId, KHash.T_SAMPLE)));
		} else {
			for (Enumeration<VeloLevel> mapEnum = maps.elements();
				mapEnum.hasMoreElements();
				)
				dependants.addAll(
					mapEnum.nextElement().getDependants());
		}
		return dependants;
	}
}
