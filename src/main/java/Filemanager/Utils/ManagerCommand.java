package Filemanager.Utils;

public enum ManagerCommand {
    HELP("help", " [/command] - Вывод в консоль всех поддерживаемых команд. [/command] - справка по команде."),
    LS("ls", " [-i] - распечатывает список файлов и директорий текущего каталога. Ключ [-i] выводит детальную информацию о файле."),
    CD("cd", " [path] | [..] - переход в указанную поддиректорию | переход в родительский каталог."),
    MKDIR("mkdir", " [name] - создание новой директории с указанным именем."),
    RM("rm", " [filename] - удаление указанного файла или директории."),
    MV("mv", " [source] [destination] [-f] - переименовать/перенести файл или директорию. [-f] принудительная перезапись."),
    CP("cp", " [source] [destination] [-f] - скопировать файл в указанную директорию. [-f] принудительная перезапись."),
    FINFO("finfo", " [filename] - получить подробную информацию о файле."),
    FIND("find", " [filename] - найти файл с указанным именем в текущем каталоге или любом его подкаталоге."),
    EXIT("exit", " - завершить работу");

    private String name;
    private String description;

    ManagerCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "    /" + name + description;
    }
}