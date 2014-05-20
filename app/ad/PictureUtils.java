package ad;

import org.apache.commons.codec.binary.Base64;
import play.Logger;
import play.Play;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert base64 images to files.
 * Needs clean up. Support should be added for: optional subfolder, extension passed in;
 * since imageData doesn't need to have the data:image/.. prefix.
 */
public class PictureUtils {


    private final String DEFAULT_EXTENSION = "jpg";
    private final String BASE64_PATTERN = "data:image/(.+);base64,";
    private String extension;
    private String imgData;
    private String fileName;
    private String relativePath;
    private String fullPath;

    public PictureUtils(String imageData, String subFolder) {
        Base64Parser parser = new Base64Parser(imageData);
        extension = parser.getExtensionAndStrip();
        imgData = parser.getStripped();

        assertSubFolder(subFolder);
        assertUniqueFile(subFolder);
    }

    public PictureUtils savePicture() {
        File file = createFile();

        fromBase64ToImage(file);
        return this;
    }

    private File createFile() {
        try {
            File file = new File(fullPath);
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("File could not be created at " + file.getAbsolutePath());
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertUniqueFile(String subFolder) {
        String currentName = UUID.randomUUID().toString();

        // create a unique file name, this loop will has a one in a ~20 trillion chance to not break at once...
        File file;
        while (true) {
            fileName = formatExtension(currentName, extension);
            fullPath = formatPath(fileName, subFolder);
            file = new File(fullPath);
            if (!file.exists()) {
                break;
            }
            currentName = UUID.randomUUID().toString();
        }
        relativePath = join(subFolder, fileName);
    }

    private void assertSubFolder(String subFolder) {
        String uploadPath = Play.application().configuration().getString("upload.path");
        File f = new File(uploadPath, subFolder);
        if (!f.exists()) {
            Logger.debug("{} does not exist, creating it.", f.getAbsolutePath());
            f.mkdir();
        }
    }

    public File fromBase64ToImage(File file) {
        try (FileOutputStream imageOutFile = new FileOutputStream(file)) {
            byte[] imgBytes = Base64.decodeBase64(imgData);

            imageOutFile.write(imgBytes);
            imageOutFile.close();

            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExtension() {
        return extension;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public static String join(String parent, String child) {
        File f = new File(parent, child);
        return f.getPath();
    }

    public static String formatPath(String fileName, String subFolder) {
        return formatPath(join(subFolder, fileName));
    }

    public static String formatPath(String fileName) {
        String uploadPath = Play.application().configuration().getString("upload.path");
        return String.format("%s%s", uploadPath, fileName);
    }

    public static String formatExtension(String s, String extension) {
        return String.format("%s.%s", s, extension);
    }

    private class Base64Parser {

        private String imgData;
        private String extension;
        private Pattern pattern = Pattern.compile(BASE64_PATTERN);
        private boolean stripped = false;

        public Base64Parser(String imgData) {
            this.imgData = imgData;
        }

        public String getExtensionAndStrip() {
            if (stripped) {
                return extension;
            }

            String extension = DEFAULT_EXTENSION;

            Matcher m = pattern.matcher(imgData);

            if (m.find()) {
                extension = m.group(1);
                imgData = m.replaceFirst("");
            }
            stripped = true;
            return extension;
        }

        public String getStripped() {
            if (!stripped) {
                extension = getExtensionAndStrip();
            }
            return imgData;
        }

    }

}
