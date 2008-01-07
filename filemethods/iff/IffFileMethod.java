/*
KurzFiler
A Soundfile Editor for Kurzweil Samplers

Copyright (c) 2003 Marc Halbruegge
Copyright (c) 2007 Sven Thoennissen
  
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


package filemethods.iff;

import java.io.File;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;

import javax.swing.filechooser.FileFilter;

import filemethods.FileMethod;
import resources.Messages;

abstract class IffFileMethod extends FileMethod
{
	class FourCC {
		private String fccstring;

		FourCC(RandomAccessFile f) throws IOException
		{
			read(f);
		}

		FourCC(String s) {
			fccstring = s;
		}

		FourCC() {
			fccstring = "none";
		}

		void read(RandomAccessFile f) throws IOException {
			byte[] buf = new byte[4];
			f.readFully(buf);
			fccstring = new String(buf);
		}

		void write(RandomAccessFile f) throws IOException {
			f.write(fccstring.getBytes());
		}

		public String toString() {
			return fccstring;
		}

		boolean equals(String s) {
			return s.equals(fccstring);
		}
	}

	class VHDR_chunk
	{
		int channels, samplerate, bitspersample, loopstart, looplen;
		boolean loop_enabled;

		VHDR_chunk( RandomAccessFile file ) throws IOException, EOFException
		{
			read(file);
		}

		void write(RandomAccessFile f) throws IOException {
			f.writeBytes("VHDR");
			f.writeInt(20);
			f.writeInt(loopstart);
			f.writeInt(looplen);
			f.write(32);
			f.writeShort(samplerate);
			f.writeByte(1); // octaves
			f.writeByte(0); // no compression
			f.writeInt(65536); // volume 1.0
		}

		void read(RandomAccessFile f) throws IOException, EOFException
		{
			loopstart = f.readInt();
			looplen = f.readInt();
			loop_enabled = f.readInt() != 0;
			samplerate = f.readShort();
			if ( f.readByte() != 1 ) {
				throw new IOException(Messages.getString("IffFileMethod.unsupported_octaves"));
			}
			if ( f.readByte() != 0 ) {
				throw new IOException(Messages.getString("IffFileMethod.unsupported_compression"));
			}
			f.readInt(); // ignore volume
			channels = 1;
			bitspersample = 8;
		}
	}

	class BODY_chunk {
		byte[] data;

		BODY_chunk( RandomAccessFile file, int len ) throws IOException, EOFException
		{
			read(file, len);
		}

		void write(RandomAccessFile f) throws IOException
		{
			throw new IOException("nyi");
		}

		void read(RandomAccessFile f, int len) throws IOException, EOFException
		{
			data = new byte[len];
			f.readFully(data);
		}
	}

	public FileFilter getFileFilter() {
		return new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return matchesExtensions(f.getName());
			}
			public String getDescription() {
				return Messages.getString("IffFileMethod.TypeNameWithExt");
			}
		};
	}

	public String getFileTypeDescription() {
		return Messages.getString("LoadIffMethod.TypeName");
	}

	public String defExtension() {
		return ".iff";
	}

	public boolean matchesExtensions(String name) {
		return name.matches(".+\056[iI][fF][fF]");
	}
}
