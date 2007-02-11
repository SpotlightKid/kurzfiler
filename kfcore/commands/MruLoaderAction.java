package kfcore.commands;

import java.awt.event.ActionEvent;
import java.io.File;

import dialogs.SaveOnReinitDialog;

import filemethods.FileMethodInterface;
import filemethods.kurzweil.LoadK2x00Method;

import kfcore.KurzFiler;
import kfcore.mrulist.AbstractMruAction;
import kfcore.mrulist.MRUList;

public class MruLoaderAction extends AbstractMruAction {
	class LoaderDelegate extends BackGroundLoaderCommand {
		private static final long serialVersionUID = 2825148763896386451L;
		protected LoaderDelegate(KurzFiler filer) {
			super(filer);
			loader = new Loader(MruLoaderAction.this.filer,this);
		}

		private Loader loader;
		@Override
		protected Loader getLoader() {
			return loader;
		}

		private LoadK2x00Method filemethod = new LoadK2x00Method();
		@Override
		protected FileMethodInterface getFileMethod() {
			return filemethod;
		}

		@Override
		protected boolean appendToCurrentObject() {
			return false;
		}

		@Override
		public String getName() {
			// is egal!
			return null;
		}

		@Override
		public void Execute() {
			MruLoaderAction.this.filer.getFileObject().reinit();
			File[] file = new File[1];
			file[0]=MruLoaderAction.this.GetFile();
			setFiles(file);

			index = -1;
			loadIteration();
		}

		@Override
		public boolean isPossible() {
			// is egal
			return true;
		}

		@Override
		protected void loadCompleted() {
			super.loadCompleted();
			MruLoaderAction.this.filer.setTitle(KurzFiler.myName + getFiles()[0].getPath());
			MruLoaderAction.this.filer.getFileObject().setName(getFiles()[0].getPath());
			MruLoaderAction.this.filer.getFileObject().clearHistory();
			MruLoaderAction.this.filer.getMruList().AddFile(getFiles()[0]);
			MruLoaderAction.this.filer.getFileChooser().setCurrentDirectory(getFiles()[0]);
		}
	}
	
	LoaderDelegate delegate;
	
	private static final long serialVersionUID = -253398988218678409L;
	private final KurzFiler filer;

	public MruLoaderAction(KurzFiler filer, MRUList mruList, int index) {
		super(mruList, index);
		this.filer=filer;
		delegate=new LoaderDelegate(filer);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (this.filer.getFileObject().isDirty()) {
			SaveOnReinitDialog dia = new SaveOnReinitDialog(this.filer);
			dia.setName(this.filer.getFileObject().getName());
			dia.setVisible(true);
			if (dia.getResult()) {
				if (dia.hasConfirmed()) {
					if (this.filer.getFileObject().getName() == null)
						this.filer.getSaveasCmd().Execute();
					else
						this.filer.getSaveCmd().Execute();
				}
			} else
				return;
			//cancel new
		}

		delegate.Execute();
	}

}
