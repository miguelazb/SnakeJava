package snake;

public class Opciones {
    private DefSpeed velocidad;
    private int width;
    private int height;
    
    public Opciones(DefSpeed velocidad, int width, int height){
        set(velocidad, width, height);
    }
    
    public Opciones(Opciones o){
        set(o);
    }
    
    public void set(Opciones o){
        set(o.getSpeed(), o.getWidth(), o.getHeight());
    }
    
    public void set(DefSpeed velocidad, int width, int height){
        this.velocidad = velocidad;
        this.width = width;
        this.height = height;
    }
    
    public DefSpeed getSpeed(){
        return velocidad;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    
}
