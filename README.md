# An AspectJ-based API-usage tracer

The primary and only goal is to log how often the methods in the public API of a specific project are called at runtime.
Calls are written directly to a CSV file. Calls need to be counted afterwards.
The tracer can be integrated (relatively) easily
into an existing Maven or Gradle build.

## Installing

Run `mvn clean install -DskipTests` in this directory. This installs the tracer to your local
m2 repo, and it should be usable from then on.

## Tracing a project

### For Maven

Enabling the tracing for an existing project requires some modifications to the `pom.xml` build file and
project structure.

- Step 1: add an aop.xml file to `src/test/resources/META-INF`. The content can be like below. Replace the
log4j stuff in the example with a package name pattern that makes sense for the lib you want to trace.

```xml
<aspectj>
	 <!-- <weaver options="-verbose -showWeaveInfo -debug"> -->
	 <weaver>
	 	<include within="org.apache.logging.log4j..**"/>
	 	<include within="ch.uzh.ifi.seal.dynamicanalyzer.LogAspect"/>
     <!--<exclude within="org.apache.logging.log4j.osgi..**" />-->
	 </weaver>
	 	<aspects>
		  <aspect name="ch.uzh.ifi.seal.dynamicanalyzer.LogAspect"/>
	</aspects>
</aspectj>
```

- Step 2: Add some dependencies to `pom.xml`. You will need to add the following dependencies:

```xml
<dependency>
  <groupId>hopper</groupId>
  <artifactId>aspectj-tracer</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjrt</artifactId>
  <version>1.8.10</version>
</dependency>
```

- Step 3: Enable LTW. The following assumes you want to trace unit test execution. In that case add
the following to your surefire configuration:

```xml
<configuration>
  <argLine>
    -javaagent:$HOME/.m2/repository/org/aspectj/aspectjweaver/1.8.10/aspectjweaver-1.8.10.jar -Dtracer.file=output.csv -Dtracer.projectname=testproject -Dtracer.libname=log4j2
  </argLine>
</configuration>
```

(replace $HOME with your home directory, and the other parameters as you want, see below)

### For Gradle

For Gradle the process is *almost* the same.

- Step 1: the same as for Mvn (see above).

- Step 2: add repository mavenLocal if not used:
```gradle
repositories {
  mavenLocal()
}
```

- Step 3: Add some dependencies to `build.gradle`. You will need to add the following dependencies:

```gradle
testCompile("org.aspectj:aspectjrt:1.8.10")
testCompile("org.aspectj:aspectjweaver:1.8.10")
testCompile("hopper:aspectj-tracer:0.0.1-SNAPSHOT")
```

- Step 4: Enable LTW. The following assumes you want to trace unit test execution. In that case add
the following to your Gradle test plugin configuration:

```gradle
jvmArgs '-javaagent:$HOME/.m2/repository/org/aspectj/aspectjweaver/1.8.10/aspectjweaver-1.8.10.jar'
systemProperty 'tracer.file', 'output.csv'
systemProperty 'tracer.projectname', 'reveno'
systemProperty 'tracer.libname', 'protostuff'
```

## Parameters

The tracer accepts 3 parameters as environment variables. You can either set them via
an argline in the `pom.xml` as above, but you could also export them before launching the app, or give them
as parameter to your Maven or Gradle build (`-Dtracer.file=output.csv`)

- "tracer.file" is the name of the output file that the results will be written to
- "tracer.projectname" is the project name to use in the CSV file
- "tracer.libname" is the library name to use in the CSV file


## Other things of import

The way the tracer is currently implemented is *slow*. Do not be puzzled if it *significantly* slows down
unit test execution. Some builds might even break because of that.
