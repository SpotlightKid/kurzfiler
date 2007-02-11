/*
 * Created on 29.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author mahal
 *
 */
public class Messages {

	private static final String BUNDLE_NAME = "resources.text"; //$NON-NLS-1$

	private ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * 
	 */
	private Messages() {

	}
	/**
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		try {
			Messages m = new Messages();
			return m.RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
