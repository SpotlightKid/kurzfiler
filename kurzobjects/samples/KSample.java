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
import java.util.Iterator;
import java.util.Vector;

import kurzobjects.KObject;
import kurzobjects.NoteNr;
import resources.Messages;


public class KSample extends KObject {
	public short baseID;
	public short numHeaders; //-1
	public short HeadersOfs;
	public byte flags; //Stereo=1
	public byte ks1;
	public short copyID;
	public short ks2;

	protected Vector<Soundfilehead> headers;
	protected Vector<Envelope> envelopes;

	private class headerIterator implements Iterator {
		private int pos;
		public headerIterator () {
			pos=0;
		}
		public boolean hasNext() {
			return pos<=numHeaders;
		}
		public Object next() {
			short h=(short)(pos);
			pos++;
			if (isStereo()) pos++;
			return new Short(h);
		}
		public void remove() {}
	}

	protected byte extraData[];

	public KSample ()
	{
		envelopes=null;
		headers=null;
		extraData=null;
	
		baseID		= 0;
		numHeaders	= -1;
		HeadersOfs	= 0;
		flags		= 0;
		ks1			= 0;
		copyID		= 0;
		ks2			= 0;
	}

	public KObject deepCopy () {
		KSample ks = new KSample();
		ks.copyInfo(this);

		ks.envelopes=envelopes;
		ks.extraData=extraData;
		ks.headers=new Vector<Soundfilehead>();

		for (int i=0; i<=numHeaders; i++)
			ks.headers.add( new Soundfilehead( getheader(i) ) );

		ks.baseID		= baseID;
		ks.numHeaders	= numHeaders;
		ks.HeadersOfs	= HeadersOfs;
		ks.flags		= flags;
		ks.ks1			= ks1;
		ks.copyID		= copyID;
		ks.ks2			= ks2;

		return ks;
	}

	public KSample(int i, RandomAccessFile f)
	throws IOException
	{
		int size;
		int ofs;

		byte[] b;
		//int count;
		int n, datasize;

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
		
		baseID		= f.readShort();
		numHeaders	= f.readShort();
		HeadersOfs	= f.readShort();
		flags		= f.readByte();
		ks1			= f.readByte();
		copyID		= f.readShort();
		ks2			= f.readShort();
		
		headers=new Vector<Soundfilehead>();
		for (n=0;n<=numHeaders;n++) headers.addElement ((new Soundfilehead(f)));
		
		datasize=size-ofs-4-12-n*32;
		
		n=datasize / 12;

		envelopes=new Vector<Envelope>();
		for (;n>0;n--) envelopes.addElement ((new Envelope(f)));

		 if ((datasize % 12)>0) {
			datasize = datasize % 12;
			extraData=new byte[datasize];
			f.readFully (extraData);
		}

/*
		System.out.print("Sample "+k_name);
		System.out.println(" "+KHash.getType(i)+" "+KHash.getID(i));*/
	}

	public void write (RandomAccessFile f)
	{
		long l;
		int n;
		short envofs;

		l=writestart(f);
		HeadersOfs=8; //zur Sicherheit

		try {
			f.writeShort(baseID);
			f.writeShort(numHeaders);
			f.writeShort(HeadersOfs);
			f.writeByte(flags);
			f.writeByte(ks1);
			f.writeShort(copyID);
			f.writeShort(ks2);

			for (n=0,envofs=(short)(headers.size()*32-32); n<headers.size(); n++,envofs-=32) 
			{
				headers.elementAt(n).offsetToEnvelope=(short)(envofs+8);
				headers.elementAt(n).altOffsetToEnvelope=(short)(envofs+6);
				headers.elementAt(n).write(f);
			}
			for (n=0;n<envelopes.size();n++) envelopes.elementAt(n).write(f);
			
			if (extraData!=null) f.write(extraData);
		
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("KSample.Error_while_writing_Sample")+getName()); //$NON-NLS-1$
		}

		writefinish(f,l);
	}

	public Iterator getIterator () {
		return new headerIterator();
	}
	
	public Soundfilehead getheader (int n) {
		if (n<0) {
			throw new RuntimeException("GetHeader "+n); //$NON-NLS-1$
		} //$NON-NLS-1$
		if (n>numHeaders) {
			//System.out.println("GetHeader "+n);
			throw new RuntimeException("GetHeader "+n); //$NON-NLS-1$
		} //$NON-NLS-1$
		return headers.elementAt(n);
	}

	public int getSize () {
		int h;
		h=12;
		h+=32*headers.size();
		h+=6*envelopes.size();
		return h+super.getSize();
	}

	public int getRamSize() {
		int ram=0;
		for (int i=0;i<=numHeaders;i++) ram+=getheader(i).getRamSize();
		return ram;
	}

	public String getLongName () {
		String s = super.getLongName();
		int ram=0;
		if (isStereo()) {
			if (isMultiRoot()) s+= "  MultiRoot Stereo"; //$NON-NLS-1$
			else s+= "  " + NoteNr.getNote(getheader(0).rootkey) + " Stereo"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			if (isMultiRoot()) s+= "  MultiRoot Mono"; //$NON-NLS-1$
			else s+= "  " + NoteNr.getNote(getheader(0).rootkey) + " Mono"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		ram=getRamSize()/1024;  //in KBytes
		s+=" "+String.valueOf(ram)+"KB"; //$NON-NLS-1$ //$NON-NLS-2$
		return s;
	}

	public String getDescription () {
		String s;
		int ram=0;
		if (isStereo()) {
			if (isMultiRoot()) s= "MultiRoot Stereo"; //$NON-NLS-1$
			else s= NoteNr.getNote(getheader(0).rootkey) + " Stereo"; //$NON-NLS-1$
		}
		else {
			if (isMultiRoot()) s= "MultiRoot Mono"; //$NON-NLS-1$
			else s= NoteNr.getNote(getheader(0).rootkey) + " Mono"; //$NON-NLS-1$
		}
		ram=getRamSize()/1024;  //in KBytes
		s+=" "+String.valueOf(ram)+"KB"; //$NON-NLS-1$ //$NON-NLS-2$
		return s;
	}

	public String getRootKey () {
		return NoteNr.getNote(getheader(0).rootkey);
	}

	public void setRootKey (String root) {
		byte b;
		String h = root.trim().toUpperCase();
		if ((h.charAt(0)>='A')&&(h.charAt(0)<='G')) {
			b = NoteNr.getNumber(h.charAt(0));
			if (h.charAt(1)=='#') {
				b++;
				h=h.substring(2, h.length()); }
			else {
				h=h.substring(1, h.length());
			}
			b+=Byte.parseByte(h)*12+12;
		}
		else {
			b=Byte.parseByte(h);
		}
		if ((b>=0)&&(b<128)) {
			getheader(0).setRootKey(b);
			if (isStereo()) getheader(1).setRootKey(b);
		}
		else throw new RuntimeException();
	}

	public short insertHeader (Soundfilehead s) {
		if (headers==null) headers=new Vector<Soundfilehead>();
		headers.addElement(s);
		numHeaders++;
		return (short)(numHeaders+1);
	}

	public boolean isStereo() {
		return (flags==1);
	}

	public boolean isMultiRoot() {
		if (isStereo()) return (numHeaders>1);
		else return (numHeaders>0);
	}

	public void generateEnvelopes () {
		if (envelopes==null) envelopes=new Vector<Envelope>();
		envelopes.addElement(new Envelope());
		envelopes.addElement(new Envelope());
	}

	public void setLoopStartToSampleStart() {
		Iterator<Soundfilehead> iter=headers.iterator();
		Soundfilehead sfh;
		byte[] data;

		while (iter.hasNext()) {
			sfh=iter.next();
			sfh.sampleStart=sfh.sampleLoopStart;
			sfh.altSampleStart=sfh.sampleLoopStart;
			data = new byte[(sfh.sampleEnd-sfh.sampleStart+1)*2];
			System.arraycopy(sfh.sampledata, sfh.sampleStart*2, data, 0, (sfh.sampleEnd-sfh.sampleStart+1)*2);
			sfh.sampledata=data;
		}
	}

	public void setAltStartToSampleEnd() {
		Iterator<Soundfilehead> iter=headers.iterator();
		Soundfilehead sfh;

		while (iter.hasNext()) {
			sfh=iter.next();
			sfh.altSampleStart=sfh.sampleEnd;
		}
	}
}
