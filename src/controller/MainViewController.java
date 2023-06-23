/**
 * Sample Skeleton for 'MainView.fxml' Controller Class
 */

package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.rockwell.ModelRockwell;
import model.siemens.ModelSiemens;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class MainViewController implements Initializable{

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="openRockwellBtn"
    private Button openRockwellBtn; // Value injected by FXMLLoader

    @FXML // fx:id="openSiemensBtn"
    private Button openSiemensBtn; // Value injected by FXMLLoader

    @FXML // fx:id="imageRockwellButton"
    private ImageView imageRockwellButton; // Value injected by FXMLLoader
    
    @FXML // fx:id="imageSiemensButton"
    private ImageView imageSiemensButton; // Value injected by FXMLLoader

	private Stage stage;

    @FXML
    void openRockwellWindow(ActionEvent event) throws IOException {
    	Node source=(Node)event.getSource();
    	Stage stage= (Stage)source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainViewRockwell.fxml"));
		Parent root = loader.load();
		ModelRockwell modelRockwell=new ModelRockwell(stage);
		MainViewControllerRockwell mainController = (MainViewControllerRockwell) loader.getController();
		mainController.setModel(modelRockwell);
		modelRockwell.setController(mainController);
		Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
		stage.getIcons().add(icon);
        stage.setTitle("TagMaker - Rockwell");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void openSiemensWindow(ActionEvent event) throws IOException {
     	Stage stage = new Stage();
//     	ModelSiemens modelSiemens=new ModelSiemens(stage);
    	FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainViewSiemens.fxml"));
		Parent root = loader.load();
       
        MainViewControllerSiemens mainController = (MainViewControllerSiemens) loader.getController();
//        mainController.setModel(modelSiemens);
        mainController.getModel().setController(mainController);
		Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
		stage.getIcons().add(icon);
        stage.setTitle("TagMaker - Siemens");
        stage.setScene(new Scene(root));
        stage.show();
        ((Node)(event.getSource())).getScene().getWindow().hide();
//        new Alert(Alert.AlertType.ERROR, "Not yet implemented!").showAndWait();
        
       }
    
   

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	 assert openRockwellBtn != null : "fx:id=\"openRockwellBtn\" was not injected: check your FXML file 'MainView.fxml'.";
         assert imageRockwellButton != null : "fx:id=\"imageRockwellButton\" was not injected: check your FXML file 'MainView.fxml'.";
         assert openSiemensBtn != null : "fx:id=\"openSiemensBtn\" was not injected: check your FXML file 'MainView.fxml'.";
         assert imageSiemensButton != null : "fx:id=\"imageSiemensButton\" was not injected: check your FXML file 'MainView.fxml'.";
   
         stage = (Stage) openRockwellBtn.getScene().getWindow();
         
       

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
           
	}
}
