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
import java.io.File;
import java.util.Iterator;

import kurzobjects.KHashtable;
import kurzobjects.KProgram;
import resources.Messages;

public class SaveK2500noKDFXMethod extends K2x00FileMethod {
	public boolean Save(KHashtable kobjlist, String name) {
		KProgram kp;
		Iterator<Integer> iter = kobjlist.getProgramIterator();
		while (iter.hasNext()) {
			kp = ((KProgram) kobjlist.getKObject(iter.next()));
			if (kp.mode() > 3) {
				kp.stripkdfx();
			}
		}
		return super.Save(kobjlist, name);
	}

	public javax.swing.filechooser.FileFilter getFileFilter() {
		return new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String name = f.getName();
				if (name.toUpperCase().endsWith(".K25")) //$NON-NLS-1$
					return true;

				return false;
			}
			public String getDescription() {
				return Messages.getString("SaveK2500noKDFXMethod.Kurzweil_K2500_File_(.k25)"); //$NON-NLS-1$
			}
		};
	}
	public String getFileTypeDescription() {
		return Messages.getString("SaveK2500noKDFXMethod.Name"); //$NON-NLS-1$
	}
	public String defExtension() {
		return ".k25"; //$NON-NLS-1$
	}
}
