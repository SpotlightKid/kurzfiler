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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.samples.KSample;
import kurzobjects.samples.Soundfilehead;
import resources.Messages;

public class LoadWaveMethod extends WavFileMethod {

	public String getFileTypeDescription() {
		return Messages.getString("LoadWaveMethod.Windows_Wave_Audio"); //$NON-NLS-1$
	}

	public String defExtension() {
		return ".wav"; //$NON-NLS-1$
	}

	public boolean matchesExtensions(String name) {
		if (name.endsWith(".WAV") || name.endsWith(".wav")) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		return false;
	}

	protected boolean open(String openname) throws IOException {
		theFile = new RandomAccessFile(openname, "r"); //$NON-NLS-1$
		return true;
	}

	public boolean Save(KHashtable kobjlist, String name) {
		return false;
	}

	//gemeinsame Variablen

	protected Formatchunk fmt;
	protected Datachunk data;
	protected SoundForgeSampleInfo smplinfo;
	
	protected abstract class Wav2KSample {
		public abstract void InsertData(KHashtable kobjlist, String name);
		public Soundfilehead CreateSoundfileHead(int numSamples) {
			Soundfilehead sh;
			sh = new Soundfilehead();
			if (smplinfo.isValid()) {
				if(smplinfo.getSamplePeriod() != 0) {
					sh.samplePeriod =
						smplinfo.getSamplePeriod();
				} else {
					sh.samplePeriod =
						(int) Math.round(
							Math.ceil(1000000000L / fmt.getSamplerate()));
				}
								
				sh.flags = 0x70; 
				sh.setRootKey(smplinfo.getRootKey());
							
				sh.sampleStart = 0;
				sh.altSampleStart = 0;

				if (smplinfo.isLooped()) {
					sh.sampleEnd = smplinfo.getLoopEnd();
					sh.sampleLoopStart = smplinfo.getLoopStart();															
				} else {
					sh.sampleEnd = numSamples - 1;
					sh.sampleLoopStart = sh.sampleEnd;
				}
			} else {
				sh.samplePeriod =
					(int) Math.round(
						Math.ceil(1000000000L / fmt.getSamplerate()));
				sh.flags = 0x70; //Loopswitch=on
				//needs load
				//RAM based
				//Shareware					
				sh.setRootKey((byte) 60);
	
				sh.sampleStart = 0;
				sh.altSampleStart = 0;
				sh.sampleEnd = numSamples - 1;
				sh.sampleLoopStart = sh.sampleEnd;
			}
			return sh;
		}
	};
	
	protected class Wav16BitMono2KSample extends Wav2KSample {
		public void InsertData(KHashtable kobjlist, String name) {
			KSample ks;
			Soundfilehead sh;
			ks = new KSample();
			sh = CreateSoundfileHead(data.data.length / 2);

			SwapBytes(data.data);
			sh.sampledata = data.data;

			try {
				ks.setName(name.toLowerCase());
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
	};

	protected class Wav16BitStereo2KSample extends Wav2KSample {
		public void InsertData(KHashtable kobjlist, String name) {
			KSample ks;
			Soundfilehead sh;

			ks = new KSample();
			try {
				ks.setName(new File(name).getName().toLowerCase());
			} catch (Exception e) {
			}
			ks.setHash(KHash.generate(200, KHash.T_SAMPLE));

			SwapBytes(data.data);
			int datalen = data.data.length / 2;


			sh = CreateSoundfileHead(datalen / 2);
			sh.sampledata = new byte[datalen];
			for (int i = 0; i < datalen; i++, i++) {
				sh.sampledata[i] = data.data[2 * i];
				sh.sampledata[i + 1] = data.data[(2 * i) + 1];
			}
			ks.insertHeader(sh);


			sh = CreateSoundfileHead(datalen / 2);
			sh.sampledata = new byte[datalen];
			for (int i = 0; i < datalen; i++, i++) {
				sh.sampledata[i] = data.data[(2 * i) + 2];
				sh.sampledata[i + 1] = data.data[(2 * i) + 3];
			}
			
			ks.insertHeader(sh);

			ks.generateEnvelopes();
			ks.baseID = 1;
			ks.copyID = 0;
			ks.ks1 = 0;
			ks.ks2 = 0;
			ks.flags = 1; //stereo

			kobjlist.put(new Integer(ks.getHash()), ks);
		}
	};

	protected class Wav8BitMono2KSample extends Wav2KSample {
		public void InsertData(KHashtable kobjlist, String name) {
			KSample ks;
			Soundfilehead sh;
			ks = new KSample();
			sh = new Soundfilehead();
			sh = CreateSoundfileHead(data.data.length);
						
			int datalen = data.data.length;

			sh.sampledata = new byte[datalen * 2];
			for (int i = 0; i < datalen * 2; i++, i++) {
				sh.sampledata[i] =
					(byte) (data.data[i >> 1] ^ 0x80);
				//höchstes Bit invertieren!!
			}
			
			try {
				ks.setName(name.toLowerCase());
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
	};
		
	protected class Wav2KSampleFactory {
		void InsertData(KHashtable kobjlist, String name) {
			Wav2KSample wavtype;					
			if ((fmt.channels == 1) && 
				(fmt.bitspersample == 16)) {
				wavtype = new Wav16BitMono2KSample();
			} else if ((fmt.channels == 1) && 
					(fmt.bitspersample == 8)) {
				wavtype = new Wav8BitMono2KSample();
			} else if ((fmt.channels == 2) && 
					(fmt.bitspersample == 16)) {
				wavtype = new Wav16BitStereo2KSample();
			} else {
				throw new RuntimeException(Messages.getString("LoadWaveMethod.Unsupported_Waveformat")); //$NON-NLS-1$
			}

			wavtype.InsertData(kobjlist, name);
		}
	}
	
	public boolean Load(KHashtable kobjlist, String name) {
		fmt = new Formatchunk();
		data = new Datachunk();
		smplinfo = new SoundForgeSampleInfo();
		
		FourCC chunk_id = new FourCC();
		LE_int len = new LE_int();

		//KSample ks;
		//Soundfilehead sh;

		int gesamtlen;

		try {
			open(name);
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("LoadWaveMethod.Could_not_open_the_file")); //$NON-NLS-1$
		}

		try {
			chunk_id.read(theFile);
			if (chunk_id.equals("RIFF")) { //$NON-NLS-1$
				len.read(theFile);
				gesamtlen = len.valueOf();

				chunk_id.read(theFile);
				if (chunk_id.equals("WAVE")) { //$NON-NLS-1$
					fmt.read(theFile);
					do {
						if (!data.read(theFile)) {
							if (!smplinfo.read(theFile)) {
								//overread it!
								DiscardChunk(theFile);
							}
						}
					} while (theFile.getFilePointer()<gesamtlen+8);

					try {
						Wav2KSampleFactory wavtype = new Wav2KSampleFactory();					
						wavtype.InsertData(kobjlist, new File(name).getName());
					} catch (RuntimeException e) {
						close();
						throw e;
					}

				} else {
					//keine Wave Datei
					close();
					throw new RuntimeException(Messages.getString("LoadWaveMethod.Unknown_Fileformat_(expected_WAVE)")); //$NON-NLS-1$
				}
			} else {
				//keine RIFF Datei
				close();
				throw new RuntimeException(Messages.getString("LoadWaveMethod.Unknown_Fileformat_(expected_RIFF)")); //$NON-NLS-1$
			}

			close();
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("LoadWaveMethod.Error_while_loading_Wavefile")); //$NON-NLS-1$
		}
		return true;
	}
}
