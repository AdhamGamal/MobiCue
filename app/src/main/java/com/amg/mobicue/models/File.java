package com.amg.mobicue.models;

public class File {
    private final String FILE_NAME;
    private final String FILE_CONTENT;
    private final int TEXT_SIZE;
    private final String TEXT_STYLE;
    private final int SCROLL_SPEED;


    public File(String FILE_NAME, String FILE_CONTENT, int TEXT_SIZE, String TEXT_STYLE, int SCROLL_SPEED) {
        this.FILE_NAME = FILE_NAME;
        this.FILE_CONTENT = FILE_CONTENT;
        this.TEXT_SIZE = TEXT_SIZE;
        this.TEXT_STYLE = TEXT_STYLE;
        this.SCROLL_SPEED = SCROLL_SPEED;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public String getFILE_CONTENT() {
        return FILE_CONTENT;
    }

    public int getTEXT_SIZE() {
        return TEXT_SIZE;
    }

    public String getTEXT_STYLE() {
        return TEXT_STYLE;
    }

    public int getSCROLL_SPEED() {
        return SCROLL_SPEED;
    }
}
