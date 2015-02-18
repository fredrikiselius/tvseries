package tvseries;

import javax.swing.*;
import java.net.URL;



public class Tester
{
    public static void main(String[] args) {
	String searchInput = JOptionPane.showInputDialog("Enter the name of the series");
	URL search = URLHandler.searchURL("suits");
    }

}
