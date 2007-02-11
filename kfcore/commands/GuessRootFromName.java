package kfcore.commands;

import resources.Messages;
import kfcore.KurzFiler;
import kurzobjects.KObject;
import kurzobjects.samples.KSample;

public class GuessRootFromName extends KMultipleCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6926256076505099602L;

	public GuessRootFromName(KurzFiler filer) {
		super(filer);
	}

	private boolean SetRoot(KSample ks, String s) {
		if (s.length()>=3) try {
			ks.setRootKey(s.substring(s.length()-3));
			return true;
		} catch (Exception e) {}
		if (s.length()>=2) try {
			ks.setRootKey(s.substring(s.length()-2));
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	@Override
	void SingleCommand(Integer o) {
		KObject ko = getFiler().getFileObject().getKObject(o);
		if (ko instanceof KSample) {
			if (!((KSample) ko).isMultiRoot()) {
				KSample newko = (KSample) ko.deepCopy();
				getFiler().getFileObject().removeKObject(o);
				
				String name = newko.getName();
				if (!SetRoot(newko,name)) {
					for (String part : name.split("\\.")) if (SetRoot(newko,part)) break;
					for (String part : name.split(" ")) if (SetRoot(newko,part)) break;
					for (String part : name.split("-")) if (SetRoot(newko,part)) break;
				}
				getFiler().getFileObject().addKObject(newko);
			}
		}
	}

	@Override
	public String getName() {
		return Messages.getString("KurzFiler.GuessRootFromName"); 
	}
	@Override
	public char getMnemonic() {
		return new String(Messages.getString("KurzFiler.GuessRootFromName_Mnem")).charAt(0); 
	}

	@Override
	public boolean isPossible() {
		return getFiler().getSelMonoRootSampleNum() > 0;
	}

}
