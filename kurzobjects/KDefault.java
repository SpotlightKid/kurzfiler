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

package kurzobjects;
import java.io.IOException;
import java.io.RandomAccessFile;
import resources.Messages;

public class KDefault extends KObject {
	private byte[] data;

	public KDefault(int i, RandomAccessFile f)
	throws IOException
	{
		int size;
		int ofs;

		byte[] b;
		int count;

		setHash(i);
		size=f.readUnsignedShort();
		ofs=f.readUnsignedShort();

		b= new byte[ofs-2];
		f.readFully(b);
		
		String name=new String();
		for (int j=0;b[j]!=0; j++) {
			name+=(char)b[j];
		}
		try {
			// some .k26 files contain names that are too long for a K2000
			setName (name);
		} catch (Exception e) {
			// do nothing
		}

		count=size-ofs-4;

		data=new byte[count];
		f.readFully(data);
	}

	private KDefault () {}

	public int getSize () {
		return super.getSize() + data.length;
	}

	public KObject deepCopy () {
		KDefault kd = new KDefault();
		kd.copyInfo(this);
		kd.data=data;
		return kd;
	}

	public void write (RandomAccessFile f)
	{
		long l;
		l=writestart(f);
		try {f.write(data);} catch (IOException e) {
			throw new RuntimeException(Messages.getString("KDefault.Error_while_writing_Object")+getName()); //$NON-NLS-1$
		}

		writefinish(f,l);
	}

}
