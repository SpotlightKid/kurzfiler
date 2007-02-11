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

package filemethods.kurzweil;
import java.io.IOException;
import java.io.RandomAccessFile;

import resources.Messages;

public class KHeader {
	protected byte[] magic = new byte[4];
	public int osize;
	protected int[] rest = new int[6];

	public void init () {
		magic[0]='P';
		magic[1]='R';
		magic[2]='A';
		magic[3]='M';
		rest[2]=353; //Software-Version
	}

	public boolean read (RandomAccessFile f) {
		if (f!=null) {
			try {
				f.readFully (magic);
				osize=f.readInt();
				for (int i=0;i<6;i++) rest[i]=f.readInt();
			} catch (IOException e) {
				throw new RuntimeException(Messages.getString("KHeader.Could_not_read_the_header")); //$NON-NLS-1$
			}
		}
		if (
			(magic[0]!='P') ||
			(magic[1]!='R') ||
			(magic[2]!='A') ||
			(magic[3]!='M') 
			) return false;
		
		return true;
	}

	public boolean write (RandomAccessFile f) {
		init();
		if (f!=null) {
			try {
				//f.seek(0);
				f.setLength(0);
				f.write (magic);
				f.writeInt (osize);
				for (int i=0;i<6;i++) f.writeInt (rest[i]);
			} catch (IOException e) {
				//throw new RuntimeException("Could not write the header");
				return false;
			}
		}		
		return true;
	}
}
