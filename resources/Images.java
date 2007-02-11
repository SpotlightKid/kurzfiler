/*
KurzFiler
A Soundfile Editor for Kurzweil Samplers

Copyright (c) 2003-2006 Marc Halbruegge
  
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

package resources;

import javax.swing.ImageIcon;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

public class Images {
	//private static final Class loader = "ressources.text"; //$NON-NLS-1$

	public static final String NEW_ICON = "new.gif";
	public static final String OPEN_ICON = "open.gif";
	public static final String SAVE_ICON = "save.gif";
	public static final String UNDO_ICON = "undo.gif";
	public static final String REDO_ICON = "redo.gif";
	
	public static final String APPLICATION_ICON = "kurzfiler.gif";

	/**
	 * private constructor -> use static fields and methods only!
	 */
	private Images() {
	}

	public static ImageIcon getImage(String key) {
		return new ImageIcon(new Images().getClass().getResource(key));
	}

	/** The App Icon needs to be loaded "AWT" style */
	public static Image getAppIcon(Component c) {
		URL imgURL = new Images().getClass().getResource(APPLICATION_ICON);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = null;
		try {
			MediaTracker m = new MediaTracker(c);
			img = tk.getImage(imgURL);
			m.addImage(img, 0);
			m.waitForAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}
}
