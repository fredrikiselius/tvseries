package tvseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
    private final static int BYTE_SIZE = 1024;
    private final static  String INPUT_ZIP_FILE = "showdata/%s/en.zip";
    private final static  String OUTPUT_FOLDER = "showdata/%s/";


    public static void unZipIt(int tvDbId) {
	String outputFolder = String.format(OUTPUT_FOLDER, tvDbId);
	String zipFile = String.format(INPUT_ZIP_FILE, tvDbId);
    	byte[] buffer = new byte[BYTE_SIZE];
    	try {
    	    File folder = new File(outputFolder);
	    if (!folder.exists()) {
		folder.mkdir();
	    }

	    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    ZipEntry ze = zis.getNextEntry();

	    while (ze != null) {
		String fileName = ze.getName();
		File newFile = new File(outputFolder + File.separator + fileName);

		new File(newFile.getParent()).mkdirs();

		FileOutputStream fos = new FileOutputStream(newFile);

		int len;
		while ((len = zis.read(buffer)) > 0) {
		    fos.write(buffer, 0, len);
		}

		fos.close();
		ze = zis.getNextEntry();
	    }
	    zis.closeEntry();
	    zis.close();

    	} catch (IOException e) {
	    e.printStackTrace();
	}
        }
}
