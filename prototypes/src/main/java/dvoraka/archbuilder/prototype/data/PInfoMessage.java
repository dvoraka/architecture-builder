package dvoraka.archbuilder.prototype.data;

public class PInfoMessage implements PMessageInterface2 {

    private String id;
    private final PData data;


    public PInfoMessage(PData data) {
        this.data = data;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PData getData() {
        return data;
    }

    public void customMethod() {
    }
}
