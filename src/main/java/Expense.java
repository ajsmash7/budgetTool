import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Expense extends Bank implements Budget {

    String expenseType;

    public Expense(){
        super();
    }

    public Expense(String addName, double addAmt, Date dateEntered, String type){
        super(addName, addAmt, dateEntered);
        this.expenseType = type;
    }

    public Expense(int addID, String addName, double addAmt, Date dateEntered, String type){
        super(addID, addName, addAmt, dateEntered);
        this.expenseType = type;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }
}
