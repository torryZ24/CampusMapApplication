package org.uwo.cs2212;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.uwo.cs2212.model.Layer;
import org.uwo.cs2212.model.PointOfInterest;


/**
 * This class represents an ImageLayer used to draw points of interest on the map. It extends the JavaFX Canvas class to
 * allow for drawing on the image.
 * The ImageLayer class contains a constructor method that initializes a new ImageLayer object with a given width, height,
 * zoom level and layer of points of interest. The class also contains a method that draws the points of interest on the
 * canvas, with their corresponding colors, fonts, sizes, and selected state. Additionally, the class provides a utility
 * method to check if a layer is a base layer, and to hide/show points of interest based on the layer's hideLayer flag.
 */

public class ImageLayer extends Canvas {

    /**
     * Initializes a new ImageLayer object with the given width, height, zoom level, and layer of points of interest.
     * This method is used to signify if a POI is selected by increasing the point size and making the POI's text bold
     * on the map
     *
     * @param width  The width of the canvas
     * @param height The height of the canvas
     * @param zoom   The zoom level of the canvas
     * @param layer  The layer of points of interest to draw on the canvas
     */
    public ImageLayer(double width, double height, double zoom, Layer layer) {
        super(width * zoom, height * zoom);
        GraphicsContext graphic = getGraphicsContext2D();
        Color color = Color.valueOf(layer.getColor());
        graphic.setStroke(color);
        graphic.setFill(color);
        Font normalFont = Font.font(layer.getFont(), FontWeight.NORMAL, layer.getSize());
        Font boldFont = Font.font(layer.getFont(), FontWeight.BOLD, layer.getSize() * 1.5);

        boolean isBaseLayer = ImageLayer.isBaseLayer(layer);
        boolean isHiddenLayer = layer.isHideLayer();

        for (PointOfInterest poi : layer.getPoints()) {
            if (isBaseLayer || !isHiddenLayer || poi.isSelected()) {
                if (poi.isSelected()) {
                    graphic.setFont(boldFont);
                    graphic.fillOval(poi.getX() * zoom - 8, poi.getY() * zoom - 8, 16, 16);
                    graphic.fillText(poi.getName(), poi.getX() * zoom - 16, poi.getY() * zoom - 16);
                } else {
                    graphic.setFont(normalFont);
                    graphic.fillOval(poi.getX() * zoom - 6, poi.getY() * zoom - 6, 12, 12);
                    graphic.fillText(poi.getName(), poi.getX() * zoom - 10, poi.getY() * zoom - 10);
                }
            }
        }
    }

    /**
     * Checks if the given layer is a base layer or not. A base layer is a layer that contains the base map image and is always shown.
     *
     * @param layer The layer to check
     * @return True if the layer is a base layer, false otherwise
     */
    private static boolean isBaseLayer(Layer layer) {
        return layer != null && layer.getLayerType() != null && "base".equalsIgnoreCase(layer.getLayerType().trim());
    }
}
