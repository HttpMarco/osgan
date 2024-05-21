package dev.httpmarco.osgan.networking.client;


import lombok.SneakyThrows;

public final class ReconnectQueue extends Thread {
    private static final long RECONNECT_TIMEOUT = 5000;

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final NettyClient nettyClient;

    public ReconnectQueue(NettyClient nettyClient) {
        super("netty-reconnect-queue");

        this.nettyClient = nettyClient;
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @SneakyThrows
    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }

                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException ex) {
                        break;
                    }

                    if (!running) {
                        break;
                    }
                }
            }

            sleep(RECONNECT_TIMEOUT);

            if (!this.nettyClient.isConnected()) {
                this.nettyClient.connect();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();

        running = false;
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }
}