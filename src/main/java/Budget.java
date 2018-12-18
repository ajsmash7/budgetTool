import java.util.Date;

/**
 * Created by Ashley Johnson on 12/13/2018.
 *
 * This is the class interface. It's called a tagging interface, containing all the getMethods for all subclasses. A
 * tagging interface abstracts the class so that it can be generically passed to allow for any subclass value to be accepted
 * in parameter passing
 */
public interface Budget {
    int getID();
    String getName();
    double getAmount();
    Date getDate();
    String getExpenseType();
}
