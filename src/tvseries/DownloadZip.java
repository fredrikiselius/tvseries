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
    public static final String DOWNLOAD_FOLDER = "/home/freis685/tvseries/seriesdata";

    public static void downloadFile(String fileURL) throws IOException {
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

	    System.out.println(disposition + "\n" + contentType);

	    if (disposition != null) {
		int index = disposition.indexOf("filename=");
		if (index > 0) {
		    fileName = disposition.substring(index + 10, disposition.length() - 1);
		}
	    } else {
		fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
	    }

	    System.out.println("Content-Type = " + contentType);
	    System.out.println("Content-Disposition = " + disposition);
	    System.out.println("Content-Length = " + contentLength);
	    System.out.println("fileName = " + fileName);

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

	    System.out.println("File downloaded");
	} else {
	    System.out.println("No file to download. Server replied HTTP code: " + responseCode);
	}
	httpConnection.disconnect();
    }


    public static void main(String[] args) throws IOException {
	String url = "http://thetvdb.com/api/6A988698B3E59C3C/series/121361/all/en.zip";
	downloadFile(url);

	UnZip unzip = new UnZip();
	unzip.unZipIt(DOWNLOAD_FOLDER + "/en.zip", DOWNLOAD_FOLDER);
    }
}
