
package nmsvrscreenshotfix;

import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;

/**
 * Iterates through every image in the executable's folder and if applicable, 
 * creates a copy of it with an aspect ratio to 1:1
 * @author Noah Ortega
 */
public class NMSVRScreenshotFix {
    static int totalFiles = 0;
    static BufferedImage curImage;
    static String appendToNewFile = "_fix";
    
    /**
     * Iterates through the files in the directory, validates files before 
     * allowing resizing.
     */
    public static void main(String[] args) {
        File curFolder = new File(System.getProperty("user.dir")); //current directory
        String curFilePath;
        
        System.out.println("searching in " + curFolder.getPath());

        for (final File fileEntry : curFolder.listFiles()) {
            curFilePath = fileEntry.getPath();
            try {
                if (!fileEntry.isDirectory() && isImage(curFilePath)) {
                    totalFiles++;
                    System.out.print(totalFiles +". ");
                    System.out.println(curFilePath);

                    curImage = ImageIO.read(fileEntry);

                    if (shouldResize(curImage.getWidth(), curImage.getHeight())) {
                        squish(curFilePath);
                    }
                }
            }
            catch (IOException e){
                System.err.println(">> Caught IOException on file '" + curFilePath + "':\n  " + e.getMessage());
            }   
        }
    }

    
    /**
     * Determines if a file is a basic image type
     * @param path file path of image
     * @return true if extension is .png .jpg or .jpeg
     */
    public static boolean isImage(String path) {
        //extention of file, from file after final period
        int dotIndex = path.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "no extension" : path.substring(dotIndex + 1);
        
        return (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg"));
    }
    
    
    /**
     * Determines if an image should be resized based on criteria:
     * - width must be greater than height
     * @param width image width
     * @param height image height
     * @return true if image matches criteria
     */
    public static boolean shouldResize(int width, int height) {
        if (width == height) {
            System.out.println(">> Already 1:1 aspect ratio");
            return false;
        } else if (width < height) {
            System.out.println(">> Height is greater than width");
            return false;
        } else {
            return true;
        }
    }
    
    
    /**
     * Resizes an image to a 1:1 aspect ratio by changing the width
     * @param inputImagePath Path of the original input image
     * @throws java.io.IOException
     * 
     * based on code by Nam Ha Minh from article "How to resize images in Java"
     * https://www.codejava.net/java-se/graphics/how-to-resize-images-in-java
     */
    public static void squish(String inputImagePath) throws IOException {

        int finalWidth = curImage.getHeight();
        int height = curImage.getHeight();
        
        // creates output image
        BufferedImage outputImage = new BufferedImage(finalWidth,
        height, curImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(curImage, 0, 0, finalWidth, height, null);
        g2d.dispose();

        // extracts extension of output file
        int dotIndex = inputImagePath.lastIndexOf('.');
        String formatName = inputImagePath.substring(dotIndex + 1);
        
        String outputImagePath = generateOutputPath(inputImagePath, dotIndex);
        
        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));

        System.out.println(">> Converted");
    }
    
    /**
     * For generating the path of the output image
     * @param inputPath Path of the original input image
     * @param dotIndex the index of the dot in the input path
     * @return the final output path
     */
    public static String generateOutputPath(String inputPath, int dotIndex) {
        return inputPath.substring(0, dotIndex) + appendToNewFile + inputPath.substring(dotIndex);
    }
}
