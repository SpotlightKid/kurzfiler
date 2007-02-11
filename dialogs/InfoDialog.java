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
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import resources.Messages;

public class InfoDialog
extends JDialog
implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3179116984275080462L;
	protected static final String OKCmd = Messages.getString("OKCancelDialog.OK"); //$NON-NLS-1$
	protected static final String format = Messages.getString("InfoDialog.MsgPrefix"); //$NON-NLS-1$

	public InfoDialog(Frame parent, String myName)
	{
		super(parent,myName,false);

		Point parloc = parent.getLocation();
		setBounds(parloc.x + 30, parloc.y + 30,400,300);
		setBackground(Color.lightGray);
		getContentPane().setLayout(new BorderLayout());
		//Panel
		JPanel panel = new JPanel();
		customizeLayout(panel);

		getContentPane().add("Center",panel); //$NON-NLS-1$

		//Buttons
		panel = new JPanel();
		JButton button = new JButton(OKCmd);
		button.addActionListener(this);
		panel.add(button);

		getContentPane().add("South", panel); //$NON-NLS-1$

		//Window-Listener
		addWindowListener(
		    new WindowAdapter() {
				public void windowClosing(WindowEvent event)
				{
				  endDialog();
				}
			}
		);
		pack();
  	}

	public void actionPerformed(ActionEvent event)
	{
		endDialog();
	}


	protected void customizeLayout(JPanel panel)
	{
	}

	protected void endDialog()
	{
		setVisible(false);
		dispose();
		((Window)getParent()).toFront();
		getParent().requestFocus();
	}

}
