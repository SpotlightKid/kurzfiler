/*
 * Created on 07.03.2004
 *
 */
package tests;

import java.io.RandomAccessFile;
import junit.framework.TestCase;

/**
 * @author mahal
 *
 */
public class TestCaseFileCompare extends TestCase {
	
	/**
	 * @param arg0
	 */
	public TestCaseFileCompare(String arg0) {
		super(arg0);
	}

	protected void assertEqualFiles(String name1, String name2) {
		RandomAccessFile f1=null, f2=null;
		try {
			f1 = new RandomAccessFile(name1, "r");
			f2 = new RandomAccessFile(name2, "r");
			
			long len = f1.length();
			assertEquals(len, f2.length());
			
			for (long i=0; i<len; i++)
				assertEquals(f1.read(), f2.read());
			
			f1.close(); f1=null;
			f2.close(); f2=null;
		} catch(Exception e) {
			try {
				if (f1!=null) f1.close();
				if (f2!=null) f2.close();
			} catch (Exception e2) {}
			fail();
		}
	}
	
	static public final String TMP_WAV_NAME = "tests/test.wav";
	static public final String TMP_KRZ_NAME = "tests/test.krz";
	static public final String TMP_K25_NAME = "tests/test.k25";
	static public final String TMP_K26_NAME = "tests/test.k26";
	static public final String TMP_OUT_WAV_NAME = "tests/x0.wav";

	static public final String TMP_GUI_WAV_NAME = "test.wav";
	static public final String TMP_GUI_KRZ_NAME = "test.krz";
	static public final String TMP_GUI_K25_NAME = "test.k25";
	static public final String TMP_GUI_K26_NAME = "test.k26";
	static public final String TMP_GUI_OUT_WAV_NAME = "x";

}
