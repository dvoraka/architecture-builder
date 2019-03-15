package dvoraka.archbuilder.build;

public interface BuildTool {

    void prepareEnv();

    void build();

    void prepareEnvAndBuild();
}
