import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class UserDAOPostgres extends UserDAO {
    private static final String DBURL;
    private static final String DBUSER;
    private static final String DBPWD;

    //empty constructor
    public UserDAOPostgres() {}

    //constructor that actually does the job with information
    public UserDAOPostgres() {

    }

    //creates a users table in the database
    public void createTableUsers(Connection conn) throws SQLException {
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
            //don't know if while loop is necessary here
            while (res.next()) {
                Long id = res.getLong("id");
                String username = res.getString("username");
                String pwdHash = res.getString("passwordHash");

                //print to check it works
                System.out.println(
                        "User \n"
                                + "id : "+id+"\n"
                                + "username : "+username+"\n"
                                + "password hash : "+pwdHash
                );
                //need to take care of cases where there's no request result or several of them
                User foundUser = new User(id,username,pwdHash);
                return foundUser;
            }

        }
    }

    //insert a User in the database using a username u and a password hash pwdHash
    public void insertUser(Connection conn, String u, String pwdHash) throws SQLException {
        String sql = "INSERT INTO users (username,passwordHash) VALUES (?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, u);
        pstmt.setString(2, pwdHash);
        /* I don't know if we need that
        int rowsAffected = pstmt.executeUpdate();
        System.out.println(rowsAffected+" livre(s) ajouté(s)");
         */
        pstmt.close();
    }

    //returns true if username u exists in the database and has pwd as password
    public boolean checkCredentials(String u, String pwd) {

    }
  }
