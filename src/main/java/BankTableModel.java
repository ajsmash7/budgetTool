import javax.swing.table.AbstractTableModel;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Ashley Johnson on 12/12/2018.
 *
 * Since I have two Jtables, and more importantly am implementing the use of Object subclasses, I have to use an
 * AbstractTableModel.
 * This model populates its data from a Vector of Bank objects, and a Vector of column names. Preferably you would
 * completely define the tableModel with Thread safe ArrayLists, but due to assignment time constraints I am extending
 * AbstractTableModel instead of implementing TableModel to use some of it's built in functions to construct the tables.
 *
 */
public class BankTableModel extends AbstractTableModel {
    private Vector<Bank> data;
    private Vector<String> dataColumnNames;
    //initialize vectors

    //Model Constructor
    BankTableModel(Vector<Bank> tableData, Vector<String> columns) {
        this.data = tableData;
        this.dataColumnNames = columns;
    }

    //get and set methods
    public Vector<Bank> getData() {
        return data;
    }

    public void setData(Vector<Bank> data) {
        this.data = data;
    }

    public Vector<String> getDataColumnNames() {
        return dataColumnNames;
    }

    public void setDataColumnNames(Vector<String> dataColumnNames) {
        this.dataColumnNames = dataColumnNames;
    }

    /*
    Overridden methods to customize it to the Vector <Bank> and Vector<String> data types instead of a Vector of Vectors
    Or a vector of arrays.

    Defines those necessary to the functionality of AbstractTableModel getRowCount, getColumnCount and custom getValueAt
    which allows me to define the data types of each cell
     */

    @Override
    public int getRowCount() {
        return this.data.size();
    }

    @Override
    public int getColumnCount() {
        return this.dataColumnNames.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return data.get(rowIndex).getID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getName();
        }
        if (columnIndex == 2) {
            return data.get(rowIndex).getAmount();
        }
        if (columnIndex == 3) {
            return data.get(rowIndex).getDate();
        }
        if (getColumnCount()==5 && columnIndex == 4) {
                return data.get(rowIndex).getExpenseType();
            }
        else{
            return "Could not find";
        }
    }

    //setValueAt method talks to database to update the row in the database containing the cell that was changed.
    //There is a corresponding TableModelListener for each table in the gui that calls this method to update the DB
    //and fire table data change.

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        try {

            int ID = (int) getValueAt(rowIndex, 0);
            String name = (String) getValueAt(rowIndex, 1);
            double amt = (double) getValueAt(rowIndex, 2);
            Date date = (Date) getValueAt(rowIndex, 3);

            if (getColumnCount() == 5) {
                String expense = (String) getValueAt(rowIndex, 4);
                if (expense.equals("Deposit")) {
                    Credit editCredit = new Credit(ID, name, amt, date, expense);
                    BudgetDB.updateDB(editCredit, ID);
                } else {
                    Expense editExpense = new Expense(ID, name, amt, date, expense);
                    BudgetDB.updateDB(editExpense, ID);
                }
            }
            if (getColumnCount() == 4) {
                Bill editBill = new Bill(ID, name, amt, date);
                BudgetDB.updateDB(editBill, ID);
            } else {
                System.out.println("ERROR in column count, current count: " + getColumnCount());
            }
        } catch (NumberFormatException n){
            System.out.println("Cell entry is invalid");
            throw new RuntimeException(n);
        }
    }

    @Override
    public String getColumnName(int column) {
        return super.getColumnName(column);
    }

    //Custom method that I created to behave as setDataVector() does for DefaultTableModel
    //updateTable() in BankGUI is called to fetch the updated rows from the database
    //then calls this method to reset the data, and first table data change.
    public void resetData(Vector<Bank> refreshData, Vector<String> columnNames) {
        setData(refreshData);
        setDataColumnNames(columnNames);
        fireTableDataChanged();

    }

}

