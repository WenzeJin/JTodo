import frame.MainFrame;
import javax.swing.*;
import task.TaskManager;
import utils.Settings;

/**
 * Main class of this programme.
 */
public class Main {

  /**
   * Entry point of this programme.
   *
   * @param args args from command line.
   */
  public static void main(String[] args) {
    Settings.getInstance();
    TaskManager.getInstance();
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    });
  }
}