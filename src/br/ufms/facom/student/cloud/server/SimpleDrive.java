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
        var fileInput = new FileInputStream(file);
        var ret = fileInput.readAllBytes();
        fileInput.close();

        return ret;
    }

    @Override
    public String[] list(String path) throws IOException{
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        var fileindirectory = new File(mWorkingDirectory.getAbsolutePath(), path);
        return fileindirectory.list();
    }

    @Override
    public void move(String source, String destination) throws IOException{
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        System.out.println("MV "+source+" TO "+destination);
        var filesource = new File(mWorkingDirectory.getAbsolutePath(), source);
        var filesourceInput = new FileInputStream(filesource);
        var filesourcebytes = filesourceInput.readAllBytes();
        filesourceInput.close();

        var filename = filesource.getName();
        var destinationfolder = new File(mWorkingDirectory.getAbsolutePath(), destination);

        var filedestination = new File(destinationfolder, filename);
        var filedestinationoutput = new FileOutputStream(filedestination);
        filedestinationoutput.write(filesourcebytes);
        filedestinationoutput.close();

        if (filesource.exists()){
            filesource.delete();
        }
    }

    @Override
    public void put(String filename, byte[] data) throws IOException {
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        var file = new File(mWorkingDirectory.getAbsolutePath(), filename);
        var fileoutput = new FileOutputStream(file);
        fileoutput.write(data);
        fileoutput.close();
    }

    @Override
    public Boolean remove(String filename) throws IOException {
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        //System.out.println("RM "+filename);
        var file = new File(mWorkingDirectory.getAbsolutePath(), filename);
        System.out.println("RM "+file);
        Boolean ret = file.exists();
        if (file.exists()){
            file.delete();
        }
        return ret;
    }
}
