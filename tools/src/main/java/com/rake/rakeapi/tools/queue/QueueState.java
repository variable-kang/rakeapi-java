package com.rake.rakeapi.tools.queue;

import com.rake.rakeapi.queue.FileBlockingQueue;

public class QueueState {
    private String promptPrefix;
    private String prompt;
    private String promptSuffix;
    private String queueDir;
    private FileBlockingQueue<String> fileBlockingQueue;
    private boolean isOpened;

    public QueueState() {
        setPromptPrefix(" ");
        setPromptSuffix("> ");
        setPrompt("");
    }

    public String getAllPrompt() {
        return getPromptPrefix() + getPrompt() + getPromptSuffix();
    }

    public String getPromptPrefix() {
        return promptPrefix;
    }

    public void setPromptPrefix(String promptPrefix) {
        this.promptPrefix = promptPrefix;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getPromptSuffix() {
        return promptSuffix;
    }

    public void setPromptSuffix(String promptSuffix) {
        this.promptSuffix = promptSuffix;
    }

    public String getQueueDir() {
        return queueDir;
    }

    public void setQueueDir(String queueDir) {
        this.queueDir = queueDir;
    }

    public FileBlockingQueue<String> getFileBlockingQueue() {
        return fileBlockingQueue;
    }

    public void setFileBlockingQueue(FileBlockingQueue<String> fileBlockingQueue) {
        this.fileBlockingQueue = fileBlockingQueue;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }
}
