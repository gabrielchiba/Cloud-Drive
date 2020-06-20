package br.ufms.facom.student.cloud;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static final String USAGE =
            "Usage as a client:\n" +
            "     ./cloud localhost\n" +
            "\n" +
            "Usage as a server:\n" +
            "     ./cloud [--server]";

    /**
     * Command line interface for Cloud.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
	    if (args.length == 0) {
	        // Print usage
            System.out.println(USAGE);
            System.exit(1);
        }

	    if (args[0].equals("--server")) {
	        String[] v = {};
	        HelloServer.main(v);
        } else {
            String[] v = {args[0]};
            HelloClient.main(v);
        }
    }
}
