package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerTM;
import model.ItemTM;
import model.OrderTM;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class PlaceOrderFormController {

    public JFXComboBox<CustomerTM> cmbCustomerId;
    public JFXComboBox<ItemTM> cmbItemCode;
    public JFXTextField txtCustomerName;
    public JFXTextField txtDescription;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtQty;
    public JFXButton btnNewOrder;
    public JFXTextField txtDate;
    public JFXTextField txtOrderId;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView <OrderTM>tblPlaceOrder;
    public JFXTextField txtNetTotal;
    public JFXButton btnPlaceOrder;
    public AnchorPane placeOrderForm;
    public void initialize(){
        setInitialize();
        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                if(cmbCustomerId.getSelectionModel().getSelectedIndex()==-1){
                    txtCustomerName.clear();

                }
                else {

                    CustomerTM selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
                    txtCustomerName.setText(selectedCustomer.getCustomerName());
                }
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                if(cmbItemCode.getSelectionModel().getSelectedIndex()==-1){
                    txtDescription.clear();
                    txtQtyOnHand.clear();
                    txtUnitPrice.clear();

                }
                else {

                    ItemTM selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
                    txtDescription.setText(selectedItem.getDescription());
                    txtQtyOnHand.setText(selectedItem.getQtyOnHand() + "");
                    txtUnitPrice.setText(selectedItem.getUnitPrice() + "");
                }
            }
        });
        tblPlaceOrder.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderTM> observable, OrderTM oldValue, OrderTM newValue) {
                if(tblPlaceOrder.getSelectionModel().getSelectedIndex()==-1){
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    cmbItemCode.getSelectionModel().clearSelection();




                }
                else {



                    btnSave.setText("Update");
                    btnDelete.setDisable(false);
                    txtQty.clear();

                    ObservableList<ItemTM> cmbItems = cmbItemCode.getItems();
                    for(ItemTM itemTM:cmbItems){
                        if(itemTM.getItemCode().equals(tblPlaceOrder.getSelectionModel().getSelectedItem().getItemCode())){
                            cmbItemCode.setValue(itemTM);
                        }
                    }



                }

            }
        });


    }


    public void btnNewOrder_OnAction(ActionEvent actionEvent) {
        setNewOrderOnClick();
        setOrderIds();
        setOrderDate();


    }



    public void btnSaveOnAction(ActionEvent actionEvent) {
        if (cmbCustomerId.getSelectionModel().getSelectedIndex() == -1 || cmbItemCode.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "Select At Least One Customer And One Item ", ButtonType.OK).show();
            return;

        }
        if (txtQty.getText().trim().length() == 0 || Double.parseDouble(txtQty.getText())<=0) {
            new Alert(Alert.AlertType.ERROR, "Qty Cannot Be Empty Or 0", ButtonType.OK).show();
            return;

        }
        if (validateQty(txtQty.getText()) == false) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty Entered", ButtonType.OK).show();
            return;
        }
        if (Double.parseDouble(txtQtyOnHand.getText()) < Double.parseDouble(txtQty.getText())) {
            new Alert(Alert.AlertType.ERROR, "Qty Is Larger Than QtyOnHand", ButtonType.OK).show();
            return;

        }
        if (btnSave.getText().equals("Save")) {


            boolean flag = false;
            for (OrderTM order : tblPlaceOrder.getItems()) {
                if (order.getItemCode().equals(cmbItemCode.getSelectionModel().getSelectedItem().getItemCode())) {
                    if(order.getQty()+Double.parseDouble(txtQty.getText())>Double.parseDouble(txtQtyOnHand.getText())){
                        new Alert(Alert.AlertType.ERROR, "Qty Is Larger Than QtyOnHand", ButtonType.OK).show();
                        return;

                    }
                    else {
                        flag = true;
                        order.setQty(order.getQty() + Double.parseDouble(txtQty.getText()));
                        order.setTotal(order.getQty() * order.getUnitPrice());

                        tblPlaceOrder.refresh();

                        try {
                            Connection connection = DBConnection.getDbConnection().getConnection();
                            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE orderDetails set qty=(?) WHERE orderId=(?) AND itemCode=(?)");
                            preparedStatement.setObject(1, order.getQty());
                            preparedStatement.setObject(2, txtOrderId.getText());
                            preparedStatement.setObject(3, cmbItemCode.getSelectionModel().getSelectedItem().getItemCode());
                            preparedStatement.executeUpdate();
                            break;

                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                    }



                }
            }


            if (flag != true) {

                try {
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO orderDetails VALUES (?,?,?)");
                    preparedStatement.setObject(1, txtOrderId.getText());
                    preparedStatement.setObject(2, cmbItemCode.getSelectionModel().getSelectedItem().getItemCode());
                    preparedStatement.setObject(3, txtQty.getText());

                    preparedStatement.executeUpdate();


                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                tblPlaceOrder.getItems().add(new OrderTM(cmbItemCode.getSelectionModel().getSelectedItem().getItemCode(), txtDescription.getText(), Double.parseDouble(txtUnitPrice.getText()),
                        Double.parseDouble(txtQty.getText()), Double.parseDouble(txtUnitPrice.getText()) * Double.parseDouble(txtQty.getText())
                ));
                tblPlaceOrder.refresh();

            }


        }
        else{
            try {
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE orderDetails set qty=(?) WHERE orderId=(?) AND itemCode=(?)");
                preparedStatement.setObject(1,txtQty.getText());
                preparedStatement.setObject(2,txtOrderId.getText());
                preparedStatement.setObject(3,tblPlaceOrder.getSelectionModel().getSelectedItem().getItemCode());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            tblPlaceOrder.getSelectionModel().getSelectedItem().setQty(Double.parseDouble(txtQty.getText()));
            tblPlaceOrder.getSelectionModel().getSelectedItem().setTotal(Double.parseDouble(txtQty.getText())*tblPlaceOrder.getSelectionModel().getSelectedItem().getUnitPrice());
            tblPlaceOrder.refresh();
            tblPlaceOrder.getSelectionModel().clearSelection();
            String itemCode = tblPlaceOrder.getItems().get(tblPlaceOrder.getItems().size() - 1).getItemCode();
            for(ItemTM itemTM:cmbItemCode.getItems()){
                if(itemTM.getItemCode().equals(itemCode)){
                    cmbItemCode.setValue(itemTM);

                }
            }

        }
    }





    public void btnDeleteOnAction(ActionEvent actionEvent) {

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM orderDetails WHERE itemCode=(?) AND orderId=(?)");
            preparedStatement.setObject(1,tblPlaceOrder.getSelectionModel().getSelectedItem().getItemCode());
            preparedStatement.setObject(2,txtOrderId.getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        tblPlaceOrder.getItems().remove(tblPlaceOrder.getSelectionModel().getSelectedItem());
        tblPlaceOrder.refresh();
        tblPlaceOrder.getSelectionModel().clearSelection();



    }

    public void btnPlaceOrderOnClick(ActionEvent actionEvent) {
        ObservableList<OrderTM> orders = tblPlaceOrder.getItems();
        double netTotal=0;
        for(OrderTM orderTM:orders){
            netTotal+=orderTM.getTotal();

        }
        txtNetTotal.setText(netTotal+"");
        ArrayList<String> itemCodes=new ArrayList<>();

        try {
           /* Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item set qtyOnHand=(?) WHERE itemCode=(?)");*/


            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String itemCode = resultSet.getString(1);
                itemCodes.add(itemCode);


            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(String str:itemCodes){
            for(OrderTM orderTM:tblPlaceOrder.getItems()){
                if(str.equals(orderTM.getItemCode())){

                    try {
                        Connection connection = DBConnection.getDbConnection().getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item SET qtyOnHand=(?) WHERE itemCode=(?)");
                        preparedStatement.setObject(1,Double.parseDouble(txtQtyOnHand.getText())-orderTM.getQty());
                        preparedStatement.setObject(2,orderTM.getItemCode());
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }
        }


        try {

            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText());
            java.sql.Date sqlDate = new java.sql.Date(date1.getTime());

           Connection  connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `order` VALUES(?,?,?,?)");
            preparedStatement.setObject(1,txtOrderId.getText());
            preparedStatement.setObject(2,sqlDate);
             preparedStatement.setObject(3,cmbCustomerId.getSelectionModel().getSelectedItem().getCustomerId());
            preparedStatement.setObject(4,Double.parseDouble(txtNetTotal.getText()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        btnSave.setDisable(true);
        new Alert(Alert.AlertType.INFORMATION,"Order Placed Successfully",ButtonType.OK).show();
        btnPlaceOrder.setDisable(true);








    }

    public void imgHomeOnClick(MouseEvent mouseEvent) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/view/MainForm.fxml"));
            Scene newScene=new Scene(root);
            Stage mainStage = (Stage) placeOrderForm.getScene().getWindow();
            mainStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setInitialize(){


        tblPlaceOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblPlaceOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblPlaceOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblPlaceOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblPlaceOrder.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        txtOrderId.setDisable(true);
        txtDate.setDisable(true);
        txtNetTotal.setDisable(true);
        txtCustomerName.setDisable(true);
        txtDescription.setDisable(true);
        txtQtyOnHand.setDisable(true);
        txtUnitPrice.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);
        txtQty.setDisable(true);
        btnPlaceOrder.setDisable(true);
        cmbItemCode.setDisable(true);
        cmbCustomerId.setDisable(true);
        setCustomerIds();
        setItemIds();




    }
    private void setCustomerIds(){
        ObservableList customerIds = cmbCustomerId.getItems();
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customer");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String customerId = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                customerIds.add(new CustomerTM(customerId,name,address));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setItemIds(){
        ObservableList itemCodes = cmbItemCode.getItems();
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM item");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String itemCode = resultSet.getString(1);
                String description = resultSet.getString(2);
                double unitPrice = resultSet.getDouble(3);
                double qtyOnHand = resultSet.getDouble(4);

                itemCodes.add(new ItemTM(itemCode,description,unitPrice,qtyOnHand));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setNewOrderOnClick(){
        txtQty.clear();
        cmbCustomerId.setDisable(false);
        cmbItemCode.setDisable(false);
        btnSave.setDisable(false);
        txtOrderId.setDisable(false);
        txtDate.setDisable(false);
        txtCustomerName.setDisable(false);
        txtDescription.setDisable(false);
        txtQtyOnHand.setDisable(false);
        txtUnitPrice.setDisable(false);
        btnPlaceOrder.setDisable(false);
        txtNetTotal.setDisable(false);
        txtQty.setDisable(false);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
    }

    private void setOrderIds() {
        ArrayList <String> orderIDs=new ArrayList<>();

        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM orderDetails");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String orderId = resultSet.getString(1);
                orderIDs.add(orderId);

            }
            if(orderIDs.size()==0){
                txtOrderId.setText("O001");
            }
            else{
                int lastOrderIdNumber = Integer.parseInt(orderIDs.get(orderIDs.size() - 1).substring(1, 4));
                if(lastOrderIdNumber<9){
                    txtOrderId.setText("O00"+(lastOrderIdNumber+1));
                }
                else if(lastOrderIdNumber<99){
                    txtOrderId.setText("O0"+(lastOrderIdNumber+1));
                }
                else{
                    txtOrderId.setText("O"+(lastOrderIdNumber+1));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void setOrderDate(){
        LocalDate today=LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        txtDate.setText(formattedDate);
    }
    private boolean validateQty(String str){
        boolean flag=false;
        boolean is=false;
        int dotCount=0;
        for(int i=0;i<str.length();i++){
            if(!Character.isDigit(str.charAt(i))){
                if(str.charAt(i)=='.'){
                    dotCount++;

                }
                else{
                    is= false;

                }
            }
            else{
                is=true;
            }
        }
        if(is==true && dotCount<=1){
            flag=true;
        }
        return flag;
    }



}
