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

package filemethods;
import java.io.IOException;
import java.io.RandomAccessFile;

import resources.Messages;

public abstract class FileMethod implements FileMethodInterface {
	protected RandomAccessFile theFile;

	protected boolean open(String openname) throws IOException {
		theFile = new RandomAccessFile(openname, "rw"); //$NON-NLS-1$
		return true;
	}

	protected boolean close() {
		if (theFile != null) {
			try {
				theFile.close();
			} catch (IOException e) {
				//oops
				throw new RuntimeException(Messages.getString("FileMethod.CouldNotCloseErrMsg")); //$NON-NLS-1$
			}
		}
		theFile = null;
		return true;
	}

	public String processFileName(String name) {
		if (!matchesExtensions(name))
			return name + defExtension();
		return name;
	}

	public boolean onlyAsACopy() {
		return false;
	}
}
