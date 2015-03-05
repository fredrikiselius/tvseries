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
    private static final String INPUT_ZIP_FILE = "showdata/en.zip";
    private static final String OUTPUT_FOLDER = "showdata";

    public static void unZipIt() {
    	byte[] buffer = new byte[1024];
    	try {
    	    File folder = new File(OUTPUT_FOLDER);
	    if (!folder.exists()) {
		folder.mkdir();
	    }

	    ZipInputStream zis = new ZipInputStream(new FileInputStream(INPUT_ZIP_FILE));
	    ZipEntry ze = zis.getNextEntry();

	    while (ze != null) {
		String fileName = ze.getName();
		File newFile = new File(OUTPUT_FOLDER + File.separator + fileName);

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
