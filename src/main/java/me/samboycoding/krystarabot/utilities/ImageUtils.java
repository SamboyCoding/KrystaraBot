package me.samboycoding.krystarabot.utilities;

import java.awt.Graphics2D;
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
    public BufferedImage joinHorizontal(File left, File right) throws IOException
    {
        final BufferedImage leftImage = ImageIO.read(left);
        final BufferedImage rightImage = ImageIO.read(right);

        final int width = leftImage.getWidth();
        final int height = leftImage.getHeight();

        final BufferedImage result = new BufferedImage(2 * width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D drawer = result.createGraphics();
        drawer.drawImage(leftImage, 0, 0, null);
        drawer.drawImage(rightImage, width, 0, null);

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
    public BufferedImage joinVertical(File top, File bottom) throws IOException
    {
        final BufferedImage topImage = ImageIO.read(top);
        final BufferedImage bottomImage = ImageIO.read(bottom);

        final int width = topImage.getWidth();
        final int height = topImage.getHeight();

        final BufferedImage result = new BufferedImage(width, 2 * height, BufferedImage.TYPE_INT_RGB);

        Graphics2D drawer = result.createGraphics();
        drawer.drawImage(topImage, 0, 0, null);
        drawer.drawImage(bottomImage, 0, height, null);

        return result;
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
    public void writeImageToFile(BufferedImage image, String imageType, File where) throws IOException
    {
        ImageIO.write(image, imageType, where);
    }
}
