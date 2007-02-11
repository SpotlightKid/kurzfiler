package dialogs.runtimemsg;

import javax.swing.JOptionPane;

public class GuiDisplay implements MsgDisplay {

	public void ShowErrorMessage(String msg, String caption) {
		JOptionPane.showMessageDialog(null, msg, 
			caption,
			JOptionPane.ERROR_MESSAGE);
	}
}
