package dialogs.runtimemsg;

public class ConsoleDisplay implements MsgDisplay {

	public void ShowErrorMessage(String msg, String caption) {
		System.err.println(msg);
	}
}
