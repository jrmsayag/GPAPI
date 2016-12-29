# GPAPI #

A Genetic-Programming library for the Java(tm) platform.

### What is this repository for? ###

This repository contains the GPAPI Genetic-Programming library, and a few examples that illustrate how the library can be used.

More documentation is to come concerning what exactly the library consists in, what are the available features, and those that are planned. Similarly, a richer Javadoc, global implementation notes and objectives, and better code comments, should be incorporated in the future.

### How do I get set up? ###

The repository contains two separated Eclipse projects, one for the library itself and the other for the examples, located respectively in the GPAPI and GPAPI-Examples sub-folders. Both of these projects are configured with Maven and therefore have their own pom.xml file.

Until the project is deployed to a public Maven repository, the library can be used the following way :

* Clone this repository.
* In Eclipse, import both projects as Maven projects.
* In your own Eclipse project, add a Maven dependency to GPAPI (refer to GPAPI's pom.xml file for group, artifcat, and version information).
* Alternatively, if your own project does not use Maven, you can manually add GPAPI's source folders to your classpath.

### Maven Dependencies ###

* JUNG
* JFreeChart

### Licence ###

* LGPL

### Contact ###

Feel free to contact me at <jrmsayag@gmail.com> if you have any question about GPAPI, or simply to let me know you're using it in your project, I'd be glad to hear about it !
