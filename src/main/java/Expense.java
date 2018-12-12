import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Expense extends Bank {

    protected Date addDate;
    protected String expenseType;

    public Expense(int addID, String addName, double addAmt, String type, Date dateEntered){
        super(addID, addName, addAmt);
        this.addDate = dateEntered;
        this.expenseType = type;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }
}
