package dvoraka.archbuilder.sample.microservice.server;

import dvoraka.archbuilder.sample.microservice.Management;
import dvoraka.archbuilder.sample.microservice.net.Acknowledgment;

public abstract class AbstractServer implements Management {

    private volatile boolean running;


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
