package com.gameoflife;

import com.gameoflife.ui.GameOfLifeUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {// 启动图形界面
            new GameOfLifeUI().setVisible(true);
        });
    }
    // 控制台版本
    private static void runConsoleVersion() throws InterruptedException {
        com.gameoflife.core.GameBoard board = new com.gameoflife.core.GameBoard(20, 20);
        board.setCell(10, 10, true);
        board.setCell(10, 11, true);
        board.setCell(11, 10, true);
        board.setCell(11, 11, true);

        com.gameoflife.console.ConsoleRenderer renderer = new com.gameoflife.console.ConsoleRenderer();

        int generation = 0;
        while (true) {
            renderer.clearScreen();
            System.out.println("=== 生命游戏 ===\n");
            renderer.render(board);
            renderer.showInfo(board, generation);
            board.nextGeneration();
            generation++;
            Thread.sleep(500);
        }
    }
}