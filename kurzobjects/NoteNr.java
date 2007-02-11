package kurzobjects;
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

public final class NoteNr {
	private static final byte numbers[] = 
	{
		9, //A
		11, //B
		0,	//C
		2,	//D
		4,	//E	
		5,	//F
		7	//G
	};
	
	private static final String nnr[] = 
		{ "C", "C#", "D",
		 "D#", "E", "F",
		 "F#", "G", "G#",
		 "A", "A#", "B"
		};

	private static final String nno[] =
		{ "-1", "0", "1",
		 "2", "3", "4",
		 "5", "6", "7",
		 "8", "9", "10"
		};

	public static final String getNote (short s) {
		return nnr[s % 12] + nno[s / 12];
	}

	public static final byte getNumber (char c) {
		return numbers[c-'A'];
	}
	
}