package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

class UserDAOPostgres extends UserDAO {
    private Connection conn;

    //empty constructor
    public UserDAOPostgres() {}

    //constructor that actually does the job with information
    public UserDAOPostgres(Connection conn) {
        this.conn = conn;
    }

    //creates a users table in the database
    public void createTableUsers() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                +"id SERIAL PRIMARY KEY,"
                +"username VARCHAR(50) NOT NULL,"
                +"passwordHash VARCHAR(100) NOT NULL";
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        System.out.println("Table 'users' créée ou déjà existante");
        stmt.close();
    }

    //retrieves the User with username u from the database and returns it
    public User findUserByUsername(String u) throws SQLException {
        String sql = "SELECT * from users WHERE username=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, u);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                //TODO
                //what's the type of that foundData ? it's the first element of a ResultSet
                // but I can't seem to understand what it represents
                Long id = res.getLong("id");
                String username = res.getString("username");
                String pwdHash = res.getString("passwordHash");

                //print to check it works, remove if okay, just for debug
                System.out.println(
                        "User \n"
                                + "id : "+id+"\n"
                                + "username : "+username+"\n"
                                + "password hash : "+pwdHash
                );
                return new User(id, username, pwdHash);
            }
            else {
                return null;
            }

        }
    }

    //insert a User in the database using a username u and a password hash pwdHash
    public void insertUser(String u, String pwdHash) throws SQLException {
        String sql = "INSERT INTO users (username,passwordHash) VALUES (?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, u);
        pstmt.setString(2, pwdHash);
        /* I don't know if we need that, it's just to print number of rows changed, might just be debug
        int rowsAffected = pstmt.executeUpdate();
        System.out.println(rowsAffected+" user(s) ajouté(s)");
         */
        pstmt.close();
    }

    //returns true if username u exists in the database and has pwd as password
    public boolean checkCredentials(String u, String pwd) {
        String sql = "SELECT username,passwordHash FROM users WHERE username=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                String storedHash = res.getString("passwordHash");
                //compares password with stored hash
                return pwd.equals(storedHash);
            }
            else {
                //no user
                return false;
            }
    }
}