# **Project: simple-webserver**

**Version:** 1.0.0

## **1\. Project Overview**

This project implements a simple, slightly secure web application based on the requirements outlined in prd-simple-webserver-sec.md. The server provides a styled home page at the root URL (/) and a custom 404 "Not Found" page for all other routes. The core focus is on establishing a solid architectural foundation with a clear separation of concerns, adherence to security best practices, and correct HTTP behavior.

## **2\. Dependencies**

The project relies on a minimal set of well-established libraries from the Clojure ecosystem.

* **org.clojure/clojure "1.12.0"**: The core Clojure language.  
* **ring/ring-core "1.14.1"**: The standard HTTP server abstraction for Clojure.  
* **ring/ring-jetty-adapter "1.14.1"**: The Jetty server implementation that runs the Ring application.  
* **compojure "1.7.1"**: A concise routing library used to define URL handlers.  
* **hiccup "2.0.0"**: A library for generating HTML from Clojure data structures.

## **3\. Architecture**

The application follows a standard Ring/Compojure architecture.

### **Component Overview**

graph TD  
    A\[Jetty Web Server\] \--\> B\[Ring Middleware Chain\]  
    B \--\> C\[Compojure Routes\]  
    C \-- "/" \--\> D\[home-handler\]  
    C \-- "/\*" \--\> E\[not-found-handler\]  
    D \--\> F\[Layout Function\]  
    E \--\> F  
    F \--\> G\[HTML Response\]

1. **Jetty Server**: The entry point that listens for HTTP requests.  
2. **Middleware Chain**: Requests are passed through a series of wrappers:  
   * wrap-resource: Serves static files (like style.css) from the resources/public directory.  
   * wrap-content-type: Adds a Content-Type header based on the response.  
   * wrap-security-headers: Adds a strict set of security-related HTTP headers.  
3. **Compojure Routes**: The request is dispatched to the appropriate handler based on the URL.  
4. **Handlers**: Functions that generate a Ring response map (status, headers, body).  
5. **Layout**: A Hiccup-based function that renders the final HTML, ensuring consistent styling across pages.

## **4\. Quick Start**

### **Prerequisites**

* Java (JDK 11 or later)  
* [Leiningen](https://leiningen.org/)

### **Running the Application**

1. Clone the repository.  
2. Navigate to the project directory: cd simple-webserver  
3. Start the server:  
   lein run

4. The server will be running at http://localhost:3000.

### **Building the Standalone JAR**

1. From the project directory, run:  
   lein uberjar

2. The compiled application will be located at target/simple-webserver-standalone.jar.  
3. Run the JAR file:  
   java \-jar target/simple-webserver-standalone.jar

## **5\. API Documentation**

The application exposes two endpoints:

* **GET /**: Returns the home page with a 200 OK status.  
* **All other routes**: Return a custom 404 page with a 404 Not Found status.