package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServerMain {
    public static void main(String[] args) {

        ThreadServer ts;

        ServerSocket servSock = null;
        Socket sockComm = null;


        int portServ = 0;

        if (args.length != 1 ) {
            System.out.println("Nombre d'arguments incorrect !");
            System.exit(1);
        }
        try {
            portServ = Integer.parseInt(args[0]);//susceptible de lever NumberFormatException
        } catch (NumberFormatException e) {
            System.out.println("Numéro de port ou argument non valide !");
            System.exit(1);
        }
        if ( portServ < 1024 || portServ > 65535 ){
            System.out.println("Numéro de port non autorisé ou non valide !");
            System.exit(1);
        }
        try {
            servSock = new ServerSocket(portServ); //on creer une nouvelle socket pour le transfert du fichier
            while (true) {
                sockComm = servSock.accept();
                ts = new ThreadServer();
                ts.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (sockComm != null)
                    sockComm.close();
                if (servSock != null)
                    servSock.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                System.out.println("Erreur IO2");
            }
        }



    }
}
