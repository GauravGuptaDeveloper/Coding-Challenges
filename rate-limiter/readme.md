### Challenge 27 - Rate-Limiter

This challenge corresponds to the Rate Limiter which is of the Coding Challenges series by John Crickett.

### Description
The Rate Limiter is written in Java. The main purpose of this repository is to have understanding of how to build your own rate limiter, you will see that there are many @Bean annotation and filters and algorithm.

### Build Jar
Clone the project and build the jar, Otherwise I have also added the jar also which can be used directly. Read the usage section.

````
./gradlew clean build
````

I would highly suggest to clone this project and play with this, and have understanding of rate limiter.

### How to run it?

Just simply remove the comment from @Bean annotation and @Component from Services and filter and you are good to go.
This can be improved, but again the main purpose is to learn about rate limiter.

### Usage
Default port is 8080;

If you want to use redis then please add redis in localhost or any other server and add the IP in main class.