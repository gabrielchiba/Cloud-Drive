package br.ufms.facom.student.cloud.server;

import br.ufms.facom.student.cloud.rmi.Drive;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleDrive extends UnicastRemoteObject implements Drive {
    public static final String[] FORBIDDEN_FILENAMES = {".", ".."};

    private final Path mDriveDirectory;
    // private final File mLockFile;

    public SimpleDrive(String path) throws RemoteException {
        super();

        mDriveDirectory = Paths.get(path).toAbsolutePath().normalize();
        var workingDir = mDriveDirectory.toFile();

        // Check the directory existence
        if (workingDir.exists()) {
            if (!workingDir.isDirectory())
                throw new RuntimeException(path+" already exists and it is not a directory");
        } else {
            workingDir.mkdirs();
        }
    }

    /**
     * Takes a String and converts it to a path relative to our Working Directory.
     *
     * It also ensures path is inside our drive directory and denies the use of relative paths to access private files,
     * e.g, "../../../some/file". In this case, an exception is thrown.
     *
     * @param path String containing path.
     * @return The path.
     */
    public Path convertToLocalPath(String path) throws FileNotFoundException {
        var filePath = mDriveDirectory.resolve(Paths.get(path)).normalize();

        if (!filePath.startsWith(mDriveDirectory))
            throw new FileNotFoundException();

        return filePath;
    }

    @Override
    public void copy(String source, String destination) throws IOException {
        System.out.println("COPY "+source+" TO "+destination);

        var filesource = convertToLocalPath(source).toFile();

        var filename = filesource.getName();
        var destinationfolder = convertToLocalPath(destination).toFile();
        var filedestination = new File(destinationfolder, filename);

        Files.copy(filesource.toPath(), filedestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public byte[] get(String filename) throws IOException {
        System.out.println("GET "+filename);

        var file = convertToLocalPath(filename).toFile();
        try (var fileInput = new FileInputStream(file)) {
            return fileInput.readAllBytes();
        }
    }

    @Override
    public String[] list(String path) throws IOException {
        return convertToLocalPath(path).toFile().list();
    }

    @Override
    public void move(String source, String destination) throws IOException{
        System.out.println("MOVE "+source+" TO "+destination);
        var filesource = convertToLocalPath(source).toFile();

        var filename = filesource.getName();
        var destinationfolder = convertToLocalPath(destination).toFile();
        var filedestination = new File(destinationfolder, filename);

        Files.move(filesource.toPath(),
                filedestination.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void put(String filename, byte[] data) throws IOException {
        var file = convertToLocalPath(filename).toFile();
        var fileoutput = new FileOutputStream(file);
        fileoutput.write(data);
        fileoutput.close();
    }

    @Override
    public Boolean remove(String filename) throws IOException {
        //System.out.println("RM "+filename);
        var file = convertToLocalPath(filename).toFile();
        System.out.println("RM "+file);
        Boolean ret = file.exists();
        if (file.exists()){
            file.delete();
        }
        return ret;
    }

    @Override
    public String hash(String filename) throws IOException, NoSuchAlgorithmException {
        var file = convertToLocalPath(filename).toFile();
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
