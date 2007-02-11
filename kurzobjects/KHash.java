package kurzobjects;

import resources.Messages;

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

public final class KHash {

    public static final short T_PROGRAM =	36;
    public static final short T_KEYMAP	=	37;
    public static final short T_SAMPLE	=	38;
    public static final short T_QABANK	=	111;
    public static final short T_SONG	=	112;
    public static final short T_EFFECT	=	113;

    public static final int MIN_SAMPLE	=	generate (0, T_SAMPLE);
    public static final int MAX_SAMPLE	=	generate (1023, T_SAMPLE);

    public static final int MIN_KEYMAP	=	generate (0, T_KEYMAP);
    public static final int MAX_KEYMAP	=	generate (1023, T_KEYMAP);

    public static final int MIN_PROGRAM	=	generate (0, T_PROGRAM);
    public static final int MAX_PROGRAM	=	generate (1023, T_PROGRAM);
	

    public static final String PROGRAM_NAME	=	"Program ";
    public static final String KEYMAP_NAME	=	"Keymap  ";
    public static final String SAMPLE_NAME	=	"Sample  ";
    public static final String SONG_NAME	=	"Song    ";
    public static final String EFFECT_NAME	=	"Effect  ";
    public static final String DEFAULT_NAME	=	"Object  ";
	
	public static final short getID (int s) {
		if ((s&0x8000)>0) return (short)(s&1023); 
		switch (getType(s)) {
		case T_EFFECT:	return ((s&255)<38) ? (short)(s&255) : (short)(((s&255)-8)%10 + 10*(((s&255)-28)-((s&255)-8)%10));
		case T_SONG:
		case T_QABANK:	return (short)(1023 & ( ((s&255)-16)%20 + 5*(((s&255)-56)-((s&255)-16)%20) ));
		}
		return (short)(s&255);
	}

	public static final short getType (int h) {
		return ((h&0x8000)>0) ? (short)(h>>>10) : (short)(h>>>8);
	}

	public static final int generate (int ID, short Type) {
		if (Type<=42) return (Type<<10) + ID;
		else {
			switch (Type) {
			case T_EFFECT:	
				if (ID<100) return (Type<<8 + ID);
				else return ((Type<<8) + ((ID/100)*10) + (ID%100) + 28);
			case T_SONG:
			case T_QABANK:	
				if (ID<100) return ((Type<<8) + ID)&0xffff;
				else return (( (Type<<8) + (ID/100)*20 + (ID%100) + 56))&0xffff;
			}
		}
		return Type<<8 + ID;
	}

	public static final int successor (int h) {
		if (getID(h)<999) return h+1;
		else return 0;
	}

	public static final String getName (int h) {
/*		
		switch (getType(h)) {
		case T_PROGRAM:	return PROGRAM_NAME;
		case T_KEYMAP:	return KEYMAP_NAME;
		case T_SAMPLE:	return SAMPLE_NAME;
		case T_SONG:	return SONG_NAME;
		case T_EFFECT:	return EFFECT_NAME;
		}
		return DEFAULT_NAME;
*/
		switch (getType(h)) {
		case T_PROGRAM:	return Messages.getString("KurzFiler.ProgramMenu");
		case T_KEYMAP:	return Messages.getString("KurzFiler.KeymapMenu");
		case T_SAMPLE:	return Messages.getString("KurzFiler.SampleMenu");
		}
		
		return Messages.getString("KurzFiler.ObjectMenu");
	}
	
}