package dvoraka.archbuilder.prototype.statecoordinator.state;

public interface State<PD> {

    void process();

    default void resume(PD data) {
        throw new UnsupportedOperationException();
    }
}
