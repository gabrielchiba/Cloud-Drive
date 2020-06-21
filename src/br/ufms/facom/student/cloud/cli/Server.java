package br.ufms.facom.student.cloud.cli;

import br.ufms.facom.student.cloud.server.SimpleDrive;

import java.io.IOException;
import java.rmi.Naming;

public class Server {
    public static final String USAGE =
            "Usage:\n" +
            "./cloud-server directory";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(USAGE);
            return;
        }

        try {
            var obj = new SimpleDrive(args[0]);
            Naming.rebind("//localhost/Drive", obj);
        } catch (IOException ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }
}
