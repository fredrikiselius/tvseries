package tvseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
    List<String> fileList;
    private static final String INPUT_ZIP_FILE = "/home/freis685/tvseries/seriesdata/en.zip";
    private static final String OUTPUT_FOLDER = "/home/freis685/tvseries/seriesdata";

    public void unZipIt(String zipFile, String outputFolder) {
    	byte[] buffer = new byte[1024];

    	try {
    	    File folder = new File(OUTPUT_FOLDER);
	    if (!folder.exists()) {
		folder.mkdir();
	    }

	    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    ZipEntry ze = zis.getNextEntry();

	    while (ze != null) {
		String fileName = ze.getName();
		File newFile = new File(outputFolder + File.separator + fileName);

		System.out.println("file unzip : " + newFile.getAbsoluteFile());

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

	    System.out.println("Done");

    	} catch (IOException e) {
	    e.printStackTrace();
	}
        }
}
