package br.edu.fs.simulator;


import java.util.Scanner;


public class FileSystemSimulatorShell {

    private FileSystemManager fsManager;


    public FileSystemSimulatorShell() {

        this.fsManager = new FileSystemManager();

        System.out.println("File System Simulator Shell. Digite 'help' para comandos.");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String line;
        boolean running = true;

        while (running) {
            System.out.print(fsManager.getCurrentPath() + "> ");
            line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            //

            String[] parts = line.split("\s+", 2);
            String command = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "mkdir":
                    if (!arg.isEmpty()) {
                        fsManager.createDirectory(arg);
                    } else {
                        System.out.println("Uso: mkdir <nome_diretorio>");
                    }
                    break;
                case "cd":
                    if (!arg.isEmpty()) {
                        fsManager.changeDirectory(arg);
                    } else {
                        System.out.println("Uso: cd <caminho>");
                    }
                    break;
                case "ls":
                    fsManager.listDirectory(arg.isEmpty() ? "." : arg);
                    break;
                case "create":
                    if (!arg.isEmpty()) {
                        fsManager.createFile(arg);
                    } else {
                        System.out.println("Uso: create <nome_arquivo>");
                    }
                    break;
                case "rm":
                    if (!arg.isEmpty()) {
                        fsManager.delete(arg);
                    } else {
                        System.out.println("Uso: rm <caminho>");
                    }
                    break;
                case "pwd":
                    System.out.println(fsManager.getCurrentPath());
                    break;
                case "log":

                    Journal journal = fsManager.getJournal();
                    if (journal != null) {
                        journal.printLog();
                    } else {
                        System.out.println("Erro: Journal não está disponível.");
                    }
                    break;
                case "journal":
                    if (arg.equalsIgnoreCase("flush")) {
                        Journal j = fsManager.getJournal();
                        if (j != null) {
                            j.flushToFile("journal.log");
                        } else {
                            System.out.println("Erro: Journal não está disponível.");
                        }
                    } else {
                        System.out.println("Uso: journal flush");
                    }
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    running = false;
                    System.out.println("Saindo do simulador.");
                    break;
                default:
                    System.out.println("Comando desconhecido: " + command);
                    break;
            }
        }
        scanner.close();
    }

    private void printHelp() {
        System.out.println("Comandos disponíveis:");
        System.out.println("  mkdir <nome_diretorio>    - Cria um novo diretório.");
        System.out.println("  cd <caminho>              - Muda o diretório atual.");
        System.out.println("  ls [caminho]              - Lista o conteúdo do diretório.");
        System.out.println("  create <nome_arquivo>     - Cria um novo arquivo (exemplo).");
        System.out.println("  rm <caminho>              - Remove um arquivo ou diretório (exemplo).");
        System.out.println("  pwd                       - Mostra o caminho do diretório atual.");
        System.out.println("  log                       - Mostra o log de operações do sistema (do Journal em memória).");
        System.out.println("  journal flush             - Escreve todas as entradas do journal para journal.log e limpa o journal em memória.");
        System.out.println("  help                      - Mostra esta ajuda.");
        System.out.println("  exit                      - Sai do shell.");
    }

    public static void main(String[] args) {
        FileSystemSimulatorShell shell = new FileSystemSimulatorShell();
        shell.start();
    }
}