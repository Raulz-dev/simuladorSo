package br.edu.fs.simulator;

import br.edu.fs.simulator.util.MyArrayList;

public class DirectoryNode extends Node {
    private MyArrayList<Node> children;

    public DirectoryNode(String name, DirectoryNode parent) {
        super(name, parent, NodeType.DIRECTORY);
        this.children = new MyArrayList<>();
    }

    public MyArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        if (child == null) return;
        if (findChild(child.getName()) != null) {
            System.err.println("Error: Node with name '" + child.getName() + "' already exists in this directory.");
            return;
        }
        child.setParent(this);
        children.add(child);
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean removeChild(String name) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getName().equals(name)) {
                children.remove(i);
                this.updatedAt = System.currentTimeMillis();
                return true;
            }
        }
        return false;
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

    @Override
    public int getSize() {
        int totalSize = 0;
        for (int i = 0; i < children.size(); i++) {
            totalSize += children.get(i).getSize();
        }
        return totalSize;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}