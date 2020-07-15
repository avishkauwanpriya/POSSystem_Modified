package controller;

import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.SearchOrderTM;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class SearchOrderFormController {
    public JFXTextField txtSearchOrder;
    public AnchorPane searchOrderForm;
    public TableView<SearchOrderTM> tblSearchOrder;

    public void initialize(){
        ArrayList<SearchOrderTM> orderDetails=new ArrayList<>();
        ArrayList<String> customerArrayList=new ArrayList<>();

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT customerName FROM customer INNER JOIN `order` O ON customer.customerId=O.customerId");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String customer = resultSet.getString(1);
                customerArrayList.add(customer);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }




       try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `order`");
            ResultSet resultSet = preparedStatement.executeQuery();
            int i=0;
            while (resultSet.next()){
                String orderId = resultSet.getString(1);
                String orderDate = resultSet.getString(2);
                String customerId = resultSet.getString(3);
                String total = resultSet.getString(4);
                orderDetails.add(new SearchOrderTM(orderId,orderDate,customerId,customerArrayList.get(i),Double.parseDouble(total)));
                i++;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        tblSearchOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblSearchOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblSearchOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tblSearchOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblSearchOrder.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        txtSearchOrder.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue+"-"+oldValue);
                tblSearchOrder.getItems().clear();
                for(SearchOrderTM searchOrderTM:orderDetails){
                    if(searchOrderTM.getOrderId().contains(newValue)||
                            searchOrderTM.getCustomerName().contains(newValue)||
                            searchOrderTM.getCustomerId().contains(newValue)||
                            searchOrderTM.getOrderDate().contains(newValue)

                    ){
                        tblSearchOrder.getItems().add(searchOrderTM);
                        tblSearchOrder.refresh();

                    }



                }

            }
        });

        /*ObservableList<SearchOrderTM> searchOrderObList = FXCollections.observableList(orderDetails);
        tblSearchOrder.setItems(searchOrderObList);
        tblSearchOrder.refresh();*/





    }

    public void imgHomeOnClick(MouseEvent mouseEvent) {

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene newScene=new Scene(root);
            Stage mainStage = (Stage) searchOrderForm.getScene().getWindow();
            mainStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
