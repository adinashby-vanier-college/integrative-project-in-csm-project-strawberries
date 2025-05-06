package edu.vanier;

import edu.vanier.strawberries.ui.MainApp;

/**
 * The Launcher class serves as the entry point to the application.
 * Currently, it launches the {@link MainApp} class's main method to start the
 * application.
 */
public class Launcher {

    /**
     * The entry point of the application that invokes the
     * {@link MainApp#main(String[])} method to start the FX main application.
     *
     * @param args Command-line arguments passed to the application. These are
     * forwarded to {@link MainApp#main(String[])}.
     */
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
