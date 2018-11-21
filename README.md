# SOSJ-OPCUA (Service Oriented SystemJ)


Developed for research purpose by Udayanto Dwi Atmojo

This framework is developed by combining the OPC-UA framework Eclipse Milo by Kevin Herron (detail below), the work by Stefan Profanter et al [1] which introduces OPC UA Local Discovery Server function in Eclipse Milo, and a service oriented programming approach Service Oriented SystemJ (SOSJ) developed Udayanto Dwi Atmojo [2]


[1] Profanter, Stefan, et al. "OPC UA for plug & produce: Automatic device discovery using LDS-ME." Proceedings of the IEEE International Conference on Emerging Technologies And Factory Automation (ETFA). 2017.

[2] Atmojo, Udayanto Dwi, Zoran Salcic, and K. I. K. Wang. "SOSJ: A new programming paradigm for adaptive distributed systems." IEEE 10 th Conference on Industrial Electronics and Applications (ICIEA). 2015.


# Eclipse Milo


[![Build status](https://travis-ci.org/eclipse/milo.svg?branch=master)](https://travis-ci.org/eclipse/milo)

Milo is an open-source implementation of OPC UA. It includes a high-performance stack (channels, serialization, data structures, security) as well as client and server SDKs built on top of the stack.

While this project has existed for some time, it is new to the Eclipse foundation and is therefore considered to be in [incubation](https://eclipse.org/projects/dev_process/development_process.php#6_2_3_Incubation). While in incubation it will continue to use `0.x.x` versions.

Gitter: [![Join the chat at https://gitter.im/eclipse/milo](https://badges.gitter.im/eclipse/milo.svg)](https://gitter.im/eclipse/milo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Mailing list: https://dev.eclipse.org/mailman/listinfo/milo-dev

Stack Overflow tag: [milo](http://stackoverflow.com/questions/tagged/milo)

## Maven

Snapshots and releases are available using Maven. In order to use snapshots a reference to the Sonatype snapshot repository must be added to your pom file:

```xml
<repository>
    <id>oss-sonatype</id>
    <name>oss-sonatype</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

### OPC UA Client SDK

```xml
<dependency>
    <groupId>org.eclipse.milo</groupId>
    <artifactId>sdk-client</artifactId>
    <version>0.1.1</version>
</dependency>
```

### OPC UA Server SDK

```xml
<dependency>
    <groupId>org.eclipse.milo</groupId>
    <artifactId>sdk-server</artifactId>
    <version>0.1.1</version>
</dependency>
```

### OPC UA Stack

```xml
<dependency>
    <groupId>org.eclipse.milo</groupId>
    <artifactId>stack-client</artifactId>
    <version>0.1.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.eclipse.milo</groupId>
    <artifactId>stack-server</artifactId>
    <version>0.1.1</version>
</dependency>
```
