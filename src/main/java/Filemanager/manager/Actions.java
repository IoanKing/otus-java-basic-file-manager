package Filemanager.manager;

import Filemanager.Utils.ManagerCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Actions {
    public boolean isActive = true;
    private static final Path DEFAUT_PATH = Path.of("").toAbsolutePath();
    private static Path currentPath = Path.of(DEFAUT_PATH + "\\FileManagerDirectory\\");

    public static void appStart() {
        Scanner scanner = new Scanner(System.in);
        String command;
        Actions action = new Actions();

        do {
            printCommandList();
            printCurrentPath(currentPath);
            command = scanner.nextLine();
            action.get(command);
        } while (action.isActive);
    }

    public void get(String action) {
        String[] command = action.split(" ");
        String currentCommand = command[0].substring(1);
        switch (currentCommand) {
            case "help":
                if (command.length > 1)
                    if (command[1].startsWith("/")) {
                        getHelp(command[1].substring(1).toUpperCase());
                        break;
                    }
                getHelp();
                break;
            case "ls":
                if (command.length > 1)
                    if (command[1].startsWith("-i")) {
                        getFileList(currentPath, true);
                        break;
                    }
                getFileList(currentPath);
                break;
            case "cd":
                if (command.length > 1) {
                    if (command[1].startsWith("..")) {
                        setDirectory();
                        break;
                    }
                    setDirectory(command[1]);
                }
                break;
            case "mkdir":
                if (command.length > 1) {
                    createDirectory(command[1]);
                }
                break;
            case "rm":
                if (command.length > 1) {
                    deleteFile(command[1]);
                }
                break;
            case "mv":
                if (command.length > 3) {
                    if (Objects.equals(command[3], "-f")) {
                        moveFile(command[1], command[2], true);
                    }
                }
                if (command.length > 2) {
                    moveFile(command[1], command[2], false);
                }
                break;
            case "cp":
                if (command.length > 3) {
                    if (Objects.equals(command[3], "-f")) {
                        copyFile(command[1], command[2], true);
                    }
                }
                if (command.length > 2) {
                    copyFile(command[1], command[2], false);
                }
                break;
            case "finfo":
                if (command.length > 1) {
                    getFileInfo(command[1]);
                }
                break;
            case "find":
                if (command.length > 1) {
                    findFile(currentPath.toAbsolutePath().toFile(), command[1]);
                }
                break;
            case "exit":
                isActive = false;
                break;
            default:
                break;
        }
    }

    public void getHelp() {
        Arrays.stream(ManagerCommand.values()).forEach(System.out::println);
    }

    public void getHelp(String command) {
        System.out.println(ManagerCommand.valueOf(command).toString());
    }

    public void getFileList(Path path) {
        System.out.println("---Список файлов: ---");
        try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
            for (Path file : files)
                System.out.println(file.getFileName());
        } catch (IOException ex) {
            System.out.println("Ошибка при получении списка файлов.");
            System.out.println(ex.getMessage());
        }
    }

    public void getFileList(Path path, boolean isDetailed) {
        if (!isDetailed) {
            getFileList(path);
            return;
        }
        System.out.println("---Список файлов: ---");
        try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
            BasicFileAttributes fileAttr;
            System.out.printf("%-30s %-20s %-30s", "Наименование", "Размер (byte)", "Дата изменения");
            System.out.println();
            for (Path file : files) {
                fileAttr = Files.readAttributes(file, BasicFileAttributes.class);
                System.out.format("%-30s %-20d %-30s", file.getFileName(), fileAttr.size(), fileAttr.lastModifiedTime());
                System.out.println();
            }
        } catch (IOException ex) {
            System.out.println("Ошибка при получении списка файлов.");
            System.out.println(ex.getMessage());
        }
    }

    public static void printCurrentPath(Path path) {
        System.out.println(path.toAbsolutePath());
        System.out.println("---------------------------------------------------------------");
    }

    public void setDirectory() {
        currentPath = currentPath.toAbsolutePath().getParent();
        System.out.println("Директория изменена.");
    }

    public void setDirectory(String filename) {
        String newPath;
        if (filename.contains(":")) {
            newPath = filename;
        } else {
            newPath = currentPath.toAbsolutePath() + "\\" + filename;
        }
        if (Files.exists(Path.of(newPath))) {
            currentPath = Path.of(newPath);
            System.out.println("Директория изменена.");
            return;
        }
        System.out.println("Указанной директории не существует.");
    }

    public void createDirectory(String filename) {
        if (Files.exists(Path.of(currentPath + "\\" + filename))) {
            System.out.println("Указанная директория существует.");
            return;
        }
        try {
            Path newDirectory = Files.createDirectory(Paths.get(currentPath + "\\" + filename));
            if (Files.exists(Path.of(newDirectory.toUri()))) {
                System.out.println("Указанная директория успешно создана.");
            }
        } catch (IOException ex) {
            System.out.println("Ошибка при создании директории");
            System.out.println(ex.getMessage());
        }
    }

    public void getFileInfo(String filename) {
        if (Files.notExists(Path.of(currentPath.toAbsolutePath() + "\\" + filename))) {
            System.out.println("Указанный файл не существует.");
            return;
        }
        try {
            Path file = Path.of(currentPath.toAbsolutePath() + "\\" + filename);
            BasicFileAttributes fileAttr = Files.readAttributes(file, BasicFileAttributes.class);
            System.out.printf("%-30s %-20s %-20s", "Наименование", "Размер (byte)", "Дата изменения");
            System.out.println();
            System.out.format("%-30s %-20d %-20s", file.getFileName(), fileAttr.size(), fileAttr.lastModifiedTime());
            System.out.println();
        } catch (IOException ex) {
            System.out.println("Ошибка при получении атрибутов файла.");
            System.out.println(ex.getMessage());
        }
    }

    public void moveFile(String filename, String newpath, boolean isForseRewrite) {
        Path destinationPath;
        if (Files.notExists(Path.of(currentPath.toAbsolutePath() + "\\" + filename))) {
            System.out.println("Указанный файл не существует.");
            return;
        }
        try {
            if (newpath.startsWith("\\")) {
                destinationPath = Path.of(currentPath.toAbsolutePath() + newpath);
            } else if (newpath.startsWith("/")) {
                destinationPath = Path.of(currentPath.toAbsolutePath() + newpath.replace("/", "\\"));
            } else {
                destinationPath = Path.of(newpath);
            }
            Path sourcePath = Path.of(currentPath.toAbsolutePath() + "\\" + filename);
            if (Files.exists(destinationPath) && isForseRewrite == false) {
                System.out.println("Указанный файл уже существует.");
                return;
            }
            Files.move(sourcePath, destinationPath);
            System.out.println("Файл перемещён/переименован.");
        } catch (IOException ex) {
            System.out.println("Ошибка при перемещении файла.");
            System.out.println(ex.getMessage());
        }
    }

    public void copyFile(String filename, String newpath, boolean isForseRewrite) {
        Path destinationPath;
        if (Files.notExists(Path.of(currentPath.toAbsolutePath() + "\\" + filename))) {
            System.out.println("Указанный файл не существует.");
            return;
        }
        try {
            if (newpath.startsWith("\\")) {
                destinationPath = Path.of(currentPath.toAbsolutePath() + newpath);
            } else if (newpath.startsWith("/")) {
                destinationPath = Path.of(currentPath.toAbsolutePath() + newpath.replace("/", "\\"));
            } else {
                destinationPath = Path.of(newpath);
            }
            Path sourcePath = Path.of(currentPath.toAbsolutePath() + "\\" + filename);
            if (Files.exists(destinationPath) && isForseRewrite == false) {
                System.out.println("Указанный файл уже существует.");
                return;
            }
            Files.copy(sourcePath, destinationPath);
            System.out.println("Файл скопирован.");
        } catch (IOException ex) {
            System.out.println("Ошибка при перемещении файла.");
            System.out.println(ex.getMessage());
        }
    }

    public void findFile(File directory, String filename) {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findFile(file, filename);
            } else {
                if (file.getName().contains(filename)) {
                    System.out.println(file);
                }
            }
        }
    }

    public void deleteFile(String filename) {
        if (Files.notExists(Path.of(currentPath.toAbsolutePath() + "\\" + filename))) {
            System.out.println("Указанный файл не существует.");
            return;
        }
        try {
            Path sourcePath = Path.of(currentPath.toAbsolutePath() + "\\" + filename);
            Files.deleteIfExists(sourcePath);
            System.out.println("Файл/директория удалена.");
        } catch (IOException ex) {
            System.out.println("Ошибка при удалении файла/директории.");
            System.out.println(ex.getMessage());
        }
    }

    public static void printCommandList() {
        System.out.println("--------------------Доступные команды: /[command] -------------");
        System.out.print("|");
        Arrays.stream(ManagerCommand.values()).map(ManagerCommand::getName).forEach(x -> System.out.print(" " + x + " |"));
        System.out.println();
        System.out.println("------------------------Текущая директория----------------------");
    }
}
