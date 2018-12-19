import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Ashley Johnson on 12/11/2018.
 *
 * The GUI for the database. It talks to the tableModel and the Database. It credits Bills, Expenses, and Credits. It has
 * a user input validation method, and an updateTable method that re fires the tables and resets the gui to it's default
 * values. If expense is selected from the option drop down menu, the expense type selector becomes visible.
 *
 * The labels on the gui change based on which option you choose; Bill, Deposit or Expense. All buttons and tables have
 * listeners to update as the user conducts CRUD operations.
 */
public class BankGUI extends JFrame {
    private JPanel MainPanel;
    private JComboBox<String> typeSelector;
    private JTextField desc_name;
    private JLabel type_name;
    private JTextField amount;
    private JTextField date_field;
    private JTable bankTable;
    private JLabel label_date;
    private JButton pay_now;
    private JLabel after_billpay;
    private JLabel available_cash;
    private JTable current_bills;
    private JButton addButton;
    private JButton deleteButton;
    private JLabel next_bill_date;
    private JLabel bill_due_name;
    private JButton delete_bill;
    private JLabel type_label;
    private JComboBox<String> expense_type;
    private JButton quitButton;

    //initialize the database
    private BudgetDB db;

    //declare defaults for labels and text fields
    private String nameDefault = "Description Name:";
    private String dateDefault = "Date:";
    private String dateFieldDefault = "MM-dd-yyy";

    //declare instances of the table models and columnNames
    private BankTableModel bankModel;
    private BankTableModel billModel;
    private Vector<String> bankColNames;
    private Vector<String> billColNames;
    private String table; //instantiate a global variable for the table selected name

    //construct the gui with a current instance of the database
    BankGUI (BudgetDB db){
        //Create a local instance
        this.db = db;

        setContentPane(MainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add combobox values
        typeSelector.addItem("Select From the List");
        typeSelector.addItem("Bill");
        typeSelector.addItem("Deposit");
        typeSelector.addItem("Expense");


        expense_type.addItem("Grocery");
        expense_type.addItem("Gas");
        expense_type.addItem("Dining");
        expense_type.addItem("Shopping");
        expense_type.addItem("Entertainment");
        expense_type.addItem("Cash");
        expense_type.addItem("Other");

        getRootPane().setDefaultButton(addButton);
        //load the gui, wait for listeners
        setDefaults();
        configureTables();
        addListeners();
        setVisible(true);


    }
    //listen for what the user selects in the options combo box. change the labels and set visibility based on selection
    private void addListeners(){
        typeSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selection = (String) typeSelector.getSelectedItem();
                switch (selection){
                    case "Select From the List":
                        break;
                    case "Bill":
                        type_name.setText("Bill Name:");
                        amount.setText("Total Due:");
                        label_date.setText("Due Date:");
                        break;
                    case "Deposit":
                        type_name.setText("Deposit Description:");
                        amount.setText("Amount Deposited:");
                        label_date.setText("Deposited On:");
                        date_field.setText(getCurrentDateString());
                        break;
                    case "Expense":
                        type_label.setVisible(true);
                        expense_type.setVisible(true);
                        amount.setText("Amount Spent:");
                        label_date.setText("Date of Purchase:");
                        break;
                }

            }
        });

        //listen for add button. assign the option selection to a string, pass it for data input validation
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String selection = typeSelector.getSelectedItem().toString();
               validateData(selection);
            }
        });
        //listener for delete button for transaction tabel model. assign table name constant, pass to deleteSelection method
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String tableName = BudgetDB.TRANSACTION_TABLE;
                deleteSelection(tableName);
            }
        });
        //table listener for current bills cell edits. setValueAt talks to the database to update with the changes
        // updateTable method calls a fireTableDataChanged to refresh table
        current_bills.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = current_bills.getSelectedRow();
                int column = current_bills.getSelectedColumn();
                Object a = current_bills.getValueAt(row,column);
                current_bills.setValueAt(a, row, column);
                updateTable();
            }
        });
        //listener for the transaction table. setValue at calls the database to update the correct table with the changes
        //updateTable refreshes the gui and table.
        bankTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //int column = bankTable.getSelectedColumn();
                //int row = bankTable.getSelectedRow();
                //Object a = bankTable.getValueAt(row, column);
                //bankTable.setValueAt(a, row, column);
                updateTable();
            }
        });
        //bill pay button. when pressed it adds the selected bill to the transaction table, and deletes it from the billtable.
        pay_now.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = BudgetDB.BILL_TABLE;
                int row = current_bills.getSelectedRow();
                String name = "PAID: " + current_bills.getValueAt(row, 1);
                double amount = (double)current_bills.getValueAt(row, 2);
                Date date = getDateNow();
                Bill bill = new Bill(name, amount, date);
                String result = db.addTransToDB(bill);

                if (result.equals(BudgetDB.OK)){
                    deleteSelection(tableName);
                    updateTable();
                }else{
                    errorDialog("Error adding expense to Database");
                }

            }
        });
        //this is for when you made an error or a bill was forgiven without payment. deletes it from the bill table.
        delete_bill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = BudgetDB.BILL_TABLE;
                deleteSelection(tableName);
            }
        });

        //disposes the gui. closes the program.
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BankGUI.this.dispose();
            }
        });



    }
    //reset the gui
    public void setDefaults(){
        type_name.setText(nameDefault);
        amount.setText("");
        desc_name.setText("");
        typeSelector.setSelectedIndex(0);
        label_date.setText(dateDefault);
        date_field.setText(dateFieldDefault);
        type_label.setVisible(false);
        expense_type.setVisible(false);
        calculateFunds();
        nextBillDue();
    }
    //validate user input. If the selection is empty, or equals a default, prompt the user to enter a date. catches the
    //NumberFormatException for non-numeric entry on date and amount.
    public void validateData(String selection){
        String name = "";
        double amt = 0.0;
        Date date = getDateNow();
        String expenseType = "";
        try {
            if (desc_name.getText().isEmpty() || desc_name.getText().equals("")) {
                errorDialog("You must enter a name");
            } else{
                name = desc_name.getText();
            }
            if (amount.getText().isEmpty() || amount.getText().equals("")) {
                errorDialog("You must enter an amount");
            } else {
                amt = Double.parseDouble(amount.getText());
            }
            if (date_field.getText().isEmpty() || date_field.getText().equals(dateFieldDefault)) {
                errorDialog("You must enter a date");
            } else {
                date = convertDate(date_field.getText());
            }
            if (selection.equals("Expense") && expense_type.getSelectedIndex() == -1) {
                errorDialog("You must select an expense type");
            } else {
                expenseType = expense_type.getSelectedItem().toString();
            }

            /*
            ONCE VALIDATED USE IF STATEMENTS TO ASSIGN THE RAW USER DATA A CLASS, AND CORRESPONDING TABLE.

             */

            if (selection.equals("Expense")) {
                table = BudgetDB.TRANSACTION_TABLE;
                addExpense(name, amt, date, expenseType);
            }
            if (selection.equals("Deposit")) {
                table = BudgetDB.TRANSACTION_TABLE;
                addDeposit(name, amt, date);
            }
            if (selection.equals("Bill")) {
                table = BudgetDB.BILL_TABLE;
                addBill(name, amt, date);
            } else {
                errorDialog("You need to select an Option to Add from the drop down list");
            }
        }catch (NumberFormatException n){
            System.out.println("Invalid Data Entry, try again");
        }
    }


    public void addExpense(String name, double amt, Date datePaid, String expenseType){

        Expense expense = new Expense(name, amt, datePaid, expenseType);
        String result = db.addTransToDB(expense);
        //If database entry was successful update table.
        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding expense to Database");
        }
    }
    public void addDeposit(String name, double amt, Date dateAdded){
        Credit deposit = new Credit(name, amt, dateAdded);

        String result = db.addTransToDB(deposit);
        //if successful, update table
        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding deposit to Database");
        }
    }
    public void addBill(String name, double amt, Date due_date){

        Bill bill = new Bill (name, amt, due_date);

        String result = db.BillToDB(bill);
        //if successfully added to the database, update table
        if (result.equals(BudgetDB.OK)){
            updateTable();
        }else{
            errorDialog("Error adding Bill to Database");
        }

    }
    public void deleteSelection(String tableName) {
        //use an if statement to choose which table the item is to be deleted from
        //if the user did not select a row to be deleted, it will return -1. If the selectedRow = -1 prompt the
        //user to select what they want to delete. if they did select a row, send the row index and the table name
        //to the database delete method.
        int currentRow;
        if (tableName.equals(BudgetDB.BILL_TABLE)) {
            currentRow = current_bills.getSelectedRow();
            if (currentRow == -1) {
                errorDialog("Error: you have not selected a row");

            }else{
                db.deleteFromDB(tableName, currentRow);
                updateTable();
            }

        }
        if (tableName.equals(BudgetDB.TRANSACTION_TABLE)){
            currentRow = bankTable.getSelectedRow();
            if (currentRow == -1){
                errorDialog("You haven't selected a row");
            }else{
                db.deleteFromDB(tableName,currentRow);
                updateTable();
            }
        }


    }

    //method to calculate the total funds available, and total funds once all bills are paid.
    //call the database methods to add up the totals for both the bill table and the after bill pay table.
    public void calculateFunds(){
        String totalCash = String.valueOf(db.bankTotal());

        available_cash.setText(totalCash);

        String totalBills = String.valueOf(db.billTotal());

        after_billpay.setText(totalBills);

    }
    //method to populate when your next bill is due, what the name of it is, and how much. set gui labels
    public void nextBillDue() {
        ArrayList<Bill> nextBill = new ArrayList<>();
        nextBill = db.earliestDate();


        for(Bill bill: nextBill){
            bill_due_name.setText(bill.getName() + bill.getAmount());
            next_bill_date.setText(convertDateToString(bill.getDate()));

        }

    }
    //date formatter to return a formatted String of the current date
    private static String getCurrentDateString(){


            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(date);

            return currentDate;
    }

    private static String convertDateToString(Date date) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(date);

        return currentDate;
    }
    //date formatter to get the current date in the java.util.Date datatype.
    private Date getDateNow(){

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            String dateString = format.format(new Date());
            Date date = format.parse(dateString);
            return date;

        }catch (ParseException d){
            errorDialog("Can't parse date");
            throw new RuntimeException(d);
        }


    }
    //another date converter but to take a legacy date and convert it to a java.util.Date.
    private Date convertDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parsedDate = dateFormat.parse(date);


            return parsedDate;

        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    //configure table load on startup
    private void configureTables(){

        current_bills.setGridColor(Color.GRAY);
        bankTable.setGridColor(Color.GRAY);

        billColNames = db.getColumnNames(BudgetDB.BILL_TABLE);
        Vector<Bank> billData = db.getAll(BudgetDB.BILL_TABLE);
        bankColNames = db.getColumnNames(BudgetDB.TRANSACTION_TABLE);
        Vector<Bank> bankData = db.getAll(BudgetDB.TRANSACTION_TABLE);

        bankModel = new BankTableModel(bankData,bankColNames);
        billModel = new BankTableModel(billData, billColNames);

        bankTable.setModel(bankModel);
        current_bills.setModel(billModel);

        bankModel.fireTableDataChanged();
        billModel.fireTableDataChanged();


    }
    //update table after every Listener and CRUD operation
    public void updateTable(){
        Vector <Bank> bankData = db.getAll(BudgetDB.TRANSACTION_TABLE);
        Vector <Bank> billData = db.getAll(BudgetDB.BILL_TABLE);

        bankModel.resetData(bankData, bankColNames);
        billModel.resetData(billData, billColNames);

        setDefaults();


    }//error messaging
     void errorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }


}
