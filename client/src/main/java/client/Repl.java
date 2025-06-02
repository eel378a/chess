package client;

import java.util.Scanner;

public class Repl {
    private Client client;
    private final ServerFacade serverFacade;

    public Repl(String url) {
        serverFacade = new ServerFacade(url);
        client = new PreLoginClient(serverFacade);
    }

    public void run() {
        System.out.println("Welcome to Chess!");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            System.out.print(">>> ");
            String command = scanner.nextLine();
            try {
                result = client.eval(command);
                if (result.equals("login")) {
                    client = new PostLogin(client);
                }else if (result.equals("logout")) {
                client = new PreLoginClient(client);
                } else if (result.equals("join") || result.equals("observe")) {
                    client = new GameClient(client);
                } else if (result.equals("leave")) {
                    client = new PostLogin(client);
                } else if (result.equals("quit")) {
                    break;
                } else {
                    System.out.println(result);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}