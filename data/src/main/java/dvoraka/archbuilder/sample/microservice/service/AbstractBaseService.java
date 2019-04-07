package dvoraka.archbuilder.sample.microservice.service;

public class AbstractBaseService implements BaseService {

    private volatile boolean running;


    @Override
    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }
}
