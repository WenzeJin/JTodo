import frame.MainFrame;
import utils.Settings;
import task.TaskManager;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        Settings.getInstance();
        SwingUtilities.invokeLater(() -> {
            TaskManager taskManager = new TaskManager();
            MainFrame mainFrame = new MainFrame(taskManager);
            mainFrame.setVisible(true);
        });
    }
}