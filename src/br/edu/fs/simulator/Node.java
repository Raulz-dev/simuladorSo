package br.edu.fs.simulator;
import br.edu.fs.simulator.util.MyStringBuilder;
import br.edu.fs.simulator.util.MyArrayList;

public abstract class Node {
    protected String name;
    protected DirectoryNode parent;
    protected long createdAt;
    protected long updatedAt;
    protected NodeType type;

    public Node(String name, DirectoryNode parent, NodeType type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public DirectoryNode getParent() {
        return parent;
    }

    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public NodeType getType() {
        return type;
    }

    public String getPath() {
        if (parent == null) {
            return "/";
        }
        MyStringBuilder pathBuilder = new MyStringBuilder();
        Node current = this;
        MyArrayList<String> pathParts = new MyArrayList<>();

        while (current != null && current.getParent() != null) {
            pathParts.add(0, current.getName());
            current = current.getParent();
        }

        if (pathParts.isEmpty() && this.getName().equals("/")) {
            return "/";
        }

        for (int i = 0; i < pathParts.size(); i++) {
            pathBuilder.append("/");
            pathBuilder.append(pathParts.get(i));
        }

        if (pathBuilder.length() == 0) {
            if (!this.name.equals("/")) return "/" + this.name;
            else return "/";
        }

        return pathBuilder.toString();
    }


    public abstract int getSize();
}