import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Emanuel
 */
public class chat {
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {
        public void run() {
            // En un ciclo infinito se recibirán los mensajes enviados al 
            // grupo 230.0.0.0 a través del puerto 50000 y se desplegarán en la pantalla.
                try{
                    
                    InetAddress mcastaddr = InetAddress.getByName("230.0.0.0");
                    InetSocketAddress grupo = new InetSocketAddress(mcastaddr, 50000 );
                    NetworkInterface netIf = NetworkInterface.getByName("bge0");
                    MulticastSocket socket = new MulticastSocket(50000);
                    socket.joinGroup(grupo, netIf);         
			for(;;) {
                    		byte[] buf = recibe_mensaje_multicast(socket, 1024);
                    		System.out.println();
                    		System.out.println(new String(buf,"UTF-8"));
                        }//socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
       }
    }
    public static void main(String[] args) throws Exception {
        new Worker().start();
        
        String nombre = args[0];
       //Scanner scanner = new Scanner(System.in);
       BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
    
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará el
        // mensaje al grupo 230.0.0.0 a través del puerto 50000.
        for(;;) {
            System.out.println("Inserte mensaje que desea enviar:");
            String mensaje = in.readLine();
            byte buf[] = String.format("%s dice %s", nombre, mensaje).getBytes(StandardCharsets.UTF_8);
            envia_mensaje_multicast(buf, "230.0.0.0",50000);
        }
    }
}
