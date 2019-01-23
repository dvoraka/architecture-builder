package dvoraka.archbuilder;

public interface BuildTool {

    void prepareEnv();

    void build();

    void prepareEnvAndBuild();
}
