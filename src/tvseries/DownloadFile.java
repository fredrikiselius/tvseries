package tvseries;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// test class to download a zip
public class DownloadFile {
    private static final int BUFFER_SIZE = 4096;
    public static final String DOWNLOAD_FOLDER_DATA = "showdata/";
    public static final String DOWNLOAD_FOLDER_IMG = "showdata/";

    public static void deleteShowDir(String tvDbId) {
	deleteDirectory(new File("showdata/"+tvDbId+"/"));
    }

    private static void deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        directory.delete();
    }

    public static void fetchPoster(String posterName, String tvDbId) throws IOException {
	String posterUrl = "http://thetvdb.com/banners/_cache/" + posterName;
	downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG+tvDbId+"/", "poster");

    }

    public static void fetchFanart(String posterName, String tvDbId) throws IOException {
	String posterUrl = "http://thetvdb.com/banners/" + posterName;
	downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG+tvDbId+"/", "fanart");
    }

    public static void fetchZip(String tvDbId) throws IOException {
	downloadFile(URLHandler.ZipUrl(tvDbId), DOWNLOAD_FOLDER_DATA + tvDbId, null);
	UnZip.unZipIt(tvDbId);

    }

    public static void downloadFile(String fileURL, String dlFolder, String newName) throws IOException {
	System.out.println("LOG: (DownloadFile) Downloading file from: " + fileURL);
	URL url = new URL(fileURL);
	File folder = new File(dlFolder);
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

	    if (newName != null) {
		fileName = newName + ".jpg";
	    }

	    String saveFilePath = dlFolder + File.separator + fileName;

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
