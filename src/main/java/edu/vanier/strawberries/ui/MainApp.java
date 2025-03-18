package edu.vanier.strawberries.ui;

import edu.vanier.strawberries.controllers.MainAppFXMLController;
import edu.vanier.strawberries.controllers.SceneController;
import edu.vanier.strawberries.controllers.StartScreenFXMLController;
import edu.vanier.strawberries.helpers.FxUIHelper;
import java.io.IOException;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JavaFX project template to be used for creating GUI applications.
 *
 * The JavaFX GUI framework (version: 22.0.2) is linked to this project in the
 * build.gradle file.
 * @link: https://openjfx.io/javadoc/22/
 * @see: /Build Scripts/build.gradle
 * @author frostybee.
 */
public class MainApp extends Application {

    // The FXML file name of the primary scene.
    public static final String MAINAPP_SCENE = "MainApp_layout";
    // The FXML file name of the secondary scene.
    public static final String START_SCENE = "StartScreen_layout";
    private final static Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static Scene scene;
    private static SceneController sceneController;
    public static Stage stage;

    @Override
    public void stop() {
        // TODO: 
        // Here, we need to perform teardown operations such as stopping running 
        // animation, etc.
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try {
            logger.info("Bootstrapping the application...");
            // Load the scene of the primary stage.
            Parent root = FxUIHelper.loadFXML(START_SCENE, new StartScreenFXMLController());
            scene = new Scene(root, 640, 480);
            // Add the primary scene to the scene-switching controller.
            sceneController = new SceneController(scene);
            sceneController.addScene(START_SCENE, root);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.setTitle("Circuit Builder!");
            // Request putting this application's main window on top of other already-opened windows upon launching the app.
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
            primaryStage.setAlwaysOnTop(false);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Switches between scenes based on the provided FXML file name. This method
     * checks the type of scene (primary or secondary) and either activates an
     * existing scene or loads the specified FXML scene for the first time and
     * adds it to the scene controller.
     *
     * @param fxmlFileName the name of the FXML file that represents the scene
     * to switch to.
     */
    public static void switchScene(String fxmlFileName) {
        try {
            if (fxmlFileName.equals(START_SCENE)) {
                // No need to register the start scene as it was already done in the start method.
                sceneController.activateScene(fxmlFileName);
            } else if (fxmlFileName.equals(MAINAPP_SCENE)) {
                if (!sceneController.sceneExists(fxmlFileName)) {
                    // Instantiate the corresponding FXML controller if the
                    // specified scene is being loaded for the first time.
                    MainAppFXMLController controller = new MainAppFXMLController();
                    Parent root = FxUIHelper.loadFXML(fxmlFileName, controller);
                    sceneController.addScene(MAINAPP_SCENE, root);
                }
                // The scene has been previously added, we activate it.
                sceneController.activateScene(fxmlFileName);
                stage.setHeight(550);
                stage.setWidth(860);
            }
            //TODO: You can register or activate additional scenes here, 
            //      based on the logic used to add the secondary scene (as shown above).            
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
