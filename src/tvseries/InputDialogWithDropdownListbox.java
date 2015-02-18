package tvseries;

import javax.swing.JOptionPane;

public class InputDialogWithDropdownListbox
{
    public static void main(String[] a) {
	String[] choices = { "A", "B", "C", "D", "E", "F" };
	String input = (String) JOptionPane
		.showInputDialog(null, "Choose now...", "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
				 // default
				 // icon
				 choices, // Array of choices
				 choices[0]); // Initial choice
	System.out.println(input);
    }
}
