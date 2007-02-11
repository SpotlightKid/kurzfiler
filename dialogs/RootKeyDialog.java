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

import javax.swing.JPanel;
import javax.swing.JTextField;

import resources.Messages;

public class RootKeyDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6219161879024380672L;
	static final String myName = Messages.getString("RootKeyDialog.Titel"); //$NON-NLS-1$
	JTextField name;
	JTextField rootKey;

	public RootKeyDialog(Frame parent)
	{
		super(parent,myName);
  	}

  
	protected void customizeLayout(JPanel panel)
	{
		panel.setLayout(new FlowLayout());
	
		name = new JTextField(30);
//		name.setEditable(false);
		name.setEnabled(false);

		rootKey = new JTextField(5);
		rootKey.addKeyListener(this);

		panel.add(name);

		panel.add(rootKey);
		
	}

	public void setName (String name) {
		this.name.setText(name);
	}

	public void setRootKey (String name) {
		rootKey.setText(name);
		rootKey.selectAll();
	}

	public String getRootKey () {
		return rootKey.getText();
	}
}
