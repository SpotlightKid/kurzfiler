package kfcore.mrulist;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.JMenu;



public class MRUList implements Serializable {
	private static final long serialVersionUID = 6748892644407745182L;
	public static final String MRU_LIST_LENGTH = "MRU_LIST_LENGTH";
	public static final String MRU_LIST_ITEM = "MRU_LIST_ITEM";
	public static final int MAX_MRU_LIST_LENGTH = 8;
	
	private transient PropertyChangeSupport pcSupp=null;
	private transient JMenu menu=null;
	private transient int menuPos;
	private transient MruActionFactory factory=null;
	
	private LinkedList<File> files =null;
	
	public MRUList() {
		files=new LinkedList<File>();
		pcSupp=new PropertyChangeSupport(this);
	}
	
	public static MRUList Read(String filename) {
		MRUList mruList;
		try {
			FileInputStream mru=new FileInputStream(filename);
			ObjectInputStream stream=new ObjectInputStream(mru);
			mruList = (MRUList) stream.readObject();
		} catch(Exception e) {
			mruList=new MRUList();
		}
		return mruList;
	}

	public void Write(String filename) {
		try {
			FileOutputStream mru=new FileOutputStream(filename);
			ObjectOutputStream stream=new ObjectOutputStream(mru);
			stream.writeObject(this);
		} catch(Exception e) {
		}
	}
	
	public void setFiles(LinkedList<File> files) {
		this.files = files;
	}

	public LinkedList<File> getFiles() {
		return files;
	}

	public void AddFile(File file) {
		int oldListLength=getFiles().size();

		// Entfernen, falls schon vorhanden -> umsortieren
		getFiles().remove(file);
		
		getFiles().addFirst(file);
		if (getFiles().size()>MAX_MRU_LIST_LENGTH) getFiles().removeLast();
		firePropertyChange(MRU_LIST_LENGTH,oldListLength,getFiles().size());
		for (int i=0;i<getFiles().size();i++)
			firePropertyChange(MRU_LIST_ITEM,-1,i);
		
		if (menu!=null) {
			if (oldListLength<getFiles().size()) {
				if (oldListLength==0) {
					menu.insertSeparator(menuPos);
					menuPos++;
				}
				menu.insert(factory.CreateMRUListAction(this,oldListLength),menuPos);
				menuPos++;
			}
		}
	}
	
	public void AddMruSection(JMenu menu, MruActionFactory factory) {
		this.menu=menu;
		if (getFiles().size()>0) {
			menu.addSeparator(); 
			try {
			for (int i=0;i<getFiles().size();i++)
				menu.add(factory.CreateMRUListAction(this,i));
			} catch (NullPointerException e) {
				// Falls in der Datei Müll steht: neu initialisieren
				setFiles(new LinkedList<File>());
			}
		}
		this.menuPos=menu.getItemCount();
		this.factory=factory;
	}

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		if (pcSupp==null) pcSupp=new PropertyChangeSupport(this);
		pcSupp.addPropertyChangeListener(arg0);
	}

	public void firePropertyChange(String arg0, int arg1, int arg2) {
		if (pcSupp==null) pcSupp=new PropertyChangeSupport(this);
		pcSupp.firePropertyChange(arg0, arg1, arg2);
	}

	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		if (pcSupp==null) pcSupp=new PropertyChangeSupport(this);
		pcSupp.removePropertyChangeListener(arg0);
	}

	public void addPropertyChangeListener(String arg0, PropertyChangeListener arg1) {
		if (pcSupp==null) pcSupp=new PropertyChangeSupport(this);
		pcSupp.addPropertyChangeListener(arg0, arg1);
	}
}
