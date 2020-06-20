package br.ufms.facom.student.cloud;

import java.rmi.Naming;

public class HelloClient {
    public static void main(String[] args) {
        try {
            HelloWorld obj = (HelloWorld) Naming.lookup("//" + args[0] + "/HelloWorld");
            System.out.println("Mensagem do Servidor: " + obj.hello());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }
}
