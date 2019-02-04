package dvoraka.archbuilder.springconfig;

public interface SpringConfigTemplate {

    //TODO: exceptions

    Object simpleReturn(BeanMapping beanMapping) throws ClassNotFoundException;

    Object paramReturn(BeanMapping beanMapping) throws ClassNotFoundException;
}
