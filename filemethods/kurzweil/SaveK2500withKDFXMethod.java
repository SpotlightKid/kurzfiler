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

import resources.Messages;

public class SaveK2500withKDFXMethod extends K2x00FileMethod {
	public javax.swing.filechooser.FileFilter getFileFilter() {
		return new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String name = f.getName();
				if (name.toUpperCase().endsWith(".K25") //$NON-NLS-1$
					|| name.toUpperCase().endsWith(".K26")) //$NON-NLS-1$
					return true;

				return false;
			}
			public String getDescription() {
				return Messages.getString("SaveK2500withKDFXMethod.Kurzweil_K2500_or_K2600_File_(.k25;.k26)"); //$NON-NLS-1$
			}
		};
	}
	public String getFileTypeDescription() {
		return Messages.getString("SaveK2500withKDFXMethod.Name"); //$NON-NLS-1$
	}
	public String defExtension() {
		return ".k25"; //$NON-NLS-1$
	}
}
