package br.ufms.facom.student.cloud.cli;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
    public static final String USAGE =
            "Usage:\n" +
            "./cloud-client <host>";

    public static void main(String[] args) {
        try {
            var host = args[0];
            var drive = (Drive) Naming.lookup("//"+host+"/Drive");

            System.out.println("Host "+host+" connected");

            new Client(drive, new Scanner(System.in)).run();

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    private Drive mDrive;
    private Scanner mScanner;

    Client(Drive drive, Scanner commandScanner) {
        mDrive = drive;
        mScanner = commandScanner;
    }

    public void run() {
        // Command loop
        while (mScanner.hasNext()) {
            var command = mScanner.next().toLowerCase();

            switch (command) {
                case "get": get(); break;
            }
        }
    }

    private void get() {
        var filename = mScanner.next();
        System.out.println("Getting file "+filename);

        try {
            var data = mDrive.get(filename);

            var file = new FileOutputStream("outfile");
            file.write(data);
            file.close();

            System.out.println("Get succeeded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
