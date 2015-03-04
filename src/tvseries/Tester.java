package tvseries;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;


public class Tester {
    public static void main(String[] args) throws SQLException, IOException {
	CreateDatabase createDatabase = new CreateDatabase("tvseries");
    }
}
