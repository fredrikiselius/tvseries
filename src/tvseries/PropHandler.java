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
    private static String databaseName = null;
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



	    prop.setProperty("databaseName", getDatabaseName());
	    prop.setProperty("lastUpdate", Integer.toString(lastUpdate));

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
	if (databaseName != null) {
	    return databaseName;
	}
	return DEFAULT_DB_NAME;
    }

    public int getLastUpdate() {
	return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
	this.lastUpdate = lastUpdate;
	saveProp();
    }
}
