JUNIT_JAR = ../junit5.jar
JAVAC = javac
JAVA = java
CLASS_FILES = *.class

# Compile and run the server
runServer: *.java
	$(JAVAC) -cp .:$(JUNIT_JAR) *.java
	sudo java WebApp 80

# Compile and run tests
runTests: *.java
	$(JAVAC) -cp .:$(JUNIT_JAR) *.java
	$(JAVA) -jar $(JUNIT_JAR) --class-path=. --scan-class-path

# Clean up compiled files
clean:
	rm -f $(CLASS_FILES)

