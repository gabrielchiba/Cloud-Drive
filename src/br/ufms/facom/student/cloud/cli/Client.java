package br.ufms.facom.student.cloud.cli;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
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

            var command = mScanner.nextLine().split(" "); // cmd = "ls a".split() = {"ls","a"}
            var name = command.length < 2 ? "" : command[1];

            switch (command[0]) {
                case "get": get(name); break;
                case "rm": remove(name); break;
                case "put": put(name); break;
                case "ls": list(name); break;
            }
        }
    }

    private void get(String filename) {
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

    private void remove(String filename) {
        System.out.println("Removing file "+filename);
        Boolean response = false;

        try{
            response = mDrive.remove(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response)
            System.out.println("Rm succeeded");
        else
            System.out.println("File \"" +filename+ "\" does not exist");
    }

    private void put(String stringpath) {
        stringpath = stringpath.replaceAll("\\p{C}", "");
        var filepath = Paths.get(stringpath);
        var filename = filepath.getFileName().toString();
        System.out.println("Putting file "+filename);

        try {
            var fileinput = new FileInputStream(stringpath);
            //var filebytes = fileinput.readAllBytes();
            
            mDrive.put(filename, fileinput.readAllBytes());

            fileinput.close();

            System.out.println("Put succeeded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void list(String dirname) {
        try{
            var archivearray = mDrive.list(dirname);
            for (String element : archivearray) {
                System.out.println(element);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}


