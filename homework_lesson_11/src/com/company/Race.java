package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class Race {
    public static AtomicLong startRaceTime = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        int numberOfCars = 3;
        CountDownLatch latch = new CountDownLatch(numberOfCars);

        List<RaceCarRunnable> cars = new ArrayList<>();
        cars.add(new RaceCarRunnable("Car 1", 100, 1000, latch));
        cars.add(new RaceCarRunnable("Car 2", 120, 1000, latch));
        cars.add(new RaceCarRunnable("Car 3", 110, 1000, latch));

        List<Thread> threads = new ArrayList<>();
        for (RaceCarRunnable car : cars) {
            threads.add(new Thread(car));
        }

        startRace(threads);
        latch.await();

        RaceCarRunnable winner = cars.get(0);
        for (RaceCarRunnable car : cars) {
            if (car.getFinishTime() < winner.getFinishTime() && car.getFinishTime() != 0) {
                winner = car;
            }
        }
        System.out.printf("Winner is %s with time %s%n", winner.name, RaceCarRunnable.convertToTime(winner.getFinishTime()));
    }

    public static void startRace(List<Thread> cars) {
        new Thread(() -> {
            try {
                System.out.println("3...");
                Thread.sleep(500);
                System.out.println("2...");
                Thread.sleep(500);
                System.out.println("1...");
                Thread.sleep(500);
                System.out.println("GO!!!");
                startRaceTime.set(System.currentTimeMillis());
                for (Thread car : cars) {
                    car.start();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
