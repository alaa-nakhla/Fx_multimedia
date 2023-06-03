package fx1;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

public class ToIndexedImage {

    public static BufferedImage rgbaToIndexedBufferedImage(BufferedImage sourceBufferedImage) {
        BufferedImage indexedImage = new BufferedImage(sourceBufferedImage.getWidth(), sourceBufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_INDEXED);
        ColorModel cm = indexedImage.getColorModel();
        IndexColorModel icm = (IndexColorModel) cm;
        int size = icm.getMapSize();
        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];
        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);
        WritableRaster raster = indexedImage.getRaster();
        int pixel = raster.getSample(0, 0, 0);
        IndexColorModel icm2 = new IndexColorModel(8, size, reds, greens, blues, pixel);
        indexedImage = new BufferedImage(icm2, raster, sourceBufferedImage.isAlphaPremultiplied(), null);
        indexedImage.getGraphics().drawImage(sourceBufferedImage, 0, 0, null);
        return indexedImage;
    }

}
