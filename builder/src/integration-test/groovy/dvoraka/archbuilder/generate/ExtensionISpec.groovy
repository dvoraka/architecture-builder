package dvoraka.archbuilder.generate


import dvoraka.archbuilder.DirType
import dvoraka.archbuilder.Directory
import dvoraka.archbuilder.sample.*
import dvoraka.archbuilder.sample.generic.*
import dvoraka.archbuilder.sample.microservice.data.BaseException
import dvoraka.archbuilder.sample.microservice.data.ResultData
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired

class ExtensionISpec extends BaseISpec {

    @Autowired
    Generator mainGenerator


    def "simple class extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(SimpleClass.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestSimpleClass')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 1
    }

    def "generated simple class extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewSimpleClass')
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNewSimpleClass')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 1
    }

    def "Object abstract extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('component', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Object.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('AbstractTestObject')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "Object extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('component', DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Object.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestObject')
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "Timer extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("component")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Timer.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("TestTimer")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            clazz.getSuperclass() == Timer.class
    }

    def "class 1m extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Class1m.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("TestClass1m")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "class 1p extension"() {
        given:
            Class<?> cls = Class1p
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "generated class 1p extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewClass1p')
                    .parameterTypeName("T")
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(Integer)
                    .filename('Test' + abs.getFilename().get())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "generated class 1p extension NP"() {
        given:
            Directory abs = new Directory.DirectoryBuilder('test', DirType.NEW_TYPE)
                    .parent(srcBase)
                    .filename('NewClass1p')
                    .parameterTypeName("T")
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext', DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + abs.getFilename().get())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "class 1p1m extension NP"() {
        given:
            Class<?> cls = Class1p1m
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "class 1pb1m extension NP"() {
        given:
            Class<?> cls = Class1pb1m
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "abstract class 1am extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(AbstractClass1am.class)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("TestAbstractClass1am")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "abstract class 1p1am extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(AbstractClass1p1am)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(String.class)
                    .filename("TestAbstractClass1p1am")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "abstract class 1p1am extension NP"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(AbstractClass1p1am)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename("TestNPAbstractClass1p1am")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
    }

    def "abstract class 1pb1am extension NP"() {
        given:
            Class<?> cls = AbstractClass1pb1am
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "abstract class 1pb1am2 extension NP"() {
        given:
            Class<?> cls = AbstractClass1pb1am2
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            hasDeclaredMethods(clazz)
    }

    def "abstract class 1p1am abstract extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(AbstractClass1p1am)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .abstractType()
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(String.class)
                    .filename("AbstractClass1p1amAbs")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "class 1p2c1m extension"() {
        given:
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(Class1p2c1m)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(Long.class)
                    .filename("TestClass1p2c1m")
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublic(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 2
    }

    def "class 1p2c1am1m extension"() {
        given:
            Class<?> cls = Class1p2c1am1m
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeClass(Long.class)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublic(clazz)
            hasNoTypeParameters(clazz)
            hasDeclaredMethods(clazz)
            declaredMethodCount(clazz) == 1
            declaredConstructorCount(clazz) == 2
    }

    def "class 3c1m extension"() {
        given:
            Class<?> cls = Class3c1m
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublic(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 2
    }

    def "class 2pp extension"() {
        given:
            Class<?> cls = Class2pp
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory dataAbs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(ResultData)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
//                    .parameterTypeDir(dataAbs)
            // for current implementation is's possible to use already parametrized type
            // but it would be great to have options to build a complete type programmatically
                    .parameterTypeClass(TestingResultData)
                    .parameterTypeClass(BaseException)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
    }

    def "ResponseMessage extension"() {
        given:
            Class<?> cls = ResponseMessage
            Directory dataAbs = new Directory.DirectoryBuilder('')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(TestingResultData.class)
                    .parameterTypeClass(BaseException.class)
                    .build()
            Directory abs = new Directory.DirectoryBuilder('')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .parameterTypeDir(dataAbs)
                    .parameterTypeClass(BaseException.class)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
    }

    def "class 3c1m extension extension"() {
        given:
            Class<?> cls = Class3c1m
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext1 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .filename('Test1' + cls.getSimpleName())
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ext1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 2
    }

    def "class 3c1m abstract extension extension"() {
        given:
            Class<?> cls = Class3c1m
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext1 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('Test1' + cls.getSimpleName())
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ext1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
            declaredConstructorCount(clazz) == 2
    }

    def "abstract class E3c1m abstract extension extension"() {
        given:
            Class<?> cls = AbstractClassE3c1am1m.class
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext1 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('Test1' + cls.getSimpleName())
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ext1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 1
            declaredConstructorCount(clazz) == 2
    }

    def "abstract class E1pb2am abstract extension extension NP"() {
        given:
            Class<?> cls = AbstractClassE1pb2am
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext1 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('Test1' + cls.getSimpleName())
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ext1)
                    .filename('Test2' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "abstract class E1pb2am abstract extension extension"() {
        given:
            Class<?> cls = AbstractClassE1pb2am
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext1 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .abstractType()
                    .filename('Test10' + cls.getSimpleName())
                    .build()
            Directory ext2 = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(ext1)
                    .parameterTypeClass(Long.class)
                    .filename('Test20' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext2))
        then:
            notThrown(Exception)
            isPublicNotAbstract(clazz)
            hasNoTypeParameters(clazz)
            declaredMethodCount(clazz) == 2
    }

    def "simple interface extension"() {
        given:
            Class<?> cls = SimpleInterface.class
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .interfaceType()
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface 4p1am extension"() {
        given:
            Class<?> cls = Interface4p1m.class
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory ext = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .interfaceType()
                    .filename('Test' + cls.getSimpleName())
                    .parameterTypeName('java.lang.String')
                    .parameterTypeClass(Long.class)
                    .parameterTypeName('java.lang.Boolean')
                    .parameterTypeClass(SimpleInterface.class)
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(ext))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface 4p1am extension NP"() {
        given:
            Class<?> cls = Interface4p1m.class
            Directory interface4p = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory interface4pImpl = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(interface4p)
                    .interfaceType()
                    .filename('TestNP' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(interface4pImpl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface E1pb extension"() {
        given:
            Class<?> cls = InterfaceE1pb
            Directory abs = new Directory.DirectoryBuilder('test')
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .interfaceType()
                    .parameterTypeClass(Long.class)
                    .filename('Test' + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasNoTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }

    def "interface E1pb extension NP"() {
        given:
            Class<?> cls = InterfaceE1pb
            Directory abs = new Directory.DirectoryBuilder("test")
                    .type(DirType.ABSTRACT)
                    .parent(srcBase)
                    .typeClass(cls)
                    .build()
            Directory impl = new Directory.DirectoryBuilder('ext')
                    .type(DirType.IMPL)
                    .parent(srcBase)
                    .superType(abs)
                    .interfaceType()
                    .filename("TestNP" + cls.getSimpleName())
                    .build()
        when:
            mainGenerator.generate(root)
            Class<?> clazz = loadClass(getClassName(impl))
        then:
            notThrown(Exception)
            isPublicAbstract(clazz)
            hasTypeParameters(clazz)
            hasNoDeclaredMethods(clazz)
    }
}
