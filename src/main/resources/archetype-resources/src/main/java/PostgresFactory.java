package persistence;

import java.sql.Connection;
import java.sql.DriverManager;

class PostgresFactory extends AbstractFactory {
    private static final String DBURL;
    private static final String DBUSER;
    private static final String DBPWD;
    private Connection conn;

    //empty constructor
    public PostgresFactory() {}

    //constructor that actually does the job with information
    public PostgresFactory() {
        this.conn = createConnection();
    }

    //TODO: laysa mets tout sa dans un env docker
    //creates connection to database
    public Connection createConnection() {
        this.DBURL = "jdbc:postgresql://postgres-syncstudy:5432/syncstudy";
        this.DBUSER = "user";
        this.DBPWD = "pwd";
        return DriverManager.getConnection(DBURL,DBUSER,DBPWD);
    }

    //creates a concrete UserDAO
    public UserDAOPostgres getUserDAO() {
        return new UserDAOPostgres(this.conn);
    }

    //other DAOs to create later here
}