package department.gui;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class FXUtils {
	
	public static void bindTooltip(Node node, Tooltip tooltip) {
		node.setOnMouseMoved(e -> tooltip.show(node, e.getScreenX(), e.getScreenY() + 15));
		node.setOnMouseExited(e -> tooltip.hide());
	}
}
