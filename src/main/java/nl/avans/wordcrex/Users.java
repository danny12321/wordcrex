package nl.avans.wordcrex;
import nl.avans.wordcrex.data.Database;

import java.util.ArrayList;

public class Users {

    private Database database;

    private ArrayList<User> userList = new ArrayList<User>();

    public Users(Database d)
    {
        this.database = d;
    }

    public void AddUser(User u)
    {
        this.userList.add(u);
    }

    public void FillUserList()
    {
        
    }

}
class User
{
    String username;
    String[] roles;

    public User(String n, String[] r)
    {
        this.username = n;
        this.roles = r;
    }
}
