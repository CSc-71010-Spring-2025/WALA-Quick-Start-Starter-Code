# WALA Quick Start

Setting up and trying the TJ Watson Library for Analysis (WALA).

## Prerequisites

You will need the following for this exercise:

1. A strong and reliable internet connection.
1. [Git](https://git-scm.com/), the version control system.
1. [The Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
1. [maven](https://maven.apache.org/), the build system.
1. [Gradle](https://gradle.org/), another build system.

## Steps

### Ensure Java is Installed

1. Check that you have java installed. On the command line, run: `java -version`. You should get something like (note that the version may not match):
    ```
    openjdk version "11.0.16" 2022-07-19
    OpenJDK Runtime Environment (build 11.0.16+8-post-Ubuntu-0ubuntu1)
    OpenJDK 64-Bit Server VM (build 11.0.16+8-post-Ubuntu-0ubuntu1, mixed mode, sharing)
    ```
1. Also make sure that you have the java compiler by running: `javac -version` and getting something like this (note that the version may not match):
    ```
    javac 11.0.16
    ```

### Build WALA

1. On the command line, clone WALA: `git clone https://github.com/wala/WALA.git`. It might take a while as it's a large project. As such, make sure that you are on a strong network connection.
1. Change directory (`cd`) to `WALA`.
1. Set your `JAVA_HOME` environmental variable. The variable's value should hold the path of where your JDK is installed. **NOTE**: The installation will not work correctly if this variable is not set correctly. For example, on a Linux system, the JDK might be installed at: `/usr/lib/jvm/default-jdk`. To set the environmental variable on such a system using bash, one may issue the command: `export JAVA_HOME=/usr/lib/jvm/default-jdk`.
1. Build WALA by running: `./gradlew clean build -x test`. This may also take a while as there are many dependencies to download.

### Run WALA Example Drivers

WALA is not really a "program" but rather a framework to help *create* programs that do program analysis. Note that WALA is not a transformation framework per se and is focused on analysis. There are other source-to-source transformations frameworks we will explore, such as the Eclipse SDK.

WALA does come, however, with some example driver programs that show you how to use WALA for program analysis.

1. Try to run the `JavaViewerDriver`, which allows you to view the "call graph", "class hierarchy", and "pointer analysis" of a given class path:
    ```bash
    java -cp com.ibm.wala.core/build/libs/com.ibm.wala.core-1.5.9-SNAPSHOT.jar:com.ibm.wala.util/build/libs/com.ibm.wala.util-1.5.9-SNAPSHOT.jar:com.ibm.wala.shrike/build/libs/com.ibm.wala.shrike-1.5.9-SNAPSHOT.jar com.ibm.wala.examples.drivers.JavaViewerDriver
    ```
    **NOTE**: We are setting the class path using the `-cp` option above but you can also set it using the `CLASSPATH` environmental variable for convenience.
1. You should see an error like this:
    ```
    Exception in thread "main" java.lang.UnsupportedOperationException: expected command-line to include -appClassPath
        at com.ibm.wala.examples.drivers.JavaViewerDriver.validateCommandLine(JavaViewerDriver.java:52)
        at com.ibm.wala.examples.drivers.JavaViewerDriver.main(JavaViewerDriver.java:44)
    ```
    It just means that we're missing a command-line option.
1. Create a "test" directory:
    ```bash
    mkdir test
    ```
1. Create the following small test program in the `test` directory (e.g., `vim test/Test.java`) using a text editor:
    ```java
    class Test {
        public static void main(String[] a) {
            int x = 5;
            int y = x + 6;
            System.out.println(y);
        }
    }
    ```
1. Save the file as `Test.java` in the `test` directory and compile it:
    ```
    cd test
    javac Test.java
    cd -
    ```
    That will produce `Test.class` in the `test` directory.
1. Now, run the `JavaViewerDriver` using the option:
    ```bash
    java -cp com.ibm.wala.core/build/libs/com.ibm.wala.core-1.5.9-SNAPSHOT.jar:com.ibm.wala.util/build/libs/com.ibm.wala.util-1.5.9-SNAPSHOT.jar:com.ibm.wala.shrike/build/libs/com.ibm.wala.shrike-1.5.9-SNAPSHOT.jar com.ibm.wala.examples.drivers.JavaViewerDriver -appClassPath test
    ```
    Replace "1.5.9" above with the version of WALA that you have built.

1. A window should pop up with a list on the left-hand side pane. Select the entry for the `main()` method (`invokestatic < Application, LTest, main([Ljava/lang/String;)V > @5`). You'll see something that looks like assembly code on the right. This is a pretty-print version of the intermediate representation (IR) for the `main()` method used by WALA. This representation is called Shrike, and it is constructed from the bytecode of class `Test` (i.e., from file `Test.class`). Have a look inside to get some idea what is the IR for the input Java method `Test.main()`.
1. Drill one level down from the `invokestatic` node to the node that reads `Node: < Application, LTest, main([Ljava/lang/String;)V > Context: Everywhere`. Select it. Notice the instruction at program counter (PC) 4 (the first number on the left) that corresponds to "line 4" in the original source. **Explain the following**:
    1. What is this instruction doing?
    1. In the original source, computation occurs on both lines 3 and 4. Why in the IR is there only an instruction for line 4?

### Static Analysis with WALA

For illustration purposes, we will use a Java program called jlex; this program is similar to the classic 'lex' scanner generator. First, we will focus on static analysis of the Jimple for program's methods. 

1. Accept the [GitHub Classroom invitation] and set up your repository.
1. Clone your repository, for example:
    ```bash
    git clone https://github.com/CSc-71010-Fall-2025/wala-quick-start-khatchad
    ```
1. In the directory that gets created as a result of the clone, run the following:
    ```bash
    mvn clean install
    java -jar target/CTA-0.0.1-SNAPSHOT.jar JLex.jar
    ```
1. The analysis may take a while, so don't give up. When it's finished, you'll see some output with instructions for particular methods declared in the classes within `JLex.jar` by analyzing the bytecode for each class.
1. Have a look at the source code with an editor or open the project in Eclipse, for example:
    ```bash
    vim src/main/java/cta/Main.java
    ```
1. Change it to measure the following:
    1. Total number of instructions in all methods.
    1. Total number of instructions in all methods that are branching statements. These include instances of interfaces `com.ibm.wala.ssa.SSAGotoInstruction`, `com.ibm.wala.ssa.SSAConditionalBranchInstruction`, and `com.ibm.wala.ssa.SSASwitchInstruction`. Strictly speaking, we also need to consider `com.ibm.wala.ssa.SSAThrowInstruction` (since it throws an exception and the flow of control jumps to the exception handler), but for now we will ignore exceptions and throw statements. The [WALA API documentation](https://wala.github.io/javadoc) may be helpful.
1. Submit your code through GitHub by the deadline in Blackboard.
1. Also submit, in Blackboard, answers to the questions above, as well as the numbers above for jlex.

## Credits

This is assignment is based on an assignment by Atanas Rountev.

[GitHub Classroom invitation]: https://classroom.github.com/a/hQy-Ko52
