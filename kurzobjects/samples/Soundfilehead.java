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

package kurzobjects.samples;
import java.io.IOException;
import java.io.RandomAccessFile;

import resources.Messages;
import dialogs.runtimemsg.DisplayFactory;
import dialogs.runtimemsg.MsgDisplay;


public class Soundfilehead 
{
	public byte rootkey;
	public byte flags;
	public byte volumeAdjust;
	public byte altVolumeAdjust;
	public short maxPitch;
	public short offsetToName;
	//Start - End ist wortweise gezählt!!
	public int sampleStart;
	public int altSampleStart;
	public int sampleLoopStart;
	public int sampleEnd;
	public short offsetToEnvelope;
	public short altOffsetToEnvelope;
	public int samplePeriod;

	public byte[] sampledata;
	
	public Soundfilehead()
	{
		rootkey			= 0;
		flags			= 0;
		volumeAdjust	= 0;
		altVolumeAdjust = 0;
		maxPitch		= 0;
		offsetToName	= 0;
		sampleStart		= 0;
		altSampleStart	= 0;
		sampleLoopStart	= 0;
		sampleEnd		= 0;
		offsetToEnvelope= 0;
		altOffsetToEnvelope	= 0;
		samplePeriod	= 0;
	};

	public Soundfilehead(Soundfilehead s)
	{
		rootkey			= s.rootkey;
		flags 			= s.flags;
		volumeAdjust 	= s.volumeAdjust;
		altVolumeAdjust = s.altVolumeAdjust;
		maxPitch 		= s.maxPitch;
		offsetToName 	= s.offsetToName;
		sampleStart		= s.sampleStart;
		altSampleStart	= s.altSampleStart;
		sampleLoopStart	= s.sampleLoopStart;
		sampleEnd		= s.sampleEnd;
		offsetToEnvelope= s.offsetToEnvelope;
		altOffsetToEnvelope	= s.altOffsetToEnvelope;
		samplePeriod	= s.samplePeriod;
		sampledata  	= s.sampledata;
		//nur Referenz auf Samples übernehmen!!
	};

	public Soundfilehead(RandomAccessFile f)
	throws IOException
	{
		rootkey			= f.readByte();
		flags			= f.readByte();
		volumeAdjust	= f.readByte();
		altVolumeAdjust	= f.readByte();
		maxPitch		= f.readShort();
		offsetToName	= f.readShort();
		sampleStart		= f.readInt();
		altSampleStart	= f.readInt();
		sampleLoopStart	= f.readInt();
		sampleEnd		= f.readInt();
		offsetToEnvelope= f.readShort();
		altOffsetToEnvelope	= f.readShort();
		samplePeriod	= f.readInt();
		
		// perform checks!
		MsgDisplay display=DisplayFactory.GetDisplay();
		if (samplePeriod<1) {
			samplePeriod=22675;
			setRootKey(rootkey);
			display.ShowErrorMessage("Bad sampling frequency reset to 44100 Hz.", 
				Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
		}
		if (sampleStart>sampleEnd) {
			sampleStart=0;
			display.ShowErrorMessage("Bad sample start reset to 0..", 
				Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
		}
		if (sampleLoopStart>sampleEnd) {
			sampleLoopStart=0;
			display.ShowErrorMessage("Bad sample loop start reset to 0..", 
				Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
		}
		if (sampleEnd<0) {
			sampleEnd=0;
			display.ShowErrorMessage("Bad sample end reset to 0..", 
				Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
		}
	}

	public void write (RandomAccessFile f)
	{
		try {
			f.writeByte(rootkey);
			f.writeByte(flags);
			f.writeByte(volumeAdjust);
			f.writeByte(altVolumeAdjust);
			f.writeShort(maxPitch);
			f.writeShort(offsetToName);
			f.writeInt(sampleStart);
			f.writeInt(altSampleStart);
			f.writeInt(sampleLoopStart);
			f.writeInt(sampleEnd);
			f.writeShort(offsetToEnvelope);
			f.writeShort(altOffsetToEnvelope);
			f.writeInt(samplePeriod);
		} catch (IOException e) {
			throw new RuntimeException(Messages.getString("Soundfilehead.Error_while_writing_sample_sound_block")); //$NON-NLS-1$
		}
	}

	public boolean needsLoad () {
		return (flags&0x40)==0x40;
	}
	
	public boolean isLooped() {
		return (flags&0x80) == 0; // 0 = an
	}

	public int getRamSize() {
		if (needsLoad()) return (sampleEnd-sampleStart+1)*2;
		else return 0;
	}
	
	public boolean readsampledata (RandomAccessFile f, long offset) {
		if (needsLoad()) {
			sampledata=new byte[(sampleEnd-sampleStart+1)*2];
			//einer mehr!
			try {
				f.seek(offset+sampleStart*2);
				f.readFully(sampledata);
			}
			catch (IOException e) {
				sampleEnd=-1;
				sampleLoopStart=0;
				altSampleStart=0;
				sampleStart=0;
				sampledata=new byte[0];
				return false;
			}
			sampleEnd-=sampleStart;
			sampleLoopStart-=sampleStart;
			altSampleStart-=sampleStart;
			sampleStart=0;
			if (altSampleStart>sampleEnd) altSampleStart=sampleStart;
		}
		return true;
	}

	public int prewrite (int offset) {
		if (needsLoad()) {
			sampleEnd-=sampleStart;
			sampleLoopStart-=sampleStart;
			altSampleStart-=sampleStart;
			sampleStart=0;
			if (altSampleStart>sampleEnd) altSampleStart=sampleStart;

			sampleEnd+=offset;
			sampleLoopStart+=offset;
			altSampleStart+=offset;
			sampleStart+=offset;
			return sampleEnd+1; //neues Offset für nächstes Sample
		}
		else return offset;
	}

	public void writesampledata (RandomAccessFile f)
	{
		if (needsLoad()) {
			sampleEnd-=sampleStart;
			sampleLoopStart-=sampleStart;
			altSampleStart-=sampleStart;
			sampleStart=0;
			if (altSampleStart>sampleEnd) altSampleStart=sampleStart;
			if (sampleLoopStart>sampleEnd) sampleLoopStart=sampleEnd;

			try {
				f.write(sampledata,0,2+sampleEnd*2);
			} catch (IOException e) {
				throw new RuntimeException(Messages.getString("Soundfilehead.Error_while_writing_sample_data")); //$NON-NLS-1$
			}
		}
	}

	public void setRootKey (byte r) {
		rootkey=r;
		maxPitch=(short) (Math.ceil(1200.0*Math.log(96000.0/1000000000L*samplePeriod)/Math.log(2.0)) + 100*r-1200);
	}
};

