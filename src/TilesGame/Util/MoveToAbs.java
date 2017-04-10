package TilesGame.Util;

import javafx.scene.Node;
import javafx.scene.shape.MoveTo;

public class MoveToAbs extends MoveTo{
    public MoveToAbs(Node node){
        super(
                node.getLayoutBounds().getWidth()/2,
                node.getLayoutBounds().getHeight()/2);
    }


}
