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

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import kfcore.fillmodes.FillMode;
import kfcore.fillmodes.FillModeAppend;
import kfcore.fillmodes.FillModeFill;

import resources.Messages;

public class FillModeDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -220791377120107582L;
	static final String myName = Messages.getString("FillModeDialog.Titel"); //$NON-NLS-1$
	static final int NUM_MODES = 2;

	FillMode[] modes; 
	JRadioButton[] boxes; 
	ButtonGroup group;

	public FillModeDialog(Frame parent)
	{
		super(parent,myName);
  	}

  
	protected void customizeLayout(JPanel panel)
	{
		modes = new FillMode[NUM_MODES];
		boxes = new JRadioButton[NUM_MODES];
		group = new ButtonGroup();

		modes[0] = new FillModeFill();
		modes[1] = new FillModeAppend();
//		modes[2] = new FillModeMerge();
		
	
		panel.setLayout(new GridLayout(NUM_MODES,1));

		for (int i=0; i<NUM_MODES; i++) {
			boxes[i]=new JRadioButton(modes[i].getFillModeName(), false);
			boxes[i].addKeyListener(this);
			panel.add(boxes[i]);
			group.add(boxes[i]);
		}
	}

	public void setAuswahl (FillMode f) {
		for (int i=0; i<NUM_MODES; i++) 
			if (modes[i].getFillModeNumber()==f.getFillModeNumber()) 
				boxes[i].setSelected(true);
	}

	public FillMode getAuswahl () {
		for (int i=0; i<NUM_MODES; i++) 
			if (boxes[i].isSelected()) return modes[i];
		return modes[0];
	}

}
