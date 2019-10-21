package ahorcadosnb;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
/*@author Isaac*/
public class AhorcadoSNB_C 
{
    //private  static String errores[] = new String[4];
    private static int uno;
    private static int dos;
    
    private static int intentos = 3;
    private static int restante = 0;
    
    private static String cadena;
    private static String cadenaOculta="";
    
    public static void main(String[] args) 
    {   
        int flgR = 1;
        int flgW = 1;
        int opc = 0;       
        int puerto = 1234;
        String host = "127.0.0.1";                
        Scanner sc = new Scanner(System.in);
                               
        try {
            InputStreamReader dis = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            SocketChannel cliente = SocketChannel.open();   
            cliente.configureBlocking(false);
            Selector sel = Selector.open();
            cliente.connect(new InetSocketAddress(host, puerto));
            cliente.register(sel, SelectionKey.OP_CONNECT);
            while( true )
            {                
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while(it.hasNext())
                {
                    SelectionKey key = (SelectionKey)it.next();
                    it.remove();
                    if(key.isConnectable())
                    {
                        SocketChannel ch = (SocketChannel)key.channel();
                        if(ch.isConnectionPending())
                        {
                            try {
                                //Forzando finalizacion
                                ch.finishConnect();
                                System.out.println("Conexión establecida");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ch.register(sel, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                        continue;
                    }
                    else if (key.isReadable())
                    {
                        SocketChannel ch2 = (SocketChannel)key.channel();
                        ByteBuffer b = ByteBuffer.allocate(1000);
                        b.clear();
                        ch2.read(b);
                        b.flip();                        
                        String palabraRecibida = new String(b.array(), 0, b.limit());
                        if(palabraRecibida.equalsIgnoreCase("Salir"))
                        {
                            System.out.println("Adios");
                            ch2.close();
                        }
                        if( flgR == 1 )
                        {
                            cadena = palabraRecibida;
                            uno = (int)(Math.random()*cadena.length());  
                            dos = (int)(Math.random()*cadena.length()); 
                            flgR++;
                            key.interestOps(SelectionKey.OP_WRITE);                                   
                            imprimirJuego();
                        }
                        else
                        {
                            if (intentos != -1) {
                                if(restante==0)
                                {
                                    System.out.println("El usuario ha ganado la partida");
                                    System.exit(0);
                                }
                                else
                                {
                                    llenarVacios(palabraRecibida);
                                    key.interestOps(SelectionKey.OP_WRITE);                                   
                                }
                            }
                            else
                            {
                                System.out.println("El usuario ha perdido");
                                System.exit(0);
                            }
                            
                        }
                        
                        continue;
                    }
                    else if (key.isWritable()) 
                    {
                        SocketChannel ch2 = (SocketChannel)key.channel();                                            
                        if(flgW == 1)
                        {
                            System.out.println(" ¡BIENVENIDO AL AHORCADO! \n");
                            System.out.print(" 1. PRINCIPIANTE\n 2. INTERMEDIO\n 3. EXPERTO\n");
                            System.out.print("\n DIFICULTAD:\t> ");
                            String opcS = br.readLine();                            
                            ByteBuffer b = ByteBuffer.wrap(opcS.getBytes());                                                                                   
                            ch2.write(b);                            
                            flgW++;
                            key.interestOps(SelectionKey.OP_READ);                           
                        }
                        else if(flgW > 1)
                        {
                            if(intentos != -1)
                            {
                                if(restante != 0)
                                {                                    
                                    //Pedir caracter para mandar                            
                                    System.out.print("\nIngresa un caracter\t>");
                                    String c = br.readLine();
                                    if(c.length() > 1)
                                    {
                                        System.out.println("Debe ser solo un caracter");
                                        continue;
                                    }
                                    else
                                    {
                                        //System.out.println("Caracter leido: " + c + " length: " + c.length());
                                        ByteBuffer b = ByteBuffer.wrap(c.getBytes());                                
                                        ch2.write(b);
                                        key.interestOps(SelectionKey.OP_READ);
                                        continue;
                                    }
                                }
                                else
                                {
                                    String d = "SALIR";
                                    System.out.println("El usuario ha ganado la partida :)\nSaliendo...");
                                    ByteBuffer b = ByteBuffer.wrap(d.getBytes());                                
                                    ch2.write(b);
                                    ch2.close();
                                    System.exit(0);
                                }
                            }
                            else
                            {
                                System.out.println("El usuario ha perdio la partida :(\nSaliendo...");                                
                                String d = "SALIR";
                                ByteBuffer b = ByteBuffer.wrap(d.getBytes());                                
                                ch2.write(b);
                                ch2.close();
                                System.exit(0);
                            }                                                                                                               
                        }                                                                        
                    }
                }
            }
        } catch (Exception e) {
        }                
    }   
    
    
    public static void llenarVacios(String caracterRecibido)
    {   
        caracterRecibido = caracterRecibido.toUpperCase();
        char[] aux = cadenaOculta.toCharArray();
        
        if(cadena.contains(caracterRecibido))
        {
            for( int i = 0; i < cadena.length(); i++ )
            {
                if(cadena.charAt(i) == caracterRecibido.charAt(0) && i != uno && i!=dos)                    
                {                                        
                    aux[i] = caracterRecibido.charAt(0);
                    restante--;
                }                               
            }
            cadenaOculta = new String(aux);
        }else{           
            imprimirError();
        }
        
        System.out.println("\n\t"+cadenaOculta);
        
    }
    
    public static void imprimirJuego()
    {                    
        System.out.println("\n\n");
        System.out.println("*** JUEGO INICIADO ***\n");
        System.out.print("\t");
        for (int i = 0; i < cadena.length(); i++) 
        {            
            if(i == uno)                
            {
                System.out.print(cadena.charAt(i));
                cadenaOculta = cadenaOculta + cadena.charAt(i);
            }
            else if(i == dos)
            {   
                System.out.print(cadena.charAt(i));
                cadenaOculta = cadenaOculta + cadena.charAt(i);
            }
            else                    
            {
                System.out.print("×");
                cadenaOculta = cadenaOculta + "×";
                restante++;
            }
        }
        System.out.println("\n");
    }
    
    
    public static void imprimirError()
    {        
        switch (intentos){
            case 3:
                System.out.print(" - - - - - \n |        | \n |        | \n |       \n |       \n |       \n |       \n - - - \n" );
                intentos--;
               break;
            case 2:
                System.out.print(" - - - - - \n |        | \n |        | \n |      "+"(º-º)"+"\n |       \n |       \n |       \n - - - \n" );
                intentos--;
                break;
            case 1: 
                System.out.print(" - - - - - \n |        | \n |        | \n |      "+"(º-º)"+"\n |      "+"─┴─"+"\n |       \n |       \n - - - \n" );
                intentos--;
                break;
            case 0:
                System.out.print(" - - - - - \n |        | \n |        | \n |      "+"(º-º)"+"\n |      "+"─┴─"+"\n |        "+'|'+"\n |       "+"┘└"+"\n - - - \n" );
                intentos--;
                break;
            default: 
                break;
        }
    }
    
    /**/
    public static void imprimirAdivinados(String posiciones, char caracter)
    {
        
    }
}
