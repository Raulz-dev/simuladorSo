package br.edu.fs.simulator;


import br.edu.fs.simulator.util.MyArrayList;

public class FileSystemManager {

    private Directory root;
    private Directory currentDirectory;
    private Journal journal;


    private static class Node {
        String name;
        Directory parent;
        long creationTime;
        long lastModifiedTime;

        public Node(String name, Directory parent) {
            this.name = name;
            this.parent = parent;
            this.creationTime = System.currentTimeMillis();
            this.lastModifiedTime = this.creationTime;
        }

        public String getName() {
            return name;
        }

        public String getAbsolutePath() {
            if (parent == null) {
                return name.equals("/") ? "/" : "/" + name;
            }
            String parentPath = parent.getAbsolutePath();
            return parentPath.equals("/") ? "/" + name : parentPath + "/" + name;
        }
    }

    private static class FileNode extends Node {


        public FileNode(String name, Directory parent) {
            super(name, parent);

        }
    }

    private static class Directory extends Node {
        private MyArrayList<Node> children;

        public Directory(String name, Directory parent) {
            super(name, parent);
            this.children = new MyArrayList<>();
        }

        public void addChild(Node child) {
            children.add(child);
            child.parent = this;
        }

        public Node findChild(String name) {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                if (child.getName().equals(name)) {
                    return child;
                }
            }
            return null;
        }

        public MyArrayList<Node> getChildren() {
            return children;
        }
    }


    public FileSystemManager() {
        this.journal = new Journal();
        this.root = new Directory("/", null);
        this.currentDirectory = this.root;
        this.journal.logOperation("INIT_FS", "Sistema de arquivos inicializado. Raiz em /");
    }


    public Journal getJournal() {
        return this.journal;
    }

    public String getCurrentPath() {
        return currentDirectory.getAbsolutePath();
    }

    public void createDirectory(String name) {
        if (name == null || name.isEmpty() || name.contains("/") || name.contains(" ")) {
            System.out.println("Erro: Nome de diretório inválido.");
            journal.logOperation("MKDIR_FAIL", "Tentativa de criar diretório com nome inválido: " + (name == null ? "null" : name));
            return;
        }
        if (currentDirectory.findChild(name) != null) {
            System.out.println("Erro: Diretório '" + name + "' já existe.");
            journal.logOperation("MKDIR_FAIL", "Diretório já existe: " + currentDirectory.getAbsolutePath() + (currentDirectory.getAbsolutePath().equals("/") ? "" : "/") + name);
            return;
        }

        Directory newDir = new Directory(name, currentDirectory);
        currentDirectory.addChild(newDir);
        System.out.println("Diretório '" + name + "' criado.");
        // Log da operação no Journal
        journal.logOperation("MKDIR", "Diretório criado: " + newDir.getAbsolutePath());
    }

    public void createFile(String name) {
        if (name == null || name.isEmpty() || name.contains("/") || name.contains(" ")) {
            System.out.println("Erro: Nome de arquivo inválido.");
            journal.logOperation("CREATE_FILE_FAIL", "Tentativa de criar arquivo com nome inválido: " + (name == null ? "null" : name));
            return;
        }
        if (currentDirectory.findChild(name) != null) {
            System.out.println("Erro: Arquivo ou diretório '" + name + "' já existe.");
            journal.logOperation("CREATE_FILE_FAIL", "Arquivo/Diretório já existe: " + currentDirectory.getAbsolutePath() + (currentDirectory.getAbsolutePath().equals("/") ? "" : "/") + name);
            return;
        }

        FileNode newFile = new FileNode(name, currentDirectory);
        currentDirectory.addChild(newFile);
        System.out.println("Arquivo '" + name + "' criado.");
        // Log da operação no Journal
        journal.logOperation("CREATE_FILE", "Arquivo criado: " + newFile.getAbsolutePath());
    }

    public void listDirectory(String path) {
        Directory targetDir = path.equals(".") || path.isEmpty() ? currentDirectory : findDirectory(path);
        if (targetDir == null) {
            System.out.println("Erro: Diretório '" + path + "' não encontrado.");
            journal.logOperation("LS_FAIL", "Diretório não encontrado para listagem: " + path);
            return;
        }

        System.out.println("Conteúdo de " + targetDir.getAbsolutePath() + ":");
        MyArrayList<Node> children = targetDir.getChildren();
        if (children.isEmpty()) {
            System.out.println("(vazio)");
        } else {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String type = (child instanceof Directory) ? "d" : "f";
                System.out.println(type + " " + child.getName());
            }
        }
        journal.logOperation("LS", "Listado diretório: " + targetDir.getAbsolutePath());
    }

    public void changeDirectory(String path) {
        if (path.equals("..")) {
            if (currentDirectory.parent != null) {
                currentDirectory = currentDirectory.parent;
                journal.logOperation("CD", "Mudou para diretório pai: " + currentDirectory.getAbsolutePath());
            } else {
                System.out.println("Erro: Já está no diretório raiz.");
                journal.logOperation("CD_FAIL", "Tentativa de ir para pai da raiz.");
            }
            return;
        }

        Directory targetDir = findDirectory(path);
        if (targetDir != null) {
            currentDirectory = targetDir;
            journal.logOperation("CD", "Mudou para diretório: " + currentDirectory.getAbsolutePath());
        } else {
            System.out.println("Erro: Diretório '" + path + "' não encontrado.");
            journal.logOperation("CD_FAIL", "Diretório não encontrado: " + path);
        }
    }


    private Directory findDirectory(String path) {
        if (path.equals("/")) {
            return root;
        }
        if (path.startsWith("/")) {
            String[] parts = path.substring(1).split("/");
            Directory current = root;
            for (String part : parts) {
                if (part.isEmpty()) continue;
                Node child = current.findChild(part);
                if (child instanceof Directory) {
                    current = (Directory) child;
                } else {
                    return null;
                }
            }
            return current;
        } else {
            String[] parts = path.split("/");
            Directory current = currentDirectory;
            for (String part : parts) {
                if (part.isEmpty()) continue;
                Node child = current.findChild(part);
                if (child instanceof Directory) {
                    current = (Directory) child;
                } else {
                    return null;
                }
            }
            return current;
        }
    }

    public void delete(String path) {

        Node nodeToRemove = currentDirectory.findChild(path);

        if (nodeToRemove == null) {
            System.out.println("Erro: Arquivo ou diretório '" + path + "' não encontrado.");
            journal.logOperation("RM_FAIL", "Não encontrado para remover: " + currentDirectory.getAbsolutePath() + (currentDirectory.getAbsolutePath().equals("/") ? "" : "/") + path);
            return;
        }

        if (nodeToRemove instanceof Directory) {
            Directory dirToRemove = (Directory) nodeToRemove;
            if (!dirToRemove.getChildren().isEmpty()) {
                System.out.println("Erro: Diretório '" + path + "' não está vazio.");
                journal.logOperation("RM_FAIL", "Tentativa de remover diretório não vazio: " + dirToRemove.getAbsolutePath());
                return;
            }
        }


        String removedPath = nodeToRemove.getAbsolutePath();
        boolean removed = currentDirectory.children.remove(nodeToRemove);

        if (removed) {
            System.out.println((nodeToRemove instanceof Directory ? "Diretório" : "Arquivo") + " '" + path + "' removido.");
            journal.logOperation("RM", (nodeToRemove instanceof Directory ? "Diretório removido: " : "Arquivo removido: ") + removedPath);
        } else {
            System.out.println("Erro ao tentar remover '" + path + "'.");
            journal.logOperation("RM_FAIL", "Erro interno ao remover: " + removedPath);
        }
    }



}