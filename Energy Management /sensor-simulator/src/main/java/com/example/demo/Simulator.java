package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Simulator {

    private static final String QUEUE_NAME = "sensor-data-queue";

    private static final List<String> DEVICE_IDS = Arrays.asList(
            "d5abf1e8-f539-4207-98db-64e2762bed2f",
            "acd71bf6-d360-41c6-87cf-38a72a711304",
            "b06bc5f4-d89d-45db-b507-21fe3bae042d",
            "273dc632-ac8c-4436-942c-daa3145a0747"
    );

    public static void main(String[] args) {
        System.out.println("--- SENSOR SIMULATOR STARTED (MULTI-DEVICE RANDOMIZED) ---");

        Random random = new Random();

        ConnectionFactory factory = new ConnectionFactory();

        String rabbitMqHost = System.getenv("RABBITMQ_HOST");
        if (rabbitMqHost == null || rabbitMqHost.isEmpty()) {
            rabbitMqHost = "localhost";
        }
        factory.setHost(rabbitMqHost);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            ObjectMapper objectMapper = new ObjectMapper();

            while (true) {
                System.out.println("--> Reading sensor.csv from the beginning...");

                InputStream is = Simulator.class.getClassLoader().getResourceAsStream("sensor.csv");
                if (is == null) {
                    System.err.println("Fisierul sensor.csv nu a fost gasit!");
                    return;
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;

                        try {
                            double value = Double.parseDouble(line.trim());
                            long timestamp = System.currentTimeMillis() + 7200000;

                            String randomIdString = DEVICE_IDS.get(random.nextInt(DEVICE_IDS.size()));
                            UUID currentDeviceId = UUID.fromString(randomIdString);

                            SensorData data = new SensorData(currentDeviceId, timestamp, value);
                            String jsonPayload = objectMapper.writeValueAsString(data);

                            Map<String, Object> headers = new HashMap<>();
                            headers.put("__TypeId__", "com.example.demo.dtos.SensorDataDTO");

                            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                                    .contentType("application/json")
                                    .headers(headers)
                                    .priority(0)
                                    .build();

                            channel.basicPublish("", QUEUE_NAME, props, jsonPayload.getBytes());

                            System.out.println(" [x] Sent measurement: " + value + " for Device: " + currentDeviceId);

                            Thread.sleep(2000);

                        } catch (NumberFormatException e) {
                            System.err.println("Valoare invalida in CSV: " + line);
                        }
                    }
                }

                System.out.println("--> Finished file. Restarting loop...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}