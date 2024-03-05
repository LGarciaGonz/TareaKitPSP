import java.util.concurrent.Semaphore;

public class EscanerKit {
    public static void main(String[] args) {
        // Creación del buzón compartido
        Buzon l_Buzon = new Buzon();

        // Creación de los objetos para imprimir los puntos y la letra "O"
        Puntos l_ObjPuntoDer = new Puntos(l_Buzon, "der");
        Circulos l_ObjCirculo = new Circulos(l_Buzon);
        Puntos l_ObjPuntoIzq = new Puntos(l_Buzon, "izq");

        // Creación de los hilos.
        Thread l_HiloPunto2 = new Thread(l_ObjPuntoIzq);
        Thread l_HiloCirculo = new Thread(l_ObjCirculo);
        Thread l_HiloPunto1 = new Thread(l_ObjPuntoDer);

        // Inicio de la ejecución de los hilos.
        l_HiloPunto2.start();
        l_HiloCirculo.start();
        l_HiloPunto1.start();
    }
}

// Clase para escribir los puntos.
class Puntos extends Thread {
    private Buzon a_Buzon;
    private String a_Figura;

    public Puntos(Buzon a_Buzon, String a_Figura) {
        this.a_Buzon = a_Buzon;
        this.a_Figura = a_Figura;
    }

    @Override
    public void run() {
        // Variables locales
        int l_Puntos = 0;
        int l_Contador = 0;

        // Bucle infinito para imprimir puntos
        while (true) {
            try {
                // Espera a que se le permita imprimir puntos
                a_Buzon.a_SemaforoPunto.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (a_Figura.equals("izq")) {
                // Si la figura es izquierda, imprime puntos desde a_PuntosIzq
                l_Puntos = a_Buzon.a_PuntosIzq;

                for (l_Contador = 0; l_Contador < l_Puntos; l_Contador++) {
                    System.out.print("·");
                }

                // Incrementa a_PuntosIzq y permite que se imprima un círculo
                a_Buzon.a_PuntosIzq++;
                a_Buzon.a_SemaforoCirculo.release();

            } else if (a_Figura.equals("der")) {
                // Si la figura es derecha, imprime puntos desde a_PuntosDer
                l_Puntos = a_Buzon.a_PuntosDer;

                for (l_Contador = 0; l_Contador < l_Puntos; l_Contador++) {
                    System.out.print("·");
                }

                // Imprime nueva línea, decrementa a_PuntosDer y permite que se imprima un punto
                System.out.println();
                a_Buzon.a_PuntosDer--;
                a_Buzon.a_SemaforoPunto.release();
            }

            // Si se alcanza el final, reinicia a_PuntosIzq y a_PuntosDer
            if (a_Buzon.a_PuntosDer < 0) {
                a_Buzon.a_PuntosIzq = 0;
                a_Buzon.a_PuntosDer = 8;
            }
        }
    }
}

// Clase para escribir los círculos.
class Circulos extends Thread {
    private Buzon a_Buzon;

    public Circulos(Buzon a_Buzon) {
        this.a_Buzon = a_Buzon;
    }

    @Override
    public void run() {

        // Bucle infinito para imprimir círculos
        while (true) {
            try {
                // Espera a que se le permita imprimir un círculo
                a_Buzon.a_SemaforoCirculo.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Imprime un círculo
            System.out.print("O");

            // Permite que se imprima un punto
            a_Buzon.a_SemaforoPunto.release();
        }
    }
}

// Clase que actúa como buzón.
class Buzon {
    public static final int TOKENS_PUNTO = 0;
    public static final int TOKENS_CIRCULO = 1;
    public int a_PuntosIzq = 0;
    public int a_PuntosDer = 8;
    public Semaphore a_SemaforoPunto = new Semaphore(TOKENS_PUNTO, true);
    public Semaphore a_SemaforoCirculo = new Semaphore(TOKENS_CIRCULO);
}
