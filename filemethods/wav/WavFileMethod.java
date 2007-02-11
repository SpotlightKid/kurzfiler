/*
 * Created on 29.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package filemethods.wav;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.filechooser.FileFilter;

import filemethods.FileMethod;
import resources.Messages;

abstract class WavFileMethod extends FileMethod {
	static protected class LE_int {
		private int wert;
		private byte[] buf = new byte[4];

		private void buf2wert() {
			wert = buf[0];
			wert &= 0xff;

			int dummy = buf[1];
			dummy <<= 8;
			dummy &= 0xff00;
			wert |= dummy;
			//wert |= 0xff00 & (buf[1] << 8);
			
			dummy = buf[2];
			dummy <<= 16;
			dummy &= 0xff0000;
			wert |= dummy;
			//wert |= 0xff0000 & (buf[2] << 16);
			
			dummy = buf[3];
			dummy <<= 24;
			dummy &= 0xff000000;
			wert |= dummy;
			//wert |= 0xff000000 & (buf[3] << 24);
		}

		private void wert2buf() {
			buf[0] = (byte) (wert & 255);
			buf[1] = (byte) ((wert >>> 8) & 255);
			buf[2] = (byte) ((wert >>> 16) & 255);
			buf[3] = (byte) ((wert >>> 24) & 255);
		}

		void read(RandomAccessFile f) throws IOException {
			f.readFully(buf);
			buf2wert();
		}

		void write(RandomAccessFile f) throws IOException {
			wert2buf();
			f.write(buf);
		}

		int valueOf() {
			return wert;
		}

		void setValue(int i) {
			wert = i;
		}
	}

	static protected class FourCC {
		private char[] id = new char[4];

		void read(RandomAccessFile f) throws IOException {
			byte[] buf = new byte[4];
			f.readFully(buf);
			for (int i = 0; i < 4; i++)
				id[i] = (char) buf[i];
		}

		void write(RandomAccessFile f) throws IOException {
			byte[] buf = new byte[4];
			for (int i = 0; i < 4; i++)
				buf[i] = (byte) id[i];
			f.write(buf);
		}

		String getSignature() {
			return new String(id);
		}

		void setSignature(String s) {
			for (int i = 0; i < 4; i++)
				id[i] = s.charAt(i);
		}

		boolean equals(String s) {
			for (int i = 0; i < 4; i++)
				if (id[i] != s.charAt(i))
					return false;
			return true;
		}
	}

	static protected class Formatchunk {
		int channels;
		private int samplerate;
		int bitspersample;

		void write(RandomAccessFile f) throws IOException {
			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();

			chunk_id.setSignature("fmt "); //$NON-NLS-1$
			chunk_id.write(f);

			len.setValue(16);
			len.write(f);

			f.writeByte(1);
			//Standard-PCM
			f.writeByte(0);

			f.writeByte((byte) channels);
			//ein Kanal
			f.writeByte(0);

			len.setValue(getSamplerate());
			len.write(f);

			len.setValue(getSamplerate() * channels * 2);
			//Bytes per Sekunde
			len.write(f);

			f.writeByte((byte) channels * 2);
			//Bytes pro (Stereo-) Sample
			f.writeByte(0);

			f.writeByte((byte) bitspersample);
			//16
			f.writeByte(0);
		}

		void read(RandomAccessFile f) throws IOException {
			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();
			int fmtlen;

			chunk_id.read(f);
			if (chunk_id.equals("fmt ")) { //$NON-NLS-1$
				len.read(f);
				fmtlen = len.valueOf();

				if (f.readByte() != 1) {
					//komische wav-Datei
					throw new RuntimeException(Messages.getString("WavFileMethod.unknown_Waveformat")); //$NON-NLS-1$
				}
				f.readByte();

				channels = f.readByte();
				f.readByte();

				len.read(f);
				setSamplerate(len.valueOf());

				len.read(f);
				//bytespersecond ist uns egal

				f.readShort();
				//BlockAlign, z.B. 4 für stereo ínterleaved 16bit

				bitspersample = f.readByte();
				f.readByte();

				for (int i = 16; i < fmtlen; i++)
					f.readByte();
				//sicherstellen, daß chunk zuende ist
			}
		}

		public void setSamplerate(int samplerate) {
			if (Math.abs(samplerate - 44100) < 2) {
				this.samplerate = 44100;				
			} else if (Math.abs(samplerate - 22050) < 2) {
				this.samplerate = 22050;				
			} else {
				this.samplerate = samplerate;
			}
		}

		public int getSamplerate() {
			return samplerate;
		}
	}

	static protected class Datachunk {
		byte[] data;
		private static final String signature = "data";

		boolean isValid() {
			return (data != null);
		}

		void write(RandomAccessFile f) throws IOException {
			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();

			chunk_id.setSignature(signature);
			chunk_id.write(f);

			len.setValue(data.length);
			len.write(f);

			f.write(data);
		}

		boolean read(RandomAccessFile f) throws IOException {
			if (isValid()) return false;
			
			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();

			chunk_id.read(f);
			if (chunk_id.equals(signature)) {
				len.read(f);
				data = new byte[len.valueOf()];
				f.readFully(data);
				return true;
			} else {
				//soll's wer anders probieren
				f.seek(f.getFilePointer() - 4);
			}
			return false;
		}

	}

	static protected class SoundForgeSampleInfo {
		private LE_int[] data = null;
		private LE_int[] loopdata = null;
		private static final String signature = "smpl";

		void write(RandomAccessFile f) throws IOException {
			if (data == null)
				return;

			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();

			chunk_id.setSignature(signature);
			chunk_id.write(f);

			if (loopdata == null) {
				len.setValue(9 * 4);
				len.write(f);
				for (int i = 0; i < 9; i++)
					data[i].write(f);
			} else {
				len.setValue(9 * 4 + 6 * 4);
				len.write(f);
				for (int i = 0; i < 9; i++)
					data[i].write(f);
				for (int i = 0; i < 6; i++)
					loopdata[i].write(f);
			}
		}

		boolean read(RandomAccessFile f) throws IOException {
			if (isValid()) return false;

			FourCC chunk_id = new FourCC();
			LE_int len = new LE_int();

			chunk_id.read(f);
			if (chunk_id.equals(signature)) {
				len.read(f);
				int bytesRead = 0;
				
				data = new LE_int[9];
				for (int i = 0; i < 9; i++) {
					data[i]= new LE_int();
					data[i].read(f);
					bytesRead += 4;
				}

				if (isLooped()) {
					loopdata = new LE_int[6];
					for (int i = 0; i < 6; i++) {
						loopdata[i]= new LE_int();
						loopdata[i].read(f);
						bytesRead += 4;
					}
				}
				
				if (bytesRead < len.valueOf()) {
					f.skipBytes(len.valueOf()-bytesRead);
				}

				return true;
			} else {
				//soll's wer anders probieren
				f.seek(f.getFilePointer() - 4);
			}

			return false;
		}

		boolean isValid() {
			return (data != null);
		}
	
		void CheckMemAllocated() {
			if (!isValid()) {
				data = new LE_int[9];
				for (int i = 0; i < 9; i++) 
					data[i]= new LE_int();			
			}
		}

		boolean isLooped() {
			if (! isValid()) return false;
			return data[7].valueOf() == 1;
		}
		void setLooped(boolean flag) {
			CheckMemAllocated();
			data[7].setValue(flag ? 1 : 0);
			if (flag && (loopdata == null)) {
				loopdata = new LE_int[6];
				for (int i = 0; i < 6; i++) {
					loopdata[i]= new LE_int();
				}				
			}
		}
		
		byte getRootKey() {
			if (! isValid()) return 60; //C4
			return (byte) data[3].valueOf();
		}
		void setRootKey(byte b) {
			CheckMemAllocated();
			data[3].setValue(b);
		}
		
		int getLoopStart() {
			return loopdata[2].valueOf();
		}
		void setLoopStart(int s){
			CheckMemAllocated();
			loopdata[2].setValue(s);
		}

		int getLoopEnd() {
			return loopdata[3].valueOf();
		}
		void setLoopEnd(int e){
			CheckMemAllocated();
			loopdata[3].setValue(e);
		}		
		
		int getSamplePeriod() {
			return data[2].valueOf();
		}
		void setSamplePeriod(int p){
			CheckMemAllocated();
			data[2].setValue(p);
		}		
	}

	public FileFilter getFileFilter() {
		return new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String name = f.getName();
				if (name.endsWith(".wav") || name.endsWith(".WAV")) { //$NON-NLS-1$ //$NON-NLS-2$
					return true;
				}
				return false;
			}
			public String getDescription() {
				return Messages.getString("WavFileMethod.Windows_Wave_(.wav)"); //$NON-NLS-1$
			}
		};
	}

	public String getFileTypeDescription() {
		return Messages.getString("WavFileMethod.Windows_Wave_Audio"); //$NON-NLS-1$
	}

	public String defExtension() {
		return ".wav"; //$NON-NLS-1$
	}

	public boolean matchesExtensions(String name) {
		if (name.endsWith(".WAV") || name.endsWith(".wav")) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		return false;
	}

	protected static void SwapBytes(byte[] data) {
		byte h;
		for (int i = 0; i < data.length; i++, i++) {
			h = data[i];
			data[i] = data[(i ^ 1)];
			data[(i ^ 1)] = h;
		}
	}

	protected static void DiscardChunk(RandomAccessFile f) throws IOException {
		FourCC chunk_id = new FourCC();
		LE_int len = new LE_int();

		chunk_id.read(f);
		len.read(f);
		//System.out.println("Discarded " + len.valueOf() + " Bytes");
		f.skipBytes(len.valueOf());
	}
}
