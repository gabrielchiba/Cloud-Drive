package br.ufms.facom.student.cloud.rmi;

import java.io.IOException;
import java.rmi.Remote;

public interface Drive extends Remote {
    // void copy(String destination, String source);
    byte[] get(String filename) throws IOException;
    // String[] list(String path);
    // void move(String destination, String source);
    // void put(String filename, byte[] data);
    // void remove(String filename);
}
