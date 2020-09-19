# Architecture builder

[![Build Status](https://travis-ci.org/dvoraka/architecture-builder.svg?branch=master)](https://travis-ci.org/dvoraka/architecture-builder)
[![codecov](https://codecov.io/gh/dvoraka/architecture-builder/branch/master/graph/badge.svg)](https://codecov.io/gh/dvoraka/architecture-builder)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/87c3fd4174c74af7b7a35f717d6c9afe)](https://www.codacy.com/app/dvoraka/architecture-builder?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dvoraka/architecture-builder&amp;utm_campaign=Badge_Grade)

It's an application/project builder from prebuilt structures. You can use smaller blocks to build your application
dynamically (generate source code and all other stuff). There are high-level Java modules for building the whole
project. For instance, micro-service module is for building new micro-service project with known architecture.
It possible to use your own abstract types with the module or build your own modules/templates.

Prototype currently works with Java 8 only.

## Building blocks

Description of main building blocks you can use.

### Directory

Data structure for defining parts of a project. You can describe filesystem directory, abstract type implementation,
dependency on a generated type, build configuration and many other situations.

### Architecture module

Architecture module uses **Directory** tree and encapsulates it. It's used for defining a concrete
architecture/project and making it configurable, like name of the base package, main abstract class,
type of the build system, etc.

### Architecture submodule

Submodules are smaller than modules and encapsulate parts of a **Directory** tree. For instance, a network subsystem.

### Source template

For Java classes it's possible to use configurable source templates. For instance, a Spring Boot application class
is usually very similar and you only need to choose a name of the application. The templates are used by Directories.

### Text template

Template for any text based data. It could be used simple text builder to build dynamic text templates.
The templates are used by Directories.

## Examples

### Micro-service template

It creates the whole micro-service project skeleton based on known abstraction.

Starting abstraction is (from sample package in the project):
```text
microservice/
├── data
│   ├── BaseException.java
│   ├── message
│   │   ├── BaseMessage.java
│   │   ├── Message.java
│   │   ├── RequestMessage.java
│   │   ├── ResponseMessage.java
│   │   └── ResultMessage.java
│   └── ResultData.java
├── Management.java
├── net
│   ├── Acknowledgment.java
│   ├── BaseNetComponent.java
│   ├── GenericNetComponent.java
│   ├── NetMessageListener.java
│   ├── receive
│   │   ├── BaseNetReceiver.java
│   │   └── NetReceiver.java
│   ├── send
│   │   ├── AbstractNetSender.java
│   │   └── NetSender.java
│   └── ServiceNetComponent.java
├── server
│   └── AbstractServer.java
└── service
    └── BaseService.java
```

And generated project structure:

```text
budget-service/
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    └── main
        ├── java
        │   └── test
        │       └── budget
        │           ├── BudgetApp.java
        │           ├── configuration
        │           │   └── BudgetConfig.java
        │           ├── data
        │           │   ├── BudgetData.java
        │           │   └── message
        │           │       ├── BudgetMessage.java
        │           │       └── BudgetResponseMessage.java
        │           ├── exception
        │           │   └── BudgetException.java
        │           ├── net
        │           │   ├── BudgetNetAdapter.java
        │           │   ├── BudgetNetComponent.java
        │           │   ├── BudgetNetReceiver.java
        │           │   └── BudgetNetResponseReceiver.java
        │           ├── server
        │           │   └── BudgetServer.java
        │           └── service
        │               ├── BudgetService.java
        │               └── DefaultBudgetService.java
        └── resources
            └── application.properties
```

And then you can insert your logic easily into prepared structure.

Source code for the generation:
```java
package dvoraka.archbuilder;

import dvoraka.archbuilder.build.BuildTool;
import dvoraka.archbuilder.build.GradleBuildTool;
import dvoraka.archbuilder.generate.Generator;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.module.microservice.DefaultMicroservice;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;

public class MicroserviceTemplate {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dvoraka.archbuilder");
        Generator mainGenerator = context.getBean(Generator.class);
        SpringConfigGenerator configGenerator = context.getBean(SpringConfigGenerator.class);
        BuilderProperties properties = context.getBean(BuilderProperties.class);

        String rootDirName = "budget-service";
        String packageName = "test.budget";
        String serviceName = "Budget";

        BuilderHelper helper = new BuilderHelper(properties, rootDirName, packageName, serviceName);
        Module microservice = new DefaultMicroservice(helper, configGenerator);

        mainGenerator.generate(microservice.getRootDirectory());

        BuildTool buildTool = new GradleBuildTool(new File(rootDirName));
        buildTool.prepareEnv();
    }
}
```

Source code for the template:
```java
package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.net.DefaultNetSubmodule;
import dvoraka.archbuilder.submodule.net.NetSubmodule;
import dvoraka.archbuilder.submodule.service.DefaultServiceSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.ServiceSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class DefaultMicroservice implements Module, TemplateHelper {

    private final Directory root;


    public DefaultMicroservice(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        root = root(helper.getRootDirName());
        Directory srcBase = srcRootAndBase(root, pkg2path(helper.getPackageName()));

        // service
        ServiceSubmodule serviceSubmodule = new DefaultServiceSubmodule(helper, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // network
        NetSubmodule netSubmodule = new DefaultNetSubmodule(
                helper, serviceSubmodule.getService(), configGenerator);
        netSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule = new ServiceSpringBootAppSubmodule(helper);
        springBootAppSubmodule.addSubmoduleTo(srcBase);

        // Spring configuration
        SpringConfigSubmodule springConfigSubmodule =
                new SpringConfigSubmodule(helper.getBaseName(), configGenerator);
        springConfigSubmodule.addMappings(serviceSubmodule.getConfiguration());
        springConfigSubmodule.addMappings(netSubmodule.getConfiguration());
        springConfigSubmodule.addSubmoduleTo(srcBase);

        // application properties
        properties(root, new AppPropertiesTemplate());

        // build
        BuildSubmodule buildSubmodule = new DefaultGradleSubmodule(helper.getBaseName());
        buildSubmodule.addSubmoduleTo(root);

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
```
