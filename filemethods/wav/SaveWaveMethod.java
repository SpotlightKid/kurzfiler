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

package filemethods.wav;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import kurzobjects.KHashtable;
import kurzobjects.NoteNr;
import kurzobjects.samples.KSample;
import kurzobjects.samples.Soundfilehead;
import resources.Messages;

public class SaveWaveMethod extends WavFileMethod {

	public boolean onlyAsACopy() {
		return true;
	}

	public boolean Save(KHashtable kobjlist, String name) {
		KSample ks;
		Soundfilehead sfh, sfh2;
		SoundForgeSampleInfo smplinfo = new SoundForgeSampleInfo();
		Iterator headers;
		Iterator iter = kobjlist.getSampleIterator();
		Formatchunk fmt = new Formatchunk();
		Datachunk data = new Datachunk();
		FourCC chunk_id = new FourCC();
		LE_int len = new LE_int();
		int counter = 0;
		short headnum;
		int i;

		while (iter.hasNext()) {
			ks = (KSample) kobjlist.get(iter.next());
			headers = ks.getIterator();
			while (headers.hasNext()) {
				
				headnum = ((Short) headers.next()).shortValue();
				sfh = ks.getheader(headnum);

				if (sfh.samplePeriod==0) {
					System.err.println("Sample period: " + sfh.samplePeriod);
					sfh.samplePeriod=22675;
				}

				if (ks.isStereo()) {
					sfh2 = ks.getheader(headnum + 1);
					fmt.channels = 2;
					fmt.setSamplerate((int) Math.floor(1000000000L / sfh.samplePeriod));
					//kann zu Rundungsfehlern führen!!
					fmt.bitspersample = 16;

					data.data = new byte[sfh.sampledata.length * 2];

					for (i = 0; i < sfh.sampledata.length; i++, i++) {
						data.data[2 * i] = sfh.sampledata[i];
						data.data[2 * i + 1] = sfh.sampledata[i + 1];
						data.data[2 * i + 2] = sfh2.sampledata[i];
						data.data[2 * i + 3] = sfh2.sampledata[i + 1];
					}

					SwapBytes(data.data);
				} else {
					fmt.channels = 1;
					fmt.setSamplerate((int) Math.floor(1000000000L / sfh.samplePeriod));
					//kann zu Rundungsfehlern führen!!
					fmt.bitspersample = 16;

					data.data = new byte[sfh.sampledata.length];
					System.arraycopy(
						sfh.sampledata,
						0,
						data.data,
						0,
						sfh.sampledata.length);
					SwapBytes(data.data);
				}
				
				
				smplinfo.setSamplePeriod(sfh.samplePeriod);
				smplinfo.setRootKey(sfh.rootkey);
				
				if (sfh.isLooped()) {
					smplinfo.setLooped(true);
					smplinfo.setLoopStart(sfh.sampleLoopStart);
					smplinfo.setLoopEnd(sfh.sampleEnd);
				}
				
								

				try {
					String filename=name + " " + String.valueOf(counter+1) +
						" " + NoteNr.getNote(sfh.rootkey);
					open(filename);
					counter++;

					chunk_id.setSignature("RIFF"); //$NON-NLS-1$
					chunk_id.write(theFile);

					len.write(theFile);
					//nachher richtig machen!

					chunk_id.setSignature("WAVE"); //$NON-NLS-1$
					chunk_id.write(theFile);

					fmt.write(theFile);
					data.write(theFile);
					smplinfo.write(theFile);

					len.setValue((int) theFile.getFilePointer() - 8);
					theFile.seek(4);
					len.write(theFile);

					data.data = null;
					//bitte freigeben!!

					close();
				} catch (IOException e) {
					throw new RuntimeException(Messages.getString("SaveWaveMethod.Error_while_writing_the_file")); //$NON-NLS-1$
				}
			}
		}
		return true;

	}

	protected boolean open(String openname) throws IOException {
		theFile = new RandomAccessFile(processFileName(openname), "rw"); //$NON-NLS-1$
		return true;
	}

	public boolean Load(KHashtable kh, String name) {
		return false;
	}

}
