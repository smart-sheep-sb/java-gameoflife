package com.gameoflife.console;

import com.gameoflife.core.GameBoard;

public class ConsoleRenderer {
    private String lastBoardHash = "";//记录上一次的网络状态
    private int stableCount = 0;//稳定持续计数

    public void render(GameBoard board) {// 渲染网格到控制台
        int rows = board.getRows();
        int cols = board.getCols();

        System.out.print("┌");// 打印顶部边框
        for (int j = 0; j < cols; j++) {
            System.out.print("──");
        }
        System.out.println("┐");

        for (int i = 0; i < rows; i++) {// 打印每一行
            System.out.print("│");  // 左边框
            for (int j = 0; j < cols; j++) {
                if (board.getCell(i, j)) {
                    System.out.print("● ");//活细胞用实心圆
                } else {
                    System.out.print("○ ");//死细胞用空心圆
                }
            }
            System.out.println("│");//右边框
        }

        System.out.print("└");// 打印底部边框
        for (int j = 0; j < cols; j++) {
            System.out.print("──");
        }
        System.out.println("┘");
    }

    public void clearScreen() {// 清屏方法（通过打印大量空行实现）
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public void showInfo(GameBoard board, int generation) {// 显示网格信息
        int aliveCount = 0;// 统计活细胞数量
        StringBuilder currentHash = new StringBuilder();

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                if (board.getCell(i, j)) {
                    aliveCount++;
                    currentHash.append(i).append(",").append(j).append(";");//生成位置字符串
                }
            }
        }
        String currentBoardHash = currentHash.toString();

        if (currentBoardHash.equals(lastBoardHash)){//检测是否进入稳定状态
            stableCount++;
        }else {
            stableCount = 0;
            lastBoardHash =currentBoardHash;
        }

        System.out.println("第 " + generation + " 代 | 活细胞数: " + aliveCount);
        System.out.println("▶ 已稳定持续 " + stableCount + "代");

}
public void showDebugInfo(GameBoard board){
    System.out.println("\n活细胞位置：");
    for (int i = 0; i < board.getRows(); i++){
        for (int j = 0; j < board.getCols(); j++){
            if (board.getCell(i, j)){
                System.out.print("(" + i + "," + j + ") ");
            }
        }
    }
    System.out.println();
    }
}