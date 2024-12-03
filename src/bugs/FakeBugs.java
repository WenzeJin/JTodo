package bugs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is designed for containing rubbish codes to trigger warnings in static analysis.
 */
public class FakeBugs {

  // 1. 未使用的变量 (Unused Variable)
  private static final String UNUSED_CONSTANT = "This is unused";
  private int unusedVariable;

  // 2. 硬编码密码 (Hardcoded Password)
  private static final String PASSWORD = "123456";

  // 3. SQL 注入 (SQL Injection) 与 资源泄露 (Resource Leak)
  public void sqlInjection(String userInput) throws SQLException {
    String query = "SELECT * FROM users WHERE username = '" + userInput + "'"; // SQL 注入漏洞
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "pass");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery(query);  // 未使用 PreparedStatement
    while (rs.next()) {
      System.out.println(rs.getString("username"));
    }
  }

  // 4. 空指针异常 (NullPointerException)
  public void nullPointer() {
    String str = null;
    System.out.println(str.length());  // 可能导致空指针异常
  }

  // 5. 冗长代码 (Duplicate Code)
  public void duplicateCode() {
    System.out.println("This is duplicated code");
    System.out.println("This is duplicated code");  // 重复代码
  }

  // 6. 低效数据结构使用 (Inefficient Data Structure)
  public boolean inefficientSearch(List<String> list, String target) {
    for (String item : list) {
      if (item.equals(target)) {
        return true;
      }
    }
    return false;  // 线性搜索，低效
  }

  // 7. 死代码 (Dead Code)
  public void deadCode() {
    if (false) {  // 条件永远不会为 true
      System.out.println("This code will never be executed.");
    }
  }

  // 8. 资源泄露 (Resource Leak)
  public void resourceLeak() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("file.txt"));
    String line = br.readLine();
    System.out.println(line);
    // br.close(); // 未关闭资源，可能导致资源泄露
  }

  // 9. 竞争条件 (Race Condition)
  private int counter = 0;

  public void increment() {
    counter++;  // 没有同步，可能导致竞争条件
  }

  public void simulateRaceCondition() {
    Runnable task = this::increment;

    Thread thread1 = new Thread(task);
    Thread thread2 = new Thread(task);

    thread1.start();
    thread2.start();
  }

  // 10. 未使用的导入 (Unused Import)
  // import java.util.Date; // 此导入未使用，建议删除
  public static void main(String[] args) {
    FakeBugs bugs = new FakeBugs();
    try {
      bugs.sqlInjection("admin' OR '1'='1");
      bugs.nullPointer();
      bugs.duplicateCode();
      bugs.inefficientSearch(new ArrayList<>(), "test");
      bugs.deadCode();
      bugs.resourceLeak();
      bugs.simulateRaceCondition();
    } catch (Exception e) {
      System.out.println("Exception occurred: " + e.getMessage());
    }
  }
}