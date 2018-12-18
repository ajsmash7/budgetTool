import java.util.Date;

/**
 * Created by Ashley Johnson on 12/11/2018.
 *
 * a subclass of Bank it implements Budget to inherit all of Bank Object functionality. It has a transaction type, which
 * I dumbly named expenseType, but it's too late to change that now. Except unlike an expense, its transaction type
 * remains constant. There are various types of descriptions for deposits, but ultimately the need to sort these is moot
 * as you could just as easily sort the deposit name and obtain the same result. Description of a deposit is "payroll"
 * "gift" where with an expense the expense type of "Dining" could have a description of "McDonalds", so a sortable
 * expense type is appropriate in this case.
 * the same features as a expense, with the exception of the positive and negative attributes.
 */
public class Credit extends Bank implements Budget{

    String expenseType;

    //blank constructor default
    public Credit(){
        super();
    }
    //constuctor for the first credit assignment
    public Credit (String addName, double addAmt, Date depositDate){
        super(addName, addAmt, depositDate);
    }
    //constructor when it's pulled from the database
    public Credit (int addID, String addName, double addAmt, Date dateEntered, String type) {
        super(addID, addName, addAmt, dateEntered);
        this.expenseType = type;
    }

    public String getExpenseType(){
        return "Deposit";
    } //called to add to the database. redundant to have a constant in constuctor
}
