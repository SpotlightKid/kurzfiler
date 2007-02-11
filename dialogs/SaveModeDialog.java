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

package dialogs;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import filemethods.FileMethodInterface;
import resources.Messages;

public class SaveModeDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -690178627985616139L;

	static final String myName = Messages.getString("SaveModeDialog.Titel"); //$NON-NLS-1$

	Vector<FileMethodInterface> modes; 
	Vector<JRadioButton> boxes; 
	ButtonGroup group;
	JPanel mypanel;
	int max;

	public SaveModeDialog(Frame parent, int num)
	{
		super(parent,myName);
		max=num;
  	}

  
	protected void customizeLayout(JPanel panel)
	{
		mypanel=panel;
		group = new ButtonGroup();
		modes = new Vector<FileMethodInterface>();
		boxes = new Vector<JRadioButton>();
	}

	public void addMode (FileMethodInterface mode) {
		mypanel.setLayout(new GridLayout(max,1));
		
		modes.add(mode);
		JRadioButton rb = new JRadioButton(mode.getFileTypeDescription(), true);
		boxes.add(rb);
		rb.addKeyListener(this);
		group.add(rb);
		mypanel.add(rb);
		pack();
	}
/*
	public void setAuswahl (FileMethodIterface f) {
		Iterator iter=modes.iterator();
		int i=0;
		while (iter.hasNext()) {
			if ( ((FileMethodIterface)iter.next()).getName().equals(f.getName()))
				((JRadioButton)boxes.elementAt(i)).setSelected(true);
			i++;
		}
	}
*/
	public FileMethodInterface getAuswahl () {
		Iterator<JRadioButton> iter=boxes.iterator();
		int i=0;
		while (iter.hasNext()) {
			if ( iter.next().isSelected() )
				return modes.elementAt(i);
			i++;
		}
		return null;
	}

}
