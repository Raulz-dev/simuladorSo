package br.edu.fs.simulator; // Ou o pacote onde esta classe deve estar

import java.util.Scanner;


public class FileSystemSimulatorShell {

    private static FileSystemManager fileSystemManager;
    private static Journal journal;

    public static void main(String[] args) {

        if (fileSystemManager == null) {
            System.out.println("[AVISO] FileSystemManager não inicializado. Alguns comandos não funcionarão.");
        }
        if (journal == null) {
            System.out.println("[AVISO] Journal não inicializado. Comandos de journal não funcionarão.");
        }


        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Bem-vindo ao File System Simulator!");
        System.out.println("Digite 'help' para ver os comandos disponíveis.");

        while (running) {
            System.out.print("fs> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\s+", 2);
            String commandName = parts[0].toLowerCase();
            String arg = (parts.length > 1) ? parts[1] : null;

            try {
                switch (commandName) {
                    case "help":
                        displayHelp();
                        break;
                    case "mkdir":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: mkdir <dirname>");
                        } else {
                            System.out.println("Comando 'mkdir " + arg.trim() + "' (a ser implementado).");
                        }
                        break;
                    case "cd":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: cd <dirname>");
                        } else {
                            System.out.println("Comando 'cd " + arg.trim() + "' (a ser implementado).");
                        }
                        break;
                    case "ls":
                        System.out.println("Comando 'ls " + (arg == null ? "" : arg.trim()) + "' (a ser implementado).");
                        break;
                    case "create":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: create <filename> <content...>");
                        } else {
                            String[] createArgs = arg.trim().split("\s+", 2);
                            if (createArgs.length < 2) {
                                System.out.println("Uso: create <filename> <content...>");
                            } else {
                                String filename = createArgs[0];
                                String content = createArgs[1];
                                System.out.println("Comando 'create " + filename + " com conteúdo' (a ser implementado).");
                            }
                        }
                        break;
                    case "cat":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: cat <filename>");
                        } else {
                            System.out.println("Comando 'cat " + arg.trim() + "' (a ser implementado).");
                        }
                        break;
                    case "rm":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: rm <path>");
                        } else {
                            System.out.println("Comando 'rm " + arg.trim() + "' (a ser implementado).");
                        }
                        break;
                    case "rmdir":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: rmdir <dirname>");
                        } else {
                            System.out.println("Comando 'rmdir " + arg.trim() + "' (a ser implementado).");
                        }
                        break;
                    case "mv":
                        if (arg == null || arg.trim().isEmpty() || arg.trim().split("\s+").length < 2) {
                            System.out.println("Uso: mv <source> <destination>");
                        } else {
                            String[] mvArgs = arg.trim().split("\s+", 2);
                            System.out.println("Comando 'mv " + mvArgs[0] + " " + mvArgs[1] + "' (a ser implementado).");
                        }
                        break;
                    case "cp":
                        if (arg == null || arg.trim().isEmpty() || arg.trim().split("\s+").length < 2) {
                            System.out.println("Uso: cp <source> <destination>");
                        } else {
                            String[] cpArgs = arg.trim().split("\s+", 2);
                            System.out.println("Comando 'cp " + cpArgs[0] + " " + cpArgs[1] + "' (a ser implementado).");
                        }
                        break;
                    case "tree":
                        System.out.println("Comando 'tree " + (arg == null ? "" : arg.trim()) + "' (a ser implementado).");
                        break;
                    case "search":
                        if (arg == null || arg.trim().isEmpty()) {
                            System.out.println("Uso: search <keyword> [path]");
                        } else {
                            String[] searchArgs = arg.trim().split("\s+", 2);
                            String keyword = searchArgs[0];
                            String searchPath = (searchArgs.length > 1) ? searchArgs[1] : null;
                            System.out.println("Comando 'search " + keyword + (searchPath != null ? " in " + searchPath : "") + "' (a ser implementado).");
                        }
                        break;
                    case "pwd":
                        System.out.println("Comando 'pwd' (a ser implementado).");
                        break;
                    case "journal":
                        if (arg != null && arg.equalsIgnoreCase("flush")) {
                            System.out.println("Comando 'journal flush' (a ser implementado).");
                        } else if (arg == null) {
                            System.out.println("Comando 'journal' (a ser implementado).");
                        } else {
                            System.out.println("Uso: journal [flush]");
                        }
                        break;
                    case "exit":
                        running = false;
                        System.out.println("Saindo do simulador...");
                        break;
                    default:
                        System.out.println("Comando não reconhecido: " + commandName + ". Digite 'help' para ajuda.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Ocorreu um erro ao processar o comando: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void displayHelp() {
        System.out.println("Comandos disponíveis:");
        System.out.println("  help                          - Mostra esta mensagem de ajuda.");
        System.out.println("  mkdir <dirname>               - Cria um novo diretório.");
        System.out.println("  cd <dirname>                  - Muda o diretório atual. Use '..' para subir.");
        System.out.println("  ls [dirname]                  - Lista o conteúdo do diretório.");
        System.out.println("  create <filename> <content>   - Cria um novo arquivo com conteúdo.");
        System.out.println("  cat <filename>                - Mostra o conteúdo do arquivo.");
        System.out.println("  rm <path>                     - Remove um arquivo ou diretório (vazio).");
        System.out.println("  rmdir <dirname>               - Remove um diretório vazio (alternativa ao rm).");
        System.out.println("  mv <source> <destination>     - Move/renomeia um arquivo ou diretório.");
        System.out.println("  cp <source> <destination>     - Copia um arquivo ou diretório.");
        System.out.println("  tree [dirname]                - Mostra a árvore de diretórios.");
        System.out.println("  search <keyword> [path]       - Procura por uma palavra-chave em nomes/conteúdos.");
        System.out.println("  pwd                           - Mostra o caminho do diretório de trabalho atual.");
        System.out.println("  journal                       - Mostra todas as entradas do journal.");
        System.out.println("  journal flush                 - Escreve todas as entradas do journal para journal.log e limpa o journal em memória.");
        System.out.println("  exit                          - Sai do shell.");
    }
}