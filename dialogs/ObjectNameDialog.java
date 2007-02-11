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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import resources.Messages;

public class ObjectNameDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3696152544601251085L;
	static final String myName = Messages.getString("ObjectNameDialog.Titel"); //$NON-NLS-1$
	JTextField name;
	JTextField newName;

	public ObjectNameDialog(Frame parent)
	{
		super(parent,myName);
  	}

  
	protected void customizeLayout(JPanel panel)
	{
		panel.setLayout(new FlowLayout());
		
		JPanel labelpanel = new JPanel();
		labelpanel.setLayout(new GridLayout(2,1));

		labelpanel.add(new JLabel(Messages.getString("ObjectNameDialog.Object"))); //$NON-NLS-1$
		labelpanel.add(new JLabel(Messages.getString("ObjectNameDialog.Name"))); //$NON-NLS-1$

		JPanel fieldpanel = new JPanel();
		fieldpanel.setLayout(new GridLayout(2,1));
		name = new JTextField(30);
		name.setEnabled(false);
		fieldpanel.add(name);

		newName = new JTextField(30);
		newName.addKeyListener(this);
		fieldpanel.add(newName);

		panel.add(labelpanel);
		panel.add(fieldpanel);
	}

	public void setName (String name) {
		newName.setText(name);
		newName.selectAll();
	}

	public void setObjectDescription (String name) {
		this.name.setText(name);
	}

	public String getName () {
		return newName.getText();
	}

}
