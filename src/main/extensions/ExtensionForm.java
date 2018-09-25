package main.extensions;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.protocol.HMessage;
import main.protocol.HPacket;
import main.ui.GEarthController;

import java.net.URL;

/**
 * Created by Jonas on 22/09/18.
 */
public abstract class ExtensionForm extends Application {

    private Extension extension = null;
    protected static String[] args;
    private volatile Stage primaryStage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);
        setStageData(primaryStage);
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Platform.runLater(primaryStage::hide);
        });
        ExtensionForm thiss = this;

        ExtensionInfo extInfo = getClass().getAnnotation(ExtensionInfo.class);

        Thread t = new Thread(() -> {
            extension = new Extension(args) {
                @Override
                protected void init() {
                    thiss.initExtension();
                }

                @Override
                protected void onClick() {
                    thiss.onClick();
                }

                @Override
                protected void onStartConnection() {
                    thiss.onStartConnection();
                }

                @Override
                protected void onEndConnection() {
                    thiss.onEndConnection();
                }

                @Override
                ExtensionInfo getInfoAnnotations() {
                    return extInfo;
                }
            };
//            Platform.runLater(primaryStage::close);
            //when the extension has ended, close this process
            Platform.exit();
        });
        t.start();
    }

    public abstract void setStageData(Stage primaryStage) throws Exception;

    protected boolean requestFlags(Extension.FlagsCheckListener flagRequestCallback){
        return extension.requestFlags(flagRequestCallback);
    }
    protected void writeToConsole(String s) {
        extension.writeToConsole(s);
    }
    protected void intercept(HMessage.Side side, Extension.MessageListener messageListener) {
        extension.intercept(side, messageListener);
    }
    protected void intercept(HMessage.Side side, int headerId, Extension.MessageListener messageListener){
        extension.intercept(side, headerId, messageListener);
    }
    protected boolean sendToServer(HPacket packet){
        return extension.sendToServer(packet);
    }
    protected boolean sendToClient(HPacket packet){
        return extension.sendToClient(packet);
    }


    /**
     * Gets called when a connection has been established with G-Earth.
     * This does not imply a connection with Habbo is setup.
     */
    protected void initExtension(){}

    /**
     * The application got doubleclicked from the G-Earth interface. Doing something here is optional
     */
    private void onClick(){
        Platform.runLater(() -> {
            primaryStage.show();
        });
    }

    /**
     * A connection with Habbo has been started
     */
    protected void onStartConnection(){}

    /**
     * A connection with Habbo has ended
     */
    protected void onEndConnection(){}
}
