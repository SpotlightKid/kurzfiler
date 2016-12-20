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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import kurzobjects.keymaps.KKeymap;
import resources.Messages;

public class KProgram extends KObject {
	public Vector<Segment> segmente = new Vector<Segment>();

	public class Segment {
		byte tag;
		byte[] data;
		public static final short PGMSEGTAG 	= 8;
		public static final short LYRSEGTAG 	= 9;
		public static final short FXSEGTAG  	= 15;
		public static final short ASRSEGTAG 	= 16;
		public static final short LFOSEGTAG 	= 20;
		public static final short FUNSEGTAG 	= 24;
		public static final short ENCSEGTAG 	= 32;
		public static final short ENVSEGTAG 	= 33;
		public static final short IMPSEGTAG		= 39;
		public static final short CALSEGTAG 	= 64;
		public static final short HOBSEGTAG 	= 80;
		public static final short KDFXSEGTAG	= 104;
		public static final short KB3SEGTAG		= 120;

		int len (short tag) {
			if (tag==PGMSEGTAG) return 15;
			if (tag==LYRSEGTAG) return 15;
			if (tag==FXSEGTAG) return 7;

			switch (tag&0xf8) {
			case FUNSEGTAG:
				return 3;
			case ASRSEGTAG:
			case LFOSEGTAG:
			case KDFXSEGTAG:
				return 7;
			case ENCSEGTAG:
			case HOBSEGTAG:
				return 15;
			case CALSEGTAG:
			case KB3SEGTAG:
				return 31;
			}
			return 0;
		}

		Segment(byte tag) {
			this.tag=tag;
			data=new byte[len(tag)];
		}

		Segment (Segment s) {
			tag=s.tag;
			data=new byte[len(tag)];
			for (int i=0;i<len(tag);i++)
				data[i]=s.data[i];
		}

		Segment(RandomAccessFile f) 
		throws IOException {
			tag=f.readByte();
			data=new byte[len(tag)];
			if (data==null) throw new IOException("wrong Segment length");
			f.readFully(data);
		}

		public void write (RandomAccessFile f) {
			try {
				f.writeByte(tag);
				f.write(data);
			} catch (IOException e) {
				throw new RuntimeException(Messages.getString("KProgram.Error_while_writing_Program_segment")); //$NON-NLS-1$
			}
		}
	}

	public KProgram(int i, RandomAccessFile f)
	throws IOException
	{
		int size;
		int ofs;

		byte[] b;

		long pos;

		setHash(i);
		size=f.readUnsignedShort();
		ofs=f.readUnsignedShort();

		b= new byte[ofs-2];
		f.readFully(b);
		
		String name = new String();
		for (int j = 0; j<ofs-2 && b[j]!=0; j++) {
			name += (char) b[j];
		}
		try {
			// some .k26 files contain names that are too long for a K2000
			setName (name);
		} catch (Exception e) {
			// do nothing
		}
		

		pos=f.getFilePointer();

		Segment seg;
		while (f.getFilePointer() - pos < (size-ofs-4)) { 
			seg=new Segment(f);
			if (seg.tag!=0) segmente.addElement(seg);
		}
		//wenn Datei kaputt, dann Exception vom Konstruktor
	}

	public KProgram () {
	}

	public KProgram (KProgram kp) {
		Iterator<Segment> iter=kp.segmente.iterator();
		while (iter.hasNext()) 
			segmente.add( new Segment( iter.next() ) );
	}

	public int getSize () {
		int s=super.getSize();

		Iterator<Segment> iter=segmente.iterator();
		while (iter.hasNext()) 
			s+=iter.next().data.length + 1;
		s+=2; //Leerwort am Ende
		return s;
	}

	public KObject deepCopy () {
		KProgram kp = new KProgram(this);
		kp.copyInfo(this);
		return kp;
	}

	public void write (RandomAccessFile f)
	{
		long l;
		l=writestart(f);

		for (Enumeration<Segment> el=segmente.elements(); el.hasMoreElements(); ) 
			el.nextElement().write(f);
	
		//noch ein abschließendes Leerwort!!
		try {
			f.writeShort(0);
		} catch (Exception e) {
			throw new RuntimeException(Messages.getString("KProgram.Error_while_writing_Program")+getName()); //$NON-NLS-1$
		}

		writefinish(f,l);
	}

	public int mode ()
	{
		Segment s;

		for (Enumeration<Segment> el=segmente.elements(); el.hasMoreElements(); ) {
			s=el.nextElement();
			if (s.tag==Segment.PGMSEGTAG) {
				return s.data[0];
			}
		}
		return 0;
	}

	public int numLayers ()
	{
		Segment s;

		for (Enumeration<Segment> el=segmente.elements(); el.hasMoreElements(); ) {
			s=el.nextElement();
			if (s.tag==Segment.PGMSEGTAG) {
				return s.data[1];
			}
		}
		return 0;
	}

	public void makePGMblock () {
		Segment s = new Segment((byte)Segment.PGMSEGTAG);
		s.data[0]=2;
		s.data[1]=0; //numLayers
		s.data[3]=0x37; //Bendrange
		s.data[4]=64; //Portamento
		segmente.add(s);
	}

	public void addLayer (KKeymap kk, boolean stereo) {
		Segment s;
		if (stereo) {
			s = new Segment((byte)Segment.LYRSEGTAG);
			s.data[0]=0;
			s.data[1]=(byte)0x18; 
			s.data[2]=0; 
			s.data[3]=0; //untere Grenze
			s.data[4]=(byte)0x7f; //obere Grenze
			s.data[5]=0;
			s.data[6]=(byte)0x7f; 
			s.data[7]=0; 
			s.data[8]=(byte)0x24; //Enable: Norm. Stereo = 32!!!
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.ENCSEGTAG);
			s.data[0]=0;
			s.data[1]=0; //nicht mehr Natural
			s.data[2]=0; 
			s.data[3]=0; 
			s.data[4]=0; 
			s.data[5]=0;
			s.data[6]=0; 
			s.data[7]=0; 
			s.data[8]=0;
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.ENVSEGTAG); //AmpEnv
			s.data[0]=0;
			s.data[1]=100; 
			s.data[2]=0; 
			s.data[3]=0; 
			s.data[4]=0; 
			s.data[5]=0;
			s.data[6]=0; 
			s.data[7]=100; 
			s.data[8]=0;
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.CALSEGTAG);
			s.data[0]=(byte)0x7f;
			s.data[1]=0; //keymap transpose
			s.data[3]=(byte)0x2b; 
			//s.data[10]=(byte)0x40;
			s.data[29]=1; 
			short keymap=KHash.getID( KHash.getID(kk.getHash()) );
			s.data[7]=(byte)((keymap>>>8)&0xff);
			s.data[8]=(byte)(keymap&0xff);
			s.data[11]=(byte)((keymap>>>8)&0xff);
			s.data[12]=(byte)(keymap&0xff);
			segmente.add(s);

			s = new Segment((byte)0x50);
			s.data[0]=62;
			segmente.add(s);

			s = new Segment((byte)0x51);
			s.data[0]=60;
			segmente.add(s);

			s = new Segment((byte)0x52);
			s.data[0]=60;
			segmente.add(s);

			s = new Segment((byte)0x53);
			s.data[0]=1;
			s.data[2]=(byte)0x70;
			s.data[13]=4;
			s.data[14]=(byte)0x90; //Panning Fixed
			segmente.add(s);
		}
		else {
			s = new Segment((byte)Segment.LYRSEGTAG);
			s.data[0]=0;
			s.data[1]=(byte)0x18; 
			s.data[2]=0; 
			s.data[3]=0; //untere Grenze
			s.data[4]=(byte)0x7f; //obere Grenze
			s.data[5]=0;
			s.data[6]=(byte)0x7f; 
			s.data[7]=0; 
			s.data[8]=(byte)0x4; //Enable: Norm. Stereo = 32!!!
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.ENCSEGTAG);
			s.data[0]=0;
			s.data[1]=0; //nicht mehr Natural
			s.data[2]=0; 
			s.data[3]=0; 
			s.data[4]=0; 
			s.data[5]=0;
			s.data[6]=0; 
			s.data[7]=0; 
			s.data[8]=0;
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.ENVSEGTAG); //AmpEnv
			s.data[0]=0;
			s.data[1]=100; 
			s.data[2]=0; 
			s.data[3]=0; 
			s.data[4]=0; 
			s.data[5]=0;
			s.data[6]=0; 
			s.data[7]=100; 
			s.data[8]=0;
			s.data[9]=0; 
			s.data[10]=0;
			s.data[11]=0; 
			s.data[12]=0; 
			s.data[13]=0; 
			s.data[14]=0; 
			segmente.add(s);

			s = new Segment((byte)Segment.CALSEGTAG);
			s.data[0]=(byte)0x7f;
			s.data[1]=0; //keymap transpose
			s.data[3]=(byte)0x2b; 
			//s.data[10]=(byte)0x40;
			s.data[29]=1; 
			short keymap=KHash.getID( KHash.getID(kk.getHash()) );
			s.data[7]=(byte)((keymap>>>8)&0xff);
			s.data[8]=(byte)(keymap&0xff);
			s.data[11]=(byte)((keymap>>>8)&0xff);
			s.data[12]=(byte)(keymap&0xff);
			segmente.add(s);

			s = new Segment((byte)0x50);
			s.data[0]=62;
			segmente.add(s);

			s = new Segment((byte)0x51);
			s.data[0]=60;
			segmente.add(s);

			s = new Segment((byte)0x52);
			s.data[0]=60;
			segmente.add(s);

			s = new Segment((byte)0x53);
			s.data[0]=1;
			s.data[2]=(byte)0x70;
			s.data[13]=4; //6dB??
			s.data[14]=(byte)0x00; //Panning Fixed
			segmente.add(s);
		}
		for (Enumeration<Segment> el=segmente.elements(); el.hasMoreElements(); ) {
			s=el.nextElement();
			if (s.tag==Segment.PGMSEGTAG) {
				s.data[1]++; //numLayers
				break;
			}
		}
	}
	
	public String getLongName () {
		String s = super.getLongName();
		s+= "  " + String.valueOf(numLayers()) + " Layers "; //$NON-NLS-1$ //$NON-NLS-2$
		s+= " Mode " + String.valueOf(mode()); //$NON-NLS-1$
		return s;
	}

	public String getDescription () {
		String s = String.valueOf(numLayers()) + " Layers "; //$NON-NLS-1$
		switch (mode()) {
		case 3: s+=Messages.getString("KProgram.requires_K2500"); break; //$NON-NLS-1$
		case 4: s+=Messages.getString("KProgram.requires_KDFX"); break; //$NON-NLS-1$
		}
		return s;
	}

	public void strip ()
	{
		Segment s;

		Iterator<Segment> it = segmente.iterator();
		while (it.hasNext()) {
			s=it.next();
			switch (s.tag) {
			case Segment.PGMSEGTAG: 
				s.data[0]=2;
				break;
			case Segment.IMPSEGTAG: 
				it.remove();
				break;
			default:
				if ((s.tag&96)==96) it.remove();
			}
		}
	}
	
	public void stripkdfx()
	{
		Segment s;

		Iterator<Segment> it = segmente.iterator();
		while (it.hasNext()) {
			s=it.next();
			switch (s.tag) {
			case Segment.PGMSEGTAG: 
				s.data[0]=3;
				break;
			case Segment.IMPSEGTAG: 
				//it.remove(); 
				//Impact drinlassen
				break;
			default:
				if ((s.tag&96)==96) it.remove();
			}
		}
	}

	protected void updateLink (Hashtable<Integer, Integer> newIDsTbl) {
		Segment s;
		int keymap;
		Integer alt, neu;

		Iterator<Segment> it = segmente.iterator();

		while (it.hasNext()) {
			s=it.next();
			if  (s.tag==Segment.CALSEGTAG) {
				keymap=(s.data[7]<<8)+s.data[8];
				if (s.data[8]<0) keymap+=256; //bytes sind signed
				alt = new Integer(KHash.generate (keymap, KHash.T_KEYMAP));
				neu=newIDsTbl.get(alt);
				if (neu!=null) {
					keymap=KHash.getID(neu);
					s.data[7]=(byte)((keymap>>>8)&0xff);
					s.data[8]=(byte)(keymap&0xff);
				}

				keymap=(s.data[11]<<8)+s.data[12];
				if (s.data[12]<0) keymap+=256; //bytes sind signed
				alt = new Integer(KHash.generate (keymap, KHash.T_KEYMAP));
				neu=newIDsTbl.get(alt);
				if (neu!=null) {
					keymap=KHash.getID(neu);
					s.data[11]=(byte)((keymap>>>8)&0xff);
					s.data[12]=(byte)(keymap&0xff);
				}
			}
		}
	}

	public boolean hasDependants () {
		return true;
	}

	public TreeSet<Integer> getDependants () {
		TreeSet<Integer> dependants = new TreeSet<Integer> ();
		Segment s;
		int keymap;

		Iterator<Segment> it = segmente.iterator();

		while (it.hasNext()) {
			s=it.next();
			if  (s.tag==Segment.CALSEGTAG) {
				keymap=(s.data[7]<<8)+s.data[8];
				if (s.data[8]<0) keymap+=256; //bytes sind signed
				if (keymap!=0) {
					dependants.add(new Integer(KHash.generate ((short)keymap, KHash.T_KEYMAP)));
					
				}
				keymap=(s.data[11]<<8)+s.data[12];
				if (s.data[12]<0) keymap+=256; //bytes sind signed
				if (keymap!=0) {
					dependants.add(new Integer(KHash.generate ((short)keymap, KHash.T_KEYMAP)));
				}
			}
		}
		return dependants;
	}
}
