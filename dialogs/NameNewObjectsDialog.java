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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kurzobjects.KHash;
import resources.Messages;

public class NameNewObjectsDialog
extends OKCancelDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 92122778497013590L;

	static final String myName = Messages.getString("NameNewObjectsDialog.Titel"); //$NON-NLS-1$

	JTextField keymapName;
	JTextField programName;

	public NameNewObjectsDialog(Frame parent)
	{
		super(parent,myName);
 		//setSize(400,300);
 	}

  
	protected void customizeLayout(JPanel panel)
	{
		panel.setLayout(new BorderLayout(5,5));
		
		JPanel mypanel = new JPanel();
		mypanel.setLayout(new GridLayout(2,1));
		
		mypanel.add(new JLabel(KHash.getName(KHash.MIN_KEYMAP)));
		mypanel.add(new JLabel(KHash.getName(KHash.MIN_PROGRAM)));

		panel.add("West",mypanel); //$NON-NLS-1$

		mypanel = new JPanel();
		mypanel.setLayout(new GridLayout(2,1));

		keymapName=new JTextField();
		mypanel.add(keymapName);
		keymapName.addKeyListener(this);

		programName=new JTextField();
		mypanel.add(programName);
		programName.addKeyListener(this);

		panel.add("Center",mypanel); //$NON-NLS-1$


		mypanel = new JPanel();
		JLabel label = new JLabel(Messages.getString("NameNewObjectsDialog.SelectAName")); //$NON-NLS-1$
		label.setForeground(Color.black);
		mypanel.add(label);

		panel.add("North",mypanel); //$NON-NLS-1$

		pack();
	}

	public void setKeymapName (String s) {
		keymapName.setText(s);
		keymapName.setSelectionStart(0);
		keymapName.setSelectionEnd(s.length());

	}

	public String getKeymapName () {
		return keymapName.getText();
	}

	public void setProgramName (String s) {
		programName.setText(s);
		programName.setSelectionStart(0);
		programName.setSelectionEnd(s.length());
	}

	public String getProgramName () {
		return programName.getText();
	}

}
