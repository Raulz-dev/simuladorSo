package br.edu.fs.simulator;

import br.edu.fs.simulator.util.MyArrayList;
import br.edu.fs.simulator.util.MyStringBuilder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSystemManager {
    private DirectoryNode root;
    private DirectoryNode currentDirectory;
    private Journal journal;

    public FileSystemManager() {
        this.root = new DirectoryNode("/", null);
        this.root.setParent(this.root);
        this.currentDirectory = this.root;
        this.journal = new Journal();
        journal.logOperation("INIT", "File system initialized. Root created.");
    }

    private Node getNodeByPath(String path) {
        if (path == null || path.isEmpty()) return null;

        String[] parts;
        DirectoryNode startNode;

        if (path.equals("/")) return root;

        if (path.startsWith("/")) {
            startNode = root;
            if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            parts = path.substring(1).split("/");
        } else {
            startNode = currentDirectory;
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            parts = path.split("/");
        }

        if (parts.length == 1 && parts[0].isEmpty() && path.startsWith("/")) { // Handles root itself when path is just "/"
            return root;
        }


        Node currentNode = startNode;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (part.equals(".")) {
                continue;
            } else if (part.equals("..")) {
                if (currentNode instanceof DirectoryNode) {
                    currentNode = ((DirectoryNode) currentNode).getParent();
                    if (currentNode == null) return null;
                } else {
                    return null;
                }
            } else {
                if (currentNode instanceof DirectoryNode) {
                    currentNode = ((DirectoryNode) currentNode).findChild(part);
                    if (currentNode == null) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return currentNode;
    }

    private DirectoryNode getParentDirectoryFromPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return null;
        }

        int lastSlash = -1;
        for(int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/') {
                lastSlash = i;
                break;
            }
        }

        if (lastSlash == -1) {
            return currentDirectory;
        }
        if (lastSlash == 0) {
            return root;
        }

        String parentPath = path.substring(0, lastSlash);
        Node parentNode = getNodeByPath(parentPath);
        if (parentNode instanceof DirectoryNode) {
            return (DirectoryNode) parentNode;
        }
        return null;
    }

    private String getBaseNameFromPath(String path) {
        if (path == null || path.isEmpty()) return "";
        if (path.equals("/")) return "/";

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() -1);
        }

        int lastSlash = -1;
        for(int i = path.length() - 1; i >= 0; i--) {
            if (path.charAt(i) == '/') {
                lastSlash = i;
                break;
            }
        }
        return path.substring(lastSlash + 1);
    }

    public void mkdir(String path) {
        String dirName = getBaseNameFromPath(path);
        DirectoryNode parentDir = getParentDirectoryFromPath(path);

        if (dirName.isEmpty() || dirName.equals(".") || dirName.equals("..")) {
            System.out.println("Error: Invalid directory name '" + dirName + "'.");
            journal.logOperation("MKDIR_FAIL", "Invalid name: " + path);
            return;
        }
        if (parentDir == null) {
            System.out.println("Error: Cannot create directory. Parent path does not exist or is not a directory: " + path);
            journal.logOperation("MKDIR_FAIL", "Parent not found for: " + path);
            return;
        }
        if (parentDir.findChild(dirName) != null) {
            System.out.println("Error: Directory or file '" + dirName + "' already exists in '" + parentDir.getPath() + "'.");
            journal.logOperation("MKDIR_FAIL", "Already exists: " + path);
            return;
        }

        DirectoryNode newDir = new DirectoryNode(dirName, parentDir);
        parentDir.addChild(newDir);
        System.out.println("Directory created: " + newDir.getPath());
        journal.logOperation("MKDIR", newDir.getPath());
    }

    public void createFile(String path, String content) {
        String fileName = getBaseNameFromPath(path);
        DirectoryNode parentDir = getParentDirectoryFromPath(path);

        if (fileName.isEmpty() || fileName.equals(".") || fileName.equals("..")) {
            System.out.println("Error: Invalid file name '" + fileName + "'.");
            journal.logOperation("CREATE_FAIL", "Invalid name: " + path);
            return;
        }
        if (parentDir == null) {
            System.out.println("Error: Cannot create file. Parent path does not exist or is not a directory: " + path);
            journal.logOperation("CREATE_FAIL", "Parent not found for: " + path);
            return;
        }
        if (parentDir.findChild(fileName) != null) {
            System.out.println("Error: Directory or file '" + fileName + "' already exists in '" + parentDir.getPath() + "'.");
            journal.logOperation("CREATE_FAIL", "Already exists: " + path);
            return;
        }

        FileNode newFile = new FileNode(fileName, parentDir);
        if (content != null) {
            newFile.setContent(content);
        }
        parentDir.addChild(newFile);
        System.out.println("File created: " + newFile.getPath());
        journal.logOperation("CREATE", newFile.getPath() + " (Content Length: " + newFile.getSize() + ")");
    }

    public void ls(String path) {
        Node targetNode;
        if (path == null || path.trim().isEmpty()) {
            targetNode = currentDirectory;
        } else {
            targetNode = getNodeByPath(path);
        }

        if (targetNode == null) {
            System.out.println("ls: cannot access '" + path + "': No such file or directory");
            journal.logOperation("LS_FAIL", "Path not found: " + (path == null ? currentDirectory.getPath() : path));
            return;
        }

        if (targetNode instanceof DirectoryNode) {
            DirectoryNode dirToList = (DirectoryNode) targetNode;
            MyArrayList<Node> children = dirToList.getChildren();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");

            if (children.isEmpty()) {
                System.out.println("(empty directory)");
            } else {
                System.out.println("Type  Size      Last Modified    Name");
                System.out.println("----  --------  ---------------  ----");
                for (int i = 0; i < children.size(); i++) {
                    Node child = children.get(i);
                    String typeStr = (child.getType() == NodeType.DIRECTORY) ? "DIR  " : "FILE ";
                    MyStringBuilder sb = new MyStringBuilder();
                    sb.append(typeStr);
                    sb.append("  ");
                    String sizeStr = String.valueOf(child.getSize());
                    for(int k=0; k < (8 - sizeStr.length()); k++) sb.append(" "); // padding
                    sb.append(sizeStr);
                    sb.append("  ");
                    sb.append(sdf.format(new Date(child.getUpdatedAt())));
                    sb.append("  ");
                    sb.append(child.getName());
                    System.out.println(sb.toString());
                }
            }
            journal.logOperation("LS", dirToList.getPath());
        } else {
            System.out.println(targetNode.getName() + " (File)");
            journal.logOperation("LS", targetNode.getPath());
        }
    }

    public void cat(String path) {
        Node node = getNodeByPath(path);
        if (node == null) {
            System.out.println("cat: " + path + ": No such file or directory");
            journal.logOperation("CAT_FAIL", "Not found: " + path);
            return;
        }
        if (node instanceof FileNode) {
            System.out.println(((FileNode) node).getContent());
            journal.logOperation("CAT", path);
        } else {
            System.out.println("cat: " + path + ": Is a directory");
            journal.logOperation("CAT_FAIL", "Is directory: " + path);
        }
    }

    public void cd(String path) {
        if (path.equals("..")) {
            if (currentDirectory.getParent() != null) {
                currentDirectory = currentDirectory.getParent();
                journal.logOperation("CD", currentDirectory.getPath());
            } else {
                journal.logOperation("CD_FAIL", "Already at root");
            }
            return;
        } else if (path.equals("/") || (path.isEmpty() && currentDirectory == root)) {
            currentDirectory = root;
            journal.logOperation("CD", root.getPath());
            return;
        }

        Node targetNode = getNodeByPath(path);
        if (targetNode != null && targetNode instanceof DirectoryNode) {
            currentDirectory = (DirectoryNode) targetNode;
            journal.logOperation("CD", currentDirectory.getPath());
        } else if (targetNode != null && targetNode instanceof FileNode) {
            System.out.println("cd: " + path + ": Not a directory");
            journal.logOperation("CD_FAIL", "Not a directory: " + path);
        } else {
            System.out.println("cd: " + path + ": No such file or directory");
            journal.logOperation("CD_FAIL", "Not found: " + path);
        }
    }

    public String pwd() {
        String path = currentDirectory.getPath();
        journal.logOperation("PWD", path);
        return path;
    }

    public void rm(String path) {
        Node nodeToRemove = getNodeByPath(path);

        if (nodeToRemove == null) {
            System.out.println("rm: cannot remove '" + path + "': No such file or directory");
            journal.logOperation("RM_FAIL", "Not found: " + path);
            return;
        }
        if (nodeToRemove == root) {
            System.out.println("rm: cannot remove root directory '/'");
            journal.logOperation("RM_FAIL", "Cannot remove root");
            return;
        }

        DirectoryNode parent = nodeToRemove.getParent();
        if (parent == null) { // Should not happen if not root
            System.out.println("rm: Internal error, node has no parent: " + path);
            journal.logOperation("RM_FAIL", "Internal error (no parent): " + path);
            return;
        }

        if (nodeToRemove instanceof DirectoryNode) {
            DirectoryNode dirToRemove = (DirectoryNode) nodeToRemove;
            if (!dirToRemove.isEmpty()) {
                // Simple version: does not allow removing non-empty dirs without -r flag
                // For now, let's keep it simple and prevent non-empty dir removal
                System.out.println("rm: cannot remove '" + path + "': Directory not empty");
                journal.logOperation("RM_FAIL", "Directory not empty: " + path);
                return;
            }
        }

        boolean success = parent.removeChild(nodeToRemove.getName());
        if (success) {
            System.out.println("Removed: " + path);
            journal.logOperation("RM", path);
        } else {
            // This case should ideally not be reached if getNodeByPath worked.
            System.out.println("rm: failed to remove '" + path + "' (internal error or already removed)");
            journal.logOperation("RM_FAIL", "Failed to remove (internal): " + path);
        }
    }

    public void mv(String sourcePath, String destPath) {
        Node sourceNode = getNodeByPath(sourcePath);
        if (sourceNode == null) {
            System.out.println("mv: cannot stat '" + sourcePath + "': No such file or directory");
            journal.logOperation("MV_FAIL", "Source not found: " + sourcePath);
            return;
        }
        if (sourceNode == root) {
            System.out.println("mv: cannot move root directory '/'");
            journal.logOperation("MV_FAIL", "Cannot move root");
            return;
        }

        Node destNodeTarget = getNodeByPath(destPath);
        DirectoryNode destParentDir;
        String newName = getBaseNameFromPath(destPath);

        if (destNodeTarget != null && destNodeTarget.getType() == NodeType.DIRECTORY) {

            destParentDir = (DirectoryNode) destNodeTarget;
            newName = sourceNode.getName();
            if (destParentDir.findChild(newName) != null) {
                System.out.println("mv: cannot move '" + sourcePath + "' to '" + destPath + "/" + newName + "': Destination exists");
                journal.logOperation("MV_FAIL", "Destination exists (in dir): " + destPath + "/" + newName);
                return;
            }
        } else if (destNodeTarget != null && destNodeTarget.getType() == NodeType.FILE) {

            System.out.println("mv: cannot overwrite non-directory '" + destPath + "' with directory or file '" + sourcePath + "' (or vice-versa without explicit overwrite logic)");
            journal.logOperation("MV_FAIL", "Destination is a file and overwrite not implemented: " + destPath);
            return;
        }
        else {
            destParentDir = getParentDirectoryFromPath(destPath);
            if (destParentDir == null) {
                System.out.println("mv: cannot move to '" + destPath + "': Parent directory does not exist");
                journal.logOperation("MV_FAIL", "Dest parent not found: " + destPath);
                return;
            }
            if (destParentDir.findChild(newName) != null) {
                System.out.println("mv: cannot move '" + sourcePath + "' to '" + destPath + "': Destination exists");
                journal.logOperation("MV_FAIL", "Destination exists (as file/dir): " + destPath);
                return;
            }
        }


        if (sourceNode.getType() == NodeType.DIRECTORY) {
            Node temp = destParentDir;
            while (temp != null && temp != root) {
                if (temp == sourceNode) {
                    System.out.println("mv: cannot move directory '" + sourcePath + "' into itself or a subdirectory");
                    journal.logOperation("MV_FAIL", "Cannot move directory into itself/subdir: " + sourcePath + " to " + destPath);
                    return;
                }
                temp = temp.getParent();
            }
        }



        DirectoryNode oldParent = sourceNode.getParent();
        if (oldParent != null) {
            oldParent.removeChild(sourceNode.getName());
        }


        sourceNode.setName(newName);
        sourceNode.setParent(destParentDir);
        destParentDir.addChild(sourceNode);
        sourceNode.setUpdatedAt(System.currentTimeMillis());


        System.out.println("Moved '" + sourcePath + "' to '" + destParentDir.getPath() + (destParentDir==root && newName.startsWith("/") ? "" : "/") + newName + "'");
        journal.logOperation("MV", sourcePath + " -> " + destParentDir.getPath() + (destParentDir==root && newName.startsWith("/") ? "" : "/") + newName);
    }

    public void find(String name) {
        MyArrayList<String> foundPaths = new MyArrayList<>();
        findRecursive(root, name, foundPaths);
        if (foundPaths.isEmpty()) {
            System.out.println("find: No file or directory named '" + name + "' found.");
        } else {
            System.out.println("Found occurrences of '" + name + "':");
            for (int i = 0; i < foundPaths.size(); i++) {
                System.out.println(foundPaths.get(i));
            }
        }
        journal.logOperation("FIND", name + " (Found: " + foundPaths.size() + ")");
    }

    private void findRecursive(DirectoryNode currentDir, String nameToFind, MyArrayList<String> foundPaths) {
        if (currentDir.getName().equals(nameToFind)) {
            foundPaths.add(currentDir.getPath());
        }
        MyArrayList<Node> children = currentDir.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            if (child.getName().equals(nameToFind)) {
                foundPaths.add(child.getPath());
            }
            if (child instanceof DirectoryNode) {
                findRecursive((DirectoryNode) child, nameToFind, foundPaths);
            }
        }
    }

    public void tree(String path) {
        Node startNode;
        if (path == null || path.trim().isEmpty() || path.equals(".")) {
            startNode = currentDirectory;
        } else {
            startNode = getNodeByPath(path);
        }

        if (startNode == null) {
            System.out.println("tree: " + (path == null ? "." : path) + ": No such file or directory");
            journal.logOperation("TREE_FAIL", "Path not found: " + (path == null ? currentDirectory.getPath() : path));
            return;
        }

        System.out.println(startNode.getName() + (startNode.getType() == NodeType.DIRECTORY ? " [dir]" : " [file]"));
        if (startNode instanceof DirectoryNode) {
            printTreeRecursive((DirectoryNode) startNode, "", true);
        }
        journal.logOperation("TREE", startNode.getPath());
    }

    private void printTreeRecursive(DirectoryNode dir, String prefix, boolean isLast) {
        MyArrayList<Node> children = dir.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            boolean currentIsLast = (i == children.size() - 1);
            System.out.print(prefix);
            System.out.print(isLast && prefix.endsWith("`-- ") || !prefix.isEmpty() && prefix.endsWith("   ") ? "    " : "|   ");
            System.out.print(currentIsLast ? "`-- " : "|-- ");
            System.out.println(child.getName() + (child.getType() == NodeType.DIRECTORY ? " [dir]" : " [file]"));

            if (child instanceof DirectoryNode) {
                MyStringBuilder newPrefix = new MyStringBuilder(prefix);
                if (isLast && prefix.endsWith("`-- ") || !prefix.isEmpty() && prefix.endsWith("   ")) {
                    newPrefix.append("    ");
                } else {
                    newPrefix.append(currentIsLast ? "    " : "|   ");
                }

                printTreeRecursive((DirectoryNode) child, newPrefix.toString(), currentIsLast);
            }
        }
    }

    public void flushJournal(String filePath) {
        journal.flushToFile(filePath);
    }

    public void showJournal() {
        journal.printLog();
    }

    public DirectoryNode getCurrentDirectory() {
        return currentDirectory;
    }
}