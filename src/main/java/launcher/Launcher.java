package launcher;

import controllers.AppController;
import gui.SimpleAppView;

public class Launcher {
		
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	AppController controller = new AppController();
        		SimpleAppView windowsApp = new SimpleAppView(controller);
        		controller.registerView(windowsApp);
            }
        });
    }

}