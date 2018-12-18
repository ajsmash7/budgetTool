import java.util.Date;

/**
 * Created by Ashley Johnson on 12/10/2018.
 * This is the object super class. It is abstract to implement the Budget interface. This allows for all subclasses of
 * Bank to be able to pass into arguments accepting Bank class objects. All bank objects have an ID, a name, an amount,
 * and a date.
 *
 * Additional methods for get and set Neg amount are included here, to turn the expense amount entered into a negative
 * value to be deducted from the deposits.
 */
public abstract class Bank implements Budget{
    int ID;
    String name;
    double amount;
    Date date;

    public Bank (){} //empty constructor

    //super constructor for entry into database. ID isn't assigned until Database assigns it.

    public Bank(String addName, double addAmt, Date dateAdd){
        this.name = addName;
        this.amount = addAmt;
        this.date = dateAdd;
    }
    //Constructor for Objects being read out of the database, with an id.
    public Bank(int addID, String addName, double addAmt, Date dateAdd){
        this.ID = addID;
        this.name = addName;
        this.amount = addAmt;
        this.date = dateAdd;
    }

    //get and set methods.

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getNegAmt(double amt){return 0-amt;}

    public void setNegAmt (double negAmt){this.amount = negAmt;}
}
