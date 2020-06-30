package br.ufms.facom.student.cloud.cli;

import br.ufms.facom.student.cloud.client.RemoteInputStream;
import br.ufms.facom.student.cloud.client.RemoteOutputStream;
import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.UnaryOperator;

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

    /**
     * Runs an input loop listening for commands. Here is the grammar:
     *
     * Command  : Copy | Get | Hash | List | Move | Put | Remove | Exit
     *
     * List     : "ls"      [dirname]
     * Get      : "get"     filename
     * Put      : "put"     filename
     * Hash     : "hash"    filename
     * Remove   : "rm"      filename
     * Copy     : "cp"      source      destination
     * Move     : "mv"      source      destination
     *
     */
    public void run() {
        // Command loop
        while (mScanner.hasNext()) {
            // cmd = "ls a".split() = {"ls","a"}
            var cmd = Arrays.asList(mScanner.nextLine().split(" "))
                    .stream()
                    .filter(s -> !s.isEmpty()) // discard empty tokens
                    .iterator();

            // cmd.forEachRemaining(s -> System.out.println("e: "+s));

            switch (cmd.next()) {
                case "ls":
                    list(getdestination(cmd));
                    break;
                case "put": put2(cmd.next(), getdestination(cmd)); break;
                case "get": get2(cmd.next()); break;
                case "hash": hash(cmd.next()); break;
                case "rm": remove(cmd.next()); break;
                case "cp": copy(cmd.next(), getdestination(cmd)); break;
                case "mv": move(cmd.next(), getdestination(cmd)); break;
                case "exit": return;
            }
        }
    }

    private String getdestination(Iterator cmd){
        var result = "";
        if(cmd.hasNext()){
            result = cmd.next().toString();
            if(result.equals("/") || result.equals("\\")){
               result = "";
            }
        }
        return result;
    }

    private void get(String filename) {
        System.out.println("Getting file "+filename);

        try {
            var data = mDrive.get(filename);

            var file = new FileOutputStream(Path.of(filename).getFileName().toString());
            file.write(data);
            file.close();

            System.out.println("Get succeeded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void remove(String filename) {
        System.out.println("Removing file "+filename);
        var response = false;

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

    private void put(String stringpath, String destination) {
        stringpath = stringpath.replaceAll("\\p{C}", "");
        var filepath = Paths.get(stringpath);
        var filename = filepath.getFileName().toString();
        System.out.println("Putting file "+filename);

        try {
            var fileinput = new FileInputStream(stringpath);
            //var filebytes = fileinput.readAllBytes();
            
            mDrive.put(filename, fileinput.readAllBytes(), destination);

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

    private void move(String source, String destination) {
        try{
            mDrive.move(source, destination);
            System.out.println("Move succeeded");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void copy(String source, String destination) {
        try{
            mDrive.copy(source, destination);
            System.out.println("Copy succeeded");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void hash(String filename) {
        try{
            var hashcode = mDrive.hash(filename);
            System.out.println("HASH in MD5 of \"" +filename+ "\" is: "+hashcode);
        } catch (IOException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    private void get2(String filename) {
        System.out.println("Getting file "+filename);

        try (var remote = new RemoteInputStream(mDrive, filename);
             var local = new FileOutputStream(Path.of(filename).getFileName().toString())) {

            remote.transferTo(local);

            System.out.println("Get succeeded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void put2(String filename, String destination) {
        System.out.println("Putting file "+filename);

        try (var local = new FileInputStream(filename);
             var remote = new RemoteOutputStream(mDrive, destination)) {

            local.transferTo(remote);

            System.out.println("Put succeeded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
