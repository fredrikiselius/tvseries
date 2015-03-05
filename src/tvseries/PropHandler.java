package tvseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropHandler
{
    final static String PROP_FILE = "config.properties";
    private static String databaseName;

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

	    prop.setProperty("databaseName", "tvseries");

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
}
