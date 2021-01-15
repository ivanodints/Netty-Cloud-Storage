package Client.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Users {


    final String n = "users";
    private PreparedStatement ps = null;
    private Statement st = null;
    private ResultSet resultSet = null;
    private boolean check = false;

    public Users() throws SQLException {

    }

    public void  createUser (String login, String password) throws SQLException {

        ps = Database
                .getInstance()
                .connection()
                .prepareStatement(" INSERT INTO " + n + "(LOGIN, PASSWORD) VALUE (?, ?) ");
        ps.setString(1, login);
        ps.setString(2, password);

        ps.executeUpdate();

    }

    public boolean authUser (String login, String password) throws SQLException {
        boolean check = false;
        String user = login + password;
        ps = Database
                .getInstance()
                .connection()
                .prepareStatement("SELECT * FROM " + n + " WHERE LOGIN = ? AND PASSWORD = ?");

        ps.setString(1, login);
        ps.setString(2, password);
        ResultSet set = ps.executeQuery();

        if (set.next()) {
            check = true;
        }
        return check;
    }

    public boolean authUser (String user) throws SQLException {

       st = Database.getInstance().connection().createStatement();


        ResultSet resultSet = st.executeQuery("SELECT * FROM " + n );


        while (resultSet.next()) {
            String userInDB = resultSet.getString(1) + " " + resultSet.getString(2);
            if (user.equals(userInDB)) {
                check = true;
                break;
            }
        }
        return check;
    }
}
