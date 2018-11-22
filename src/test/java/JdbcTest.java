import com.pojo.User;
import org.junit.Test;

import java.sql.*;

public class JdbcTest {
    private static Connection connection = null;
    private static PreparedStatement statement = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://192.168.152.128/test", "root", "123");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findOne() throws SQLException {

        int id = 1;
        String sql = "select * from user where id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        User user = new User();
        while (resultSet.next()) {

            String username = resultSet.getString("username");
            Integer age = resultSet.getInt("age");

            user.setId(id);
            user.setUsername(username);
            user.setAge(age);
        }
        System.out.println(user);

//        return user;
    }

    @Test
    public void test1() throws SQLException {
        String sql = "insert into user values(null,?,?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, "liuxn");
        statement.setInt(2, 14);

        statement.execute();
    }
}
