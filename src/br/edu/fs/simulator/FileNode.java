package br.edu.fs.simulator;

import br.edu.fs.simulator.util.MyStringBuilder;

public class FileNode extends Node {
    private MyStringBuilder content;

    public FileNode(String name, DirectoryNode parent) {
        super(name, parent, NodeType.FILE);
        this.content = new MyStringBuilder();
    }

    public String getContent() {
        return content.toString();
    }

    public void setContent(String newContent) {
        this.content.clear();
        this.content.append(newContent);
        this.updatedAt = System.currentTimeMillis();
    }

    public void appendContent(String additionalContent) {
        this.content.append(additionalContent);
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public int getSize() {
        return content.length();
    }
}