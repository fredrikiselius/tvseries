package tvseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * PropHandler is used to read and write to the properties file.
 */
public class PropHandler {
    private static final String PROP_FILE = "config.properties";
    private static final String DEFAULT_DB_NAME = "tvseries";
    private static String databaseName;
    private int lastUpdate = 0;

    public PropHandler() {
	File propFile = new File(PROP_FILE);
	if (propFile.exists()) {
	    loadProp();
	} else {
	    saveProp();
	    loadProp();
	}
    }

    private void saveProp() {
	Properties prop = new Properties();
	OutputStream output = null;

	try {
	    output = new FileOutputStream(PROP_FILE);

	    if (databaseName == null) {
		databaseName = DEFAULT_DB_NAME;
	    }

	    if (lastUpdate == 0) {
		lastUpdate = 0000000000;
	    }

	    prop.setProperty("databaseName", databaseName);
	    prop.setProperty("lastUpdate", lastUpdate + "");

	    prop.store(output, null);
	} catch (IOException io) {
	    io.printStackTrace();
	} finally {
	    if (output != null) {
		try {
		    output.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private void loadProp() {
	Properties prop = new Properties();
	InputStream input = null;

	try {
	    input = new FileInputStream(PROP_FILE);
	    prop.load(input);

	    databaseName = prop.getProperty("databaseName");
	    lastUpdate = Integer.parseInt(prop.getProperty("lastUpdate"));
	} catch (IOException io) {
	    io.printStackTrace();
	} finally {
	    if (input != null) {
		try {
		    input.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public static String getDatabaseName() {
	return databaseName;
    }

    public int getLastUpdate() {
	return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
	this.lastUpdate = lastUpdate;
	saveProp();
    }
}
