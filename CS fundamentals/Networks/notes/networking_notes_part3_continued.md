# Computer Networks - Comprehensive Study Notes (Part 3 Continued)

## 4. Cookies

### Solving HTTP's Statelessness Problem

As we learned earlier, HTTP is stateless. Each request is independent, with no memory of previous requests. But modern web applications need to maintain state across multiple requests - they need to remember who you are when you navigate between pages, what's in your shopping cart, your preferences, and so on. Cookies are the primary mechanism that makes this possible.

---

### **What Cookies Really Are**

A cookie is simply a small piece of data (up to 4KB) that a web server sends to your browser, which the browser then stores and sends back to the server with subsequent requests. Think of cookies like claim tickets at a coat check. When you arrive at a restaurant and hand over your coat, they give you a numbered ticket. You don't carry your coat around all evening - you just carry the small ticket. When you want to leave, you present the ticket and get your coat back. Similarly, instead of the server maintaining your entire session state and somehow identifying you, it gives your browser a unique identifier (the cookie), and your browser presents this identifier with each request.

The magic of cookies is that they create an illusion of state in a stateless protocol. The server doesn't actually remember anything between requests. Instead, it gives the client something to remember and present back. The server can then use that identifier to look up the client's state from its own storage (like a database or in-memory cache).

---

### **How Cookies Work - The Mechanism**

Let's walk through exactly what happens when a server sets a cookie.

**Step 1: Server Sends Cookie**

When you first visit a website, the server includes a `Set-Cookie` header in its response:

```
HTTP/1.1 200 OK
Content-Type: text/html
Set-Cookie: session_id=abc123xyz; Path=/; HttpOnly; Secure; SameSite=Strict

<!DOCTYPE html>
<html>...
```

This tells the browser "Please store this cookie with the name `session_id` and value `abc123xyz`, and send it back to me with future requests."

**Step 2: Browser Stores Cookie**

Your browser saves this cookie in its cookie storage, associated with the domain that sent it (www.example.com). The browser also notes the additional attributes like Path, HttpOnly, Secure, and SameSite, which we'll explain shortly.

**Step 3: Browser Sends Cookie Back**

On your next request to the same domain, your browser automatically includes the cookie:

```
GET /profile HTTP/1.1
Host: www.example.com
Cookie: session_id=abc123xyz
```

The browser does this automatically - you don't have to do anything. Every request to www.example.com now includes this cookie.

**Step 4: Server Uses Cookie**

The server receives the cookie, extracts the session_id value (abc123xyz), and uses it to look up your session data:

```java
// Pseudocode of what happens on the server
String sessionId = request.getCookie("session_id");
Session session = sessionStore.get(sessionId);
User user = session.getUser();

// Now the server knows who you are!
```

The server might store session data in Redis, Memcached, a database, or even in-memory. The cookie is just the key that unlocks this data.

---

### **Cookie Attributes - The Fine Print**

Cookies have several attributes that control their behavior. Understanding these is crucial for both security and functionality.

#### **Domain and Path**

These attributes control which requests receive the cookie.

**Domain**: Specifies which domains can access the cookie. If not specified, defaults to the domain that set the cookie (excluding subdomains).

```
Set-Cookie: session=abc123; Domain=example.com
```

This cookie will be sent to example.com, www.example.com, api.example.com, and any other subdomain of example.com.

```
Set-Cookie: session=abc123; Domain=api.example.com
```

This cookie will only be sent to api.example.com and its subdomains, not to www.example.com or the main example.com.

**Path**: Specifies which URL paths should receive the cookie.

```
Set-Cookie: session=abc123; Path=/admin
```

This cookie will only be sent for requests to /admin, /admin/users, /admin/settings, etc. It won't be sent for requests to /home or /api.

The default path is the path of the URL that set the cookie. Usually, you want `Path=/` so the cookie is sent for all requests to the domain.

#### **Expires and Max-Age**

These control the cookie's lifetime.

**Expires**: Sets an absolute expiration date.

```
Set-Cookie: session=abc123; Expires=Wed, 31 Jan 2025 23:59:59 GMT
```

After this date and time, the browser automatically deletes the cookie.

**Max-Age**: Sets how many seconds until the cookie expires.

```
Set-Cookie: session=abc123; Max-Age=3600
```

This cookie expires in one hour (3600 seconds).

If neither Expires nor Max-Age is set, the cookie becomes a **session cookie** - it's deleted when you close your browser. Session cookies are useful for temporary data that shouldn't persist after the browsing session ends.

**Persistent cookies** (those with Expires or Max-Age) remain on disk even after closing the browser. They're used for "remember me" functionality, storing user preferences, tracking, etc.

#### **HttpOnly - Protection Against XSS**

This is a critical security attribute. When set, the cookie cannot be accessed by JavaScript.

```
Set-Cookie: session=abc123; HttpOnly
```

Without HttpOnly, JavaScript code can read cookies:

```javascript
// This works if HttpOnly is NOT set
let sessionId = document.cookie; // Can access cookies
```

Why does this matter? Cross-Site Scripting (XSS) attacks inject malicious JavaScript into a website. If this JavaScript can read your session cookie, the attacker can steal your session and impersonate you. HttpOnly prevents this attack vector.

Best practice: **Always use HttpOnly for authentication cookies.** There's rarely a legitimate reason for JavaScript to access authentication tokens.

#### **Secure - HTTPS Only**

The Secure attribute means the cookie will only be sent over HTTPS connections, never over unencrypted HTTP.

```
Set-Cookie: session=abc123; Secure
```

Without Secure, if you visit the HTTP version of a site (http://example.com), your cookies (including authentication tokens) would be sent in plain text over the network. Anyone monitoring the network (like on public Wi-Fi) could intercept these cookies.

Best practice: **Always use Secure for sensitive cookies.** In fact, modern best practice is to use HTTPS for everything and set Secure on all cookies.

#### **SameSite - CSRF Protection**

SameSite controls whether cookies are sent with cross-site requests. This protects against Cross-Site Request Forgery (CSRF) attacks.

**SameSite=Strict**: Cookie is never sent with requests from other sites.

```
Set-Cookie: session=abc123; SameSite=Strict
```

If you're on evil-site.com and it tries to make a request to bank.com, your bank.com cookies won't be sent. This is very secure but can break some legitimate use cases.

**SameSite=Lax**: Cookie is sent with top-level navigation (clicking a link) but not with AJAX requests or images from other sites.

```
Set-Cookie: session=abc123; SameSite=Lax
```

If you click a link from google.com to example.com, your example.com cookies are sent. But if evil-site.com tries to make an AJAX POST request to example.com, the cookies aren't sent.

This is a good balance for most applications.

**SameSite=None**: Cookie is sent with all requests, even cross-site. Must be combined with Secure.

```
Set-Cookie: session=abc123; SameSite=None; Secure
```

This is needed for legitimate cross-site scenarios like embedded widgets, but it opens you to CSRF attacks, so you need other CSRF protections.

---

### **Cookie Security Best Practices**

A secure cookie configuration for authentication looks like this:

```
Set-Cookie: session_id=abc123xyz; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=7200
```

Let's break down what this gives us:
- **HttpOnly**: JavaScript can't steal the cookie via XSS
- **Secure**: Cookie only sent over HTTPS
- **SameSite=Lax**: Protection against CSRF attacks
- **Max-Age=7200**: Session expires after 2 hours of inactivity
- **Path=/**: Cookie sent for all paths on the domain

Additional security considerations:
1. Generate session IDs with cryptographically secure random number generators
2. Make session IDs long and unpredictable (at least 128 bits of entropy)
3. Regenerate session IDs after authentication (prevent session fixation)
4. Implement session timeout on the server side too (don't trust cookie expiration alone)
5. Clear cookies on logout

---

### **Working with Cookies in Java**

**Reading Cookies (Server-side)**:

```java
// In a servlet or Spring controller
@GetMapping("/profile")
public String getProfile(HttpServletRequest request) {
    // Get all cookies
    Cookie[] cookies = request.getCookies();
    
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("session_id")) {
                String sessionId = cookie.getValue();
                
                // Use session ID to load user data
                Session session = sessionService.getSession(sessionId);
                if (session != null && !session.isExpired()) {
                    User user = session.getUser();
                    return "Welcome, " + user.getName();
                }
            }
        }
    }
    
    return "Please log in";
}
```

**Setting Cookies (Server-side)**:

```java
@PostMapping("/login")
public ResponseEntity<String> login(
        @RequestParam String username, 
        @RequestParam String password,
        HttpServletResponse response) {
    
    // Verify credentials
    User user = userService.authenticate(username, password);
    if (user == null) {
        return ResponseEntity.status(401).body("Invalid credentials");
    }
    
    // Create session
    Session session = sessionService.createSession(user);
    
    // Create cookie
    Cookie sessionCookie = new Cookie("session_id", session.getId());
    sessionCookie.setPath("/");
    sessionCookie.setHttpOnly(true);
    sessionCookie.setSecure(true);  // Only if using HTTPS
    sessionCookie.setMaxAge(7200);  // 2 hours
    
    // In newer servlet APIs, you can set SameSite
    // sessionCookie.setAttribute("SameSite", "Lax");
    
    // Add cookie to response
    response.addCookie(sessionCookie);
    
    return ResponseEntity.ok("Login successful");
}
```

**Deleting Cookies**:

```java
@PostMapping("/logout")
public ResponseEntity<String> logout(
        HttpServletRequest request,
        HttpServletResponse response) {
    
    // Find session cookie
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("session_id")) {
                // Delete session on server
                sessionService.deleteSession(cookie.getValue());
                
                // Delete cookie by setting MaxAge to 0
                Cookie deleteCookie = new Cookie("session_id", "");
                deleteCookie.setPath("/");
                deleteCookie.setMaxAge(0);  // Immediate expiration
                response.addCookie(deleteCookie);
                break;
            }
        }
    }
    
    return ResponseEntity.ok("Logged out");
}
```

---

### **Cookie Alternatives and Modern Approaches**

While cookies are the traditional approach, modern applications often use alternatives:

**JWT (JSON Web Tokens)**: Instead of storing a session ID in a cookie and looking up session data on the server, the server creates a signed JWT containing the user's information. This JWT is sent to the client (can still be in a cookie) and validated on each request.

```
Set-Cookie: jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; HttpOnly; Secure
```

Advantages of JWT:
- Stateless - server doesn't need to store sessions
- Can be easily validated by any server (good for distributed systems)
- Can contain user information (reducing database queries)

Disadvantages:
- Can't be easily invalidated before expiration
- If compromised before expiration, the attacker can use it
- Larger than session IDs (cookies have size limits)

**localStorage/sessionStorage**: Web applications can store data in the browser's localStorage or sessionStorage instead of cookies. However, this has security implications because JavaScript can always access this storage (no equivalent of HttpOnly). It's fine for non-sensitive data but never for authentication tokens.

**Modern practice**: Use HttpOnly cookies for authentication tokens (whether they contain session IDs or JWTs), and use localStorage for non-sensitive client-side state.

---

## 5. How Email Works

### The Journey of an Email Message

Email is one of the oldest Internet applications, predating the web by decades. Understanding how email works gives you insight into distributed systems, store-and-forward messaging, and the complexity of making a simple-seeming service work reliably at global scale.

---

### **Email Architecture Overview**

Email involves three main components:

1. **Mail User Agent (MUA)**: The email client you use (Gmail, Outlook, Apple Mail, Thunderbird)
2. **Mail Transfer Agent (MTA)**: Servers that transfer emails between domains
3. **Mail Delivery Agent (MDA)**: Servers that store emails and make them available to users

The process also uses multiple protocols:
- **SMTP (Simple Mail Transfer Protocol)**: For sending email
- **POP3 (Post Office Protocol v3)** or **IMAP (Internet Message Access Protocol)**: For receiving email

---

### **The Complete Email Journey**

Let's trace what happens when alice@example.com sends an email to bob@university.edu.

**Step 1: Composing and Sending (Alice's End)**

Alice writes her email in her email client (MUA). When she clicks "Send," her email client connects to her outgoing mail server using SMTP, typically on port 587 (submission port) or 465 (SMTPS - SMTP over TLS).

```
Alice's Email Client
    ↓ SMTP (port 587)
Example.com Mail Server
```

The SMTP conversation looks like this:

```
Client: EHLO alice-laptop.example.com
Server: 250-mail.example.com Hello alice-laptop.example.com
Server: 250-SIZE 52428800
Server: 250-AUTH PLAIN LOGIN
Server: 250 STARTTLS

Client: STARTTLS
Server: 220 Ready to start TLS

[TLS handshake - connection now encrypted]

Client: AUTH PLAIN [base64 encoded credentials]
Server: 235 Authentication successful

Client: MAIL FROM:<alice@example.com>
Server: 250 OK

Client: RCPT TO:<bob@university.edu>
Server: 250 OK

Client: DATA
Server: 354 Start mail input; end with <CRLF>.<CRLF>

Client: From: Alice <alice@example.com>
Client: To: Bob <bob@university.edu>
Client: Subject: Meeting Tomorrow
Client: Date: Thu, 23 Jan 2025 10:00:00 -0800
Client: 
Client: Hi Bob,
Client: 
Client: Don't forget about our meeting tomorrow at 2 PM.
Client: 
Client: Best regards,
Client: Alice
Client: .

Server: 250 OK: Message accepted for delivery

Client: QUIT
Server: 221 Closing connection
```

Notice several important things:
1. The connection is encrypted with TLS (STARTTLS command)
2. The client authenticates (AUTH command) - this proves Alice is who she says she is
3. The sender and recipient are specified separately from the message content
4. The message ends with a line containing only a period

**Step 2: DNS Lookup for Recipient's Mail Server**

Example.com's mail server now needs to find where to deliver the email for bob@university.edu. It doesn't send the email directly to Bob's computer - it sends it to university.edu's mail server.

To find university.edu's mail server, it performs a DNS MX (Mail Exchanger) lookup:

```
Query: What's the MX record for university.edu?

DNS Response:
university.edu.    MX    10 mail1.university.edu
university.edu.    MX    20 mail2.university.edu
```

MX records have priorities (10, 20). Lower numbers have higher priority. The sending server tries mail1.university.edu first. If that fails, it tries mail2.university.edu.

**Step 3: Server-to-Server Transfer**

Example.com's mail server connects to mail1.university.edu using SMTP, typically on port 25 (standard SMTP port for server-to-server communication).

```
Example.com Mail Server
    ↓ SMTP (port 25)
University.edu Mail Server (mail1.university.edu)
```

The SMTP conversation is similar, but without authentication (servers authenticate through other means like SPF, DKIM, DMARC):

```
Sending Server: EHLO mail.example.com
Receiving Server: 250-mail1.university.edu Hello mail.example.com
Receiving Server: 250 SIZE 52428800

Sending Server: MAIL FROM:<alice@example.com>
Receiving Server: 250 OK

Sending Server: RCPT TO:<bob@university.edu>
Receiving Server: 250 OK

Sending Server: DATA
Receiving Server: 354 Start mail input

[Email content transferred]

Receiving Server: 250 OK: Message accepted
```

If the receiving server is temporarily unavailable, the sending server will retry periodically (typically for several days) before giving up and sending a bounce message back to Alice.

**Step 4: Storage and Delivery**

University.edu's mail server accepts the email and stores it in Bob's mailbox. The email now sits on the server, waiting for Bob to retrieve it.

```
University.edu Mail Server
    ↓
Bob's Mailbox Storage (on server)
```

**Step 5: Bob Retrieves the Email**

Bob can retrieve his email using either POP3 or IMAP. Let's look at both.

**POP3 (Post Office Protocol)**:

POP3 is the older, simpler protocol. It downloads emails from the server to the client and typically deletes them from the server. Think of POP3 like physical mail - you take it out of your mailbox and bring it home.

```
Bob's Email Client
    ↓ POP3 (port 110, or 995 for POP3S)
University.edu Mail Server
```

```
Client: USER bob
Server: +OK User accepted

Client: PASS bobspassword
Server: +OK Password accepted

Client: STAT
Server: +OK 5 15000
(5 messages, 15000 bytes total)

Client: LIST
Server: +OK
Server: 1 2500
Server: 2 3000
Server: 3 4000
Server: 4 2000
Server: 5 3500
Server: .

Client: RETR 1
Server: +OK 2500 bytes
Server: [Email content]
Server: .

Client: DELE 1
Server: +OK Message deleted

Client: QUIT
Server: +OK Goodbye
```

POP3 operations:
- USER/PASS: Authentication
- STAT: Get number and size of messages
- LIST: List all messages
- RETR: Retrieve a message
- DELE: Delete a message
- QUIT: Close connection

After retrieving emails with POP3, they're typically deleted from the server. This means if you check email on your laptop, it's no longer on the server for your phone to retrieve.

**IMAP (Internet Message Access Protocol)**:

IMAP is more sophisticated. Instead of downloading emails and deleting them, IMAP synchronizes the client with the server. Emails stay on the server, and multiple devices can access the same mailbox. Think of IMAP like accessing files on a cloud storage service - you can view them from any device.

```
Bob's Email Client
    ↓ IMAP (port 143, or 993 for IMAPS)
University.edu Mail Server
```

```
Client: a001 LOGIN bob bobspassword
Server: a001 OK LOGIN completed

Client: a002 SELECT INBOX
Server: * 5 EXISTS
Server: * 0 RECENT
Server: * OK [UIDVALIDITY 1234567890]
Server: a002 OK SELECT completed

Client: a003 FETCH 1:5 (FLAGS)
Server: * 1 FETCH (FLAGS (\Seen))
Server: * 2 FETCH (FLAGS (\Seen))
Server: * 3 FETCH (FLAGS ())
Server: * 4 FETCH (FLAGS (\Seen))
Server: * 5 FETCH (FLAGS ())
Server: a003 OK FETCH completed

Client: a004 FETCH 3 (BODY[])
Server: * 3 FETCH (BODY[] {2500}
Server: [Email content]
Server: )
Server: a004 OK FETCH completed

Client: a005 STORE 3 +FLAGS (\Seen)
Server: * 3 FETCH (FLAGS (\Seen))
Server: a005 OK STORE completed

Client: a006 LOGOUT
Server: * BYE Logging out
Server: a006 OK LOGOUT completed
```

IMAP operations:
- LOGIN: Authentication
- SELECT: Choose a mailbox (INBOX, Sent, etc.)
- FETCH: Retrieve messages or parts of messages
- STORE: Change message flags (read/unread, flagged, deleted)
- SEARCH: Find messages matching criteria
- CREATE/DELETE/RENAME: Manage folders

With IMAP, you can:
- Access the same mailbox from multiple devices
- Read only message headers without downloading full messages
- Search messages on the server
- Organize messages in folders that sync across devices
- Mark messages as read/unread, and this syncs

Modern email clients primarily use IMAP because of these advantages.

---

### **Email Headers and Routing**

Email messages contain headers that track their journey. When you view "Show Original" or "View Source" on an email, you see these headers:

```
Received: from mail.example.com ([203.0.113.5])
    by mail1.university.edu
    Thu, 23 Jan 2025 10:05:32 -0800
Received: from alice-laptop ([192.168.1.100])
    by mail.example.com
    Thu, 23 Jan 2025 10:00:15 -0800
From: Alice <alice@example.com>
To: Bob <bob@university.edu>
Subject: Meeting Tomorrow
Date: Thu, 23 Jan 2025 10:00:00 -0800
Message-ID: <abc123@example.com>
Content-Type: text/plain; charset="UTF-8"

[Message body]
```

Each "Received" header is added by a mail server as the email passes through. Reading from bottom to top shows the email's path. This is useful for debugging delivery issues and tracking spam.

---

### **Email Security - SPF, DKIM, DMARC**

Email was designed in a simpler time without strong security. Anyone can claim to send email from any address. To combat spoofing and spam, several security mechanisms have been added:

**SPF (Sender Policy Framework)**:

SPF is a DNS record that specifies which mail servers are allowed to send email for a domain.

```
example.com.    TXT    "v=spf1 ip4:203.0.113.0/24 include:_spf.google.com -all"
```

This says: "Email from example.com should only come from IP addresses in 203.0.113.0/24 or from Google's servers. Reject all others (-all)."

When a receiving server gets email claiming to be from example.com, it checks the SPF record and verifies the sending server's IP is authorized.

**DKIM (DomainKeys Identified Mail)**:

DKIM adds a digital signature to emails. The sending server signs the email using a private key, and the receiving server verifies the signature using a public key published in DNS.

The email includes a DKIM-Signature header:

```
DKIM-Signature: v=1; a=rsa-sha256; d=example.com; s=selector1;
    c=relaxed/relaxed; q=dns/txt; t=1706018400;
    h=from:to:subject:date:message-id;
    bh=47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=;
    b=xyzabc123...
```

The receiving server:
1. Retrieves the public key from DNS: selector1._domainkey.example.com
2. Verifies the signature matches the email content
3. Confirms the email hasn't been tampered with

**DMARC (Domain-based Message Authentication, Reporting, and Conformance)**:

DMARC builds on SPF and DKIM, specifying what to do when authentication fails and requesting reports about email authentication.

```
_dmarc.example.com.    TXT    "v=DMARC1; p=reject; rua=mailto:dmarc-reports@example.com"
```

This says: "Reject emails that fail SPF or DKIM checks, and send me reports about authentication attempts."

Together, these three mechanisms (SPF, DKIM, DMARC) significantly reduce email spoofing and phishing.

---

### **Working with Email in Java**

Java provides the JavaMail API (now Jakarta Mail) for sending and receiving email.

**Sending Email with SMTP**:

```java
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(
            String to, 
            String subject, 
            String body) throws MessagingException {
        
        // SMTP server configuration
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        // Create session with authentication
        Session session = Session.getInstance(props, 
            new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        "alice@example.com", 
                        "password");
                }
            });
        
        try {
            // Create message
            Message message = new MimeMessage(session);
            
            // Set sender
            message.setFrom(new InternetAddress("alice@example.com"));
            
            // Set recipient
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to));
            
            // Set subject
            message.setSubject(subject);
            
            // Set body
            message.setText(body);
            
            // Send message
            Transport.send(message);
            
            System.out.println("Email sent successfully");
            
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    public static void main(String[] args) {
        try {
            sendEmail(
                "bob@university.edu",
                "Meeting Tomorrow",
                "Hi Bob,\n\nDon't forget about our meeting tomorrow at 2 PM.\n\nBest regards,\nAlice"
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
```

**Sending HTML Email**:

```java
// Create HTML content
MimeMessage message = new MimeMessage(session);
message.setFrom(new InternetAddress("alice@example.com"));
message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bob@university.edu"));
message.setSubject("Meeting Tomorrow");

// Create HTML part
MimeBodyPart htmlPart = new MimeBodyPart();
String htmlContent = """
    <html>
    <body>
        <h1>Meeting Reminder</h1>
        <p>Hi Bob,</p>
        <p>Don't forget about our meeting tomorrow at <strong>2 PM</strong>.</p>
        <p>Best regards,<br>Alice</p>
    </body>
    </html>
    """;
htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

// Create multipart message (can include both plain text and HTML)
Multipart multipart = new MimeMultipart();
multipart.addBodyPart(htmlPart);

// Set content
message.setContent(multipart);

// Send
Transport.send(message);
```

**Sending Email with Attachments**:

```java
MimeMessage message = new MimeMessage(session);
message.setFrom(new InternetAddress("alice@example.com"));
message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("bob@university.edu"));
message.setSubject("Report Attached");

// Text part
MimeBodyPart textPart = new MimeBodyPart();
textPart.setText("Hi Bob,\n\nPlease find the report attached.\n\nBest regards,\nAlice");

// Attachment part
MimeBodyPart attachmentPart = new MimeBodyPart();
attachmentPart.attachFile(new File("/path/to/report.pdf"));

// Combine parts
Multipart multipart = new MimeMultipart();
multipart.addBodyPart(textPart);
multipart.addBodyPart(attachmentPart);

message.setContent(multipart);
Transport.send(message);
```

**Receiving Email with IMAP**:

```java
import javax.mail.*;
import javax.mail.search.*;
import java.util.Properties;

public class EmailReceiver {
    public static void receiveEmail() throws MessagingException {
        // IMAP configuration
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", "imap.example.com");
        props.put("mail.imap.port", "993");
        
        // Connect to mail server
        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect("imap.example.com", "bob@university.edu", "password");
        
        // Open INBOX
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        
        // Get message count
        int messageCount = inbox.getMessageCount();
        System.out.println("Total messages: " + messageCount);
        
        // Get unread messages
        Message[] messages = inbox.search(
            new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        
        System.out.println("Unread messages: " + messages.length);
        
        // Process each message
        for (Message message : messages) {
            System.out.println("From: " + message.getFrom()[0]);
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Date: " + message.getSentDate());
            
            // Get content
            if (message.isMimeType("text/plain")) {
                System.out.println("Content: " + message.getContent());
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        System.out.println("Content: " + bodyPart.getContent());
                    }
                }
            }
            
            // Mark as read
            message.setFlag(Flags.Flag.SEEN, true);
            
            System.out.println("---");
        }
        
        // Clean up
        inbox.close(false);
        store.close();
    }
    
    public static void main(String[] args) {
        try {
            receiveEmail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
```

---

This is extensive content. Would you like me to continue with the remaining sections (DNS Deep Dive, Transport Layer details, TCP/UDP, Network Layer, Control Plane, IP, IPv4 vs IPv6, NAT, Data Link Layer)? I should create these as separate continuation files to make them more manageable.

