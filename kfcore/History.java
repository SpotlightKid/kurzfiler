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

package kfcore;
import java.util.Iterator;
import java.util.Vector;

import kurzobjects.KHashtable;
import kurzobjects.KObject;

public class History {
	public static final int ADD = 1;
	public static final int REMOVE = 2;
	
	private static final int ACTION_STARTS = 1;
	private static final int ACTION_ENDS = 2;
	private static final int AT_SAVEPOINT = 4;

	private Vector<Element> undoList = new Vector<Element>();
	private Vector<Element> redoList = new Vector<Element>();
	private boolean isAddingActions;

	private Vector<StateListener> undoredoListener = new Vector<StateListener>();
	
	private Element lastSavePoint;

	private class Element {
		int action;
		KObject ko;
		int position;
		public Element(int a, KObject o) {
			action = a;
			ko = o;
			position = 0; //ACTION_STARTS|ACTION_ENDS;
		}
		public Element(int a, KObject o, int p) {
			action = a;
			ko = o;
			position = p;
		}
		boolean isFirst() {
			return (position & ACTION_STARTS) > 0;
		}
		void setFirst() {
			position |= ACTION_STARTS;
		}

		boolean isLast() {
			return (position & ACTION_ENDS) > 0;
		}
		void setLast() {
			position |= ACTION_ENDS;
		}

		boolean isSavePoint() {
			//System.out.println("isSavePoint=" + position);
			return (position & AT_SAVEPOINT) > 0;
		}
		void setSavePoint(boolean flag) {
			if (flag) position |= AT_SAVEPOINT;
			else position &= ~AT_SAVEPOINT;
		}
	}

	public History() {
		isAddingActions = false;
		lastSavePoint = null;
	}

	//This still doesn't work properly!
	public void setSafePoint() {
		if (isUndoPossible()) {
			Element e = undoList.lastElement();
			e.setSavePoint(true);
			if (lastSavePoint != null) {
				//Element l = (Element) undoList.get(undoList.indexOf(lastSavePoint));
				lastSavePoint.setSavePoint(false);
			}
			lastSavePoint = e;
		}
	}
	public boolean isAtSafePoint() {
		if (isUndoPossible()) {
			Element e = undoList.lastElement();
			return e.isSavePoint();
		}
		return true;
	}


	public void addStateListener(StateListener s) {
		undoredoListener.add(s);
		s.update();
	}

	public void removeStateListener(StateListener s) {
		undoredoListener.remove(s);
	}

	private void fireUpdate() {
		Iterator<StateListener> iter = undoredoListener.iterator();
		while (iter.hasNext())
			 iter.next().update();
	}

	public void clear() {
		undoList.clear();
		redoList.clear();
		fireUpdate();
	}

	public void startAddingActions() {
		if (isUndoPossible()) {
			Element e = undoList.lastElement();
			e.setLast();
		}
		isAddingActions = true;
	}

	public void endAddingActions() {
		if (isUndoPossible()) {
			Element e = undoList.lastElement();
			e.setLast();
		}
		isAddingActions = false;
	}

	public void doAction(int a, KObject o) {
		Element e = new Element(a, o);
		if (isAddingActions) {
			e.setFirst();
			isAddingActions = false;
		}
		undoList.add(e);
		redoList.clear();
		fireUpdate();
		//		System.out.println(o.nameOf());
	}

	public boolean isUndoPossible() {
		return undoList.size() > 0;
	}

	public void undoAction(KHashtable kh) {
		if (isUndoPossible()) {
			Element redo;
			Element e;
			boolean stop = false;
			do {
				e = undoList.lastElement();
				stop = e.isFirst();
				switch (e.action) {
					case ADD :
						redo = new Element(REMOVE, e.ko, e.position);
						redoList.add(redo);
						kh.remove(new Integer(e.ko.getHash()));
						undoList.remove(e);
						break;
					case REMOVE :
						redo = new Element(ADD, e.ko, e.position);
						redoList.add(redo);
						kh.put(new Integer(e.ko.getHash()), e.ko);
						undoList.remove(e);
						break;
				}
			} while ((!stop) && isUndoPossible());
			fireUpdate();
		}
	}

	public boolean isRedoPossible() {
		return redoList.size() > 0;
	}

	public void redoAction(KHashtable kh) {
		if (isRedoPossible()) {
			Element undo;
			Element e;
			boolean stop = false;
			do {
				e = redoList.lastElement();
				stop = e.isLast();
				switch (e.action) {
					case ADD :
						undo = new Element(REMOVE, e.ko, e.position);
						undoList.add(undo);
						kh.remove(new Integer(e.ko.getHash()));
						redoList.remove(e);
						break;
					case REMOVE :
						undo = new Element(ADD, e.ko, e.position);
						undoList.add(undo);
						kh.put(new Integer(e.ko.getHash()), e.ko);
						redoList.remove(e);
						break;
				}
			} while ((!stop) && (isRedoPossible()));
			fireUpdate();
		}
	}

}
