package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ComputationClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        System.out.println(" команди:");
        System.out.println("group <ім'я> - створити нову групу");
        System.out.println("add <число> <група> - додати завдання в групу");
        System.out.println("run <група> - запустити обчислення в групі");
        System.out.println("exit - вийти");

        while (running) {
            try {
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    running = false;
                    continue;
                }

                String[] parts = input.split("\\s+");
                switch (parts[0].toLowerCase()) {
                    case "group":
                        if (parts.length == 2) {
                            // Неблокуючий виклик
                            new Thread(() -> createGroup(parts[1])).start();
                        }
                        break;
                    case "add":
                        if (parts.length == 3) {
                            try {
                                double number = Double.parseDouble(parts[1]);
                                // Неблокуючий виклик
                                new Thread(() -> addTask(number, parts[2])).start();
                            } catch (NumberFormatException e) {
                                System.out.println("Помилка: Некоректне число");
                            }
                        }
                        break;
                    case "run":
                        if (parts.length == 2) {
                            // Неблокуючий виклик
                            new Thread(() -> runGroup(parts[1])).start();
                        }
                        break;
                    default:
                        System.out.println("Невідома команда. Спробуйте ще раз.");
                }
            } catch (Exception e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void createGroup(String groupName) {
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeUTF("CREATE_GROUP");
            out.writeUTF(groupName);
            out.flush();

            String response = in.readUTF();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static void addTask(double number, String groupName) {
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeUTF("ADD_TASK");
            out.writeDouble(number);
            out.writeUTF(groupName);
            out.flush();

            String response = in.readUTF();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static void runGroup(String groupName) {
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeUTF("RUN_GROUP");
            out.writeUTF(groupName);
            out.flush();

            String response = in.readUTF();
            if (response.equals("RESULTS")) {
                System.out.println("Результати обрахунків групи " + groupName + ":");
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    String result = in.readUTF();
                    System.out.println(result);
                }
                System.out.println("Обчислення завершено.");
            } else if (response.equals("ERROR")) {
                System.out.println(in.readUTF());
            }
        } catch (IOException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

}