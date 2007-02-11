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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import resources.Messages;

public class OKCancelDialog
extends JDialog
implements ActionListener, KeyListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3756672578436999869L;
	protected String OKCmd = Messages.getString("OKCancelDialog.OK"); //$NON-NLS-1$
	protected String cancelCmd = Messages.getString("YesNoCancelDialog.Cancel"); //$NON-NLS-1$

	protected boolean result=false;

	public OKCancelDialog(Frame parent, String myName)
	{
		super(parent,myName,true);

		Point parloc = parent.getLocation();
		setBounds(parloc.x + 30, parloc.y + 30,400,300);
		setBackground(Color.lightGray);
		getContentPane().setLayout(new BorderLayout(3,3));
		//Panel
		JPanel panel = new JPanel();
		customizeLayout(panel);

		getContentPane().add("Center",panel); //$NON-NLS-1$

		//Buttons
		panel = new JPanel();
		JButton button = new JButton(OKCmd);
		button.setMnemonic(OKCmd.charAt(0));
		button.addActionListener(this);
		panel.add(button);

		button = new JButton(cancelCmd);
		button.setMnemonic(cancelCmd.charAt(0));
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
		if (event.getActionCommand().equals(OKCmd)) {
			endDialog(true);
		}
		else if (event.getActionCommand().equals(cancelCmd)) {
			endDialog(false);
		}
	}


	public void keyPressed(KeyEvent event)
  	{
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			endDialog(false);
		}
		else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			endDialog(true);
		}
	}


	public void keyReleased(KeyEvent event)
	{
	}
	
	public void keyTyped(KeyEvent event)
	{
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

	protected void endDialog(boolean res)
	{
		result=res;
		endDialog();
	}

	public boolean getResult () {
		return result;
	}
}
