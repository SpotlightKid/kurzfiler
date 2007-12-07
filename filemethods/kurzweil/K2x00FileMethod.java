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
import java.io.IOException;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

import kurzobjects.KDefault;
import kurzobjects.KHash;
import kurzobjects.KHashtable;
import kurzobjects.KObject;
import kurzobjects.KProgram;
import kurzobjects.keymaps.KKeymap;
import kurzobjects.samples.KSample;
import filemethods.FileMethod;
import resources.Messages;

public class K2x00FileMethod extends FileMethod {
	protected KHeader header = new KHeader();
	protected KHashtable kobjlist;

	public FileFilter getFileFilter() {
		return new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String name = f.getName();
				if (name.endsWith(".K26") //$NON-NLS-1$
					|| name.endsWith(".K25") //$NON-NLS-1$
					|| name.endsWith(".KRZ") //$NON-NLS-1$
					|| name.endsWith(".krz") //$NON-NLS-1$
					|| name.endsWith(".k25") //$NON-NLS-1$
					|| name.endsWith(".k26")) { //$NON-NLS-1$
					return true;
				}
				return false;
			}
			public String getDescription() {
				return Messages.getString("K2x00FileMethod.Kurzweil_File_(.krz;.k25;.k26)"); //$NON-NLS-1$
			}
		};
	}

	public String getFileTypeDescription() {
		return Messages.getString("K2x00FileMethod.Kurzweil_File"); //$NON-NLS-1$
	}

	public String defExtension() {
		return ".krz"; //$NON-NLS-1$
	}

	public boolean matchesExtensions(String name) {
		if (name.endsWith(".K26") //$NON-NLS-1$
			|| name.endsWith(".K25") //$NON-NLS-1$
			|| name.endsWith(".KRZ") //$NON-NLS-1$
			|| name.endsWith(".krz") //$NON-NLS-1$
			|| name.endsWith(".k25") //$NON-NLS-1$
			|| name.endsWith(".k26")) //$NON-NLS-1$
			return true;
		return false;
	}

	public String processFileName(String name) {
		if (!matchesExtensions(name))
			return name + defExtension();
		return name;
	}

	public boolean Load(KHashtable kobjlist, String name) {
		this.kobjlist = kobjlist;

		try {
			open(name);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Could_not_open_the_file")); //$NON-NLS-1$
		}

		if (!readheader()) {
			close();
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Not_a_K2x00_file")); //$NON-NLS-1$
		}

		if (!readobjects()) {
			close();
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Could_not_read_the_objects")); //$NON-NLS-1$
		}

		if (!readsampledata()) {
			close();
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Could_not_read_the_sampledata")); //$NON-NLS-1$
		}

		if (!close())
			return false;
		return true;
	}

	private boolean readheader() {
		return header.read(theFile);
	}

	private boolean readobjects() {
		KObject ko;
		int Blocksize;
		int curHash;
		long pos;

		if (theFile != null) {
			try {
				Blocksize = theFile.readInt();
				pos = theFile.getFilePointer();
			} catch (IOException e) {
				return false;
			}

			while (Blocksize < 0) {

				try {
					curHash = theFile.readUnsignedShort();

					switch (KHash.getType(curHash)) {
						case KHash.T_SAMPLE :
							ko = new KSample(curHash, theFile);
							break;
						case KHash.T_KEYMAP :
							ko = new KKeymap(curHash, theFile);
							break;
						case KHash.T_PROGRAM :
							ko = new KProgram(curHash, theFile);
							break;
						default :
							ko = new KDefault(curHash, theFile);
							break;
					}

					kobjlist.put(new Integer(curHash), ko);

					theFile.seek(pos - Blocksize - 4);
					Blocksize = theFile.readInt();
					pos = theFile.getFilePointer();
				} catch (IOException e) {
					System.err.println(e.getMessage());
					return false;
				}

			}
			return true;
		}
		return false;
	}

	private boolean readsampledata() {
		if (theFile == null)
			return false;
		KSample ks;
		int i;
		boolean success = true;
		Iterator<Integer> SampleIter = kobjlist.getSampleIterator();
		while (SampleIter.hasNext()) {
			ks = (KSample) kobjlist.getKObject(SampleIter.next());
			for (i = 0; i <= ks.numHeaders; i++) {
				if (!ks.getheader(i).readsampledata(theFile, header.osize))
					success = false;
			}
		}
		return success;
	}

	public boolean Save(KHashtable kobjlist, String name) {
		this.kobjlist = kobjlist;

		try {
			open(name);
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Could_not_open_the_file_for_writing")); //$NON-NLS-1$
		}

		if (!writeheader()) {
			close();
			throw new RuntimeException(Messages.getString("K2x00FileMethod.Could_not_write_the_header")); //$NON-NLS-1$
		}

		try {
			prewrite();
			writeobjects();
			writesampledata();
		} catch (RuntimeException e) {
			throw e;
		} finally {
			if (!close())
				return false;
		}
		return true;
	}

	private boolean writeheader() {
		return header.write(theFile);
	}

	private boolean writeobjects() {
		long pos, l;
		int Blocksize;

		if (kobjlist == null)
			return false;
		if (kobjlist.isEmpty()) {
			try {
				theFile.writeInt(0);

				pos = theFile.getFilePointer();

				Blocksize = (int) pos;
				theFile.seek(4);
				theFile.writeInt(Blocksize);

				theFile.seek(pos); //ans Ende
			} catch (IOException e) {
				return false;
			}
			return true;
		}
		try {
			Iterator<Integer> keyiter = kobjlist.getIterator();
			while (keyiter.hasNext()) {

				pos = theFile.getFilePointer();
				Blocksize = 0;

				theFile.writeInt(Blocksize);

				//object schreiben
				kobjlist.getKObject(keyiter.next()).write(theFile);

				l = theFile.getFilePointer();
				while ((l & 3) > 0) {
					theFile.writeByte(0); //4er Grenzen
					l = theFile.getFilePointer();
				}

				Blocksize = (int) (pos - l);
				theFile.seek(pos);
				theFile.writeInt(Blocksize);
				theFile.seek(l);
			}

			theFile.writeInt(0);

			pos = theFile.getFilePointer();

			Blocksize = (int) pos;
			theFile.seek(4);
			theFile.writeInt(Blocksize);

			theFile.seek(pos); //ans Ende

		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void prewrite() {
		KSample ks;
		int i;
		int offset = 0;

		Iterator<Integer> SampleIter = kobjlist.getSampleIterator();
		while (SampleIter.hasNext()) {
			ks = (KSample) kobjlist.getKObject(SampleIter.next());
			for (i = 0; i <= ks.numHeaders; i++)
				offset = ks.getheader(i).prewrite(offset);
		}
	}

	private boolean writesampledata() {
		if (theFile == null)
			return false;
		KSample ks;
		int i;

		Iterator<Integer> SampleIter = kobjlist.getSampleIterator();
		while (SampleIter.hasNext()) {
			ks = (KSample) kobjlist.getKObject(SampleIter.next());
			for (i = 0; i <= ks.numHeaders; i++)
				ks.getheader(i).writesampledata(theFile);
		}

		return true;
	}
}
/*
class LoadK2x00Method extends K2x00FileMethod {
    boolean open (String openname)
	throws IOException 		{
	f=new RandomAccessFile(openname,"r");
	return true;
    }
}	

class SaveK2000Method extends K2x00FileMethod {
    public boolean Save (KHashtable kobjlist, String name){
	KProgram kp;
	Iterator iter = kobjlist.getProgramIterator();
	while (iter.hasNext()) {
	    kp=((KProgram) kobjlist.getKObject(iter.next()) );
	    if (kp.mode()>2) {
		kp.strip();
	    }
	}
	return super.Save(kobjlist, name);
    }

    public FileFilter getFileFilter () {
	return new javax.swing.filechooser.FileFilter () {
		public boolean accept(File f) {
		    if (f.isDirectory()) {
			return true;
		    }
		    String name = f.getName();
		    if (name.toUpperCase().endsWith(".KRZ")) return true;
					
		    return false;
		}
		public String getDescription() {
		    return "Kurzweil K2000 File (.krz)";
		}
	    };
    }
	
    public String getName () {
	return "K2000";
    }
}

class SaveK2500noKDFXMethod extends K2x00FileMethod {
    public boolean Save (KHashtable kobjlist, String name){
	KProgram kp;
	Iterator iter = kobjlist.getProgramIterator();
	while (iter.hasNext()) {
	    kp=((KProgram) kobjlist.getKObject(iter.next()) );
	    if (kp.mode()>3) {
		kp.stripkdfx();
	    }
	}
	return super.Save(kobjlist, name);
    }

    public FileFilter getFileFilter () {
	return new javax.swing.filechooser.FileFilter () {
		public boolean accept(File f) {
		    if (f.isDirectory()) {
			return true;
		    }
		    String name = f.getName();
		    if (name.toUpperCase().endsWith(".K25")) return true;
					
		    return false;
		}
		public String getDescription() {
		    return "Kurzweil K2500 File (.k25)";
		}
	    };
    }
    public String getName () {
	return "K2500 without KDFX";
    }
    public String defExtension () {
	return ".k25";
    }
}

class SaveK2500withKDFXMethod extends K2x00FileMethod {
    public FileFilter getFileFilter () {
	return new javax.swing.filechooser.FileFilter () {
		public boolean accept(File f) {
		    if (f.isDirectory()) {
			return true;
		    }
		    String name = f.getName();
		    if (name.toUpperCase().endsWith(".K25") || 
			name.toUpperCase().endsWith(".K26")) return true;
					
		    return false;
		}
		public String getDescription() {
		    return "Kurzweil K2500 or K2600 File (.k25;.k26)";
		}
	    };
    }
    public String getName () {
	return "K2600 or K2500 with KDFX";
    }
    public String defExtension () {
	return ".k25";
    }
}
*/
