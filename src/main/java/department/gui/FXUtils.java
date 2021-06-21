package department.gui;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class FXUtils {
	
	public static void bindTooltip(Node node, Tooltip tooltip) {
		node.setOnMouseMoved(e -> tooltip.show(node, e.getScreenX(), e.getScreenY() + 15));
		node.setOnMouseExited(e -> tooltip.hide());
	}
	
	public static boolean isFloat(String src) {
		try {
			Float.parseFloat(src);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static float getFloat(String src, float defaultValue) {
		try {
			float value = Float.parseFloat(src);
			return value;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
