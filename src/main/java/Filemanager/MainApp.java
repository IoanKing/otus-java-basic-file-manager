package Filemanager;

import Filemanager.Utils.ProjectInfo;
import Filemanager.manager.Actions;

public class MainApp {
    public static void main(String[] args) {
        ProjectInfo.aboutProject();
        Actions.appStart();
    }
}
