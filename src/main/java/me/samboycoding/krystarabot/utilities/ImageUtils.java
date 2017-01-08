package me.samboycoding.krystarabot.utilities;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Various utilities for working with images
 *
 * @author Sam
 */
public class ImageUtils
{

    /**
     * Joins two images together, one next to another
     *
     * @param left A file representing the image to put on the left
     * @param right A file representing the image to put on the right
     * @return The stitched image
     * @throws IOException If one of the images cannot be read
     */
    public static BufferedImage joinHorizontal(File left, File right) throws IOException
    {
        final BufferedImage leftImage = ImageIO.read(left); //Read in the left image
        final BufferedImage rightImage = ImageIO.read(right); //And the right

        //Get properties
        final int width = leftImage.getWidth();
        final int height = leftImage.getHeight();

        //New image twice as wide as the left hand (note: not left width + right width, but 2 x left width)
        final BufferedImage result = new BufferedImage(2 * width, height, BufferedImage.TYPE_INT_ARGB);

        //Create a drawer
        Graphics2D drawer = result.createGraphics();
        //Fill everything in transparent.
        drawer.setComposite(AlphaComposite.Clear);
        drawer.fillRect(0, 0, 2 * width, height);

        drawer.setComposite(AlphaComposite.Src);
        drawer.drawImage(leftImage, 0, 0, null); //Draw the left image
        drawer.drawImage(rightImage, width, 0, null); //Draw the right image

        return result;
    }

    /**
     * Joins two images together, one on top of another
     *
     * @param top A file representing the image to put on the top
     * @param bottom A file representing the image to put on the bottom
     * @return The stitched image
     * @throws IOException if one of the images cannot be read
     */
    public static BufferedImage joinVertical(File top, File bottom) throws IOException
    {
        final BufferedImage topImage = ImageIO.read(top);
        final BufferedImage bottomImage = ImageIO.read(bottom);

        final int width = topImage.getWidth();
        final int height = topImage.getHeight();

        final BufferedImage result = new BufferedImage(width, 2 * height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D drawer = result.createGraphics();
        drawer.setComposite(AlphaComposite.Clear);
        drawer.fillRect(0, 0, width, 2 * height);
        drawer.setComposite(AlphaComposite.Src);
        drawer.drawImage(topImage, 0, 0, null);
        drawer.drawImage(bottomImage, 0, height, null);

        return result;
    }

    /**
     * Scales an image. Value examples: 1 = keep the same, 0.5 = halve, 2 =
     * double
     *
     * @param scaleX How much to scale the x-axis by
     * @param scaleY How much to scale the y-axis by
     * @param before The original image
     * @return The resulting image
     */
    public static BufferedImage scaleImage(float scaleX, float scaleY, BufferedImage before)
    {
        //New, blank image, of the new dimensions
        BufferedImage after = new BufferedImage(Math.round(before.getWidth() * scaleX), Math.round(before.getHeight() * scaleY), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleX, scaleY); //Scale it by the scale parameters
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR); //Set bilinear mode
        after = scaleOp.filter(before, after); //Scale from old -> new

        return after;
    }

    /**
     * Writes a BufferedImage to a file.
     *
     * @param image The image to write
     * @param imageType The type of the image, "png" if the image is got via
     * {@link #joinHorizontal(java.io.File, java.io.File)} or
     * {@link #joinVertical(java.io.File, java.io.File)}
     * @param where A file representing the location to write to
     * @throws IOException If the image cannot be written, or the file is
     * inaccessible
     */
    public static void writeImageToFile(BufferedImage image, String imageType, File where) throws IOException
    {
        ImageIO.write(image, imageType, where); //Yup.
    }
}
