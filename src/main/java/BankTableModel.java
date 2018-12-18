import javax.swing.table.AbstractTableModel;
import java.sql.Date;
import java.util.Vector;

/**
 * Created by Ashley Johnson on 12/12/2018.
 */
public class BankTableModel extends AbstractTableModel {
    private Vector<Bank> data;
    private Vector<String> dataColumnNames;

    BankTableModel(Vector<Bank> tableData, Vector<String> columns) {
        this.data = tableData;
        this.dataColumnNames = columns;
    }

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
        if (columnIndex == 4) {
            return data.get(rowIndex).getExpenseType();


        }else{
            return "Could not find";
        }
    }

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


    public void resetData(Vector<Bank> refreshData, Vector<String> columnNames) {
        setData(refreshData);
        setDataColumnNames(columnNames);
        fireTableDataChanged();

    }

}

