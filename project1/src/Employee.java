/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class Employee {

    private final String email;

    public Employee(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
