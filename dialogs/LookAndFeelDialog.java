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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import resources.Messages;

public class LookAndFeelDialog extends OKCancelDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1727880002030095994L;
	Frame parent;
	/*
	    private String mac      = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
	    private String metal    = "javax.swing.plaf.metal.MetalLookAndFeel";
	    private String motif    = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	    private String windows  = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	*/
	static String metal = "Metal (Java)"; //$NON-NLS-1$
	static String metalClassName = "javax.swing.plaf.metal.MetalLookAndFeel"; //$NON-NLS-1$

	static String motif = "Motif"; //$NON-NLS-1$
	static String motifClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel"; //$NON-NLS-1$

	static String mac = "Macintosh"; //$NON-NLS-1$
	static String macClassName = "com.sun.java.swing.plaf.mac.MacLookAndFeel"; //$NON-NLS-1$

	static String windows = "Windows"; //$NON-NLS-1$
	static String windowsClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; //$NON-NLS-1$

	static final String myName = Messages.getString("LookAndFeelDialog.Titel"); //$NON-NLS-1$

	JRadioButton metalButton, motifButton, macButton, windowsButton;

	String lookandfeel;

	public LookAndFeelDialog(Frame parent) {
		super(parent, myName);
		this.parent = parent;
	}

	protected void customizeLayout(JPanel panel) {
		// Create the buttons.

		//	windowsClassName=UIManager.getSystemLookAndFeelClassName();

		metalButton = new JRadioButton(metal);
		metalButton.setActionCommand(metalClassName);
		metalButton.setEnabled(isAvailableLookAndFeel(metalClassName));

		motifButton = new JRadioButton(motif);
		motifButton.setActionCommand(motifClassName);
		motifButton.setEnabled(isAvailableLookAndFeel(motifClassName));

		macButton = new JRadioButton(mac);
		macButton.setActionCommand(macClassName);
		macButton.setEnabled(isAvailableLookAndFeel(macClassName));

		windowsButton = new JRadioButton(windows);
		windowsButton.setActionCommand(windowsClassName);
		windowsButton.setEnabled(isAvailableLookAndFeel(windowsClassName));

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(metalButton);
		group.add(motifButton);
		group.add(macButton);
		group.add(windowsButton);

		// Register a listener for the radio buttons.
		RadioListener myListener = new RadioListener();
		metalButton.addActionListener(myListener);
		motifButton.addActionListener(myListener);
		macButton.addActionListener(myListener);
		windowsButton.addActionListener(myListener);

		panel.setLayout(new GridLayout(4, 1));
		panel.add(metalButton);
		panel.add(motifButton);
		panel.add(macButton);
		panel.add(windowsButton);
	}

	/** An ActionListener that listens to the radio buttons. */
	class RadioListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String lnfName = e.getActionCommand();
			lookandfeel = lnfName;

			try {
				UIManager.setLookAndFeel(lnfName);
				SwingUtilities.updateComponentTreeUI(LookAndFeelDialog.this);
				pack();
				//parent.pack();
			} catch (Exception exc) {
				JRadioButton button = (JRadioButton) e.getSource();
				button.setEnabled(false);
				updateState();
				System.err.println(Messages.getString("LookAndFeelDialog.ErrMsg") + lnfName); //$NON-NLS-1$
			}

		}
	}

	public void updateState() {
		String lnfName = UIManager.getLookAndFeel().getClass().getName();
		if (lnfName.indexOf("metal") >= 0) { //$NON-NLS-1$
			metalButton.setSelected(true);
			lookandfeel = metalClassName;
		} else if (lnfName.indexOf("motif") >= 0) { //$NON-NLS-1$
			motifButton.setSelected(true);
			lookandfeel = motifClassName;
		} else if (lnfName.indexOf("mac") >= 0) { //$NON-NLS-1$
			motifButton.setSelected(true);
			lookandfeel = macClassName;
		} else {
			windowsButton.setSelected(true);
			lookandfeel = windowsClassName;
		}
	}

	public void setLooknFeel() {
		try {
			UIManager.setLookAndFeel(lookandfeel);
		} catch (Exception exc) {
			System.err.println(Messages.getString("LookAndFeelDialog.ErrMsg") + lookandfeel); //$NON-NLS-1$
		}

	}

	protected boolean isAvailableLookAndFeel(String laf) {
		try {
			Class lnfClass = Class.forName(laf);
			LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
			return newLAF.isSupportedLookAndFeel();
		} catch (Exception e) { // If ANYTHING weird happens, return false
			return false;
		}
	}
}
