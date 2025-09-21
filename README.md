Java Web Server

This is a simple Java web server you can run on your computer or a public server.

⚠️ WARNING: Running this server publicly might expose your IP address. Do not share it if you are concerned about privacy.

Recommended Java version: Java 8 or higher (JDK 25 suggested)

How to Use

Place the webserver folder somewhere easy to access (e.g., your Desktop).

Open Command Prompt (CMD) and navigate to the folder:

cd "C:\Users\YOURUSER\Desktop\webserver"


⚠️ If your system uses a non-English folder name (like Área de Trabalho in Portuguese), you need to use the proper folder name or rename it to Desktop for easier access.

Compile the Java server if needed:

javac SimpleWebServer.java


Run the server:

java SimpleWebServer


Open your browser and go to:

http://YOUR_IP_ADDRESS:8080


Example: http://192.168.1.100:8080

The server should now show your web page, play music, or display a Java logo depending on your HTML content.

Notes

Make sure you have JDK installed. You can check with:

java -version
javac -version


To make it accessible publicly (over the internet), you may need to forward port 8080 on your router.

Java-based servers like this are very basic. For real-world public servers, consider using Apache Tomcat, Jetty, or Node.js.
