import database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ArrayList<String> logIn=new ArrayList<>();
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String userName = resultSet.getString(1);
                //String password = resultSet.getString(2);
                logIn.add(userName);

            }
            if(logIn.size()==0){
                try {
                    Parent root=FXMLLoader.load(this.getClass().getResource("/view/ActivateUsers.fxml"));
                    Scene newScene=new Scene(root);
                    primaryStage.setScene(newScene);
                    primaryStage.centerOnScreen();
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                try {
                    Parent root=FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
                    Scene newScene=new Scene(root);
                    primaryStage.setScene(newScene);
                    primaryStage.centerOnScreen();
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }




        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }
}
