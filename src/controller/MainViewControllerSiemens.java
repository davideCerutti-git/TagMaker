/**
 * Sample Skeleton for 'MainViewSiemens.fxml' Controller Class
 */

package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
//import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.List;
//import java.util.ResourceBundle;
//import java.util.HashSet;
import javafx.scene.control.ToggleGroup;
//import java.util.Set;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.PopOver;
//import org.controlsfx.control.textfield.AutoCompletionBinding;
//import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
//import javafx.concurrent.Task;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.Node;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.MenuBar;
//import javafx.scene.control.MenuButton;
//import javafx.scene.control.MenuItem;
//import javafx.scene.control.ProgressBar;
//import javafx.scene.control.TextField;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import javafx.stage.FileChooser.ExtensionFilter;
//import model.siemens.ModelSiemens;
//import settings.Settings;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.controlsfx.ControlsFXSample;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.samples.Utils;

import impl.org.controlsfx.skin.AutoCompletePopup;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.siemens.ModelSiemens;
import settings.Settings;


public class MainViewControllerSiemens extends ViewController implements Initializable {
	ModelSiemens model;

	private ValidationSupport validationSupportDb = new ValidationSupport();
	private ValidationSupport validationSupportXlsx = new ValidationSupport();

	static List<File> selectedDbFiles;
	static List<File> selectedXlsxFiles;

	Settings properties;

	private MaskerPane masker = new MaskerPane();
	
//	private AutoCompletionBinding<String> autoCompletionBinding;
//    private String[] _possibleSuggestions;// = {"ZYC1", "ZYC2", "ZMRK", "Z200", "Z300", "Z400", "Z500", "Z600", "Z700"};
//    private Set<String> possibleSuggestions;// = new HashSet<>(Arrays.asList(_possibleSuggestions));

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="buttonBack"
	private Button buttonBack; // Value injected by FXMLLoader

	@FXML // fx:id="buttonChooseDb"
	private Button buttonChooseDb; // Value injected by FXMLLoader

	@FXML // fx:id="buttonChooseXlsx"
	private Button buttonChooseXlsx; // Value injected by FXMLLoader

	@FXML // fx:id="buttonDbToXls"
	private Button buttonDbToXls; // Value injected by FXMLLoader

	@FXML // fx:id="buttonXlsxToCsv"
	private Button buttonXlsxToCsv; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewButtonDbToXls"
	private ImageView imageViewButtonDbToXls; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewButtonFileChooserDb"
	private ImageView imageViewButtonFileChooserDb; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewButtonFileChooserXls"
	private ImageView imageViewButtonFileChooserXls; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewButtonXlsToCsv"
	private ImageView imageViewButtonXlsToCsv; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewMenuItemAbout"
	private ImageView imageViewMenuItemAbout; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewMenuItemClose"
	private ImageView imageViewMenuItemClose; // Value injected by FXMLLoader

	@FXML // fx:id="imageViewMenuItemSettings"
	private ImageView imageViewMenuItemSettings; // Value injected by FXMLLoader

	@FXML // fx:id="labelLocationDb"
	private Label labelLocationDb; // Value injected by FXMLLoader

	@FXML // fx:id="labelLocationXls"
	private Label labelLocationXls; // Value injected by FXMLLoader

	@FXML // fx:id="labelPrefixDriver"
	private Label labelPrefixDriver; // Value injected by FXMLLoader

	@FXML // fx:id="mainPane"
	private BorderPane mainPane; // Value injected by FXMLLoader

	@FXML // fx:id="mainStackPane"
	private StackPane mainStackPane; // Value injected by FXMLLoader

	@FXML // fx:id="mbPrefixDriver"
	private MenuButton mbPrefixDriver; // Value injected by FXMLLoader

	@FXML // fx:id="menuBar"
	private MenuBar menuBar; // Value injected by FXMLLoader

	@FXML // fx:id="menuItemSettingsRockwell"
	private MenuItem menuItemSettingsRockwell; // Value injected by FXMLLoader

	@FXML // fx:id="textFieldChooseDb"
	private TextField textFieldChooseDb; // Value injected by FXMLLoader

	@FXML // fx:id="textFieldChooseXlsx"
	private TextField textFieldChooseXlsx; // Value injected by FXMLLoader

	@FXML // fx:id="txtFieldPrefixDriver"
	private TextField txtFieldPrefixDriver; // Value injected by FXMLLoader
	
    @FXML // fx:id="rbAbsolute"
    private RadioButton rbAbsolute; // Value injected by FXMLLoader

    @FXML // fx:id="rbSymbolic"
    private RadioButton rbSymbolic; // Value injected by FXMLLoader


	public MainViewControllerSiemens() {
		model = new ModelSiemens(null);
		properties = model.getProp();
//		ModelSiemens.logSiem.info(Arrays.asList(properties.getProperty("listOfPrefixes").split(";")));
//		_possibleSuggestions=properties.getProperty("listOfPrefixes").split(";");
//		for(String s: _possibleSuggestions) {
//			s=s.trim();
//		}
//		possibleSuggestions = new HashSet<>(Arrays.asList(_possibleSuggestions));
	}

	public void setModel(ModelSiemens modelSiemens) {
		model = modelSiemens;
		properties = model.getProp();
	}

	/**
	 * ######################## DB files
	 * ##############################################
	 **/
	@FXML
	void openChooseFileDb(ActionEvent event) {

		ValidationSupport.setRequired(textFieldChooseDb, false);
		String filePath = "";
		model.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open db File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("db Files", "*.db"));

		if (Files.exists(Paths.get(properties.getProperty("filePath"))) && !properties.getProperty("filePath").isBlank()
				&& !properties.getProperty("filePath").isEmpty() && !properties.getProperty("filePath").isBlank()) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}

		selectedDbFiles = fc.showOpenMultipleDialog(buttonDbToXls.getParent().getScene().getWindow());
		String s = "";
		if (selectedDbFiles == null)
			return;
		for (File f : selectedDbFiles) {
			if (selectedDbFiles.size() > 1)
				s = s + f.getName() + " ; ";
			else
				s = s + f.getName();

			filePath = f.getParentFile().getAbsolutePath();
		}

		properties.setProperty("filePath", filePath);

		storePropertiesOnFile();
//		ModelSiemens.logSiem.debug("Siemens settings changes saved ");

		textFieldChooseDb.setText(s);
	}

	private void storePropertiesOnFile() {
		try {
			properties.store(new FileWriter("properties/siemensImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelSiemens.logSiem.debug("IOException");
		}
	}

	@FXML
	void dbToXls(ActionEvent event) throws IOException, CloneNotSupportedException {
		if (selectedDbFiles == null) {
			ValidationSupport.setRequired(textFieldChooseDb, true);
		} else {
			ConvertDb task = new ConvertDb(event);
			setDisable(true);
			new Thread(task).start();
		}
	}

	public class ConvertDb extends Task<Void> {

		public ConvertDb(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			if (selectedDbFiles != null) {
//				ModelSiemens.logSiem.info("Apertura elenco file");
				File file = null;
				for (File f : selectedDbFiles) {
//					ModelSiemens.logSiem.info("Lettura file " + f.getName());
					if (!ModelSiemens.readDBFile(f)) {
						ModelSiemens.logSiem.error("Errore lettura file " + f.getName());
						this.cancel();
						return null;
					}
					file = f;
				}
				System.out.println("\n");
				model.generateXlsx(file.getAbsolutePath().replace(".db", ".xlsx"));
			}
			return null;
		}

		@Override
		protected void succeeded() {
//			ModelSiemens.logSiem.info("succeeded()");
			selectedDbFiles = null;
			textFieldChooseDb.setText("select a file...");
			model.clearDbList();
			setDisable(false);
//			ModelSiemens.logSiem.info("succeeded() return");
		}

		@Override
		protected void cancelled() {
			selectedDbFiles = null;
			textFieldChooseDb.setText("select a file...");
			model.clearDbList();
			setDisable(false);
			new Alert(Alert.AlertType.ERROR, "Errore converisone file .db ").showAndWait();
		}
	}

	/**
	 * ########################################################################################
	 **/

	/**
	 * ######################## XLS files
	 * ##############################################
	 **/
	@FXML
	void openChooseFileXlsx(ActionEvent event) {

		ValidationSupport.setRequired(textFieldChooseXlsx, false);
		ValidationSupport.setRequired(txtFieldPrefixDriver, false);
		
		String filePath = "";
		model.readProperties();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open xlsx File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("xlsx Files", "*.xlsx"));

		if (Files.exists(Paths.get(properties.getProperty("filePath"))) && !properties.getProperty("filePath").isBlank()
				&& !properties.getProperty("filePath").isEmpty() && !properties.getProperty("filePath").isBlank()) {
			fc.setInitialDirectory(new File(properties.getProperty("filePath")));
		} else {
			fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop\\"));
		}

		selectedXlsxFiles = fc.showOpenMultipleDialog(buttonXlsxToCsv.getParent().getScene().getWindow());
		String s = "";
		if (selectedXlsxFiles == null)
			return;
		for (File f : selectedXlsxFiles) {
			if (selectedXlsxFiles.size() > 1)
				s = s + f.getName() + " ; ";
			else
				s = s + f.getName();

			filePath = f.getParentFile().getAbsolutePath();
		}

		properties.setProperty("filePath", filePath);

		try {
			properties.store(new FileOutputStream("properties/siemensImporterSettings.cfg"), null);
		} catch (IOException e) {
			ModelSiemens.logSiem.debug("IOException");
		}
//		ModelSiemens.logSiem.debug("Siemens settings changes saved ");

		textFieldChooseXlsx.setText(s);
	}

	@FXML
	void xlsxToCsv(ActionEvent event) throws IOException, CloneNotSupportedException {
		boolean error=false;
		if (selectedXlsxFiles == null) {
			ValidationSupport.setRequired(textFieldChooseXlsx, true);
			error=true;
		} 
		
		if (txtFieldPrefixDriver.getText().isEmpty()) {
			ValidationSupport.setRequired(txtFieldPrefixDriver, true);
			error=true;
		} 
		
		
		if(!error) {
			ConvertXlsx task = new ConvertXlsx(event);
			setDisable(true);
			new Thread(task).start();
		}
	}

	public class ConvertXlsx extends Task<Void> {

		public ConvertXlsx(ActionEvent event) {
		}

		@Override
		protected Void call() throws Exception {
			if (selectedXlsxFiles != null) {
				ModelSiemens.logSiem.info("Apertura elenco file");
				File file = null;
				for (File f : selectedXlsxFiles) {
					ModelSiemens.logSiem.info("Lettura file " + f.getName());
					if (!model.readXlsxFile(f)) {
						ModelSiemens.logSiem.error("Errore lettura file " + f.getName());
						this.cancel();
						return null;
					}
					file = f;
				}
				model.getCsvGenerator().generateCSV(file.getAbsolutePath().replace(".xlsx", ".csv"),rbSymbolic.isSelected(),false);
				model.clearListEntry();
			}
			return null;
		}

		@Override
		protected void succeeded() {
			selectedXlsxFiles = null;
			textFieldChooseXlsx.setText("select a file...");
			txtFieldPrefixDriver.setText("");
			model.clearDbList();
			setDisable(false);
		}

		@Override
		protected void cancelled() {
			selectedXlsxFiles = null;
			textFieldChooseXlsx.setText("select a file...");
			txtFieldPrefixDriver.setText("");
			model.clearDbList();
			setDisable(false);
			new Alert(Alert.AlertType.ERROR, "Errore converisone file .xlsx ").showAndWait();
		}
	}
	//-----

	/**
	 * ########################################################################################
	 **/

	/**
	 * ########################################################################################
	 **/

	private void setDisable(boolean b) {

		for (Node n : mainStackPane.getChildren()) {
			n.setDisable(b);
		}

		if (b) {
			masker.setVisible(b);
			masker.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			masker.setText("Please wait...");
			mainStackPane.getChildren().add(masker);
		} else
			mainStackPane.getChildren().remove(masker);
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert buttonBack != null
				: "fx:id=\"buttonBack\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonChooseDb != null
				: "fx:id=\"buttonChooseDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonChooseXlsx != null
				: "fx:id=\"buttonChooseXlsx\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonDbToXls != null
				: "fx:id=\"buttonDbToXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert buttonXlsxToCsv != null
				: "fx:id=\"buttonXlsxToCsv\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonDbToXls != null
				: "fx:id=\"imageViewButtonDbToXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonFileChooserDb != null
				: "fx:id=\"imageViewButtonFileChooserDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonFileChooserXls != null
				: "fx:id=\"imageViewButtonFileChooserXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewButtonXlsToCsv != null
				: "fx:id=\"imageViewButtonXlsToCsv\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemAbout != null
				: "fx:id=\"imageViewMenuItemAbout\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemClose != null
				: "fx:id=\"imageViewMenuItemClose\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert imageViewMenuItemSettings != null
				: "fx:id=\"imageViewMenuItemSettings\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelLocationDb != null
				: "fx:id=\"labelLocationDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelLocationXls != null
				: "fx:id=\"labelLocationXls\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert labelPrefixDriver != null
				: "fx:id=\"labelPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mainStackPane != null
				: "fx:id=\"mainStackPane\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert mbPrefixDriver != null
				: "fx:id=\"mbPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert menuItemSettingsRockwell != null
				: "fx:id=\"menuItemSettingsRockwell\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert textFieldChooseDb != null
				: "fx:id=\"textFieldChooseDb\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert textFieldChooseXlsx != null
				: "fx:id=\"textFieldChooseXlsx\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		assert txtFieldPrefixDriver != null
				: "fx:id=\"txtFieldPrefixDriver\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
		 assert rbAbsolute != null : "fx:id=\"rbAbsolute\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
	        assert rbSymbolic != null : "fx:id=\"rbSymbolic\" was not injected: check your FXML file 'MainViewSiemens.fxml'.";
	        
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		String[] menuItemStringsList = properties.getProperty("listOfPrefixes").split(";");
		for (String s : menuItemStringsList) {
			s = s.trim();
		}
//		for (String s : menuItemStringsList) {
//			MenuItem menuItem = new MenuItem(s.trim());
//			menuItem.setOnAction(e -> {
//				mbPrefixDriver.setText(((MenuItem) e.getSource()).getText());
//			});
//			mbPrefixDriver.getItems().add(menuItem);
//		}

		validationSupportDb.registerValidator(textFieldChooseDb, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(textFieldChooseDb, false);

		validationSupportXlsx.registerValidator(textFieldChooseXlsx, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(textFieldChooseXlsx, false);
		
		validationSupportXlsx.registerValidator(txtFieldPrefixDriver, Validator.createEmptyValidator("Field required"));
		ValidationSupport.setRequired(txtFieldPrefixDriver, false);
		
		makePopOverInfo(buttonDbToXls,
				"Converte un file db (creato con TIA) in file xlsx (formato Bottero)\n"
				+ "- I file db non hanno informazione del numero DB, quindi è necessario\n"
				+ "mettere nella cartella in cui si trova i/il file .db anche il file indexDBs.cfg\n"
				+ "questo è un file di testo con il seguente contenuto (esempio):\n"
				+ "					DB_Fix write bit=185\n"
				+ "					DB_FixManComConv=189\n"
				+ "					DB_FixReadBit=181\n"
				+ "					DB_FixReadDint=183\n"
				+ "					DB_FixReadInt=182\n"
				+ "					DB_FixReadReal=184\n"
				+ "					DB_FixWrite_Real=188\n"
				+ "					DB_FixWriteDint=187\n"
				+ "					DB_FixWriteInt=186\n"
				+ "					FixReadCap=330\n"
				+ "					FixReadCap_copy=331\n"
				+ "in questo file viene indicato a sinistra dell'uguale il nome simbolico della db e a destra il numero della db");

		makePopOverInfo(buttonXlsxToCsv,
				"Converte un file xlsx (formato Bottero) in \nfile csv (formato importabile in iFix)\nPer il momento solo in assoluto!");
		
//		txtFieldPrefixDriver.setOnKeyPressed(new EventHandler<KeyEvent>() {
//	            @Override
//	            public void handle(KeyEvent ke) {
//	                switch (ke.getCode()) {
//	                case ENTER:
//	                    autoCompletionLearnWord(txtFieldPrefixDriver.getText().trim());
//	                    break;
//	                default:
//	                    break;
//	                }
//	            }
//	        });
		
		ToggleGroup tg = new ToggleGroup();
		
		rbAbsolute.setSelected(Boolean.parseBoolean(properties.getProperty("absoluteAddressSelected")));
		rbSymbolic.setSelected(!Boolean.parseBoolean(properties.getProperty("absoluteAddressSelected")));
		
		rbAbsolute.setToggleGroup(tg);
		rbSymbolic.setToggleGroup(tg);
		
		rbAbsolute.selectedProperty().addListener((observable, oldValue, newValue) -> {
			properties.setProperty("absoluteAddressSelected", ""+rbAbsolute.isSelected());
			storePropertiesOnFile();
        });
	}
	
	

	private void makePopOverInfo(Button button, String string) {
		PopOver popOver = new PopOver(createPopOverinfo(string));

		button.setOnMouseEntered(mouseEvent -> {
			// Show PopOver when mouse enters label
			popOver.show(button);
		});

		button.setOnMouseExited(mouseEvent -> {
			// Show PopOver when mouse enters label
			popOver.hide();
		});
	}

	private VBox createPopOverinfo(String str) {
		Label lblName = new Label(str);
		VBox vBox = new VBox(lblName);
		VBox.setMargin(lblName, new Insets(10.0, 10.0, 10.0, 10.0));
		return vBox;
	}

	@FXML
	void closeProgram(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void backToMainView(ActionEvent event) throws IOException {
		Node source = (Node) event.getSource();
		Stage stage = (Stage) source.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("TagMaker");
		stage.show();
	}

	@FXML
	void openSettingsSiemens(ActionEvent event) throws IOException {
//		Stage stage = new Stage();
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getResource("/fxml/SettingsRockwell.fxml"));
//		Parent root = loader.load();
//		SettingsControllerRockwell settingsRockwellController = (SettingsControllerRockwell) loader.getController();
//		settingsRockwellController.setModel(model);
//		Image icon = new Image(getClass().getResourceAsStream("/images/tagIcon48.png"));
//		stage.getIcons().add(icon);
//		stage.setTitle("settings Rockwell importer");
//		stage.setScene(new Scene(root));
//		stage.show();
	}

	private boolean isFileClosed(File file) {

		try {
			FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
			FileLock lock = channel.tryLock();
			if (lock == null) {
				return false;
			} else {
				lock.release();
				return true;
			}
		} catch (IOException e) {
			ModelSiemens.logSiem.info("File gia' aperto in un altro processo!!!!");
		}

		return false;
	}

	public String getPrefixSelected() {
//		System.out.println("tttt");
//		System.out.println("txt: "+txtFieldPrefixDriver.getText());
		return txtFieldPrefixDriver.getText();
//		return "ATTENZIONE";
	}

	public ModelSiemens getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}

//	 private void autoCompletionLearnWord(String newWord){
//	        possibleSuggestions.add(newWord);
//	        
//	        // we dispose the old binding and recreate a new binding
//	        if (autoCompletionBinding != null) {
//	            autoCompletionBinding.dispose();
//	        }
//	        autoCompletionBinding = TextFields.bindAutoCompletion(txtFieldPrefixDriver, possibleSuggestions);
//	    }
}
