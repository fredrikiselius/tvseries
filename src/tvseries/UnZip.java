package tvseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * UnZip is used to unpack files.
 */
public final class UnZip
{
    private final static int BYTE_SIZE = 1024;
    private final static String INPUT_ZIP_FILE = "showdata/%s/en.zip";
    private final static String OUTPUT_FOLDER = "showdata/%s/";

    private UnZip() {}


    /**
     * Unzips the en.zip file, which needs to be downloaded from the tvdb first. The code is copied from an unknow source.
     *
     * @param tvDbId The tvdb is used to specify the path to the zipfile.
     */
    public static void unZipIt(int tvDbId) {
	String outputFolder = String.format(OUTPUT_FOLDER, tvDbId);
	String zipFile = String.format(INPUT_ZIP_FILE, tvDbId);

	FileOutputStream outputStream = null;
	try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
	    File folder = new File(outputFolder);
	    if (!folder.exists()) {
		//noinspection ResultOfMethodCallIgnored
		folder.mkdir();
	    }

	    //zis = new ZipInputStream(new FileInputStream(zipFile));
	    ZipEntry ze = zis.getNextEntry();

	    byte[] buffer = new byte[BYTE_SIZE];
	    while (ze != null) {
		String fileName = ze.getName();
		File newFile = new File(outputFolder + File.separator + fileName);

		//noinspection ResultOfMethodCallIgnored
		new File(newFile.getParent()).mkdirs();

		// Could not put the FileOutputStream as "try-with-resources" since it then becomes a final,
		// and we get the newFile name within the try block.
		// It is opened in front of the try block and closed in the finally.
		outputStream = new FileOutputStream(newFile);

		int len = zis.read(buffer);
		while (0 < len) {
		    outputStream.write(buffer, 0, len);
		    len = zis.read(buffer);
		}

		outputStream.close();
		ze = zis.getNextEntry();
	    }
	    zis.closeEntry();

	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (outputStream != null) {
		try {
		    outputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}
