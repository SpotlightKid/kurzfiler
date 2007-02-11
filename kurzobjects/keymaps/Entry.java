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
import java.io.RandomAccessFile;

public class Entry implements KeymapEntry {
	protected short subsamplenumber; //0x01
	protected short sampleID; //0x02
	protected byte volumeadjust; //0x04
	protected short tuning; //0x08: one byte
	//0x10: two byte

	public void setTuning(short t) {
		tuning = t;
	}
	public void setVolAdjust(byte v) {
		volumeadjust = v;
	}
	public void setSampleID(short id) {
		sampleID = id;
	}
	public void setSSNr(short nr) {
		subsamplenumber = nr;
	}

	public short getTuning() {
		return tuning;
	}
	public byte getVolAdjust() {
		return volumeadjust;
	}
	public short getSampleID() {
		return sampleID;
	}
	public short getSSNr() {
		return subsamplenumber;
	}

	public boolean isUsed() {
		return subsamplenumber != 0;
	}

	public boolean usesSameSample(KeymapEntry k) {
		if ((sampleID == k.getSampleID()) && (subsamplenumber == k.getSSNr()))
			return true;
		return false;
	}

	public void write(RandomAccessFile f) {
	}
}
