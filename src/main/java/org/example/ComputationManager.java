package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ComputationManager {
    private boolean isRunning = true;
    private Map<String, TaskGroup> groups = new HashMap<>();

    public void start() {
        System.out.println("Сервер запущено.");
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            String command = in.readUTF();
            switch (command) {
                case "CREATE_GROUP":
                    String groupName = in.readUTF();
                    groups.put(groupName, new TaskGroup(groupName));
                    out.writeUTF("Група " + groupName + " створена");
                    break;

                case "ADD_TASK":
                    double argument = in.readDouble();
                    String targetGroup = in.readUTF();
                    if (groups.containsKey(targetGroup)) {
                        ComputationTask task = new ComputationTask(argument);
                        groups.get(targetGroup).addTask(task);
                        out.writeUTF("Завдання додано до групи " + targetGroup);
                    } else {
                        out.writeUTF("Помилка: група " + targetGroup + " не існує");
                    }
                    break;

                case "RUN_GROUP":
                    String groupToRun = in.readUTF();
                    TaskGroup group = groups.get(groupToRun);

                    if (group == null) {
                        out.writeUTF("ERROR");
                        out.writeUTF("Помилка: група " + groupToRun + " не існує");
                        return;
                    }

                    if (group.isRunning()) {
                        out.writeUTF("ERROR");
                        out.writeUTF("Група вже виконується");
                        return;
                    }

                    group.setRunning(true);
                    List<String> results = Collections.synchronizedList(new ArrayList<>());
                    List<Thread> computationThreads = new ArrayList<>();

                    for (ComputationTask task : group.getTasks()) {
                        if (!task.isCompleted()) {
                            Thread computationThread = new Thread(() -> {
                                try {
                                    System.out.println("обчислюємо " + task.getArgument());
                                    Thread.sleep(5000); // очікуємо 5 секунд перед виконанням обчислення
                                    double result = Math.sqrt(task.getArgument());
                                    task.setResult(result);
                                    results.add(String.format("Аргумент: %.2f, Результат: %.2f",
                                            task.getArgument(), result));
                                    System.out.println("завершуємо обчислення для " + task.getArgument());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                            computationThread.start();
                            computationThreads.add(computationThread);
                        }
                    }

                    // Чекаємо завершення всіх обчислень
                    for (Thread thread : computationThreads) {
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    group.setRunning(false);
                    out.writeUTF("RESULTS");
                    out.writeInt(results.size());
                    for (String result : results) {
                        out.writeUTF(result);
                    }
                    break;
            }
            out.flush(); //викликаємо цей метод для відправки вмісту буфера

        } catch (IOException e) {
            System.out.println("Помилка при обробці клієнта: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}