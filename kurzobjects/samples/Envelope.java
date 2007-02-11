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

package kurzobjects.samples;
import java.io.IOException;
import java.io.RandomAccessFile;
import resources.Messages;

public class Envelope 
{
	public short data[]= new short[6];

	public Envelope () {
		data[0]=-1; data[1]=1;
		data[2]=data[3]=0;
		data[4]=-1600; data[5]=0;
	}

	public Envelope (RandomAccessFile f)
	throws IOException
	{
		try {
			for (int i=0;i<6;i++) data[i]=f.readShort();
		} catch (IOException e) {
			//oops
		}
	}
	
	public void write (RandomAccessFile f)
	{
		try {
			for (int i=0;i<6;i++) f.writeShort(data[i]);
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("Envelope.Error_while_writing_sample_envelope")); //$NON-NLS-1$
		}
	}

}

