package br.ufms.facom.student.cloud.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface Drive extends Remote {
    void copy(String source, String destination) throws IOException;
    byte[] get(String filename) throws IOException;
    String[] list(String path) throws IOException;
    void move(String source, String destination) throws IOException;
    void put(String filename, byte[] data, String destination) throws IOException;
    Boolean remove(String filename) throws IOException;
    String hash(String filename) throws IOException, NoSuchAlgorithmException;

    byte[] getChunk(String filename, int position, int length) throws IOException;
    void putChunk(String filename, int position, byte data[]) throws IOException;
}
