package TilesGame.Util;

import javafx.scene.Node;
import javafx.scene.shape.LineTo;

public class LineToAbs extends LineTo {
    public LineToAbs(Node node, double x, double y){
        super(
                x-node.getLayoutX() + node.getLayoutBounds().getWidth()/2,
                y-node.getLayoutY() + node.getLayoutBounds().getHeight()/2
        );
    }
}
