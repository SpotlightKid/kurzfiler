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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import resources.Messages;

public class SaveOnExitDialog
extends YesNoCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1983217901157627734L;
	static final String myName = Messages.getString("SaveOnExitDialog.Titel"); //$NON-NLS-1$
	JLabel name;

	public SaveOnExitDialog(Frame parent)
	{
		super(parent,myName);
  	}

  
	protected void customizeLayout(JPanel panel)
	{
		panel.setLayout(new GridLayout(2,1,2,2));
	
		name = new JLabel();
		name.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(name);

		JLabel label= new JLabel(Messages.getString("SaveOnExitDialog.Warning")); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label);
	}

	public void setName (String name) {
		if (name==null) this.name.setText(Messages.getString("SaveOnExitDialog.DefaultName")); //$NON-NLS-1$
		else this.name.setText(Messages.getString("SaveOnExitDialog.NamePrefix")+name); //$NON-NLS-1$
		pack();
	}

}
