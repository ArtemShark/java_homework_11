package com.company;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class RaceCarRunnable extends Car implements Runnable {
    private int passed = 0;
    private final int distance;
    private boolean isFinish = false;
    private final CountDownLatch latch;
    private long finishTime = 0;
    private static final Random random = new Random();

    public RaceCarRunnable(String name, int maxSpeed, int distance, CountDownLatch latch) {
        super(name, maxSpeed);
        this.distance = distance;
        this.latch = latch;
    }

    private int getRandomSpeed() {
        return maxSpeed / 2 + random.nextInt(maxSpeed / 2);
    }

    @Override
    public void run() {
        while (!isFinish) {
            int speed = getRandomSpeed();
            passed += speed;
            System.out.printf("%s => speed: %d; progress: %d/%d%n", name, speed, passed, distance);
            if (passed >= distance) {
                isFinish = true;
                finishTime = System.currentTimeMillis() - Race.startRaceTime.get();
                System.out.printf("%s FINISHED! Time: %s%n", name, convertToTime(finishTime));
                latch.countDown();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public long getFinishTime() {
        return finishTime;
    }

    public static String convertToTime(long time) {
        long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(time);
        long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(time) -
                java.util.concurrent.TimeUnit.HOURS.toMinutes(hours);
        long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(time) -
                java.util.concurrent.TimeUnit.MINUTES.toSeconds(java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(time));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
