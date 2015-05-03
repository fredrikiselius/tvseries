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
public final class FileHandler {
    private static final String ZIP_URL = "http://thetvdb.com/api/6A988698B3E59C3C/series/%d/all/en.zip";

    private static final int BUFFER_SIZE = 4096;
    private static final String DOWNLOAD_FOLDER = "showdata/";
    private static final String DOWNLOAD_FOLDER_IMG = "showdata/";

    private FileHandler() {}

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
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        // Return result not needed.
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }
        // Return result no needed.
        //noinspection ResultOfMethodCallIgnored
        directory.delete();
    }

    /**
     * Makes sure that the showdata folder exists, otherwise it creates it.
     */
    public static void checkShowDataFolder() {
        File showDataFolder = new File(DOWNLOAD_FOLDER);

        if (!showDataFolder.exists()) {
            // Return result not needed
            //noinspection ResultOfMethodCallIgnored
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
        downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG + tvDbId + "/", "poster");
    }

    /**
     * Downloads the fanart for the specified tvdb id
     * @param fanartPath Part of the path to the fanart
     * @param tvDbId     The tvdb id
     */
    public static void fetchFanart(String fanartPath, int tvDbId) {
        String posterUrl = "http://thetvdb.com/banners/" + fanartPath;
        downloadFile(posterUrl, DOWNLOAD_FOLDER_IMG + tvDbId + "/", "fanart");
    }

    /**
     * Downloads the zipfile containing the series information and unpacks it.
     * @param tvDbId The tvdb id for the series
     */
    public static void fetchZip(int tvDbId) {
        String fileUrl = String.format(ZIP_URL, tvDbId);
        downloadFile(fileUrl, DOWNLOAD_FOLDER + tvDbId, null);
        UnZip.unZipIt(tvDbId);

    }

    /**
     * Downloads a file from the specified url
     * @param fileURL The url for the file
     * @param dlFolder The folder where the file is saved
     * @param newName New name for the file, can be null
     */
    public static void downloadFile(String fileURL, String dlFolder, String newName)
    {
        System.out.println("LOG: (DownloadFile) Downloading file from: " + fileURL);

        File folder = new File(dlFolder);
        if (!folder.exists()) {
            // Return result not needed.
            //noinspection ResultOfMethodCallIgnored
            folder.mkdir();
        }

        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(fileURL);
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

                // Could not put it as "try-with-resources" since the httpConnection is declared after.
                inputStream = httpConnection.getInputStream();

                if (newName != null) {
                    fileName = newName + ".jpg";
                }

                String saveFilePath = dlFolder + File.separator + fileName;
                // Could not put it as "try-with-resources" since the full path is declard inside the try block.
                outputStream = new FileOutputStream(saveFilePath); // It is opened in front of the try block and closed in the finally.


                byte[] buffer = new byte[BUFFER_SIZE];

                int bytesRead = inputStream.read(buffer);
                while (bytesRead != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    bytesRead = inputStream.read(buffer);
                }
            }
            httpConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
