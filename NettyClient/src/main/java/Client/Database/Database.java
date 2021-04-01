package Client.Database;

import java.sql.*;


public class Database {

    private static final String url = "jdbc:mysql://127.0.0.1:3306/cloudstorage?" +
            "serverTimezone=UTC&useSSL=true&verifyServerCertificate=false";
    private static final String userDB = "root";
    private static final String passwordDB = "root";


    private static Database instance;
    private Connection conn;

//    public static void connectToDB (String account) throws SQLException, ClassNotFoundException {
//
//
//        Class.forName("com.mysql.cj.jdbc.Driver");
//
//        try (Connection conn = DriverManager.getConnection(url, userDB, passwordDB); Statement statement = conn.createStatement()) {
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
//            while (resultSet.next()) {
//                String j = resultSet.getString(1) + " " + resultSet.getString(2);
//                if (account.equals(j)) {
//                    System.out.println("такой юзер есть");
//                    break;
//                } else {
//                    System.out.println("нет совпадений");
//                }
//            }
//        }
//
//    }

    private Database() throws SQLException {

        conn = DriverManager.getConnection(url, userDB, passwordDB);

    }

    public static Database getInstance() {
        if (instance == null) {
            try {
                instance = new Database();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection connection () {
        return conn;
    }

    public ResultSet resultSet () {

        return  resultSet();
    }

}
