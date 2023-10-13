### Challenge 1 - Wc-Tool

This challenge corresponds to the first part of the Coding Challenges series by John Crickett https://codingchallenges.fyi/challenges/challenge-wc.

### Description
The WC tool is written in Java. The Jar is used to count the number of words, lines, bytes and characters in a file/stdin.

### Build Jar
Clone the project and build the jar, Otherwise I have also added the jar also which can be used directly. Read the usage section.

````
./gradlew clean build
````
Go to build/libs folder and find the jar named as wc-tool-1.0.jar

### Usage
You can use ts-node to run the tool as follows:

````
java -jar _**JARNAME**_ [option] filename
````

The following options are supported:

-w: prints the number of words in the file
-l: prints the number of lines in the file
-c: prints the number of bytes in the file
-m: prints the number of characters in the file
The tool can also be used in stdin mode as follows:

````
cat filename | java -jar _**JARNAME**_ [option]
````