package com.gameoflife.ui;

import com.gameoflife.core.GameBoard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class GameOfLifeUI extends JFrame {
    private GameBoard board;           // 游戏核心逻辑
    private GamePanel gamePanel;        // 自定义画板
    private Timer timer;                // 演化定时器
    private JButton startButton;        // 开始按钮
    private JButton pauseButton;        // 暂停按钮
    private JButton clearButton;        // 清空按钮
    private JButton randomButton;       // 随机初始化按钮
    private JLabel generationLabel;      // 显示代数
    private JLabel aliveCountLabel;      // 显示活细胞数
    private int generation = 0;          // 当前代数
    private final int cellSize = 20;      // 每个格子大小（像素）
    private final int gridRows = 30;       // 网格行数
    private final int gridCols = 40;       // 网格列数
    private JComboBox<Pattern> patternComboBox;  // 图案下拉框
    private PatternLibrary patternLibrary;        // 图案库
    private JButton placePatternButton;           // 放置图案按钮
    private boolean isPlaceMode = false;          // 是否处于放置模式

    public GameOfLifeUI() {
        setTitle("生命游戏");// 设置窗口标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置关闭操作
        setLayout(new BorderLayout());// 设置布局管理器
        board = new GameBoard(gridRows, gridCols);// 初始化游戏板

        gamePanel = new GamePanel();// 创建游戏画板
        gamePanel.setPreferredSize(new Dimension(gridCols * cellSize, gridRows * cellSize));

        JPanel controlPanel = createControlPanel();// 创建控制面板
        add(gamePanel, BorderLayout.CENTER);// 将组件添加到窗口
        add(controlPanel, BorderLayout.SOUTH);
        pack();// 调整窗口大小以适应内容
        setLocationRelativeTo(null);// 设置窗口居中
        timer = new Timer(200, e -> nextGeneration());// 初始化定时器（每200ms一帧）
        board.randomize(0.2);// 初始化随机图案
        updateStats();

        patternLibrary = new PatternLibrary();// 初始化图案库
        JPanel patternPanel = createPatternPanel();// 创建图案选择面板
        add(patternPanel, BorderLayout.NORTH);// 将图案面板添加到北边（顶部）
    }

    private JPanel createPatternPanel() {//创建图案选择面板
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("经典图案库"));
        patternComboBox = new JComboBox<>();// 创建下拉框
        patternComboBox.setRenderer(new DefaultListCellRenderer() {// 自定义渲染器，显示图案名称和描述
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                if (value instanceof Pattern) {
                    Pattern pattern = (Pattern) value;
                    value = pattern.getName() + " - " + pattern.getDescription();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        for (Pattern pattern : patternLibrary.getPatterns()) {// 添加所有图案到下拉框
            patternComboBox.addItem(pattern);
        }
        placePatternButton = new JButton("进入放置模式");// 创建放置按钮
        placePatternButton.addActionListener(e -> togglePlaceMode());

        JLabel hintLabel = new JLabel("选择图案后点击按钮，再在网格上点击放置");// 添加说明标签
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hintLabel.setForeground(Color.DARK_GRAY);

        panel.add(patternComboBox);// 布局
        panel.add(placePatternButton);
        panel.add(hintLabel);
        return panel;
    }

    private void togglePlaceMode() {//添加放置模式切换方法
        isPlaceMode = !isPlaceMode;
        if (isPlaceMode) {
            placePatternButton.setText("退出放置模式");
            placePatternButton.setBackground(new Color(255, 200, 200));
            // 暂停演化
            if (timer.isRunning()) {
                pause();
            }
        } else {
            placePatternButton.setText("进入放置模式");
            placePatternButton.setBackground(null);
        }
    }

    private void placePattern(Pattern pattern, int centerRow, int centerCol) {//添加放置图案方法
        if (pattern.getName().equals("随机填充")) {
            board.randomize(0.2);// 随机填充特殊处理
            generation = 0;
            gamePanel.repaint();
            updateStats();
            return;
        }

        int[][] cells = pattern.getCells();
        int height = pattern.getHeight();
        int width = pattern.getWidth();

        // 计算左上角位置（以点击点为中心）
        int startRow = centerRow - height / 2;
        int startCol = centerCol - width / 2;

        // 放置图案
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int row = startRow + i;
                int col = startCol + j;

                // 检查是否在网格范围内
                if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {
                    if (cells[i][j] == 1) {
                        board.setCell(row, col, true);//不擦除原有细胞，只添加新细胞
                    }
                }
            }
        }
        generation = 0;  // 重置代数
        gamePanel.repaint();
        updateStats();
    }

    private JPanel createControlPanel() {// 创建控制面板
        JPanel panel = new JPanel();

        // 创建按钮
        startButton = new JButton("开始");
        pauseButton = new JButton("暂停");
        clearButton = new JButton("清空");
        randomButton = new JButton("随机");

        // 创建标签
        generationLabel = new JLabel("第 0 代");
        aliveCountLabel = new JLabel("活细胞: 0");

        // 添加按钮事件
        startButton.addActionListener(e -> start());
        pauseButton.addActionListener(e -> pause());
        clearButton.addActionListener(e -> clear());
        randomButton.addActionListener(e -> randomize());

        // 将组件添加到面板
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(clearButton);
        panel.add(randomButton);
        panel.add(generationLabel);
        panel.add(aliveCountLabel);

        // 初始时暂停按钮不可用
        pauseButton.setEnabled(false);

        // 添加绘制提示
        JLabel hintLabel = new JLabel("左键绘制  右键擦除");
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        panel.add(hintLabel);
        return panel;
    }

    private void start() {// 开始演化
        timer.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void pause() {// 暂停演化
        timer.stop();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void clear() {// 清空网格
        timer.stop();
        board.clear();
        generation = 0;
        gamePanel.repaint();
        updateStats();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void randomize() {// 随机初始化
        timer.stop();
        board.randomize(0.2);
        generation = 0;
        gamePanel.repaint();
        updateStats();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private void nextGeneration() {// 计算下一代
        board.nextGeneration();
        generation++;
        gamePanel.repaint();
        updateStats();
    }

    private void updateStats() {// 更新统计信息
        int aliveCount = 0;
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridCols; j++) {
                if (board.getCell(i, j)) {
                    aliveCount++;
                }
            }
        }
        generationLabel.setText("第 " + generation + " 代");
        aliveCountLabel.setText("活细胞: " + aliveCount);
    }
    private class GamePanel extends JPanel {// 自定义画板类（内部类）
        private boolean isDragging = false;  // 是否正在拖拽
        private boolean dragState = true;     // 拖拽时设置的状态（true=画活细胞，false=画死细胞）
            public GamePanel() {
            // 启用鼠标事件
            setFocusable(true);
            // 添加鼠标监听器
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int row = e.getY() / cellSize;// 鼠标按下时
                    int col = e.getX() / cellSize;
                    if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {// 检查是否在网格范围内
                        if (isPlaceMode) {// 如果处于放置模式，放置选中的图案
                            Pattern selectedPattern = (Pattern) patternComboBox.getSelectedItem();
                            if (selectedPattern != null) {
                                placePattern(selectedPattern, row, col);
                                togglePlaceMode();//自动退出放置模式
                            }
                        } else {// 正常绘制模式
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                dragState = true;
                                board.setCell(row, col, true);
                            } else if (SwingUtilities.isRightMouseButton(e)) {
                                dragState = false;
                                board.setCell(row, col, false);
                            }
                            isDragging = true;
                            repaint();
                            updateStats();
                        }
                    }
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDragging) {// 鼠标拖拽时
                        int row = e.getY() / cellSize;
                        int col = e.getX() / cellSize;
                        if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {// 检查是否在网格范围内
                            board.setCell(row, col, dragState);// 根据拖拽状态设置细胞
                            repaint();
                            updateStats();
                        }
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    isDragging = false;// 鼠标释放时
                }
                @Override
                public void mouseMoved(MouseEvent e) {
                    int row = e.getY() / cellSize;// 鼠标移动时
                    int col = e.getX() / cellSize;
                    if (row >= 0 && row < gridRows && col >= 0 && col < gridCols) {
                        setToolTipText("行: " + row + " 列: " + col);
                    }
                }
            };
            // 注册鼠标事件
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.LIGHT_GRAY);// 绘制网格线（灰色）
            for (int i = 0; i <= gridRows; i++) {
                g.drawLine(0, i * cellSize, gridCols * cellSize, i * cellSize);
            }
            for (int j = 0; j <= gridCols; j++) {
                g.drawLine(j * cellSize, 0, j * cellSize, gridRows * cellSize);
            }
            for (int i = 0; i < gridRows; i++) {// 绘制活细胞（蓝色方块）
                for (int j = 0; j < gridCols; j++) {
                    if (board.getCell(i, j)) {
                        g.setColor(new Color(0, 120, 255));// 活细胞填充为蓝色
                        g.fillRect(j * cellSize + 1, i * cellSize + 1,
                                cellSize - 1, cellSize - 1);
                    }
                }
            }
        }
    }
    // 主方法
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {// 在事件调度线程中创建GUI
            new GameOfLifeUI().setVisible(true);
        });
    }
}