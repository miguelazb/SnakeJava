package snake;

public enum DefSpeed {
    MUY_LENTO(6), LENTO(9), NORMAL(12), RAPIDO(16), MUY_RAPIDO(20), MAX(25);
    
    int velocidad;
    
    DefSpeed(int velocidad){
        this.velocidad = velocidad;
    }
    
    int getS(){
        return velocidad;
    }
    
}
