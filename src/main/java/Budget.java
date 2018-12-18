import java.sql.Date;

/**
 * Created by Ashley Johnson on 12/13/2018.
 */
public interface Budget {
    int getID();
    String getName();
    double getAmount();
    Date getDate();
    String getExpenseType();
}
