package dvoraka.archbuilder.prototype.statecoordinator.state;

public interface Action<PD> {

    void process();

    default void resume(PD data) {
        throw new UnsupportedOperationException();
    }
}
