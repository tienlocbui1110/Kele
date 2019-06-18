import com.lh.component.common.KeteDatabaseConfig;
import com.lh.component.common.Point;
import com.lh.component.common.Polyline;
import com.lh.component.common.ReadableMySQL;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MySqlCnnTest {
    @Test
    public void testConnection() {
        ReadableMySQL mySQL = new ReadableMySQL(
                KeteDatabaseConfig.DB_NAME, KeteDatabaseConfig.USER, KeteDatabaseConfig.PASSWORD
        );
        mySQL.query("Select count(*) From UserTracking", (resultSet -> {
            try {
                if (!resultSet.next()) {
                    fail();
                }
            } catch (SQLException e) {
                fail(e.toString());
            }
        }));
    }
}
