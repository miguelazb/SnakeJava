package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Snake extends JPanel implements Runnable {

    int canvasWidth;
    int canvasHeight;
    int score;
    int vel;
    Thread hilo;
    private int cols;
    private int rows;
    private Items[][] grid;
    private Cas[][] cas;//casillas (visual)
    boolean inGame;
    int hx;//la x se cuenta de izquierda a derecha
    int hy;//la y se cuenta de arriba a abajo
    int fx;//posición x de la fruta
    int fy;//posición y
    List<Cas> body;
    Dir dir;//dirección
    Dir nextDir;

    public Snake(int cols, int rows, int vel) {
        grid = new Items[rows][cols];
        cas = new Cas[rows][cols];;
        this.cols = cols;
        this.rows = rows;
        this.vel = vel;
        body = new ArrayList<>();
        //paddings de 20 + espacios entre casillas (1px) + cantidad casillas
        canvasWidth = 40 + (cols - 1) + 20 * cols;
        canvasHeight = 40 + (rows - 1) + 20 * rows;
        setSize(canvasWidth, canvasHeight);
//        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
//        setMinimumSize(new Dimension(canvasWidth, canvasHeight));
        int cx = 20;//coordenada x de las casillas 
        int cy = 20;//coordenada y de las casillas
        for (int i = 0; i < rows; i++) {//sirve para guardar las coordenadas de dónde se pintara cada casilla
            cx = 20;
            for (int j = 0; j < cols; j++) {
                cas[i][j] = new Cas(cx, cy);//se asgina la coordenada
                cx += 21;//20 por el tamaño de la casilla actual y 1 de la distancia entre ellas
            }
            cy += 21;
        }
        setup();
    }

    public void setup() {
        score = 0;
        dir = Dir.R;//dirección del snake
        nextDir = Dir.R;//siguiente dirección
        inGame = false;
        //posicion cabeza
        hy = (int) rows / 2;//el snake estará a posicionado a la mitad
        hx = 4;
        //posicion cuerpo
        body.clear();
        body.add(new Cas(1, hy));
        body.add(new Cas(2, hy));
        body.add(new Cas(3, hy));
        //se inicializa todo
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = Items.NADA; //se inicializa automáticamente todo en NADA
            }
        }
        grid[hy][hx] = Items.HEAD;
        grid[hy][1] = Items.BODY;
        grid[hy][2] = Items.BODY;
        grid[hy][3] = Items.BODY;
        hilo = new Thread(this);
        colocarFruta();
        pintar();
    }

    public void actualizar() {
        int nx = hx; //siguiente x
        int ny = hy; //siguiente y
        dir = nextDir;
        switch (dir) {//según la dirección, la coordenada x o y cambia
            case U:
                inGame = --ny >= 0;
                break;
            case R:
                inGame = ++nx < cols;
                break;
            case D:
                inGame = ++ny < rows;
                break;
            case L:
                inGame = --nx >= 0;
                break;
        }
        //checa colisiones con el cuerpo
        for (Cas bod : body) {
            if (bod.x == nx && bod.y == ny) {
                inGame = false;
                break;
            }
        }
        boolean capturoFruta = nx == fx && ny == fy;//si la siguiente posición coincide con la de la fruta
        if (inGame) {//si sigue en juego se realizan los cambios
            if (!capturoFruta) {//si no se capturó fruta, se continúa (en el caso de que sí, no se borra nada y se sigue)
                Cas b = body.remove(0);//se remueve la última casilla
                grid[b.y][b.x] = Items.NADA;//se limpia
            }
            grid[hy][hx] = Items.BODY;//se le establece cuerpo a la anterior posición de la cabeza
            body.add(new Cas(hx, hy));//se añade a la lista de cuerpo la anterior posición de la cabeza
            hx = nx;//se actualiza la posición de la cabeza
            hy = ny;
            grid[hy][hx] = Items.HEAD;//se le establece cabeza a la nueva cabeza
            if (capturoFruta) {
                colocarFruta();//si se capturó una fruta, se coloca otra
                score++;
            }
            pintar();
        } else {//sino, se termina todo
            finalizar();//no sé si sea necesario
        }
    }

    //coloca una fruta
    private void colocarFruta() {
        int totalCasillas = grid.length * grid[0].length;
        int disponibles = totalCasillas - body.size() - 1;//total-cuerpo-cabeza
        int random = (int) (Math.random() * disponibles);//se coloca un random entre las disponibles
        int cont = 0;//contador de casillas
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != Items.NADA) //si hay algo, entonces se ignora esa casilla
                {
                    continue;
                }
                if (random == cont++) {//sino, se evalúa si el número elegido aleatoriamente coincide con el contador de casillas disponibles
                    grid[i][j] = Items.FRUTA;//se establece que será fruta esa posición
                    fx = j;//posición x de la fruta
                    fy = i;//posición y de la fruta
                    break;
                }
            }
        }
    }

    private void pintar() {
        repaint();
    }

    private void finalizar() {
        JOptionPane.showMessageDialog(this, "Score: " + score, "Has perdido", JOptionPane.INFORMATION_MESSAGE);
        setup();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cas c = cas[i][j];
                switch (grid[i][j]) {
                    case BODY:
                        g2.setColor(Color.GREEN);
                        break;
                    case FRUTA:
                        g2.setColor(Color.ORANGE);
                        break;
                    case HEAD:
                        g2.setColor(Color.RED);
                        break;
                    case NADA:
                        g2.setColor(Color.GRAY);
                        break;
                }
                g2.fill(new Rectangle2D.Double(c.x, c.y, 20, 20));
            }
        }
        g2.setColor(Color.BLACK);
    }

    public void comenzar() {
        hilo.start();
        inGame = true;
    }

    public synchronized void terminar() {
        inGame = false;
        setup();
    }

    //colocar synchronized cuando se vayan a arectar valores que el hilo está usando
    public synchronized void continuar() {
        notify();
        inGame = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                actualizar();
                synchronized (this) {
                    if (!inGame) {
                        wait();
                    }
                }
                Thread.sleep(1000 / vel);
            } catch (InterruptedException ex) {
                System.err.println("Error!");
            }
        }
    }

    enum Items {
        NADA, BODY, HEAD, FRUTA;
    }

    public enum Dir {
        R, L, U, D;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvasWidth, canvasHeight);
    }


    //casillas (cuerpo o casilla sumple)
    private class Cas {

        int x;
        int y;

        public Cas(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
