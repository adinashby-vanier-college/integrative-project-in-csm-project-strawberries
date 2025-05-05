package edu.vanier.strawberries.ui;

import edu.vanier.strawberries.controllers.*;
import edu.vanier.strawberries.helpers.FxUIHelper;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a JavaFX project template to be used for creating GUI applications.
 * The JavaFX GUI framework (version: 22.0.2) is linked to this project in the
 * build.gradle file.
 * @link: <a href="https://openjfx.io/javadoc/22/">https://openjfx.io/javadoc/22/</a>
 * @see: /Build Scripts/build.gradle
 * @author frostybee.
 */
public class MainApp extends Application {

    public static String recentProject;
    public static String loggedInUsername;
    // The FXML file name of the primary scene.
    public static final String MAINAPP_SCENE = "MainApp_layout";
    // The FXML file name of the secondary scene.
    public static final String START_SCENE = "StartScreen_layout";
    // login scene
    public static final String LOGIN_SCENE = "SignonScreen_layout";
    // sign up scene
    public static final String SIGNUP_SCENE = "SignupScreen_layout";
    private final static Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static Scene scene;
    private static SceneController sceneController;
    public static Stage stage;
    public static MainAppFXMLController mainAppFXMLController;
    public static StartScreenFXMLController startScreenFXMLController;
    public static SignOnLogInController signOnLogInController;
    public static SignUpController signUpController;

    public static String currentController;
    public static AnimationTimer timer;

    @Override
    public void stop() {
        timer.stop();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try {
            logger.info("Bootstrapping the application...");
            // Load the scene of the primary stage.
            startScreenFXMLController = new StartScreenFXMLController();
            Parent root = FxUIHelper.loadFXML(START_SCENE, startScreenFXMLController);
            scene = new Scene(root, 640, 480);
            // Add the primary scene to the scene-switching controller.
            sceneController = new SceneController(scene);
            sceneController.addScene(START_SCENE, root);
            primaryStage.setMinWidth(650);
            primaryStage.setMinHeight(500);
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
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(Objects.equals(currentController, "mainController")){
                        mainAppFXMLController.update();

                }
            }
        };

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
            switch (fxmlFileName) {
                case START_SCENE -> {
                    // No need to register the start scene as it was already done in the start method.
                    sceneController.activateScene(fxmlFileName);
                    currentController = "startController";
                    stage.setHeight(500);
                    stage.setWidth(500);
                }
                case MAINAPP_SCENE -> {
                    if (!sceneController.sceneExists(fxmlFileName)) {
                        // Instantiate the corresponding FXML controller if the
                        // specified scene is being loaded for the first time.
                        mainAppFXMLController = new MainAppFXMLController();
                        Parent root = FxUIHelper.loadFXML(fxmlFileName, mainAppFXMLController);
                        sceneController.addScene(MAINAPP_SCENE, root);
                    }
                    // The scene has been previously added, we activate it.
                    sceneController.activateScene(fxmlFileName);
                    currentController = "mainController";
                    stage.setHeight(550);
                    stage.setWidth(860);
                    timer.start();
                }
                case LOGIN_SCENE -> {
                    if (!sceneController.sceneExists(fxmlFileName)) {
                        SignOnLogInController loginController = new SignOnLogInController();
                        Parent root = FxUIHelper.loadFXML(fxmlFileName, loginController);
                        sceneController.addScene(LOGIN_SCENE, root);
                    }
                    sceneController.activateScene(fxmlFileName);
                    currentController = "loginController";
                }
                case SIGNUP_SCENE -> {
                    if (!sceneController.sceneExists(fxmlFileName)) {
                        SignUpController signupController = new SignUpController();
                        Parent root = FxUIHelper.loadFXML(fxmlFileName, signupController);
                        sceneController.addScene(SIGNUP_SCENE, root);
                    }
                    sceneController.activateScene(fxmlFileName);
                    currentController = "signupController";
                }
            }
            //You can register or activate additional scenes here, based on the logic used to add the secondary scene (as shown above).
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
