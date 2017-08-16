package dk.trustworks.invoicewebui.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by hans on 15/12/2016.
 */
public class ConnectionHelper {

    protected static Logger logger = LoggerFactory.getLogger(ConnectionHelper.class.getName());
    private static ConnectionHelper instance;

    public final HikariDataSource dataSource;

    private ConnectionHelper() {
        //jdbc:mysql://trustworksdb.cm3iylt6ulsl.eu-west-1.rds.amazonaws.com:3306/

        Properties props = System.getProperties();
        Map<String, String> env = System.getenv();

        //props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        //props.setProperty("dataSource.user", "test");
        //props.setProperty("dataSource.password", "test");
        //props.setProperty("dataSource.databaseName", "mydb");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(System.getenv("DB_URL"));
        config.setUsername(System.getenv("DB_USERNAME"));
        config.setPassword(System.getenv("DB_PASSWORD"));

        logger.info("System.getenv(\"DB_URL\") = " + System.getenv("DB_URL"));
        logger.info("System.getProperty(\"DB_URL\"); = " + System.getProperty("DB_URL"));
        /*
        config.setJdbcUrl(env.get("db.url"));
        config.setUsername(env.get("db.username"));
        config.setPassword(env.get("db.password"));
        */
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("maximumPoolSize", 2);

        dataSource = new HikariDataSource(config);
    }

    public final static ConnectionHelper getInstance() {
        if(instance == null) instance = new ConnectionHelper();
        return instance;
    }
}
