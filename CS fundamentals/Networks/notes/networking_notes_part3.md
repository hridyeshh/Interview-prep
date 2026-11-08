# Computer Networks - Comprehensive Study Notes (Part 3)

## Table of Contents (Part 3)
1. [HTTP Deep Dive](#1-http-deep-dive)
2. [HTTP Methods (GET, POST, PUT, DELETE)](#2-http-methods)
3. [HTTP Status Codes](#3-http-status-codes)
4. [Cookies](#4-cookies)
5. [How Email Works](#5-how-email-works)
6. [DNS Deep Dive](#6-dns-deep-dive)
7. [Transport Layer Deep Dive](#7-transport-layer-deep-dive)
8. [Checksum](#8-checksum)
9. [Timers in Transport Layer](#9-timers-in-transport-layer)
10. [UDP Deep Dive](#10-udp-deep-dive)
11. [TCP Deep Dive](#11-tcp-deep-dive)
12. [TCP Three-Way Handshake (Detailed)](#12-tcp-three-way-handshake-detailed)
13. [Network Layer Deep Dive](#13-network-layer-deep-dive)
14. [Control Plane](#14-control-plane)
15. [IP Protocol Deep Dive](#15-ip-protocol-deep-dive)
16. [Packets](#16-packets)
17. [IPv4 vs IPv6](#17-ipv4-vs-ipv6)
18. [Middle Boxes](#18-middle-boxes)
19. [NAT (Network Address Translation)](#19-nat-network-address-translation)
20. [Data Link Layer Deep Dive](#20-data-link-layer-deep-dive)

---

## 1. HTTP Deep Dive

### Understanding the Protocol that Powers the Web

HTTP (Hypertext Transfer Protocol) is the foundation of data communication on the World Wide Web. Every time you load a web page, stream a video, or submit a form, HTTP is working behind the scenes. Let's understand this protocol deeply, not just superficially.

---

### **What HTTP Really Is**

HTTP is an **application-layer protocol** that defines how messages are formatted and transmitted between web browsers (clients) and web servers. Think of HTTP as the language that your browser and web servers use to have a conversation. Just like you need to speak the same language to communicate with someone, browsers and servers need to follow HTTP's rules to exchange information.

The key insight about HTTP is that it is fundamentally a **request-response protocol**. The client always initiates the conversation by sending a request, and the server responds with the requested resource or an error message. The server never spontaneously sends data to the client without first receiving a request. This is different from protocols like WebSockets where bi-directional communication is possible.

---

### **HTTP is Stateless - What This Really Means**

One of HTTP's most important characteristics is that it is **stateless**. This means that each request-response pair is completely independent. The server does not remember anything about previous requests from the same client. Imagine going to a coffee shop where the barista has complete amnesia and forgets you the moment you step away. Every time you return, you have to introduce yourself again and specify your entire order from scratch.

Let's see what this means in practice. When you visit a website and click through multiple pages, each page load is a separate HTTP request. The server treats each request as if it's coming from a brand new client it has never seen before. If you're logged into a website and navigate to different pages, the server doesn't inherently "remember" that you're logged in from one request to the next.

This stateless design has important implications. On the positive side, it makes servers simpler and more scalable because they don't need to maintain state for thousands or millions of concurrent users. If your server crashes and restarts, it doesn't lose any state information because there was none to begin with. On the negative side, this creates challenges for applications that need to maintain user sessions, shopping carts, or authentication state across multiple requests.

---

### **HTTP Versions - The Evolution**

Understanding HTTP's evolution helps us appreciate why modern web applications work the way they do.

#### **HTTP/0.9 (1991) - The Beginning**

The original HTTP was incredibly simple. It had only one method (GET) and could only transfer HTML files. A complete HTTP/0.9 transaction looked like this:

```
Client: GET /index.html
Server: [sends HTML file and closes connection]
```

There were no headers, no status codes, and no way to send anything except HTML. While simple, this limitation meant that every single resource on a page (images, stylesheets, scripts) would require opening a new TCP connection. This was extremely inefficient.

#### **HTTP/1.0 (1996) - Adding Structure**

HTTP/1.0 added the structure we recognize today. It introduced headers that could provide metadata about the request and response, status codes to indicate success or failure, and support for different content types beyond HTML. Now you could request images, videos, and any other file type.

A typical HTTP/1.0 request looked like this:

```
GET /index.html HTTP/1.0
Host: www.example.com
User-Agent: Mozilla/5.0

[empty line indicating end of headers]
```

And the server would respond:

```
HTTP/1.0 200 OK
Content-Type: text/html
Content-Length: 1234

<!DOCTYPE html>
<html>
...
```

However, HTTP/1.0 still suffered from a major performance problem. After each request-response, the TCP connection was closed. Opening a new TCP connection requires a three-way handshake, which adds latency. For a web page with fifty images, you would need fifty separate TCP connections, each with its own handshake overhead.

#### **HTTP/1.1 (1997) - Persistent Connections**

HTTP/1.1 solved the connection problem with **persistent connections** (also called keep-alive). Now, multiple requests could be sent over the same TCP connection. This was a massive performance improvement. Instead of:

```
[Open connection] -> Request 1 -> Response 1 -> [Close connection]
[Open connection] -> Request 2 -> Response 2 -> [Close connection]
[Open connection] -> Request 3 -> Response 3 -> [Close connection]
```

You could do:

```
[Open connection] 
-> Request 1 -> Response 1 
-> Request 2 -> Response 2 
-> Request 3 -> Response 3 
[Close connection]
```

HTTP/1.1 also introduced **pipelining**, where the client could send multiple requests without waiting for each response. However, responses still had to be returned in order (head-of-line blocking), which limited its effectiveness.

HTTP/1.1 added many other features including chunked transfer encoding (for sending data without knowing the total size upfront), range requests (for resuming downloads), and better caching mechanisms. It became so successful that it remained the dominant version of HTTP for nearly two decades.

#### **HTTP/2 (2015) - Multiplexing**

HTTP/2 represented a major architectural change. The key innovation was **multiplexing** - the ability to send multiple requests and responses simultaneously over a single TCP connection, without them blocking each other. Think of it like upgrading from a single-lane road to a multi-lane highway. Multiple data streams can flow concurrently without interference.

HTTP/2 also introduced:

**Binary Protocol**: Unlike HTTP/1.x which was text-based, HTTP/2 uses binary framing. This makes parsing more efficient and less error-prone. While this makes debugging harder (you can't just read the raw HTTP/2 like you could with HTTP/1.1), the performance benefits are substantial.

**Header Compression**: HTTP headers can be quite large, especially with cookies. HTTP/2 uses HPACK compression to reduce header overhead. For subsequent requests to the same server, HTTP/2 only sends the headers that have changed, dramatically reducing bandwidth usage.

**Server Push**: Servers can proactively send resources to the client before the client requests them. If the server knows that a client requesting index.html will also need style.css and script.js, it can push those resources immediately rather than waiting for separate requests.

**Stream Prioritization**: Clients can indicate which resources are more important, allowing servers to prioritize sending critical resources like CSS before images.

#### **HTTP/3 (2020) - QUIC**

HTTP/3 represents another fundamental shift by replacing TCP with QUIC (Quick UDP Internet Connections), which runs over UDP. This might seem counterintuitive since TCP is reliable and UDP is not, but QUIC implements its own reliability mechanisms on top of UDP while solving some of TCP's inherent limitations.

The main problem HTTP/3 solves is TCP's head-of-line blocking at the transport layer. Even with HTTP/2's multiplexing, if a single TCP packet is lost, all streams on that connection must wait for retransmission. With QUIC, packet loss in one stream doesn't affect other streams.

QUIC also includes built-in encryption (based on TLS 1.3) and faster connection establishment. A new connection can often be established with zero round-trips by using previously established security credentials.

---

### **The Structure of HTTP Messages**

Let's break down exactly what an HTTP message looks like, because understanding this structure is crucial for debugging and for writing code that works with HTTP.

#### **HTTP Request Structure**

Every HTTP request has three main parts:

**1. Request Line** (also called the start line):
```
METHOD /path HTTP/version
```

For example:
```
GET /api/users/123 HTTP/1.1
```

This line tells the server what action to perform (GET), on which resource (/api/users/123), and using which version of the protocol (HTTP/1.1).

**2. Headers** (metadata about the request):
```
Host: www.example.com
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)
Accept: text/html,application/json
Accept-Language: en-US,en;q=0.9
Accept-Encoding: gzip, deflate, br
Connection: keep-alive
Cookie: session=abc123; user_id=456
```

Each header provides information about the request. The Host header is mandatory in HTTP/1.1 and specifies which domain the request is for (this allows a single server to host multiple domains). The User-Agent identifies the client software. The Accept headers tell the server what formats the client can handle. The Cookie header sends saved state information back to the server.

**3. Body** (optional, for methods like POST or PUT):
```
{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "secretpassword"
}
```

The body contains data being sent to the server, such as form submissions or API payloads. GET and DELETE requests typically don't have bodies, while POST and PUT requests usually do.

An important detail: there must be an empty line between the headers and the body. This empty line signals the end of the headers section. Without it, the server wouldn't know where the headers end and the body begins.

#### **HTTP Response Structure**

HTTP responses have a similar structure:

**1. Status Line**:
```
HTTP/version STATUS_CODE Reason-Phrase
```

For example:
```
HTTP/1.1 200 OK
```

This tells the client which protocol version the server is using, a numeric status code indicating the result, and a human-readable reason phrase.

**2. Response Headers**:
```
Date: Thu, 23 Jan 2025 10:30:00 GMT
Server: Apache/2.4.41
Content-Type: application/json
Content-Length: 156
Set-Cookie: session=xyz789; HttpOnly; Secure
Cache-Control: max-age=3600
Connection: keep-alive
```

Response headers provide metadata about the response. Content-Type tells the client what format the body is in. Content-Length specifies the size of the response body. Set-Cookie creates or updates cookies on the client. Cache-Control provides caching instructions.

**3. Response Body**:
```
{
    "id": 123,
    "username": "john_doe",
    "email": "john@example.com",
    "created_at": "2024-01-15T10:00:00Z"
}
```

The body contains the actual content being returned to the client, whether that's HTML, JSON, an image, or any other data type.

---

### **How HTTP Actually Works Under the Hood**

When you type a URL into your browser and press enter, a complex series of events unfolds. Let's walk through exactly what happens, step by step.

**Step 1: URL Parsing**

The browser first parses the URL to extract its components:
```
https://www.example.com:443/api/users/123?active=true#profile
  |       |            |    |            |           |
scheme  hostname     port  path        query    fragment
```

The scheme (https) determines which protocol to use. The hostname identifies the server. The port (443 for HTTPS, 80 for HTTP) specifies which port to connect to. The path identifies the specific resource. The query string provides additional parameters. The fragment identifies a specific part of the resource (handled client-side).

**Step 2: DNS Resolution**

The browser needs to convert the hostname (www.example.com) into an IP address. It first checks its cache to see if it recently looked up this domain. If not found, it queries the operating system's DNS resolver, which may have its own cache. If still not found, the resolver queries DNS servers, starting with the root servers and working down through the hierarchy until it finds the authoritative server for example.com. This server returns the IP address, perhaps 93.184.216.34.

**Step 3: TCP Connection Establishment**

The browser initiates a TCP connection to the server's IP address on the appropriate port. This involves the three-way handshake we discussed earlier. The client sends a SYN packet, the server responds with SYN-ACK, and the client completes the handshake with an ACK. This process takes one round-trip time.

**Step 4: TLS Handshake (for HTTPS)**

For HTTPS connections, after the TCP connection is established, a TLS handshake occurs to set up encryption. The client and server negotiate which encryption algorithms to use, exchange certificates to verify identities, and establish session keys. This adds another round-trip or two of latency but provides security.

**Step 5: Sending the HTTP Request**

Only now can the browser send the actual HTTP request. It constructs the request with the appropriate method, path, headers, and body (if applicable), then sends it as bytes over the established TCP connection.

**Step 6: Server Processing**

The server receives the request, parses it, and processes it. This might involve reading from databases, calling other services, running business logic, and constructing a response. The server's processing time can vary dramatically depending on the complexity of the request.

**Step 7: Receiving the Response**

The server sends back the HTTP response over the same TCP connection. If the response is large, it might be split across multiple TCP packets. The client's TCP layer reassembles these packets in order and delivers the complete response to the HTTP layer.

**Step 8: Rendering (for web browsers)**

If the response contains HTML, the browser begins parsing and rendering it. When the parser encounters references to other resources (CSS files, JavaScript, images), it initiates additional HTTP requests for each one. With HTTP/2, these can be multiplexed over the same connection. The browser constructs the DOM, applies styles, executes scripts, and finally displays the page to the user.

**Step 9: Connection Management**

With HTTP/1.1 and later, the connection typically remains open (keep-alive) for potential additional requests. The connection might be closed after a timeout period if no new requests arrive, or the server might close it if it's under high load.

---

### **Java Example: Making HTTP Requests**

Let's see how to work with HTTP in Java. While Java has several APIs for HTTP, we'll look at both the legacy approach and the modern approach.

**Using HttpURLConnection (Legacy but still common)**:

```java
import java.net.*;
import java.io.*;

public class HttpExample {
    public static void main(String[] args) throws Exception {
        // Step 1: Create URL object
        URL url = new URL("https://api.example.com/users/123");
        
        // Step 2: Open connection
        // This doesn't actually connect yet, just prepares the connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Step 3: Configure the request
        connection.setRequestMethod("GET");  // Specify HTTP method
        connection.setRequestProperty("Accept", "application/json");  // Set headers
        connection.setRequestProperty("User-Agent", "JavaApp/1.0");
        connection.setConnectTimeout(5000);  // 5 second connection timeout
        connection.setReadTimeout(5000);     // 5 second read timeout
        
        // Step 4: Connect and send request
        // This is when the actual TCP connection and HTTP request happen
        connection.connect();
        
        // Step 5: Get response code
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        // Step 6: Read response body
        if (responseCode == 200) {  // Success
            // Get input stream from connection
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            
            // Read response line by line
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            System.out.println("Response Body: " + response.toString());
        } else {
            // Handle error - read error stream instead
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println(errorLine);
            }
            errorReader.close();
        }
        
        // Step 7: Close connection
        connection.disconnect();
    }
}
```

This code demonstrates the complete lifecycle of an HTTP request. We create a URL, open a connection, configure it with the method and headers we want, actually connect to the server, read the response, and finally close the connection. Notice how we handle both success and error cases differently by reading from either the input stream or error stream.

**Using HttpClient (Modern Java 11+ approach)**:

```java
import java.net.http.*;
import java.net.*;
import java.time.Duration;

public class ModernHttpExample {
    public static void main(String[] args) throws Exception {
        // Step 1: Create an HttpClient
        // This client can be reused for multiple requests
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)  // Follow redirects
            .build();
        
        // Step 2: Build the request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/users/123"))
            .header("Accept", "application/json")
            .header("User-Agent", "JavaApp/1.0")
            .timeout(Duration.ofSeconds(5))
            .GET()  // Specify HTTP method
            .build();
        
        // Step 3: Send request and get response
        // This method blocks until the response is received
        HttpResponse<String> response = client.send(
            request, 
            HttpResponse.BodyHandlers.ofString()  // Parse body as String
        );
        
        // Step 4: Process response
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Headers: " + response.headers().map());
        System.out.println("Body: " + response.body());
        
        // The connection is automatically managed by the client
    }
}
```

The modern HttpClient API is much cleaner and provides better features like connection pooling, HTTP/2 support, and async requests. The client can be reused across multiple requests, which is more efficient than creating a new connection for each request.

**Asynchronous HTTP Request**:

```java
// Modern API also supports async requests
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/123"))
    .build();

// sendAsync returns a CompletableFuture
client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)  // Extract the body
    .thenAccept(body -> {
        // This callback runs when response arrives
        System.out.println("Response: " + body);
    })
    .exceptionally(throwable -> {
        // Handle errors
        System.err.println("Error: " + throwable.getMessage());
        return null;
    });

// Main thread can continue doing other work
System.out.println("Request sent, doing other work...");
```

The asynchronous approach is powerful because your program doesn't block waiting for the response. This is especially useful in applications that need to make many HTTP requests concurrently, like web crawlers or API clients that aggregate data from multiple sources.

---

### **HTTP and State Management**

Since HTTP is stateless, web applications have developed several techniques to maintain state across requests. Understanding these mechanisms is crucial for building real-world applications.

The fundamental challenge is this: You want the server to remember information about a user across multiple requests. For example, when you log into a website, you expect to stay logged in as you navigate between pages. But HTTP itself provides no mechanism for this memory.

The most common solution is to use a unique identifier (like a session ID) that the client sends with each request. The server uses this identifier to look up the user's state from a data store. The identifier itself can be transmitted through cookies, URL parameters, or custom headers. We'll explore cookies in detail in a later section, but the key concept is that the client becomes responsible for carrying a small piece of information (the identifier) that helps the server retrieve the full state from its own storage.

This approach has important security implications. If an attacker can steal or guess a user's session identifier, they can impersonate that user. This is why session IDs must be long, random, and unpredictable, and why they should only be transmitted over encrypted connections (HTTPS).

---

## 2. HTTP Methods

### The Verbs of HTTP Communication

HTTP methods (also called HTTP verbs) define the action that should be performed on a resource. While HTTP supports many methods, five are commonly used in RESTful APIs. Understanding not just what these methods do, but why they're designed the way they are, is essential for building well-designed web applications.

---

### **The Concept of REST**

Before diving into individual methods, we need to understand the REST (Representational State Transfer) architectural style, which defines how HTTP methods should be used in web APIs. REST treats everything as a resource identified by a URL, and HTTP methods represent different operations on those resources.

Think of resources like objects in object-oriented programming. Each resource has a URL as its identifier, just like objects have memory addresses. The HTTP methods are like methods you can call on those objects. When you design a RESTful API, you're creating a distributed object system where resources live on servers and clients interact with them through HTTP.

---

### **GET - Retrieving Data**

GET is the most fundamental HTTP method. Its purpose is simple: retrieve a resource from the server. When you type a URL in your browser or click a link, you're making a GET request.

**Key Characteristics of GET**:

GET requests should be **safe**, meaning they don't modify anything on the server. Reading a blog post shouldn't change the blog post or the database. This safety property is important because browsers and intermediate caches assume they can repeat GET requests without causing harm. Your browser might automatically retry a failed GET request, or cache the response and serve it directly without even contacting the server again.

GET requests should also be **idempotent**, meaning you can make the same request multiple times and get the same result. Making the same GET request ten times should be no different than making it once. The resource you're retrieving hasn't changed just because you asked for it multiple times.

Because GET requests shouldn't have side effects, all the information needed to identify the resource goes in the URL itself. This makes GET requests easily bookmarkable and shareable. When you bookmark a page, you're bookmarking a GET request encoded as a URL.

**URL Structure for GET Requests**:

```
GET /api/users/123 HTTP/1.1
Host: api.example.com
```

This retrieves user 123. The resource identifier is right there in the path.

For queries or filtering, use query parameters:

```
GET /api/users?role=admin&active=true&page=2 HTTP/1.1
Host: api.example.com
```

This retrieves users with certain filters applied. The query string (the part after ?) provides parameters without changing the resource being accessed.

**Limitations of GET**:

GET requests typically don't have a request body. While the HTTP specification doesn't forbid it, many servers and intermediaries will ignore or reject GET requests with bodies. This means all data must go in the URL.

URLs have practical length limits. While HTTP itself doesn't specify a maximum URL length, browsers and servers typically impose limits around 2000-8000 characters. This means you can't send large amounts of data in a GET request. If you need to send complex data to the server, you should use POST instead.

**Java Example - GET Request**:

```java
// Simple GET request
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/123"))
    .GET()  // This is actually optional - GET is the default
    .build();

HttpResponse<String> response = client.send(
    request, 
    HttpResponse.BodyHandlers.ofString()
);

System.out.println("User data: " + response.body());

// GET request with query parameters
// Build URL with query parameters
String baseUrl = "https://api.example.com/users";
String queryParams = "?role=admin&active=true&page=2";
URI uri = URI.create(baseUrl + queryParams);

HttpRequest requestWithParams = HttpRequest.newBuilder()
    .uri(uri)
    .GET()
    .build();

HttpResponse<String> responseWithParams = client.send(
    requestWithParams, 
    HttpResponse.BodyHandlers.ofString()
);
```

Notice how we construct the URI with query parameters. In production code, you'd want to properly URL-encode the parameter values to handle special characters correctly.

---

### **POST - Creating Resources**

POST is used to submit data to the server, typically to create a new resource. Unlike GET, POST requests have a body where you can send substantial amounts of data.

**Key Characteristics of POST**:

POST is **not idempotent**. If you submit the same POST request twice, you might create two separate resources. Think about submitting an order form twice - you'd get charged twice and receive two separate orders. This is why web applications often implement safeguards like "Are you sure?" confirmations or token-based protection against duplicate submissions.

POST is also **not safe** - it's expected to modify server state. That's the whole point. You're asking the server to do something: create a new user, process a payment, upload a file.

**When to Use POST**:

Use POST when you want to create a new resource and the server decides the identifier. For example, when creating a new user, the server typically assigns the user ID, not the client:

```
POST /api/users HTTP/1.1
Host: api.example.com
Content-Type: application/json
Content-Length: 89

{
    "username": "jane_doe",
    "email": "jane@example.com",
    "password": "securepassword123"
}
```

The server creates the user, assigns an ID (say, 456), and returns:

```
HTTP/1.1 201 Created
Location: /api/users/456
Content-Type: application/json

{
    "id": 456,
    "username": "jane_doe",
    "email": "jane@example.com",
    "created_at": "2025-01-23T10:30:00Z"
}
```

Notice the 201 Created status code (not 200 OK) and the Location header pointing to the new resource.

POST is also used for operations that don't fit neatly into the other methods, like searching with complex criteria that are too large for a URL:

```
POST /api/search HTTP/1.1
Host: api.example.com
Content-Type: application/json

{
    "query": "software engineer",
    "filters": {
        "location": "San Francisco",
        "experience": {"min": 3, "max": 7},
        "skills": ["Java", "Python", "Docker"]
    },
    "sort": "salary_desc",
    "page": 1
}
```

**Java Example - POST Request**:

```java
HttpClient client = HttpClient.newHttpClient();

// Prepare JSON body
String jsonBody = """
    {
        "username": "jane_doe",
        "email": "jane@example.com",
        "password": "securepassword123"
    }
    """;

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))  // Specify POST with body
    .build();

HttpResponse<String> response = client.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);

// Check if resource was created successfully
if (response.statusCode() == 201) {
    System.out.println("User created successfully");
    
    // Get the location of the new resource
    String location = response.headers()
        .firstValue("Location")
        .orElse("Unknown location");
    System.out.println("New user location: " + location);
    
    System.out.println("Response body: " + response.body());
} else {
    System.err.println("Failed to create user: " + response.statusCode());
}
```

In this example, we're creating a JSON string for the request body and sending it with the POST method. We then check for the 201 status code to confirm successful creation and extract the Location header to see where the new resource lives.

---

### **PUT - Updating Resources**

PUT is used to update an existing resource or create a resource at a specific URL if it doesn't exist. Think of PUT as "put this data at this location."

**Key Characteristics of PUT**:

PUT is **idempotent**. Making the same PUT request multiple times should result in the same server state. If you update a user's email address to "new@example.com" ten times, the end result is the same as doing it once - the email address is "new@example.com."

PUT typically requires you to send the complete resource representation. If a user resource has ten fields and you only send five in your PUT request, the other five might be set to null or default values (depending on server implementation). This is why PUT is about replacement rather than modification.

**When to Use PUT**:

Use PUT when you know the exact URL of the resource you're updating:

```
PUT /api/users/456 HTTP/1.1
Host: api.example.com
Content-Type: application/json
Content-Length: 123

{
    "id": 456,
    "username": "jane_doe",
    "email": "newemail@example.com",
    "first_name": "Jane",
    "last_name": "Doe",
    "phone": "+1-555-0123"
}
```

The server replaces the resource at /api/users/456 with this new representation and returns:

```
HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 456,
    "username": "jane_doe",
    "email": "newemail@example.com",
    "first_name": "Jane",
    "last_name": "Doe",
    "phone": "+1-555-0123",
    "updated_at": "2025-01-23T11:00:00Z"
}
```

PUT can also be used to create a resource if you know what its URL should be:

```
PUT /api/users/999 HTTP/1.1
...
```

If user 999 doesn't exist, the server might create it (returning 201 Created). If it does exist, the server updates it (returning 200 OK). This is called "upsert" behavior (update or insert).

**Java Example - PUT Request**:

```java
HttpClient client = HttpClient.newHttpClient();

// Complete user representation
String jsonBody = """
    {
        "id": 456,
        "username": "jane_doe",
        "email": "newemail@example.com",
        "first_name": "Jane",
        "last_name": "Doe",
        "phone": "+1-555-0123"
    }
    """;

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/456"))
    .header("Content-Type", "application/json")
    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))  // PUT method
    .build();

HttpResponse<String> response = client.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);

if (response.statusCode() == 200) {
    System.out.println("User updated successfully");
    System.out.println("Updated user: " + response.body());
} else if (response.statusCode() == 201) {
    System.out.println("User created (didn't exist before)");
} else {
    System.err.println("Failed: " + response.statusCode());
}
```

---

### **DELETE - Removing Resources**

DELETE does exactly what its name suggests: removes a resource from the server.

**Key Characteristics of DELETE**:

DELETE is **idempotent**. Deleting the same resource multiple times has the same effect as deleting it once. After the first DELETE succeeds, subsequent DELETE requests to the same URL might return 404 Not Found (because the resource no longer exists), but the server state is the same - the resource is gone.

DELETE requests typically don't have a request body. The URL identifies what to delete.

**When to Use DELETE**:

```
DELETE /api/users/456 HTTP/1.1
Host: api.example.com
```

The server deletes user 456 and might return:

```
HTTP/1.1 204 No Content
```

Status code 204 means "success, but I have nothing to send back in the body." Alternatively, the server might return 200 OK with a confirmation message:

```
HTTP/1.1 200 OK
Content-Type: application/json

{
    "message": "User 456 deleted successfully",
    "deleted_at": "2025-01-23T11:30:00Z"
}
```

If you try to delete a resource that doesn't exist, the server might return 404 Not Found:

```
HTTP/1.1 404 Not Found
Content-Type: application/json

{
    "error": "User 456 not found"
}
```

**Soft Deletes vs Hard Deletes**:

In practice, many systems implement "soft deletes" where the resource isn't actually removed from the database but marked as deleted. This allows for recovery and maintains referential integrity. From the API perspective, it still looks like a DELETE - the resource becomes inaccessible - but internally it's just an update operation.

**Java Example - DELETE Request**:

```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/456"))
    .DELETE()  // DELETE method
    .build();

HttpResponse<String> response = client.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);

if (response.statusCode() == 204) {
    System.out.println("User deleted successfully (no content returned)");
} else if (response.statusCode() == 200) {
    System.out.println("User deleted successfully");
    System.out.println("Server response: " + response.body());
} else if (response.statusCode() == 404) {
    System.out.println("User not found (may have been already deleted)");
} else {
    System.err.println("Deletion failed: " + response.statusCode());
}
```

---

### **PATCH - Partial Updates**

While not in your original list, PATCH is worth mentioning as it's commonly used alongside PUT. PATCH is for partial updates - modifying specific fields of a resource without sending the entire resource representation.

**PUT vs PATCH**:

With PUT, you send the complete resource:
```
PUT /api/users/456
{
    "id": 456,
    "username": "jane_doe",
    "email": "newemail@example.com",  // Changed
    "first_name": "Jane",
    "last_name": "Doe",
    "phone": "+1-555-0123"
}
```

With PATCH, you send only what's changing:
```
PATCH /api/users/456
{
    "email": "newemail@example.com"  // Only the field being updated
}
```

PATCH is more efficient for large resources where you only want to change one or two fields. It's also safer because you don't risk accidentally nullifying fields you forgot to include.

**Java Example - PATCH Request**:

```java
HttpClient client = HttpClient.newHttpClient();

// Only the field we're updating
String jsonBody = """
    {
        "email": "newemail@example.com"
    }
    """;

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/456"))
    .header("Content-Type", "application/json")
    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
    .build();

HttpResponse<String> response = client.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);

if (response.statusCode() == 200) {
    System.out.println("User partially updated");
    System.out.println("Updated user: " + response.body());
}
```

---

### **Method Safety and Idempotency Summary**

Understanding these properties helps you design better APIs and write more robust client code:

| Method | Safe (No Side Effects) | Idempotent (Same Result if Repeated) | Has Body | Purpose |
|--------|------------------------|---------------------------------------|----------|---------|
| GET | Yes | Yes | No (typically) | Retrieve resource |
| POST | No | No | Yes | Create resource or process data |
| PUT | No | Yes | Yes | Update/replace entire resource |
| DELETE | No | Yes | No (typically) | Remove resource |
| PATCH | No | Yes | Yes | Partial update |

These properties matter because:
- Safe methods can be cached and prefetched without concern
- Idempotent methods can be safely retried if the network fails
- Methods with bodies need Content-Type headers
- The server implements different logic based on the method

---

## 3. HTTP Status Codes

### The Language of Success and Failure

HTTP status codes are three-digit numbers that tell the client what happened with their request. Understanding status codes thoroughly is essential not just for debugging but for building robust applications that handle errors gracefully.

---

### **Status Code Categories**

Status codes are organized into five categories based on their first digit. This organization makes it easy to understand the general meaning even if you haven't seen a specific code before.

---

### **1xx - Informational Responses**

These are provisional responses that tell the client the request was received and processing is continuing. They're rarely seen in typical web applications.

**100 Continue**

This is used in an optimization scenario. When a client wants to send a large request body, it can first send just the request headers with an `Expect: 100-continue` header. The server responds with 100 Continue if it's willing to accept the body, or with an error status if it's going to reject the request anyway (wrong authentication, request too large, etc.). This prevents wasting bandwidth sending a large body that would be rejected.

```
Client sends:
POST /upload HTTP/1.1
Content-Length: 1000000000
Expect: 100-continue

Server responds:
HTTP/1.1 100 Continue

Client then sends:
[1GB of data...]
```

**101 Switching Protocols**

Used when the client requests a protocol upgrade, like switching from HTTP to WebSocket:

```
GET /chat HTTP/1.1
Upgrade: websocket
Connection: Upgrade

Server responds:
HTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade

[Connection is now using WebSocket protocol]
```

---

### **2xx - Success**

These status codes indicate the request was successfully received, understood, and accepted.

**200 OK**

The most common success status. The request succeeded and the response body contains the requested resource. You'll see 200 OK for successful GET, PUT, or PATCH requests.

```
GET /api/users/123

HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 123,
    "username": "john_doe"
}
```

**201 Created**

Used when a POST request successfully creates a new resource. The response should include a Location header pointing to the newly created resource.

```
POST /api/users

HTTP/1.1 201 Created
Location: /api/users/456
Content-Type: application/json

{
    "id": 456,
    "username": "new_user",
    "created_at": "2025-01-23T10:00:00Z"
}
```

The distinction between 200 and 201 is important. It tells the client that not only did the request succeed, but a new resource was created as a result.

**202 Accepted**

The request has been accepted for processing, but processing hasn't completed yet. This is used for asynchronous operations. The server is saying "I got your request and I'll work on it, but I can't give you the result right now."

```
POST /api/batch-process

HTTP/1.1 202 Accepted
Content-Type: application/json

{
    "job_id": "abc-123",
    "status": "processing",
    "status_url": "/api/jobs/abc-123"
}
```

The client can later poll the status_url to check if the job completed.

**204 No Content**

The request succeeded, but there's no response body to send. This is commonly used for successful DELETE operations or PUT/PATCH updates where the client already knows what the resource looks like after the update.

```
DELETE /api/users/456

HTTP/1.1 204 No Content
```

The lack of a response body saves bandwidth when the client doesn't need any data back.

---

### **3xx - Redirection**

These status codes indicate the client needs to take additional action to complete the request, typically making a request to a different URL.

**301 Moved Permanently**

The resource has permanently moved to a new location. Clients and search engines should update their bookmarks/links to use the new URL.

```
GET /old-page

HTTP/1.1 301 Moved Permanently
Location: /new-page
```

Browsers automatically follow the redirect and request /new-page. Search engines transfer the SEO value from the old URL to the new one.

**302 Found (Temporary Redirect)**

The resource temporarily resides at a different location, but clients should continue using the original URL for future requests.

```
GET /summer-sale

HTTP/1.1 302 Found
Location: /products/category/seasonal
```

This might be used during a promotional period. After the promotion ends, /summer-sale might redirect elsewhere or return to its original content.

**304 Not Modified**

Used with conditional requests to save bandwidth. The client includes headers like `If-Modified-Since` or `If-None-Match`. If the resource hasn't changed, the server returns 304 with no body, and the client uses its cached version.

```
Client sends:
GET /style.css
If-Modified-Since: Mon, 20 Jan 2025 10:00:00 GMT

Server responds:
HTTP/1.1 304 Not Modified
```

The client already has a valid cached copy, so there's no need to send the file again. This dramatically reduces bandwidth usage for static assets.

**307 Temporary Redirect & 308 Permanent Redirect**

These are newer alternatives to 302 and 301 that guarantee the HTTP method won't change during the redirect. With 302/301, some browsers historically converted POST requests to GET when following the redirect. With 307/308, a POST stays a POST.

```
POST /api/v1/users

HTTP/1.1 308 Permanent Redirect
Location: /api/v2/users

[Browser makes POST request to /api/v2/users with same body]
```

---

### **4xx - Client Errors**

These status codes indicate the client made an error. The request was syntactically incorrect, violated authorization rules, or asked for something that doesn't exist.

**400 Bad Request**

The server cannot or will not process the request due to a client error. This is a generic catch-all for malformed requests.

```
POST /api/users
Content-Type: application/json

{
    "username": "",  // Empty username
    "email": "invalid-email"  // Invalid email format
}

HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "error": "Validation failed",
    "details": [
        "username cannot be empty",
        "email must be a valid email address"
    ]
}
```

The response body should ideally explain what was wrong with the request so the client can fix it.

**401 Unauthorized**

Despite its name, this actually means "unauthenticated" - the client needs to provide valid authentication credentials. The response should include a `WWW-Authenticate` header indicating what authentication method is required.

```
GET /api/profile

HTTP/1.1 401 Unauthorized
WWW-Authenticate: Bearer realm="example"
Content-Type: application/json

{
    "error": "Authentication required",
    "message": "Please provide a valid access token"
}
```

After receiving 401, the client should authenticate (log in) and retry the request with proper credentials.

**403 Forbidden**

The client is authenticated, but doesn't have permission to access this resource. Unlike 401, providing credentials won't help - the user simply isn't allowed.

```
DELETE /api/users/999

HTTP/1.1 403 Forbidden
Content-Type: application/json

{
    "error": "Insufficient permissions",
    "message": "Only administrators can delete users"
}
```

The distinction between 401 and 403 is important. 401 means "I don't know who you are, please log in." 403 means "I know who you are, but you can't do this."

**404 Not Found**

The requested resource doesn't exist. This is one of the most common error codes.

```
GET /api/users/999999

HTTP/1.1 404 Not Found
Content-Type: application/json

{
    "error": "User not found",
    "message": "No user exists with ID 999999"
}
```

Important note: 404 can mean the resource truly doesn't exist, or that it exists but the client isn't authorized to know about it (to prevent information leakage about what resources exist).

**405 Method Not Allowed**

The HTTP method used is not supported for this resource. The response should include an `Allow` header listing the supported methods.

```
POST /api/users/123

HTTP/1.1 405 Method Not Allowed
Allow: GET, PUT, DELETE
Content-Type: application/json

{
    "error": "Method not allowed",
    "message": "POST is not supported on user resources. Use PUT to update."
}
```

This tells the client exactly which methods are valid for this resource.

**408 Request Timeout**

The server timed out waiting for the request. Perhaps the client took too long to send the request body, or the connection was very slow.

**409 Conflict**

The request conflicts with the current state of the resource. This is commonly used when an update would violate business rules.

```
PUT /api/users/123
{
    "username": "existing_user"
}

HTTP/1.1 409 Conflict
Content-Type: application/json

{
    "error": "Username conflict",
    "message": "Username 'existing_user' is already taken"
}
```

Or for version conflicts:

```
PUT /api/documents/456
If-Match: "v1"

HTTP/1.1 409 Conflict

{
    "error": "Version conflict",
    "message": "Document has been modified by another user. Current version is v3."
}
```

**410 Gone**

The resource previously existed but has been permanently removed. Unlike 404, this explicitly states the resource is gone and won't come back.

**413 Payload Too Large**

The request body is larger than the server is willing to process. Maybe you're trying to upload a 10GB file to an endpoint with a 100MB limit.

```
POST /api/upload

HTTP/1.1 413 Payload Too Large
Content-Type: application/json

{
    "error": "File too large",
    "message": "Maximum file size is 100MB. Your file is 500MB."
}
```

**415 Unsupported Media Type**

The server doesn't support the Content-Type of the request body.

```
POST /api/users
Content-Type: application/xml

<user>...</user>

HTTP/1.1 415 Unsupported Media Type
Content-Type: application/json

{
    "error": "Unsupported format",
    "message": "This endpoint only accepts application/json"
}
```

**429 Too Many Requests**

Rate limiting - the client has sent too many requests in a given time period. The response should include a `Retry-After` header indicating when the client can try again.

```
GET /api/users

HTTP/1.1 429 Too Many Requests
Retry-After: 3600
Content-Type: application/json

{
    "error": "Rate limit exceeded",
    "message": "Maximum 1000 requests per hour. Try again in 3600 seconds."
}
```

---

### **5xx - Server Errors**

These status codes indicate the server failed to fulfill a valid request. The client did nothing wrong; something went wrong on the server side.

**500 Internal Server Error**

A generic error message indicating the server encountered an unexpected condition. This is the catch-all for server-side errors that don't fit into more specific categories.

```
GET /api/users/123

HTTP/1.1 500 Internal Server Error
Content-Type: application/json

{
    "error": "Internal server error",
    "message": "An unexpected error occurred. Please try again later.",
    "request_id": "abc-123-def-456"
}
```

Including a request_id helps with debugging. The client can report this ID, and engineers can look up the detailed error in server logs.

In production, you should avoid exposing detailed error information (like stack traces) in 500 responses, as this could reveal implementation details to attackers.

**502 Bad Gateway**

The server, while acting as a gateway or proxy, received an invalid response from an upstream server. This commonly happens with load balancers or reverse proxies.

```
[Client] → [Proxy Server] → [Backend Server]
                 ↑
         Backend server crashed or returned invalid response
```

The proxy returns:
```
HTTP/1.1 502 Bad Gateway
```

**503 Service Unavailable**

The server is temporarily unable to handle the request. This might be due to overload, maintenance, or temporary failure of a dependency. The response should include a `Retry-After` header if possible.

```
GET /api/users

HTTP/1.1 503 Service Unavailable
Retry-After: 300
Content-Type: application/json

{
    "error": "Service unavailable",
    "message": "The service is temporarily unavailable due to maintenance. Please try again in 5 minutes."
}
```

Unlike 500 which indicates an error, 503 indicates temporary unavailability. The service will recover.

**504 Gateway Timeout**

The server, while acting as a gateway or proxy, didn't receive a response from an upstream server in time.

```
[Client] → [Proxy Server] → [Backend Server]
                              (takes too long to respond)
```

After the timeout period, the proxy returns:
```
HTTP/1.1 504 Gateway Timeout
```

---

### **Handling Status Codes in Java**

When writing HTTP clients, you need to handle various status codes appropriately:

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/123"))
    .header("Authorization", "Bearer " + accessToken)
    .build();

HttpResponse<String> response = client.send(
    request,
    HttpResponse.BodyHandlers.ofString()
);

int statusCode = response.statusCode();

// Check status code category
if (statusCode >= 200 && statusCode < 300) {
    // Success - process the response
    System.out.println("Success: " + response.body());
    
} else if (statusCode == 401) {
    // Unauthorized - need to re-authenticate
    System.err.println("Authentication required. Please log in.");
    // Trigger login flow
    
} else if (statusCode == 403) {
    // Forbidden - user doesn't have permission
    System.err.println("Access denied. You don't have permission for this resource.");
    
} else if (statusCode == 404) {
    // Not found
    System.err.println("Resource not found.");
    
} else if (statusCode == 429) {
    // Rate limited - check Retry-After header
    String retryAfter = response.headers()
        .firstValue("Retry-After")
        .orElse("unknown");
    System.err.println("Rate limited. Retry after " + retryAfter + " seconds.");
    
} else if (statusCode >= 400 && statusCode < 500) {
    // Client error - probably something wrong with our request
    System.err.println("Client error: " + statusCode);
    System.err.println("Response: " + response.body());
    
} else if (statusCode >= 500 && statusCode < 600) {
    // Server error - might be temporary, could retry
    System.err.println("Server error: " + statusCode);
    System.err.println("The server encountered an error. Retrying might help.");
}
```

A more robust approach with retry logic:

```java
public HttpResponse<String> makeRequestWithRetry(String url, int maxRetries) 
        throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    
    for (int attempt = 1; attempt <= maxRetries; attempt++) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        
        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        int statusCode = response.statusCode();
        
        // Success
        if (statusCode >= 200 && statusCode < 300) {
            return response;
        }
        
        // Client errors (4xx) - don't retry, these won't get better
        if (statusCode >= 400 && statusCode < 500) {
            // Exception: 429 (rate limit) might be worth retrying
            if (statusCode == 429) {
                String retryAfter = response.headers()
                    .firstValue("Retry-After")
                    .orElse("60");
                int waitSeconds = Integer.parseInt(retryAfter);
                System.out.println("Rate limited. Waiting " + waitSeconds + " seconds...");
                Thread.sleep(waitSeconds * 1000);
                continue;
            }
            // Other 4xx errors - throw exception
            throw new IOException("Client error: " + statusCode);
        }
        
        // Server errors (5xx) - these might be temporary, retry with backoff
        if (statusCode >= 500 && statusCode < 600) {
            if (attempt < maxRetries) {
                // Exponential backoff: wait 2^attempt seconds
                int waitSeconds = (int) Math.pow(2, attempt);
                System.out.println("Server error " + statusCode + 
                    ". Retrying in " + waitSeconds + " seconds... (Attempt " + 
                    attempt + "/" + maxRetries + ")");
                Thread.sleep(waitSeconds * 1000);
                continue;
            } else {
                throw new IOException("Server error after " + maxRetries + " attempts: " + statusCode);
            }
        }
    }
    
    throw new IOException("Request failed after " + maxRetries + " attempts");
}
```

This retry logic implements important patterns:
- Don't retry client errors (4xx) except rate limiting (429)
- Do retry server errors (5xx) with exponential backoff
- Respect Retry-After headers
- Give up after a maximum number of retries

---

This completes the first part of Part 3. Due to the extensive content remaining, I should continue with the rest of the topics. Would you like me to continue with Cookies, Email, DNS, and the deeper protocol dives?

