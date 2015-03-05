package tvseries;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// test class to download a zip
public class DownloadZip
{


    private static final int BUFFER_SIZE = 4096;
    public static final String DOWNLOAD_FOLDER = "showdata/";

    public static void downloadFile(String fileURL) throws IOException {
	System.out.println("downloading file from: " + fileURL);
	URL url = new URL(fileURL);
	File folder = new File(DOWNLOAD_FOLDER);
	if (!folder.exists()) {
	    folder.mkdir();
	}
	HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
	int responseCode = httpConnection.getResponseCode();

	if (responseCode == HttpURLConnection.HTTP_OK) {
	    String fileName = "";
	    String disposition = httpConnection.getHeaderField("Content-Disposition");
	    String contentType = httpConnection.getContentType();
	    int contentLength = httpConnection.getContentLength();

	    if (disposition != null) {
		int index = disposition.indexOf("filename=");
		if (index > 0) {
		    fileName = disposition.substring(index + 10, disposition.length() - 1);
		}
	    } else {
		fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
	    }

	    InputStream inputStream = httpConnection.getInputStream();
	    String saveFilePath = DOWNLOAD_FOLDER + File.separator + fileName;

	    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

	    int bytesRead = -1;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
		outputStream.write(buffer, 0, bytesRead);
	    }

	    outputStream.close();
	    inputStream.close();


	}
	httpConnection.disconnect();
    }
}
