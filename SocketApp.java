import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketApp{
    public static void main (String[] args){

        // we made an instruction for how to use the api.
        String usage = """
            //Server code ===================================================
            // <program> <server> <port> <cookie-file.txt>
    
            //client code ===================================================
            //<program> <client> <host> <port>
                """;; 
        
        //this when used on its own without other code will allow us to add strings onto our "java SocketApp" command on the terminal
        //it will then print it out.
        //args is a parameter in main that accepts command line inputs while the .class is being executed
        // for (String arg : args){
        //     System.out.println(arg);
        // }
        
        if ((args.length) != 3){
            System.out.println("Incorrect Inputs. Please check the following usage.");
            System.out.println(usage);
            return;
        }

        // toggle between server and client
        String type = args[0];
        if (type.equalsIgnoreCase("server")){
            int port = Integer.parseInt(args[1]);
            String fileName = args[2];
            StartServer(port, fileName);
        }else if (type.equalsIgnoreCase("client")){
            String hostName = args[1];
            int port = Integer.parseInt(args[2]);
            StartClient(hostName, port);
        }else{
            System.out.println("incorrect argenets, please type 'client' or 'server'");
        }


    }

    public static void StartServer (int port, String fileName){
        /*
         * This can send and receive messages at the same time.
         */
        ServerSocket server;
        try{
            server = new ServerSocket(port);
            Socket socket = server.accept();

            //IN 
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //out
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while(true){
                String fromClient = dis.readUTF();

                if (fromClient.equalsIgnoreCase("exit")){
                    //send a random cookie
                    break;
                }
                if (fromClient.equalsIgnoreCase("get-cookie")){
                    // Send a random cookie from the file
                    dos.writeUTF("Dummy cookie..");
                    dos.flush();
                }else{
                    //send a msg. 
                    dos.writeUTF("From server: Invalid command");
                    dos.flush();
                }
            }
            socket.close();

            
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void StartClient(String host, int port){
        try{
            Socket socket = new Socket(host, port);
            //in
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //out
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            //taking in the inputs
            Scanner sc = new Scanner(System.in);
            boolean stop = false;

            while(!stop){
                String line = sc.nextLine();

                //hwere we're trying to read what the user types into client
                //and having client respond accordingly
                if (line.equalsIgnoreCase("exit")){
                    stop = true;
                    break;
                }

                if (line.equalsIgnoreCase("get-cookie")){
                    //send a request to the server for a cookie
                    dos.writeUTF("get-cookie");
                    dos.flush();
                }else{
                    System.out.println("Invalid Command: " + line);
                }

                String fromServer = dis.readUTF();
                System.out.println("Response from server" + fromServer);
            }//read input loop end
            // socket.close();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        }
    }
