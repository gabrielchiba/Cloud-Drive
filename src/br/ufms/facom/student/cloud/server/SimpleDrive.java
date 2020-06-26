package br.ufms.facom.student.cloud.server;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    @Override
    public void copy(String source, String destination) throws IOException{
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        System.out.println("CP "+source+" TO "+destination);
        var filesource = new File(mWorkingDirectory.getAbsolutePath(), source);

        var filename = filesource.getName();
        var destinationfolder = new File(mWorkingDirectory.getAbsolutePath(), destination);
        var filedestination = new File(destinationfolder, filename);

        Files.copy(filesource.toPath(),
                filedestination.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
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

        var filename = filesource.getName();
        var destinationfolder = new File(mWorkingDirectory.getAbsolutePath(), destination);
        var filedestination = new File(destinationfolder, filename);

        Files.move(filesource.toPath(),
                filedestination.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
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

    @Override
    public String hash(String filename) throws IOException, NoSuchAlgorithmException {
        // FIXME Security flaw: client may use relative paths to access private files, e.g, "../../../some/file".
        var file = new File(mWorkingDirectory.getAbsolutePath(), filename);
        var md5 = MessageDigest.getInstance("MD5");
        md5.reset();

        try (InputStream fis = Files.newInputStream(file.toPath());
             BufferedInputStream bis = new BufferedInputStream(fis);
             DigestInputStream dis = new DigestInputStream(bis, md5))
        {
            while (dis.read() != -1) {
                /* Nothing, only reading */
            }
        }
        var value = md5.digest(); // value in byte []

        var valuedecodedconstructor = new StringBuilder();
        for (byte b : value) {
            valuedecodedconstructor.append(String.format("%02X", b));
        }

        var valuedecoded = valuedecodedconstructor.toString().toLowerCase(); // value in string
        System.out.println("HASH in MD5 of "+filename+" is: " +valuedecoded);
        return valuedecoded;
    }
}
