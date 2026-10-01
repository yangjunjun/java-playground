package com.learn.concurrency;

/**
 * Thread 基础：创建、start vs run、sleep、join、interrupt。
 */
public class ThreadBasics {

    public static void demo() throws InterruptedException {
        // 1) start() 新建线程执行 run()；直接调用 run() 只是当前线程的普通方法调用
        Thread worker = new Thread(() ->
                System.out.println("  [新线程] 当前线程名: " + Thread.currentThread().getName()),
                "worker-1");

        System.out.println("调用 run()（错误示范，仍在主线程）:");
        worker.run();   // 不会启动新线程

        System.out.println("调用 start()（正确写法）:");
        worker.start();
        worker.join();  // 等 worker 结束

        // 2) sleep：让出 CPU 进入 TIMED_WAITING
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(50);
                System.out.println("  [sleeper] 睡醒了");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        sleeper.start();
        sleeper.join();

        // 3) interrupt：协作式中断
        Thread slow = new Thread(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                System.out.println("  [slow] 收到中断，优雅退出");
            }
        });
        slow.start();
        slow.interrupt();
        slow.join();

        System.out.println("线程状态演示: " + slow.getState() + "（已结束）");
    }
}
