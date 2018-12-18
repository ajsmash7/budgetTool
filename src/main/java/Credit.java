import java.sql.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 */
public class Credit extends Bank implements Budget{

    String expenseType;


    public Credit(){
        super();
    }

    public Credit (String addName, double addAmt, Date depositDate){
        super(addName, addAmt, depositDate);
    }

    public Credit (int addID, String addName, double addAmt, Date dateEntered, String type) {
        super(addID, addName, addAmt, dateEntered);
        this.expenseType = type;
    }

    public String getExpenseType(){
        return "Deposit";
    }
}
