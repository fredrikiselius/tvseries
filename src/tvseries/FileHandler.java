package tvseries;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is used to handle the files used by the application.
 * It can download files, delete directories and so on.
 */
public class FileHandler {
    private static final String ZIP_URL = "http://thetvdb.com/api/6A988698B3E59C3C/series/%d/all/en.zip";

    private static final int BUFFER_SIZE = 4096;
    public static final String DOWNLOAD_FOLDER = "showdata/";
    public static final String DOWNLOAD_FOLDER_IMG = "showdata/";

    public static void deleteShowDir(int tvDbId) {
        deleteDirectory(new File("showdata/" + tvDbId + "/"));
    }

    /**
     * Deletes the specified directory and all of its content
     * @param directory The directory to be deleted
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        directory.delete();
    }

    /**
     * Makes sure that the showdata folder exists, otherwise it creates it.
     */
    public static void checkShowDataFolder() {
        File showDataFolder = new File(DOWNLOAD_FOLDER);

        if (!showDataFolder.exists()) {
            showDataFolder.mkdir();
        }
    }

    /**
     * Downloads the poster for the specified tvdb id
     * @param posterPath part of the path to the poster on the tvdb
     * @param tvDbId     The tvdb id
     */
    public static void fetchPoster(String posterPath, int tvDbId) {
        String posterUrl = "http://thetvdb.com/banners/_cache/" + posterPath;
        try {
            downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG + tvDbId + "/", "poster");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the fanart for the specified tvdb id
     * @param fanartPath Part of the path to the fanart
     * @param tvDbId     The tvdb id
     */
    public static void fetchFanart(String fanartPath, int tvDbId) {
        String posterUrl = "http://thetvdb.com/banners/" + fanartPath;
        try {
            downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG + tvDbId + "/", "fanart");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the zipfile containing the series information and unpacks it.
     * @param tvDbId The tvdb id for the series
     * @throws IOException
     */
    public static void fetchZip(int tvDbId) throws IOException {
        String fileUrl = String.format(ZIP_URL, tvDbId);
        downloadFile(fileUrl, DOWNLOAD_FOLDER + tvDbId, null);
        UnZip.unZipIt(tvDbId);

    }

    /**
     * Downloads a file from the specified url
     * @param fileURL The url for the file
     * @param dlFolder The folder where the file is saved
     * @param newName New name for the file, can be null
     * @throws IOException
     */
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
