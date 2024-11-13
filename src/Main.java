import frame.MainFrame;
import utils.Settings;
import task.TaskManager;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        Settings.getInstance();
        TaskManager.getInstance();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}