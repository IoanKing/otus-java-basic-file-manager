package Filemanager;

import Filemanager.Utils.ProjectInfo;
import Filemanager.manager.ManagerActions;

public class AppMain {
    public static void main(String[] args) {
        ProjectInfo.aboutProject();
        ManagerActions.appStart();
    }
}