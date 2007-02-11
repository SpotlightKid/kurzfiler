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

package kfcore.commands;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import resources.Messages;

public class SplashScreen extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6060087244006645963L;

	private JLabel curfile;

	public static String READ_MSG = Messages.getString("KurzFiler.Msgreading"); //$NON-NLS-1$
	public static String SAVE_MSG = Messages.getString("KurzFiler.Msgsaving"); //$NON-NLS-1$

	public SplashScreen(Frame parent, String text) {
		super(parent, text, false);

		Point parloc = parent.getLocation();

		setBounds(
			parloc.x + parent.getWidth() / 3,
			parloc.y + parent.getHeight() / 3,
			parent.getWidth() / 3,
			parent.getHeight() / 3);
		setBackground(Color.lightGray);

		curfile = new JLabel(Messages.getString("SplashScreen.working"), SwingConstants.CENTER); //$NON-NLS-1$
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("Center", curfile); //$NON-NLS-1$

		pack();

		setBounds(
			parloc.x + parent.getWidth() / 3,
			parloc.y + parent.getHeight() / 3,
			parent.getWidth() / 3,
			parent.getHeight() / 3);
	}

	public void showSplash() {
		setVisible(true);
	}

	public void hideSplash() {
		setVisible(false);
		dispose();
		((Window) getParent()).toFront();
		getParent().requestFocus();
	}

	public void setCurFile(String s) {
		curfile.setText(s);
		//curfile.repaint();
		//System.out.println(s);
	}

	//     public static void main(String[] args)
	//     {
	// 	Frame frame = new Frame("asdgf");
	// 	frame.setSize(700,500);
	// 	frame.setVisible(true);

	// 	SplashScreen wnd = new SplashScreen(frame,"sdf");
	// 	wnd.showSplash();
	//     }    
}
