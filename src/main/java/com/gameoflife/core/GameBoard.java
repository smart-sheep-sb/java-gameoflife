package com.gameoflife.core;

public class GameBoard {
    private boolean[][] grid;// 二维数组存储细胞状态，true=活，false=死
    private int rows;//网络行数
    private int cols;//网络列数

    public GameBoard(int rows, int cols) {//初始化指定大学的网络，所有细胞默认死亡
        this.rows = rows;
        this.cols = cols;
        this.grid = new boolean[rows][cols];//boolean数值默认值是false
    }
    public void randomize(double aliveProbability) {//随机初始化：每个细胞以aliveProbability的概念变为存活状态
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = (Math.random() < aliveProbability);//Math.random()返回0.0-1.0之间的随机数
            }
        }
    }
    public void nextGeneration(){//计算下一代：核心逻辑
        boolean[][] newGrid = new boolean[rows][cols];//创建新网络存储下一代
        for (int i = 0 ; i < rows; i++){
            for (int j = 0; j < cols; j++){
                int aliveNeighbors = countAliveNeighbors(i , j);//统计邻居活细胞数
                if (grid[i][j]){//当前细胞是活的
                    // 规则1：活细胞周围活邻居少于2个，死于孤独
                    // 规则2：活细胞周围活邻居多于3个，死于 overcrowding
                    // 规则3：活细胞周围有2-3个活邻居，继续存活
                    newGrid[i][j] = (aliveNeighbors == 2 ||aliveNeighbors == 3);
                }else {//当前细胞是死的
                    // 规则4：死细胞周围恰好有3个活邻居，诞生新细胞
                    newGrid[i][j] = (aliveNeighbors == 3);
                }
            }
        }
        grid = newGrid;//更新网络
    }
    private int countAliveNeighbors(int row,int col){// 统计指定细胞周围8个方向的活细胞数量
        int count = 0;
        for (int i = -1; i <= 1; i++){// 遍历周围8个方向：行偏移和列偏移
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0) continue;// 跳过当前细胞自己
                int neighborRow = row + i;
                int neighborCol = col + j;
                if (neighborRow >= 0 && neighborRow < rows && neighborCol >= 0 && neighborCol < cols){//检查邻居是否在网络范围内
                    if (grid[neighborRow][neighborCol]){
                        count++;//邻居是活的，计数加一
                    }
                }
            }
        }
        return count;
    }
    public boolean getCell(int row , int col){// 获取指定位置细胞状态
        return grid[row][col];
    }
    public void setCell(int row, int col, boolean alive){// 设置指定位置细胞状态
        grid[row][col] = alive;
    }
    public int getRows(){// 获取网格行数
        return rows;
    }
    public int getCols(){// 获取网格列数
        return cols;
    }
    public void clear(){// 清空网格：所有细胞死亡
        grid = new boolean[rows][cols];// 新建全false数组
    }
}
