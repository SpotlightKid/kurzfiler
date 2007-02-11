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
import java.awt.Label;

import javax.swing.JLabel;
import javax.swing.JPanel;

import resources.Messages;

public class AboutDialog
extends InfoDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -644980123498144148L;
	static final String myName = Messages.getString("AboutDialog.Titel"); //$NON-NLS-1$
	static final String message = Messages.getString("AboutDialog.Msg"); //$NON-NLS-1$
	
	private static final String getMyTitle() {
		return myName;
	}

	public AboutDialog(Frame parent)
	{
		super(parent,getMyTitle());
  	}


	protected void customizeLayout(JPanel panel)
	{
	    //Runtime r = Runtime.getRuntime ();
	    //long free = r.freeMemory ();
	    //long total = r.totalMemory ();

		panel.setLayout(new FlowLayout());
		panel.add(new JLabel(message), Label.LEFT);
		//panel.add(new JLabel(String.valueOf(free/1024)));
		//panel.add(new JLabel(String.valueOf(total/1024)));
	}

}
