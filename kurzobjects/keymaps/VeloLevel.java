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
import java.util.TreeSet;

import kurzobjects.KHash;
import kurzobjects.samples.KSample;
import resources.Messages;

public class VeloLevel {
	private class GenericEntry extends Entry {

		public GenericEntry() {
		}

		public GenericEntry(GenericEntry g) {
			tuning = g.tuning;
			volumeadjust = g.volumeadjust;
			sampleID = g.sampleID;
			subsamplenumber = g.subsamplenumber;
		}

		public GenericEntry(KeymapEntry g) {
			tuning = g.getTuning();
			volumeadjust = g.getVolAdjust();
			sampleID = g.getSampleID();
			subsamplenumber = g.getSSNr();
		}

		public GenericEntry(RandomAccessFile f) throws IOException {
			if ((method & 0x10) > 0)
				tuning = (short) f.readShort();
			else if ((method & 0x8) > 0)
				tuning = (short) f.readByte();
			if ((method & 0x04) > 0)
				volumeadjust = (byte) f.readByte();
			if ((method & 0x02) > 0)
				sampleID = (short) f.readShort();
			if ((method & 0x01) > 0)
				subsamplenumber = (short) f.readByte();
		}

		public void write(RandomAccessFile f) {
			try {
				if ((method & 0x10) > 0)
					f.writeShort(tuning);
				else if ((method & 0x8) > 0)
					f.writeByte(tuning);
				if ((method & 0x04) > 0)
					f.writeByte(volumeadjust);
				if ((method & 0x02) > 0)
					f.writeShort(sampleID);
				if ((method & 0x01) > 0)
					f.writeByte(subsamplenumber);
			} catch (IOException e) {
				throw new RuntimeException(Messages.getString("VeloLevel.Error_while_writing_Keymap_entry")); //$NON-NLS-1$
			}
		}
	};

	public KeymapEntry[] map;

	protected short method;

	protected int rang;

	public VeloLevel(VeloLevel vl) {
		map = new KeymapEntry[vl.map.length];

		for (int i = 0; i < map.length; i++)
			map[i] = new GenericEntry(vl.map[i]);

		method = vl.method;
		rang = vl.rang;
	}

	public VeloLevel(short method, int UBound) {
		int i;

		this.method = method;

		map = new KeymapEntry[UBound];

		for (i = 0; i < UBound; i++)
			map[i] = new GenericEntry();
	}

	public VeloLevel(short method, int UBound, RandomAccessFile f)
		throws IOException {
		int i;

		this.method = method;

		map = new KeymapEntry[UBound];

		for (i = 0; i < UBound; i++)
			map[i] = new GenericEntry(f);
	}

	public void write(RandomAccessFile f) {
		int i;
		for (i = 0; i < map.length; i++)
			map[i].write(f);
	}

	public short getSize() {
		if (map == null)
			return 0;
		return (short) (map.length * KKeymap.Method2Size(method));
	}

	public void setRang(int i) {
		rang = i;
	}

	public int getRang() {
		return rang;
	}

	public void setMethod(short m) {
		method = m;
	}

	public short getMethod() {
		return method;
	}

	protected void updateLink(Hashtable<Integer,Integer> newIDsTbl) {
		Integer id;
		for (int i = 0; i < map.length; i++) {
			id =
				new Integer(
					KHash.generate(map[i].getSampleID(), KHash.T_SAMPLE));
			if (newIDsTbl.get(id) != null)
				map[i].setSampleID(KHash.getID(newIDsTbl.get(id)));
		}
	}

	protected void exchangeSamples(Hashtable<KeymapEntry,KeymapEntry> excTbl) {
		KeymapEntry entry, newentry;
		Enumeration<KeymapEntry> iter;
		for (int i = 0; i < map.length; i++) {
			iter = excTbl.keys();
			while (iter.hasMoreElements()) {
				entry = iter.nextElement();
				if (entry.usesSameSample(map[i])) {
					newentry = excTbl.get(entry);
					map[i].setSampleID(newentry.getSampleID());
					map[i].setSSNr(newentry.getSSNr());
				}
			}
		}
	}

	protected TreeSet<Integer> getDependants() {
		TreeSet<Integer> dependants = new TreeSet<Integer>();
		for (int i = 0; i < map.length; i++)
			if (map[i].getSampleID() != 0)
				dependants.add(
					new Integer(
						KHash.generate(map[i].getSampleID(), KHash.T_SAMPLE)));
		return dependants;
	}

	public void setSample(KSample ks, int key) {
		map[key].setSSNr((short) 1);
		map[key].setSampleID(KHash.getID(ks.getHash()));
		map[key].setTuning((short) (100 * (48 - key)));
	}

	public void setSample(KSample ks, short subsample, int key) {
		//SubSample ist jetzt null-Basiert!!
		map[key].setSSNr((short) (subsample + 1));
		map[key].setSampleID(KHash.getID(ks.getHash()));
		int root = ks.getheader(subsample).rootkey - 12;
		map[key].setTuning((short) (100 * (root - key)));
	}

	public void fillSpacesBetweenSamples() {
		int i;
		boolean fill;

		do {
			fill = false;
			for (i = 1; i < map.length; i++) {
				if ((map[i].isUsed() == false)
					&& (map[i - 1].isUsed() == true)) {
					map[i] = map[i - 1];
					i++; //nur einen pro Durchgang
					fill = true;
				}
			}
			for (i = map.length - 2; i >= 0; i--) {
				if ((map[i].isUsed() == false)
					&& (map[i + 1].isUsed() == true)) {
					map[i] = map[i + 1];
					i--; //nur einen pro Durchgang
					fill = true;
				}
			}
		} while (fill);
	}
}
