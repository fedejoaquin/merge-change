package controllers;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelector {

    public static String showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();

        // filter for only show .csv files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSC Files (*.csv)", "csv");
        fileChooser.setFileFilter(filter);
        
        // open the selection file window
        int result = fileChooser.showSaveDialog(null);

        // verify if the user select a file
        if (result == JFileChooser.APPROVE_OPTION) {
            // obtain the path and the name of the selected file
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        
        return null;
    }
    
    public static String showFolderChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // open the selection file window
        int result = fileChooser.showDialog(null, "Select folder");

        // verify if the user select a folder
        if (result == JFileChooser.APPROVE_OPTION) {
            // obtain the path of the selected folder
            return fileChooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }
}

