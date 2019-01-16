# Architecture builder

[![Build Status](https://travis-ci.org/dvoraka/architecture-builder.svg?branch=master)](https://travis-ci.org/dvoraka/architecture-builder)
[![codecov](https://codecov.io/gh/dvoraka/architecture-builder/branch/master/graph/badge.svg)](https://codecov.io/gh/dvoraka/architecture-builder)

It's an application/project builder from prebuilt structures. You can use smaller blocks to build your application
dynamically (generate source code and all other stuff). There are high-level Java templates for building the whole
project. For instance, micro-service template is for building new micro-service with known architecture. It possible
to use your own abstract types with the template or build your own template.

## Building blocks

Description of main building blocks you can use.

### Directory

Data structure for defining a part of a project. You can describe filesystem directory, abstract type implementation,
build configuration and many other situations.

### Architecture template

Architecture template uses directory tree and encapsulate it. It's used for defining a concrete architecture and
making it configurable, like name of the base package, main abstract class, etc.

### Source template

For Java classes it's possible to use configurable source templates. For instance, Spring Boot application class
is usually very similar and you only need to choose the name of the application. The templates are used in Directories.

### Configuration template

It's a template for any configuration, like build configuration. The templates are used in Directories.
