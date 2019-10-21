package ahorcadosnb;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/*@author Isaac*/
public class AhorcadoSNB_S 
{
    private static int flagR = 1;
    private static ArrayList<String> principiante = new ArrayList();
    private static ArrayList<String> intermedio = new ArrayList();
    private static ArrayList<String> experto = new ArrayList();
    public static void main(String[] args) 
    {
        int puerto = 1234;
        /*ARREGLOS DE PALABRAS*/
        principiante.add("GLOBO");principiante.add("PEZ");principiante.add("CAFE");principiante.add("MOTO");principiante.add("OSO");principiante.add("VASO");principiante.add("PAPA");principiante.add("LLAVE");
        intermedio.add("LOBOMARINO");intermedio.add("MARINERO");intermedio.add("CARPINTERO");intermedio.add("VIDEOJUEGO");intermedio.add("PERPENDICULAR");intermedio.add("DIGITALIZADO");intermedio.add("CODIFICACION");
        experto.add("ESTERNOCLEIDOMASTOIDEO ");experto.add("ELECTROENCEFALOGRAFIA");experto.add("PARALEPIPEDO ");experto.add("ELECTRODOMESTICO");experto.add("CALEIDOSCOPIO ");experto.add("FISIOTERAPEUTA");
        try 
        {
            /*Creamos y configuramos el SocketChannel*/
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            server.bind(new InetSocketAddress(puerto));
            
            /*Creamos y preparamos al selector*/
            Selector sel = Selector.open();
            server.register(sel, SelectionKey.OP_ACCEPT);
            System.out.println("¡Servidor iniciado!\nEsperando cliente para iniciar la partida...");
            
            for( ; ; )
            {
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator(); /*Obtenemos el iterador en lugar del conjunto*/
                while(it.hasNext())
                {
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();
                    if(key.isAcceptable())
                    {
                        SocketChannel cliente = server.accept(); 
                        System.out.println("Un cliente se ha conectado desde el puerto: " + cliente.socket().getInetAddress().getHostName() + ":" + cliente.socket().getPort());   
                        cliente.configureBlocking(false);
                        cliente.register(sel, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                        continue;
                    }else if(key.isReadable())
                    {
                        SocketChannel ch =(SocketChannel)key.channel(); //Obtenemos la referencia
                        ByteBuffer b = ByteBuffer.allocate(1000);
                        b.clear();
                        int n = ch.read(b);                        
                        b.flip();
                        String msj = new String(b.array(),0,b.limit());
                        if(b.hasArray())
                        {                            
                            if(flagR == 1)
                            {                                
                                Random rnd = new Random();                                                                                   
                                if(msj.contains("1"))
                                {    
                                    int numR = rnd.nextInt(principiante.size());
                                    String palEsc = principiante.get(numR);
                                    ByteBuffer b2=ByteBuffer.wrap(palEsc.getBytes());
                                    ch.write(b2);                                    
                                    flagR++;
                                }else if(msj.contains("2"))
                                {                           
                                    int numR = rnd.nextInt(intermedio.size());
                                    String palEsc = intermedio.get(numR);
                                    ByteBuffer b2 = ByteBuffer.wrap(palEsc.getBytes());
                                    ch.write(b2);                                    
                                    flagR++;
                                }
                                else if(msj.contains("3"))
                                {
                                    int numR = rnd.nextInt(experto.size());
                                    String palEsc = experto.get(numR);
                                    ByteBuffer b2 = ByteBuffer.wrap(palEsc.getBytes());
                                    ch.write(b2);                                    
                                    flagR++;
                                }else
                                {
                                    System.out.println("Dificultad no recibida");
                                }                            
                            }else if(flagR > 1)
                            {            
                                System.out.println("SE HA INCREMENTADO LA BANDERA");
                                if(msj.equalsIgnoreCase("SALIR")){
                                    System.out.println("Mensaje recibido: "+msj+"\n El cliente se va..");
                                    ch.close();
                                    continue;
                                }else{
                                    System.out.println("Mensaje recibido: "+msj+"\nDevolviendo eco..");
                                    String eco = msj;
                                    ByteBuffer b2=ByteBuffer.wrap(eco.getBytes());
                                    ch.write(b2);
                                    continue;
                                }//else
                            }
                        }
                    }                 
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
