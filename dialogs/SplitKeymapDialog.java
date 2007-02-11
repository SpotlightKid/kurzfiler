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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kurzobjects.keymaps.KKeymap;
import resources.Messages;

public class SplitKeymapDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1577257637043962459L;

	static final String myName = Messages.getString("SplitKeymapDialog.Titel"); //$NON-NLS-1$

	JComboBox[] levels;

	Vector<String> maps;

	public SplitKeymapDialog(Frame parent)
	{
		super(parent,myName);
 		setSize(400,300);
 	}

  
	protected void customizeLayout(JPanel panel)
	{
		panel.setLayout(new BorderLayout(5,5));
		
		JPanel mypanel = new JPanel();
		mypanel.setLayout(new GridLayout(8,1));
		
		for (int i=0;i<KKeymap.NUM_LEVELS;i++)
			mypanel.add(new JLabel(KKeymap.LEVEL_NAME[i]));

		panel.add("West",mypanel); //$NON-NLS-1$

		maps=new Vector<String>();
		levels=new JComboBox[KKeymap.NUM_LEVELS];
		for (int i=0;i<KKeymap.NUM_LEVELS;i++)
			levels[i]=new JComboBox(maps);

		mypanel = new JPanel();
		mypanel.setLayout(new GridLayout(8,1));
		for (int i=0;i<KKeymap.NUM_LEVELS;i++)
			mypanel.add(levels[i]);

		panel.add("Center",mypanel); //$NON-NLS-1$


		mypanel = new JPanel();
		JLabel label = new JLabel(Messages.getString("SplitKeymapDialog.SelectAKeymap")); //$NON-NLS-1$
		label.setForeground(Color.black);
		mypanel.add(label);

		panel.add("North",mypanel); //$NON-NLS-1$

		pack();
	}

	public void addMode (String s) {
		maps.add(s);
//		pack();
	}

	public int[] getAuswahl () {
		int[] wahl = new int[KKeymap.NUM_LEVELS];
		for (int i=0;i<KKeymap.NUM_LEVELS;i++)
			wahl[i] = levels[i].getSelectedIndex();
		return wahl;
	}

	public void setAuswahl (int[] wahl) {
		for (int i=0;i<KKeymap.NUM_LEVELS;i++)
			levels[i].setSelectedIndex(wahl[i]);
	}

}
