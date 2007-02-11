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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

public class KHashtable extends Hashtable<Integer, KObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 192470039645717945L;


	public KHashtable () {
	}
	
	public TreeSet<Integer> getIDs () {
		TreeSet<Integer> ks = new TreeSet<Integer>();
		for (Enumeration<Integer> e=this.keys(); e.hasMoreElements(); ) 
			ks.add(e.nextElement());
		return ks;
	}

	public TreeSet<Integer> getSampleIDs () {
		TreeSet<Integer> ks = (TreeSet<Integer>) getIDs().subSet (new Integer(KHash.MIN_SAMPLE), new Integer(KHash.MAX_SAMPLE));
		return ks;
	}

	public TreeSet<Integer> getKeymapIDs () {
		TreeSet<Integer> ks = (TreeSet<Integer>) getIDs().subSet (new Integer(KHash.MIN_KEYMAP), new Integer(KHash.MAX_KEYMAP));
		return ks;
	}

	public TreeSet<Integer> getProgramIDs () {
		TreeSet<Integer> ks = (TreeSet<Integer>) getIDs().subSet (new Integer(KHash.MIN_PROGRAM), new Integer(KHash.MAX_PROGRAM));
		return ks;
	}

	public Iterator<Integer> getIterator () {
		return getIDs().iterator();
	}

	public Iterator<Integer> getSampleIterator () {
		return getSampleIDs().iterator();
	}
	
	public Iterator<Integer> getKeymapIterator () {
		return getKeymapIDs().iterator();
	}

	public Iterator<Integer> getProgramIterator () {
		return getProgramIDs().iterator();
	}

	public KObject getKObject (Integer o) {
		return this.get(o);
	}

	public TreeSet<Integer> getDependants (Integer o) {
		return getKObject(o).getDependants();
	}


	public void updateLinks (Hashtable<Integer,Integer> newIDsTbl) {
		Iterator<Integer> IDsIter=getIterator();
		while (IDsIter.hasNext()) 
			getKObject(IDsIter.next()).updateLink(newIDsTbl);
	}

}
