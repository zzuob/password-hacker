import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.io.IOException;
import java.util.List;
import java.util.Random;


public class HackingTests extends StageTest {

  boolean ready = false;
  ServerHack serverHack = null;
  Thread serverThread = null;
  String password = null;

  String randomPassword() {
    String[] passwords = new String[]{
            "chance", "frankie", "killer", "forest", "penguin",
            "jackson", "rangers", "monica", "qweasdzxc", "explorer",
            "gabriel", "chelsea", "simpsons", "duncan", "valentin",
            "classic", "titanic", "logitech", "fantasy", "scotland",
            "pamela", "christin", "birdie", "benjamin", "jonathan",
            "knight", "morgan", "melissa", "darkness", "cassie"
    };

    Random ran = new Random();
    String pass = passwords[ran.nextInt(passwords.length)];

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < pass.length(); i++) {
      char c = pass.charAt(i);
      if (ran.nextInt(2) == 1) {
        c = Character.toUpperCase(c);
      }
      sb.append(c);
    }
    return sb.toString();
  }

  void startServer() throws IOException {
    serverHack = new ServerHack(this);
    serverThread = new Thread(serverHack);
    serverThread.start();

    while (!ready) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
      }
    }
  }

  void stopServer() throws InterruptedException {
    serverHack.disconnect();
    serverThread.join();
  }

  @Override
  public List<TestCase<String>> generate() {
    try {
      startServer();
    } catch (IOException ignored) {
    }
    password = randomPassword();
    return List.of(new TestCase<String>()
            .addArguments("localhost", "9090")
            .setAttach(password)
            .setTimeLimit(25000)
    );
  }

  public CheckResult check(String reply, Object attach) {
    try {
      stopServer();
    } catch (Exception ignored) {
    }
    if (serverHack == null || !serverHack.connected) {
      return CheckResult.wrong("You didn't connect to the server");
    }
    if (serverHack.message.size() == 0) {
      return CheckResult.wrong("You sent nothing to the server");
    }
    if (reply.length() == 0 || reply.split("\n").length == 0) {
      return CheckResult.wrong("You did not print anything");
    }
    String realPassword = attach.toString();
    String printedPassword = reply.split("\n")[0];
    if (!printedPassword.equals(realPassword)) {
      return CheckResult.wrong("You printed: \"" + printedPassword + "\".\n" +
              "Correct password: \"" + realPassword + "\"");
    }
    return CheckResult.correct();
  }
}