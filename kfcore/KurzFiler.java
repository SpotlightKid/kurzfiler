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
package kfcore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import kfcore.commands.AltStartToSampleEndCommand;
import kfcore.commands.CompactCommand;
import kfcore.commands.DeSelectCommand;
import kfcore.commands.DeleteCommand;
import kfcore.commands.ExitCommand;
import kfcore.commands.FileInfoCommand;
import kfcore.commands.FillmodeCommand;
import kfcore.commands.GuessRootFromName;
import kfcore.commands.ImportCommand;
import kfcore.commands.InfoCommand;
import kfcore.commands.KCommand;
import kfcore.commands.LookAndFeelCommand;
import kfcore.commands.MruLoaderAction;
import kfcore.commands.MusicalKMCommand;
import kfcore.commands.NewCommand;
import kfcore.commands.OpenBackGroundCommand;
import kfcore.commands.RedoCommand;
import kfcore.commands.RemapCommand;
import kfcore.commands.RenameCommand;
import kfcore.commands.RevertCommand;
import kfcore.commands.SaveAsCommand;
import kfcore.commands.SaveCommand;
import kfcore.commands.SaveSelectedCommand;
import kfcore.commands.SelectAllCommand;
import kfcore.commands.SelectDependantsCommand;
import kfcore.commands.SelectUsersCommand;
import kfcore.commands.SetLangDECommand;
import kfcore.commands.SetLangENCommand;
import kfcore.commands.SetLangESCommand;
import kfcore.commands.SetLangFRCommand;
import kfcore.commands.SetLangITCommand;
import kfcore.commands.SetLangJACommand;
import kfcore.commands.SetLangNLCommand;
import kfcore.commands.SetLangPLCommand;
import kfcore.commands.SetRootCommand;
import kfcore.commands.SimpleDrumKMCommand;
import kfcore.commands.SplitKeymapCommand;
import kfcore.commands.UndoCommand;
import kfcore.dnd.DropZone;
import kfcore.dnd.FileNameTransferHandler;
import kfcore.mrulist.MRUList;
import kfcore.mrulist.MruActionFactory;
import kurzobjects.KHash;
import kurzobjects.KProgram;
import kurzobjects.keymaps.KKeymap;
import kurzobjects.samples.KSample;
import resources.Messages;
import dialogs.MessageDialog;
import dialogs.NameNewObjectsDialog;
import dialogs.runtimemsg.DisplayFactory;
import dialogs.runtimemsg.MsgDisplay;
import filemethods.KFile;
import filemethods.kurzweil.LoadK2x00Method;

public class KurzFiler extends JFrame implements MruActionFactory {

	private static final long serialVersionUID = -864030792086721135L;
	public static final String myName = "Kurzfiler "; //$NON-NLS-1$
	
	/** reuse the chooser so that it stays in a directory between
	 *  two open/save-actions
	 */
	private JFileChooser chooser = null;

	/// Dateiobjekt (Model in MVC)
	private KFile fileObject = new KFile();

	/// Controller (C in MVC)
	private Vector<KCommand> commands = new Vector<KCommand>();

	/** the exitCmd handles user questioning when unsaved data could be lost.
	 * This gets called from other commands, therefore we need a pointer to it.
	 */
	private KCommand exitCmd;
	/// save 
	private KCommand saveCmd;
	/// save as
	private KCommand saveasCmd;
	/// new file
	private KCommand newCmd;

	/// Most recently used Files
	private MRUList mruList;

	/// Dialogelemente (V in MVC)
	private JTable objectTable;

	/// helper for button states
	private int selSampleNum;
	/// helper for button states
	private int selMonoRootSampleNum;
	/// helper for button states
	private int selKeymapNum;
	/// helper for button states
	private int selCompactableKeymapNum;
	/// helper for button states
	private int selProgramNum;
	/// helper for button states
	private int selNum;

	public static void main(String[] args) {
		KurzFiler wnd = new KurzFiler();
		wnd.setSize(700, 500);
		wnd.setVisible(true);
		if (args.length>0) {
			wnd.OpenFile(args[0]);
		}
	}
	
	/// this is only used whed a filename is passed to the .jar file
	protected void OpenFile(String filename) {
		try {
			fileObject.importData(filename,new LoadK2x00Method());
			fileObject.setDirty(false);
			fileObject.updateList();
			setTitle(KurzFiler.myName + filename);
			getFileObject().setName(filename);
			getFileObject().clearHistory();
			getMruList().AddFile(new File(filename));
			loadCompletedNotification();
			getFileChooser().setCurrentDirectory(new File(filename));
			clearSel();
		} catch (Exception e) {
			MsgDisplay display=DisplayFactory.GetDisplay();
			display.ShowErrorMessage(filename+ " " +e.getLocalizedMessage(), 
				Messages.getString("KurzFiler.An_error_occured_while_reading_the_file"));
		}
	}

	/// restart the GUI after the locale has changed
	synchronized public static void restart(KurzFiler kf, Locale newLocale) {
		kf.setVisible(false);
		kf.dispose();

		Locale.setDefault(newLocale);

		KurzFiler wnd = new KurzFiler();
		wnd.setSize(700, 500);
		wnd.setVisible(true);	
	}

	static private final String MRULIST_SAVEFILENAME=".kf-mrulist.dat"; 
	protected void ReadMRUList() {
		setMruList(MRUList.Read(MRULIST_SAVEFILENAME));
	}
	public void WriteMRUList() {
		getMruList().Write(MRULIST_SAVEFILENAME);
	}
	
	
	protected KurzFiler() {
		super(myName);
		
		// Icon
		setIconImage(resources.Images.getAppIcon(this)); 
		
		ReadMRUList();
		
		KCommand command;

		setObjectTable(new JTable(getFileObject()));
		initColumnSizes(getObjectTable(), getFileObject());

		setBackground(Color.lightGray);

		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());

		JScrollPane scroll = new JScrollPane(getObjectTable());
		getObjectTable().getSelectionModel().addListSelectionListener(
			new SelectionChanged());
		contentPane.add("Center", scroll); //$NON-NLS-1$

		JToolBar toolbar = new JToolBar();
		contentPane.add("North", toolbar); //$NON-NLS-1$
		toolbar.setFloatable(false);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 1));

		JPanel buttonPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc;
		buttonPanel.setLayout(gbl);
		panel.add(buttonPanel);
		contentPane.add("East", panel); //$NON-NLS-1$

		JMenuBar menubar = new JMenuBar();
		/*
		  File menu
		*/
		JMenu menu = new JMenu(Messages.getString("KurzFiler.FileMenu")); //$NON-NLS-1$
		menu.setMnemonic(new String(Messages.getString("KurzFiler.FileMenuMnem")).charAt(0)); //$NON-NLS-1$

		setNewCmd(new NewCommand(this));
		commands.add(getNewCmd());
		addMenuItem(getNewCmd(), menu);
		toolbar.add(getNewCmd());

		command = new OpenBackGroundCommand(this);
		addMenuItem(command, menu);
		toolbar.add(command);

		command = new RevertCommand(this);
		commands.add(command);
		addMenuItem(command, menu);

		command = new ImportCommand(this);
		addMenuItem(command, menu);

		menu.addSeparator();

		command = new RemapCommand(this);
		commands.add(command);
		addMenuItem(command, menu);

		menu.addSeparator();

		setSaveCmd(new SaveCommand(this));
		commands.add(getSaveCmd());
		addMenuItem(getSaveCmd(), menu);
		toolbar.add(getSaveCmd());

		setSaveasCmd(new SaveAsCommand(this));
		commands.add(getSaveasCmd());
		addMenuItem(getSaveasCmd(), menu);

		command = new SaveSelectedCommand(this);
		commands.add(command);
		addMenuItem(command, menu);

		menu.addSeparator();

		command = new FileInfoCommand(this);
		commands.add(command);
		addMenuItem(command, menu);

		getMruList().AddMruSection(menu, this);
		
		menu.addSeparator();

		command = exitCmd = new ExitCommand(this);
		addButton(
			exitCmd,
			buttonPanel,
			gbl,
			8,
			20,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.SOUTHEAST);
		addMenuItem(exitCmd, menu);
		((JComponent) contentPane).registerKeyboardAction(
			command,
			command.getName(),
			KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		((JComponent) contentPane).registerKeyboardAction(
			command,
			command.getName(),
			KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		menubar.add(menu);

		/*
		  Edit menu
		*/

		toolbar.addSeparator();

		menu = new JMenu(Messages.getString("KurzFiler.EditMenu")); //$NON-NLS-1$
		menu.setMnemonic(new String(Messages.getString("KurzFiler.EditMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new UndoCommand(this);
		//commands.add(command);
		addMenuItem(command, menu);
		toolbar.add(command);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			3,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);
		((JComponent) contentPane).registerKeyboardAction(
			command,
			command.getName(),
			KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Event.ALT_MASK),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		command = new RedoCommand(this);
		//commands.add(command);
		addMenuItem(command, menu);
		toolbar.add(command);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			4,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		menu.addSeparator();

		command = new DeleteCommand(this);
		commands.add(command);
		addMenuItem(command, menu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			6,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		menu.addSeparator();

		command = new SelectAllCommand(this);
		commands.add(command);
		addMenuItem(command, menu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			0,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		command = new DeSelectCommand(this);
		commands.add(command);
		addMenuItem(command, menu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			1,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		Component strut = Box.createVerticalStrut(5);
		gbc = makeGridBagConstraints(8, 2, 1, 1);
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(strut, gbc);
		buttonPanel.add(strut);

		strut = Box.createVerticalStrut(5);
		gbc = makeGridBagConstraints(8, 5, 1, 1);
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(strut, gbc);
		buttonPanel.add(strut);

		strut = Box.createVerticalStrut(50);
		gbc = makeGridBagConstraints(8, 19, 1, 1);
		gbc.weightx = 0;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(strut, gbc);
		buttonPanel.add(strut);

		command = new SelectDependantsCommand(this);
		commands.add(command);
		addMenuItem(command, menu);

		command = new SelectUsersCommand(this);
		commands.add(command);
		addMenuItem(command, menu);
		command = new DeSelectCommand(this);

		menubar.add(menu);

		/*
		  Object menu
		*/
		menu = new JMenu(Messages.getString("KurzFiler.ObjectMenu")); //$NON-NLS-1$
		menu.setMnemonic(new String(Messages.getString("KurzFiler.ObjectMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new RenameCommand(this);
		commands.add(command);
		addMenuItem(command, menu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			7,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		JMenu submenu = new JMenu(Messages.getString("KurzFiler.SampleMenu")); //$NON-NLS-1$
		submenu.setMnemonic(new String(Messages.getString("KurzFiler.SampleMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new SetRootCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			8,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		//	command = new LoopOnlyCommand();
		//	commands.add(command);
		//	addMenuItem(command, submenu);
		//	addButton (command, button_panel, gbl, 
		//		   8, 8, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);

		
		command = new GuessRootFromName(this);
		commands.add(command);
		addMenuItem(command, submenu);
		
		command = new AltStartToSampleEndCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);

		menu.add(submenu);

		submenu = new JMenu(Messages.getString("KurzFiler.KeymapMenu")); //$NON-NLS-1$
		submenu.setMnemonic(new String(Messages.getString("KurzFiler.KeymapMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new CompactCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			9,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		command = new SplitKeymapCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			10,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		menu.add(submenu);

		submenu = new JMenu(Messages.getString("KurzFiler.ProgramMenu")); //$NON-NLS-1$
		submenu.setMnemonic(new String(Messages.getString("KurzFiler.ProgramMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new SimpleDrumKMCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			11,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		command = new MusicalKMCommand(this);
		commands.add(command);
		addMenuItem(command, submenu);
		addButton(
			command,
			buttonPanel,
			gbl,
			8,
			12,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.NORTHEAST);

		menu.add(submenu);

		menubar.add(menu);

		/*
		  Options menu
		*/
		menu = new JMenu(Messages.getString("KurzFiler.OptionsMenu")); //$NON-NLS-1$
		menu.setMnemonic(new String(Messages.getString("KurzFiler.OptionsMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new FillmodeCommand(this);
		addMenuItem(command, menu);
		//		addButton (command, contentPane, gbl, 8, 8, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTHEAST);

		menu.addSeparator();
		
		command = new LookAndFeelCommand(this);
		addMenuItem(command, menu);

		submenu = new JMenu(Messages.getString("KurzFiler.LanguageMenu"));
		submenu.setMnemonic(new String(Messages.getString("KurzFiler.LanguageMenuMnem")).charAt(0)); //$NON-NLS-1$

		command = new SetLangDECommand(this);
		addMenuItem(command, submenu);

		command = new SetLangENCommand(this);
		addMenuItem(command, submenu);

		command = new SetLangESCommand(this);
		addMenuItem(command, submenu);

		command = new SetLangFRCommand(this);
		addMenuItem(command, submenu);

		command = new SetLangITCommand(this);
		addMenuItem(command, submenu);

		command = new SetLangJACommand(this);
		addMenuItem(command, submenu);

		command = new SetLangNLCommand(this);
		addMenuItem(command, submenu);

		command = new SetLangPLCommand(this);
		addMenuItem(command, submenu);

		menu.add(submenu);

		menubar.add(menu);

		//		menubar.add(Box.createHorizontalGlue());

		/*
		  About menu
		*/

		menu = new JMenu(Messages.getString("KurzFiler.HelpMenu")); //$NON-NLS-1$
		//		menu.setMnemonic('h');

		command = new InfoCommand(this);
		addMenuItem(command, menu);

		menubar.add(menu);

		setJMenuBar(menubar);
		
		///enable drop actions
		DropZone dropZone= new DropZone();
		dropZone.setTransferHandler(new FileNameTransferHandler(this));
		setGlassPane(dropZone);
		dropZone.setVisible(true);

		pack();

		//Window-Listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				exitCmd.Execute();
			}
		});
		updateButtons();
	}

	private void initColumnSizes(JTable table, KFile model) {
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		TableCellRenderer renderer;
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			renderer = table.getTableHeader().getDefaultRenderer();
			comp =
				renderer.getTableCellRendererComponent(
					null,
					column.getHeaderValue(),
					false,
					false,
					0,
					0);
			headerWidth = comp.getPreferredSize().width;
			renderer = table.getDefaultRenderer(model.getColumnClass(i));
			comp =
				renderer.getTableCellRendererComponent(
					table,
					model.getLongestColumnValue(i),
					false,
					false,
					0,
					i);
			cellWidth = comp.getPreferredSize().width;
			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	private void addMenuItem(KCommand command, JMenu menu) {
		JMenuItem mi = new JMenuItem(command);
		if (command.getShortCut() != 0) {
			mi.setAccelerator(command.getKeyStroke());
			mi.setIcon(null);
		}
		menu.add(mi);
	}

	private void addButton(
		KCommand command,
		Container contentPane,
		GridBagLayout gbl,
		int x,
		int y,
		int fill,
		int anchor) {
		JButton button = new JButton(command);
		button.setIcon(null);
		button.setMnemonic(0);
		GridBagConstraints gbc = makeGridBagConstraints(x, y, 1, 1);
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbl.setConstraints(button, gbc);
		contentPane.add(button);
	}

	private GridBagConstraints makeGridBagConstraints(int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(1, 1, 1, 1);
		return gbc;
	}

	public void updateButtons() {
		Iterator<KCommand> i = commands.iterator();
		while (i.hasNext())
			 i.next().update();
	}

	public boolean nameNewObjects(KKeymap kk, KProgram kp) {
		NameNewObjectsDialog namedia = new NameNewObjectsDialog(KurzFiler.this);
		namedia.setKeymapName(kk.getName());
		namedia.setProgramName(kp.getName());
		namedia.setVisible(true);
		if (namedia.getResult()) {
			try {
				kk.setName(namedia.getKeymapName());
				kp.setName(namedia.getProgramName());
			} catch (Exception e) {
				MessageDialog dia = new MessageDialog(KurzFiler.this);
				dia.setMessage(
					Messages.getString("KurzFiler.The_name_you_typed_in_was_too_long_and_has_been_truncated")); //$NON-NLS-1$
				dia.setVisible(true);
			}
			return true;
		}
		return false;
	}

	/// For synchronizing Tests only!
	public void loadCompletedNotification() {}
	/// For synchronizing Tests only!
	public void loadStartedNotification() {}

	/**
	 * @param object_table The object_table (V in MVC) to set.
	 */
	protected void setObjectTable(JTable o) {
		this.objectTable = o;
		this.objectTable.setDragEnabled(true);
		this.objectTable.setTransferHandler(new FileNameTransferHandler(this));;
	}

	/**
	 * @return the object_table (V in MVC)
	 */
	public JTable getObjectTable() {
		return objectTable;
	}

	/**
	 * @param fileObject The fileObject (M in MVC) to set.
	 */
	protected void setFileObject(KFile fileObject) {
		this.fileObject = fileObject;
	}

	/**
	 * @return the fileObject (M in MVC)
	 */
	public KFile getFileObject() {
		return fileObject;
	}

	/**
	 * @param chooser The chooser to set.
	 */
	protected void setFileChooser(JFileChooser chooser) {
		this.chooser = chooser;
	}

	/**
	 * @return the chooser.
	 */
	public JFileChooser getFileChooser() {
		if (chooser==null) {
			chooser = new JFileChooser();
		}
		return chooser;
	}

	/**
	 * @param selCompactableKeymapNum The selCompactableKeymapNum to set.
	 */
	protected void setSelCompactableKeymapNum(int selCompactableKeymapNum) {
		this.selCompactableKeymapNum = selCompactableKeymapNum;
	}

	/**
	 * @return Returns the selCompactableKeymapNum.
	 */
	public int getSelCompactableKeymapNum() {
		return selCompactableKeymapNum;
	}

	/**
	 * @param selKeymapNum The selKeymapNum to set.
	 */
	protected void setSelKeymapNum(int selKeymapNum) {
		this.selKeymapNum = selKeymapNum;
	}

	/**
	 * @return Returns the selKeymapNum.
	 */
	public int getSelKeymapNum() {
		return selKeymapNum;
	}

	/**
	 * @param selMonoRootSampleNum The selMonoRootSampleNum to set.
	 */
	protected void setSelMonoRootSampleNum(int selMonoRootSampleNum) {
		this.selMonoRootSampleNum = selMonoRootSampleNum;
	}

	/**
	 * @return Returns the selMonoRootSampleNum.
	 */
	public int getSelMonoRootSampleNum() {
		return selMonoRootSampleNum;
	}

	/**
	 * @param selNum The selNum to set.
	 */
	protected void setSelNum(int selNum) {
		this.selNum = selNum;
	}

	/**
	 * @return Returns the selNum.
	 */
	public int getSelNum() {
		return selNum;
	}

	/**
	 * @param selProgramNum The selProgramNum to set.
	 */
	protected void setSelProgramNum(int selProgramNum) {
		this.selProgramNum = selProgramNum;
	}

	/**
	 * @return Returns the selProgramNum.
	 */
	public int getSelProgramNum() {
		return selProgramNum;
	}

	/**
	 * @param selSampleNum The selSampleNum to set.
	 */
	protected void setSelSampleNum(int selSampleNum) {
		this.selSampleNum = selSampleNum;
	}

	/**
	 * @return Returns the selSampleNum.
	 */
	public int getSelSampleNum() {
		return selSampleNum;
	}

	/**
	 * @param saveCmd The saveCmd to set.
	 */
	protected void setSaveCmd(KCommand saveCmd) {
		this.saveCmd = saveCmd;
	}

	/**
	 * @return Returns the saveCmd.
	 */
	public KCommand getSaveCmd() {
		return saveCmd;
	}

	/**
	 * @param saveasCmd The saveasCmd to set.
	 */
	protected void setSaveasCmd(KCommand saveasCmd) {
		this.saveasCmd = saveasCmd;
	}

	/**
	 * @return Returns the saveasCmd.
	 */
	public KCommand getSaveasCmd() {
		return saveasCmd;
	}

	public static final Locale NIEDERLANDE = new Locale("nl");
	public static final Locale SPANIEN = new Locale("es");
	public static final Locale ITALIEN = new Locale("it");
	public static final Locale POLEN = new Locale("pl");

	public Action CreateMRUListAction(MRUList list, int i) {
		return new MruLoaderAction(this, list, i);
	}

	protected void setMruList(MRUList mruList) {
		this.mruList = mruList;
	}

	public MRUList getMruList() {
		return mruList;
	}

	public void clearSel() {

		getObjectTable().clearSelection();

		setSelSampleNum(0);
		setSelMonoRootSampleNum(0);
		setSelKeymapNum(0);
		setSelCompactableKeymapNum(0);
		setSelProgramNum(0);
		setSelNum(0);

		updateButtons();
	}

	protected void setNewCmd(KCommand newCmd) {
		this.newCmd = newCmd;
	}

	public KCommand getNewCmd() {
		return newCmd;
	}

	private class SelectionChanged implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			setSelSampleNum(0);
			setSelMonoRootSampleNum(0);
			setSelKeymapNum(0);
			setSelCompactableKeymapNum(0);
			setSelProgramNum(0);
			setSelNum(0);
			int[] selection = getObjectTable().getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				switch (getFileObject().getObjectType(selection[i])) {
					case KHash.T_SAMPLE :
						setSelSampleNum(getSelSampleNum() + 1);
						if (!((KSample) getFileObject().getKObjectAt(selection[i]))
							.isMultiRoot())
							setSelMonoRootSampleNum(getSelMonoRootSampleNum() + 1);
						break;
					case KHash.T_KEYMAP :
						setSelKeymapNum(getSelKeymapNum() + 1);
						if (((KKeymap) getFileObject().getKObjectAt(selection[i]))
							.isCompactable())
							setSelCompactableKeymapNum(getSelCompactableKeymapNum() + 1);
						break;
					case KHash.T_PROGRAM :
						setSelProgramNum(getSelProgramNum() + 1);
						break;
				}
				setSelNum(getSelNum() + 1);
			}
			updateButtons();
		}
	}
}
