/**
 * Created by Ashley Johnson on 12/10/2018.
 */

import java.sql.*;
import java.util.*;

public class BudgetDB {

    private static final String db_url = "jdbc:sqlite:C:\\Users\\Ashley Johnson\\IdeaProjects\\budgetTool\\src\\main\\java\\BudgetDatabase";
    private static final String BILL_TABLE = "billsDue";
    private static final String TRANSACTION_TABLE = "transactions";
    private static final String DEPOSITS_TABLE = "deposits";

    private static final String CREATE_BILL_TABLE = "CREATE TABLE IF NOT EXISTS billsDue(ID INTEGER PRIMARY KEY AUTOINCREMENT, Bill_Name TEXT, Bill_Amount DECIMAL(10,2), Due_Date DATE)";
    private static final String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS transactions(ID INTEGER PRIMARY KEY AUTOINCREMENT, Type TEXT, Description TEXT, Amount DECIMAL(10,2), PaidOn_Date DATE)";
    private static final String CREATE_DEPOSITS_TABLE = "CREATE TABLE IF NOT EXISTS deposits(ID INTEGER PRIMARY KEY AUTOINCREMENT, deposit_Name TEXT, Amount DECIMAL(10,2), Date_Deposited DATE)";


    BudgetDB(){createTables(); loadTriggers();}

    private void createTables(){
        try (Connection conn = DriverManager.getConnection(db_url);
            Statement statement = conn.createStatement()){

            //Create tables
            statement.executeUpdate(CREATE_BILL_TABLE);
            statement.executeUpdate(CREATE_TRANSACTION_TABLE);
            statement.executeUpdate(CREATE_DEPOSITS_TABLE);

        }catch (SQLException sqle){
            System.out.println("Could not create tables");
            throw new RuntimeException(sqle);
        }
    }

    Vector getColumnNames(int table){
        Vector columnNames = new Vector;

        switch (table){
            case 1:
                columnNames.add("ID");
                columnNames.add("Bill_Name");
                columnNames.add("Bill_Amount");
                columnNames.add("Due_Date");
                break;
            case 2:
                columnNames.add("ID");
                columnNames.add("deposit_Name");
                columnNames.add("Amount");
                columnNames.add("Date_Deposited");
                break;
            case 3:
                columnNames.add("ID");
                columnNames.add("Expense_Name");
                columnNames.add("Amount");
                columnNames.add("Type");
                columnNames.add("PaidOn_Date");
                break;
            default:
                System.out.println("I'm throwing an error");
        }

        return columnNames;

    }



}
