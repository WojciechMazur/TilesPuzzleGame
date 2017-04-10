package TilesGame.Model;

import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;

/**
 * Created by Wojciech on 06.04.2017.
 */
public class Tile extends Rectangle {
    private BufferedImage partOfImage;
    private int tileId;

    public Tile(double width, double height, BufferedImage tileImage, int id){
        super(width, height);
        this.partOfImage=tileImage;
        this.tileId=id;
    }

    public Tile(BufferedImage img, int id){
        this.partOfImage=img;
        this.tileId=id;
    }

    public BufferedImage getTileImage() {
        return partOfImage;
    }

    public void setTileImage(BufferedImage partOfImage) {
        this.partOfImage = partOfImage;
    }

    public int getTileId() {
        return tileId;
    }

    public void setTileId(int tileId) {
        this.tileId = tileId;
    }
}
