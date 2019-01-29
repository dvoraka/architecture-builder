package dvoraka.archbuilder.sample.microservice.server;

import dvoraka.archbuilder.sample.microservice.Management;
import dvoraka.archbuilder.sample.microservice.net.Acknowledgment;

public abstract class AbstractServer implements Management {

    private volatile boolean running;


    public abstract void start();

    public abstract void stop();

    @Override
    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    protected void ackMessage(Acknowledgment acknowledgment) {
        acknowledgment.ack();
    }
}
