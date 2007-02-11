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
import java.util.Hashtable;
import java.util.TreeSet;
import resources.Messages;

public class KObject {
	private int k_hash;
	private String k_name;
	
	public KObject() 
	{
	}

	public KObject(KObject theother) 
	{
		k_hash=theother.k_hash;
		k_name=new String(theother.k_name);
	}
	
	public KObject(int i, RandomAccessFile f)
	throws IOException
	{
		//int size;
		int ofs;
		byte[] b;

		k_hash=i;
		f.readUnsignedShort(); //size=
		ofs=f.readUnsignedShort();

		b= new byte[ofs-2];
		f.readFully(b);
		
		k_name=new String();
		for (int j=0;b[j]!=0; j++) {
			k_name+=(char)b[j];
		}
	}

	public KObject deepCopy () {
		KObject ko = new KObject();
		ko.copyInfo(this);
		return ko;
	}

	protected void copyInfo (KObject theother) {
		k_hash=theother.k_hash;
		k_name=new String(theother.k_name);
	}
	
	public void setHash (int h) {
		k_hash=h;
	}

	public int getHash () {
		return k_hash;
	}

	public String getName () {
		return k_name;
	}

	public void setName (String name) {
		if (name.length()>16) {
			k_name=name.substring(0,16);
			throw new RuntimeException();
		}
		else
			k_name=name;
	}

	protected void updateLink (Hashtable<Integer, Integer> newIDsTbl) {
//		System.out.println("updateLink "+k_name);
	}

	public TreeSet<Integer> getDependants () {
		return new TreeSet<Integer>();
	}

	public boolean hasDependants () {
		return false;
	}

	public boolean isUsing (int hash) {
		if (getDependants().contains(new Integer(hash))) return true;
		return false;
	}

	public int getSize () {
		int s;
		s=k_name.length();
		s+= ((s&1)==1) ? 1 : 2; //name mit null beenden, padden
		s+= 4; //ID, Größe

		return s;
	}

	public String getLongName () {
		String s;
		s=String.valueOf(KHash.getID(k_hash)) + " " + 
			KHash.getName(k_hash) + " " + k_name;
		return s;
	}

	public String getDescription () {
		return "";
	}

	public void write (RandomAccessFile f)
	{
		//Should never reach here!
		//System.out.println("write "+k_name);
		throw new RuntimeException(Messages.getString("write "+k_name)); 
	}

	protected long writestart (RandomAccessFile f)
	{
		short ofs=0;
		int n;
		long pos;
		try {
			f.writeShort(k_hash);
			pos=f.getFilePointer();

			f.writeShort(ofs); //dummy für die Länge des Objects

			n=k_name.length();
			if ((n&1)==0) {
				ofs=(short)(n+4);
				f.writeShort(ofs);
				f.writeBytes(k_name);
				f.writeShort(0); //Pad Byte
			}
			else {
				ofs=(short)(n+3);
				f.writeShort(ofs);
				f.writeBytes(k_name);
				f.writeByte(0); //null-terminiert
			}
		} 
		catch (IOException e) {
			return 0;
		}

		return pos; //Position für die Länge
	}

	protected void writefinish (RandomAccessFile f, long l)
	{
		short size=0;
		long pos;
		try {
			pos=f.getFilePointer();
			while ((pos&1)>0) {
				f.writeByte(0); //2er Grenzen
				pos=f.getFilePointer();
			}
			size=(short)(pos-l+2);

			f.seek(l);
			f.writeShort(size);
			
			f.seek(pos);
		} 
		catch (IOException e) {
			//oops
		}

	}
}