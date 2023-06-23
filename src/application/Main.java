package application;


import java.awt.AWTException;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	//test
	/**#### FIELDS ##### 
	 * @throws IOException 
	 * @throws AWTException */
	static SystemTray tray;
	static TrayIcon trayIcon;
	
	@Override
	public void start(Stage primaryStage) throws IOException, AWTException {
		
		//System tray y
		trayIcon = new TrayIcon(ImageIO.read(getClass().getResourceAsStream("/images/tagIcon16.png")),"TagMaker");
		tray = SystemTray.getSystemTray();
		PopupMenu popup = new PopupMenu();
        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);             
            }
        }); 
        popup.add(close);
        trayIcon.setPopupMenu(popup);
		tray.add(trayIcon);
		
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
			primaryStage.getIcons().add(icon);
			primaryStage.setScene(scene);
			primaryStage.setTitle("TagMaker");
			primaryStage.show();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		launch(args);
		tray.remove(trayIcon);
		
	}
}
