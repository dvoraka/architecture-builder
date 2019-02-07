package dvoraka.archbuilder.prototype.data;

public class PBudgetMessage implements PMessageInterface<PData> {

    private String id;
    private PData data;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public PData getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(PData data) {
        this.data = data;
    }
}
