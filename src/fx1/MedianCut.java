package fx1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;

public class MedianCut {
    @FXML
    Pane c1, c2, c3, c4, c5, c6, c7, c8, c9, c10;
    @FXML
    javafx.scene.control.Label l1, l2, l3, l4, l5, l6, l7, l8, l9, l10;

    public static void main(String[] args) {
        BufferedImage sampleImg = null;
        try {
            sampleImg = ImageIO.read(new File("C:\\Users\\DELL\\Desktop\\m.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        MedianCut medianCut = new MedianCut();
        List<Color> palette = medianCut.medianCutQuantize(sampleImg, 16);

        // Print the colors in the palette
        // for (Color color : palette) {
        // System.out.println("RGB: " + color.getRed() + ", " + color.getGreen() + ", "
        // + color.getBlue());
        // }
    }

    ArrayList<int[]> rgbList = new ArrayList<int[]>();
    ArrayList<int[]> rgbListSorted = new ArrayList<int[]>();
    ArrayList<int[]> noColors = new ArrayList<int[]>();

    public List<Color> medianCutQuantize(BufferedImage img, int numColors) {
        List<Color> palette = new ArrayList<>();
        List<int[]> bucket = new ArrayList<>();
        for (int r = 0; r < img.getHeight(); r++) {
            for (int c = 0; c < img.getWidth(); c++) {
                int rgb = img.getRGB(c, r);
                Color color = new Color(img.getRGB(c, r));
                int[] rgbArr = new int[]{color.getRed(), color.getGreen(), color.getBlue()};
              //  System.out.println(rgb);
                rgbList.add(rgbArr);

                int[] pixel = {rgb, r, c};
                bucket.add(pixel);
            }
        }
        int index = getMaxColorRangeIndex();
        sortRgbListByMaxRange(index);
        getTenColors(img.getHeight(), img.getWidth());
        // System.out.println(bucket);
        // // Recursively split the bucket until the desired number of colors is reached
        // splitBucket(bucket, numColors);

       // Get the representative color of each bucket and add it to the palette
//         for (int[] pixel : bucket) {
//         int rgb = pixel[0];
//       int red = (rgb >> 16) & 0xFF;
//        int green = (rgb >> 8) & 0xFF;
//        int blue = rgb & 0xFF;
//         palette.add(new Color(red, green, blue));
//         }
        return palette;
    }

    public void sortRgbListByMaxRange(int index) {
        rgbListSorted = (ArrayList<int[]>) rgbList.clone();
        rgbListSorted.sort((o1, o2) -> {
            if (o1[index] < o2[index]) {
                return -1;
            } else if (o1[index] > o2[index]) {
                return 1;
            } else {
                return 0;
            }
        });

        int size = rgbListSorted.size();

    }

    public void getTenColors(int height, int width) {
        int size = rgbListSorted.size();
        System.out.println(size);
        int step = size / 10;
         //System.out.println(rgbListSorted);
        ArrayList<int[]> tenColors = new ArrayList<int[]>();
        for (int i = 0; i < 10; i++) {
            int[] rgb = rgbListSorted.get(i * step);
            tenColors.add(rgb);
           System.out.println(rgb[0] + " " + rgb[1] + " " + rgb[2]);
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        // Iterate over the tenColors list and set pixels in the image
        for (int i = 0; i < 10; i++) {
            int[] rgb = tenColors.get(i);
            Color color = new Color(rgb[0], rgb[1], rgb[2]);
            graphics.setColor(color);
            graphics.fillRect(i * (width / 10), 0, (width / 10), height);
        }

        try {
            File output = new File("output.png"); // Set the desired output file name and
            // extension
            ImageIO.write(image, "png", output);
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMaxColorRangeIndex() {
        int[] rangeRGB = new int[3];
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;

        int minR = 255;
        int minG = 255;
        int minB = 255;
        for (int[] ints : rgbList) {
            if (ints[0] > maxR) {
                maxR = ints[0];
            }
            if (ints[1] > maxG) {
                maxG = ints[1];
            }
            if (ints[2] > maxB) {
                maxB = ints[2];
            }

            if (ints[0] < minR) {
                minR = ints[0];
            }

            if (ints[1] < minG) {
                minG = ints[1];
            }

            if (ints[2] < minB) {
                minB = ints[2];
            }
        }
        rangeRGB[0] = maxR - minR;
        rangeRGB[1] = maxG - minG;
        rangeRGB[2] = maxB - minB;
        int maxRange = 0;
        int index = 0;
        for (int i = 0; i < rangeRGB.length; i++) {
            if (rangeRGB[i] > maxRange) {
                maxRange = rangeRGB[i];
            }
        }
        for (int i = 0; i < rangeRGB.length; i++) {
            if (rangeRGB[i] == maxRange) {
                index = i;
                break;
            }
        }
        return index;

    }

    public static void splitBucket(List<int[]> bucket, int numColors) {
        if (bucket.size() <= numColors) {
            // No need to split further
            return;
        }

        // Calculate the longest color range (either R, G, or B) in the bucket
        int maxRange = Integer.MIN_VALUE;
        int splitIndex = -1;
        for (int i = 0; i < 3; i++) {
            int minColor = Integer.MAX_VALUE;
            int maxColor = Integer.MIN_VALUE;
            for (int[] pixel : bucket) {
                int rgb = pixel[0];
                int color = (rgb >> (16 - i * 8)) & 0xFF;
                minColor = Math.min(minColor, color);
                maxColor = Math.max(maxColor, color);
            }
            int range = maxColor - minColor;
            if (range > maxRange) {
                maxRange = range;
                splitIndex = i;
            }
        }

        // Sort the bucket based on the color channel with the longest range
//         bucket.sort((p1, p2) -> {
//         int rgb1 = p1[0];
//         int rgb2 = p2[0];
////         int color1 = (rgb1 >> (16 - splitIndex * 8)) & 0xFF;
////         int color2 = (rgb2 >> (16 - splitIndex * 8)) & 0xFF;
//         //return Integer.compare(color1, color2);
//         });
         //Split the bucket into two equal-sized sub-buckets
        int splitSize = bucket.size() / 2;
        //List<int[]> subBucket1 = new ArrayList<>(bucket.subList);
    }
}
