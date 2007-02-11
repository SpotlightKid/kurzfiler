package dialogs.runtimemsg;

public class DisplayFactory {
	static protected MsgDisplay curDisplay = new GuiDisplay();
	
	
	public static MsgDisplay GetDisplay() {
		return curDisplay;
	}
	
	public enum mode{gui, console};
	
	public static void SetDisplayMode(mode m) {
		switch (m) {
		case gui: curDisplay = new GuiDisplay();
			break;
		case console: curDisplay = new ConsoleDisplay();
			break;
		}
	}
}
