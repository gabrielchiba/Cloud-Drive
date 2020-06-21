package br.ufms.facom.student.cloud.server;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SimpleDrive extends UnicastRemoteObject implements Drive {
    private File mWorkingDirectory;
    // private File lockFile;

    public SimpleDrive(String path) throws RemoteException {
        super();
        mWorkingDirectory = new File(path);

        // Check the directory existence
        if (mWorkingDirectory.exists()) {
            if (!mWorkingDirectory.isDirectory())
                throw new RuntimeException(path+" already exists and it is not a directory");
        } else {
            mWorkingDirectory.mkdirs();
            // new File("a/b/c/name.txt").mkdir() -> a/b/c/
            // new File("a/b/c/name.txt").mkdirs() -> a/b/c/name.txt/
        }
    }

    // @Override
    public void copy(String destination, String source) {

    }

    @Override
    public byte[] get(String filename) throws IOException {
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        System.out.println("GET "+filename);
        var file = new File(mWorkingDirectory.getAbsolutePath(), filename);
        return new FileInputStream(file).readAllBytes();
    }

    // @Override
    public String[] list(String path) {
        return new String[0];
    }

    // @Override
    public void move(String destination, String source) {

    }

    // @Override
    public void put(String filename, byte[] data) {

    }

    // @Override
    public void remove(String filename) {

    }
}
