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

LoadIffMethod.java written by Sven Thoennissen

*/

package filemethods.iff;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.util.regex.*;

import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.samples.KSample;
import kurzobjects.samples.Soundfilehead;
import resources.Messages;

public class LoadIffMethod extends IffFileMethod
{
	protected boolean open(String openname) throws IOException {
		theFile = new RandomAccessFile(openname, "r");
		return true;
	}

	public boolean Save(KHashtable kobjlist, String name) {
		return false;
	}

	protected VHDR_chunk vhdr;
	protected BODY_chunk body;

	private void InsertData(KHashtable kobjlist, String name)
	{
		KSample ks = new KSample();
		Soundfilehead sh = new Soundfilehead();

		sh.samplePeriod = (int) Math.round(Math.ceil(1000000000L / vhdr.samplerate));
		sh.flags = 0x70; 
		sh.setRootKey((byte)60);
		sh.sampleStart = 0;
		sh.altSampleStart = 0;
		if ( vhdr.loop_enabled ) {
			sh.sampleEnd = vhdr.loopstart + vhdr.looplen - 1;
			sh.sampleLoopStart = vhdr.loopstart;
		} else {
			sh.sampleEnd = vhdr.looplen - 1;
			sh.sampleLoopStart = sh.sampleEnd;
		}

		sh.sampledata = new byte[body.data.length * 2];
		int j = 0;
		for (int i = 0; i < body.data.length; i++) {
			sh.sampledata[j++] = body.data[i]; // MSB
			sh.sampledata[j++] = 0; // LSB
		}

		try {
			ks.setName(name);
		} catch (Exception e) {
		}
		ks.setHash(KHash.generate(200, KHash.T_SAMPLE));

		ks.insertHeader(sh);
		ks.generateEnvelopes();
		ks.baseID = 1;
		ks.copyID = 0;
		ks.ks1 = 0;
		ks.ks2 = 0;
		ks.flags = 0; //mono

		kobjlist.put(new Integer(ks.getHash()), ks);		
	}

	public boolean Load(KHashtable kobjlist, String filename)
	{
		vhdr = null;
		body = null;
		FourCC fcc = new FourCC();
		int chunklen;

		String name = new File(filename).getName();
		Matcher m = Pattern.compile("(.+)\056[iI][fF][fF]$").matcher(name);
		if (m.matches()) {
			name = m.group(1);
		}

		try {
			open(filename);
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("LoadWaveMethod.Could_not_open_the_file"));
		}

		try {
			while (true) {
				fcc.read(theFile);
				chunklen = theFile.readInt();
				if (fcc.equals("FORM")) {
					fcc.read(theFile);
					if (!fcc.equals("8SVX")) {
						close();
						throw new RuntimeException(Messages.getString("LoadWaveMethod.Unsupported_Waveformat"));
					}
				} else if (fcc.equals("VHDR")) {
					vhdr = new VHDR_chunk(theFile);
					if ((vhdr.channels == 1) && (vhdr.bitspersample == 8)) {
						// this vhdr is supported
					} else {
						throw new IOException(Messages.getString("LoadWaveMethod.Unsupported_Waveformat"));
					}
				} else if (fcc.equals("BODY")) {
					body = new BODY_chunk(theFile, chunklen);
				} else if (fcc.equals("NAME")) {
					byte[] b = new byte[chunklen];
					theFile.readFully(b);
					int i, len = 0;
					for ( i = 0; i < chunklen; ++i ) {
						if (b[i] == 0) break;
						++len;
					}
					name = new String(b, 0, len);
				} else {
					theFile.skipBytes(chunklen);
				}
			}
		} catch (EOFException e) {
			close();
		} catch (IOException e) {
			close();
			e.printStackTrace();
			throw new RuntimeException(Messages.getString("LoadWaveMethod.Error_while_loading_Wavefile") + ": " + e.getMessage());
		}

		if ( vhdr == null ) {
			throw new RuntimeException(Messages.getString("LoadIffMethod.VHDR_chunk_missing"));
		}
		if ( body == null ) {
			throw new RuntimeException(Messages.getString("LoadIffMethod.BODY_chunk_missing"));
		}
		InsertData(kobjlist, name);
		return true;
	}
}
