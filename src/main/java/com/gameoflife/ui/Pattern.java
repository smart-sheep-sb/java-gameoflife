package com.gameoflife.ui;

public class Pattern {
    private String name;        // 图案名称
    private String description; // 图案描述
    private int[][] cells;      // 图案数据（1表示活细胞，0表示死细胞）

    public Pattern(String name, String description, int[][] cells) {
        this.name = name;
        this.description = description;
        this.cells = cells;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int[][] getCells() {
        return cells;
    }
    public int getWidth() {
        return cells[0].length;
    }
    public int getHeight() {
        return cells.length;
    }
}