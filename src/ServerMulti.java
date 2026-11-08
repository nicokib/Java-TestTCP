import java.net.*;
import java.io.*;

public class ServerMulti {

    private ServerSocket ss = null;

    public ServerMulti(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Multi-Client Server started on port " + port);

            // Boucle infinie pour attendre et accepter les clients en continu
            while (true) {
                System.out.println("Waiting for a new client ...");

                // Le thread principal se bloque ici jusqu'à l'arrivée d'un client
                Socket s = ss.accept();
                System.out.println("New client accepted: " + s.getRemoteSocketAddress());

                // On crée un nouveau thread dédié à ce client pour ne pas bloquer les autres
                ClientHandler handler = new ClientHandler(s);
                Thread t = new Thread(handler);
                t.start(); // Lance la méthode run() en tâche de fond
            }
        }
        catch (IOException i) {
            System.out.println("Server exception: " + i);
        }
    }

    public static void main(String args[]) {
        ServerMulti server = new ServerMulti(5000);
    }
}

// Classe interne ou externe qui implémente Runnable pour gérer un client spécifique
class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Initialisation du flux d'entrée pour ce client spécifique
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String m = "";

            // Lit les messages de CE client jusqu'à "Over"
            while (!m.equals("Over")) {
                try {
                    m = in.readUTF();
                    // On affiche le message en précisant quel client l'a envoyé
                    System.out.println("[" + socket.getRemoteSocketAddress() + "]: " + m);
                }
                catch (IOException i) {
                    // Se produit si le client se déconnecte brutalement
                    System.out.println("Error reading from client: " + i.getMessage());
                    break;
                }
            }

            System.out.println("Closing connection for " + socket.getRemoteSocketAddress());

            // Fermeture des flux propres à ce client
            in.close();
            socket.close();
        }
        catch (IOException i) {
            System.out.println("Handler exception: " + i);
        }
    }
}
