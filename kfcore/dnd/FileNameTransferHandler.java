package kfcore.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import filemethods.FileMethod;

import kfcore.KurzFiler;

public class FileNameTransferHandler extends TransferHandler {

	KurzFiler owner;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6004930274872811167L;

	public FileNameTransferHandler(KurzFiler o) {
		owner = o;
	}
	
	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
				return true;
			}
		}
		//return super.canImport(c, flavors);
		return false;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return NONE;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}
		try {
			java.util.List l = (java.util.List)t.getTransferData(DataFlavor.javaFileListFlavor);
			
			Iterator i = l.iterator();
			while (i.hasNext()) {
				java.io.File file=(java.io.File) i.next();
				String filename= file.getAbsolutePath();
				//System.out.println(filename);

				for (FileMethod f : owner.getFileObject().getImportRoutines()) {
					if (f.matchesExtensions(filename)) {
						owner.getFileObject().importData(filename, f);
					}
				}
				owner.getFileObject().updateList();
			}
			
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
}
