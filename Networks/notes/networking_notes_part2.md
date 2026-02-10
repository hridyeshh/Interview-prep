# Computer Networks - Comprehensive Study Notes (Part 2)

## Table of Contents (Part 2)
10. [Network Topologies](#10-network-topologies)
11. [Structure of the Network](#11-structure-of-the-network)
12. [OSI Model (7 Layers)](#12-osi-model-7-layers)
13. [TCP/IP Model (5 Layers)](#13-tcpip-model-5-layers)
14. [Client-Server Architecture (Detailed)](#14-client-server-architecture-detailed)
15. [Peer-to-Peer Architecture](#15-peer-to-peer-architecture)
16. [Networking Devices Deep Dive](#16-networking-devices-deep-dive)
17. [Protocols Deep Dive](#17-protocols-deep-dive)
18. [Sockets Programming](#18-sockets-programming)

---

## 10. Network Topologies

### The Blueprint of Network Connections

Network topology defines the physical or logical arrangement of network nodes and the connections between them. The choice of topology significantly impacts performance, cost, scalability, and fault tolerance.

---

### **1. BUS TOPOLOGY**

#### **Structure**
All devices connect to a single central cable (the "bus" or "backbone"). Data travels in both directions along the bus, and all devices receive all transmissions.

```
[Computer A]---[Computer B]---[Computer C]---[Computer D]
       |_____________Backbone Cable_____________|
               (Terminators at both ends)
```

#### **How It Works**
1. Device wants to send data → Broadcasts on the bus
2. All devices on the bus receive the signal
3. Only the device with matching address accepts the data
4. Others ignore it

**Important**: Requires **terminators** at both ends to prevent signal reflection/echo.

#### **Characteristics**

**Advantages**:
- **Cost-Effective**: Minimal cabling required
- **Easy to Install**: Simple physical layout
- **Works Well for Small Networks**: 5-10 devices
- **Easy to Extend**: Add taps along the backbone

**Disadvantages**:
- **Single Point of Failure**: Backbone cable breaks → entire network fails
- **Performance Degradation**: More devices = more collisions and slower performance
- **Difficult to Troubleshoot**: Hard to identify which segment has problems
- **Limited Cable Length**: Signal degrades over distance
- **Security Issues**: All devices see all traffic (sniffing possible)
- **No Fault Isolation**: One faulty device can bring down the network

#### **Real-World Use**
- **Historical**: Early Ethernet (10BASE2, 10BASE5) used bus topology
- **Modern**: Mostly obsolete for computer networks
- **Still Used In**: 
  - Automotive networks (CAN bus)
  - Industrial control systems
  - Some legacy systems

#### **Access Method: CSMA/CD**
Bus topology typically uses CSMA/CD (Carrier Sense Multiple Access with Collision Detection):
1. **Listen** before transmitting (Carrier Sense)
2. If bus is free, **transmit**
3. **Detect collisions** (if two devices transmit simultaneously)
4. If collision detected, both stop, wait random time, retry

---

### **2. RING TOPOLOGY**

#### **Structure**
Devices connected in a circular fashion. Each device has exactly two neighbors. Data travels in one direction (or both, in dual ring) around the ring.

```
        [Computer A]
           /    \
[Computer D]    [Computer B]
           \    /
        [Computer C]
        
Data flow: A → B → C → D → A (circular)
```

#### **Types of Ring Topology**

**1. Single Ring (Unidirectional)**
- Data flows in one direction only
- Token-based access control

**2. Dual Ring (Bidirectional)**
- Two rings: primary and secondary
- Data flows in opposite directions
- If primary fails, secondary takes over
- Used in FDDI (Fiber Distributed Data Interface)

#### **How It Works: Token Ring**

A special frame called a "token" circulates around the ring:
1. Token circulates continuously
2. Device wanting to transmit **captures the token**
3. Converts token to data frame, adds data
4. Frame circulates until reaching destination
5. Destination copies data
6. Frame continues to sender
7. Sender removes frame, releases new token

**No Collisions**: Only the device with the token can transmit.

#### **Characteristics**

**Advantages**:
- **No Collisions**: Token-based access
- **Predictable Performance**: Known maximum wait time for token
- **Equal Access**: All devices get fair chance to transmit
- **Better Performance Than Bus**: Under heavy load
- **Can Cover Larger Distances**: With repeaters in each node

**Disadvantages**:
- **Single Point of Failure**: Break in ring → network fails (unless dual ring)
- **Adding/Removing Devices**: Temporarily disrupts network
- **Troubleshooting Difficulty**: Need to check entire ring
- **Slower Than Star**: Data must pass through all devices
- **Token Management**: Overhead of managing token
- **More Expensive Than Bus**: More cabling required

#### **Ring Topology Improvements: Dual Ring**

```
Primary Ring (Clockwise) →
        [Computer A]
           /    \
[Computer D]    [Computer B]
           \    /
        [Computer C]
← Secondary Ring (Counter-clockwise)

If primary breaks: Traffic automatically switches to secondary
```

**Fault Tolerance**: If one ring breaks, system automatically switches to the other ring and "wraps" around the failure.

#### **Real-World Use**
- **Token Ring**: IBM's Token Ring networks (historical, mostly obsolete)
- **FDDI**: High-speed LAN (100 Mbps), used in campus backbones (historical)
- **SONET/SDH**: Telecommunications infrastructure (still in use)
- **Metro Networks**: Some metropolitan area networks

---

### **3. STAR TOPOLOGY**

#### **Structure**
All devices connect to a central hub or switch. All communication passes through this central device.

```
           [Computer A]
                |
    [Computer D]---[Switch/Hub]---[Computer B]
                |
           [Computer C]
```

#### **How It Works**

**With Hub (Obsolete)**:
1. Device sends data to hub
2. Hub broadcasts to ALL devices
3. Intended recipient accepts, others discard
4. Essentially a bus topology in star configuration

**With Switch (Modern)**:
1. Device sends data to switch
2. Switch reads destination MAC address
3. Switch forwards ONLY to intended recipient
4. Much more efficient

#### **Characteristics**

**Advantages**:
- **Fault Isolation**: One cable fails → only that device affected
- **Easy to Add/Remove Devices**: No disruption to network
- **Easy to Troubleshoot**: Check central device and individual connections
- **Better Performance**: Especially with switches (no collisions)
- **Centralized Management**: All traffic goes through central point
- **Scalability**: Easy to expand by adding more switches

**Disadvantages**:
- **Central Point of Failure**: If switch/hub fails → entire network down
- **More Cabling Required**: Each device needs separate cable to center
- **Higher Cost**: More cables + central device
- **Limited by Central Device**: Performance bottlenecked by switch capacity

#### **Modern Enhancement: Hierarchical Star**

Large networks use multiple levels of switches:

```
                [Core Switch]
                 /    |    \
         [Switch A] [Switch B] [Switch C]
           / | \      / | \      / | \
         PCs PCs PCs PCs PCs PCs PCs PCs PCs
```

**Benefits**:
- Reduces load on any single switch
- Better cable management
- Easier to expand
- More fault-tolerant (redundant paths possible)

#### **Real-World Use**
- **Dominant Topology Today**: Virtually all modern LANs
- **Home Networks**: Router as central point
- **Office Buildings**: Switches on each floor, connected to core switches
- **Data Centers**: Massive hierarchical star topologies

---

### **4. TREE TOPOLOGY (Hierarchical)**

#### **Structure**
Combination of star topologies arranged in a hierarchy. Looks like an inverted tree with root switch at top.

```
                    [Root Switch]
                    /           \
            [Switch 1]         [Switch 2]
            /       \          /       \
      [Switch 3] [Switch 4] [Switch 5] [Switch 6]
       /  |  \    /  |  \    /  |  \    /  |  \
      PC PC PC  PC PC PC  PC PC PC  PC PC PC
```

#### **How It Works**
- Data flows hierarchically
- Root switch at top level
- Branch switches connect to root
- Leaf devices connect to branch switches
- Sometimes multiple hierarchical levels

#### **Characteristics**

**Advantages**:
- **Scalable**: Easy to expand by adding more branches
- **Hierarchical Management**: Different levels of control
- **Fault Isolation**: Problem in one branch doesn't affect others
- **Supports Large Networks**: Can connect thousands of devices
- **Organized**: Logical structure (departments, floors, buildings)
- **Efficient**: Shorter paths between many devices

**Disadvantages**:
- **Root Switch Failure**: Can be catastrophic (but usually redundant)
- **Complex Cabling**: More complex than simple star
- **Dependent on Root**: Heavy load on root switch
- **More Expensive**: Multiple switches needed
- **Configuration Complexity**: Managing hierarchy

#### **Spanning Tree Protocol (STP)**

Important concept for tree topologies:
- **Problem**: Multiple paths can create loops → broadcast storms
- **Solution**: STP automatically disables redundant paths
- **Mechanism**: Switches elect a "root bridge" and calculate shortest paths
- **Benefit**: Prevents loops while maintaining backup paths

#### **Real-World Use**
- **Enterprise Networks**: Large companies with multiple buildings
- **Campus Networks**: Universities with many buildings
- **ISP Infrastructure**: Hierarchical routing
- **Data Centers**: Organized by racks, rows, sections

---

### **5. MESH TOPOLOGY**

#### **Structure**
Every device connects to every other device (full mesh) or multiple devices (partial mesh).

**Full Mesh**:
```
    [A]─────[B]
    /│\    /│\
   / │ \  / │ \
  /  │  \/  │  \
 /   │  /\  │   \
[D]──┼─/──\─┼───[C]
  \  │/    \│  /
   \ /      \ /
    \/      \/
    [E]─────[F]
    
Every device connected to every other device
```

**Partial Mesh**:
```
    [A]─────[B]
    │       │
    │   [C]─┘
    │   │
    [D]─┘
    
Some devices have multiple connections, not all
```

#### **How It Works**

**Data Routing**:
1. Multiple paths available between any two nodes
2. Routing algorithms choose best path
3. If one path fails, data takes alternate route
4. Dynamic or static routing

**Full Mesh Formula**:
Number of connections = n(n-1)/2, where n = number of devices
- 5 devices = 10 connections
- 10 devices = 45 connections
- 100 devices = 4,950 connections!

#### **Characteristics**

**Advantages**:
- **Extremely Reliable**: Multiple redundant paths
- **No Congestion**: Multiple paths reduce bottlenecks
- **Fault Tolerant**: Network survives multiple link failures
- **Dedicated Links**: No sharing (in some implementations)
- **High Performance**: Data can take optimal path
- **Privacy/Security**: Point-to-point links harder to tap

**Disadvantages**:
- **Very Expensive**: Massive cabling costs in full mesh
- **Complex Installation**: Exponential increase in connections
- **Space Requirements**: Cable management nightmare
- **Difficult Maintenance**: Troubleshooting complex
- **Scalability Issues**: Adding devices exponentially increases connections

#### **Types by Application**

**Wired Mesh**:
- Rare in local networks (too expensive)
- Used in: Network backbones, Internet core routers
- Partial mesh more common than full mesh

**Wireless Mesh**:
- More practical than wired
- Each node acts as router
- Data hops between nodes to reach destination
- Used in: Smart city networks, IoT, campus Wi-Fi

**Example: Wireless Mesh Network**
```
Internet
   ↓
[Gateway]
   ↓
[Node A]──[Node B]
   │ \    / │
   │  \  /  │
   │   \/   │
   │   /\   │
[Node C]──[Node D]
   
If Node B fails, data routes: A → C → D
```

#### **Real-World Use**

**Wireless Mesh Networks**:
- Google Wifi, Eero home mesh systems
- Smart city deployments
- Disaster recovery networks
- Military tactical networks

**Wired Partial Mesh**:
- Internet backbone (autonomous systems interconnected)
- Data center fabrics
- High-availability clusters
- Telecommunications networks

**Full Mesh (Rare)**:
- Small, critical systems requiring maximum redundancy
- Financial trading systems
- Mission-critical military communications

---

### **Topology Comparison Summary**

| Topology | Cost | Scalability | Fault Tolerance | Performance | Complexity |
|----------|------|-------------|-----------------|-------------|------------|
| **Bus** | Low | Poor | Very Low | Poor (high load) | Low |
| **Ring** | Medium | Medium | Low (better with dual ring) | Good | Medium |
| **Star** | Medium | Good | Medium | Good (with switch) | Low-Medium |
| **Tree** | High | Excellent | Good | Good | Medium-High |
| **Mesh** | Very High | Poor (full), Good (partial) | Excellent | Excellent | High |

---

### **Choosing the Right Topology**

**For Home Networks**: Star (simple, effective)
**For Small Office**: Star or Tree (easy to manage)
**For Large Enterprise**: Tree or Hierarchical Star (scalable)
**For Critical Systems**: Partial Mesh (redundancy)
**For Wireless Deployments**: Mesh (coverage, reliability)

---

## 11. Structure of the Network

### The Internet's Hierarchical Organization

The Internet isn't a single network but a "network of networks" organized in a hierarchical structure. Understanding this structure is crucial for comprehending how data flows globally.

---

### **The Tiered Structure**

The Internet is organized into three main tiers, forming a hierarchical structure:

```
                    Tier 1 ISPs
                    (Global Backbone)
                        |
                    Tier 2 ISPs
                  (Regional Networks)
                        |
                    Tier 3 ISPs
                  (Local Access Providers)
                        |
                   End Users
                (Homes, Businesses)
```

---

### **Tier 1 Networks (The Internet Backbone)**

#### **What Are Tier 1 ISPs?**

Tier 1 networks form the "backbone" of the Internet. These are the largest networks that:
- Can reach every other network on the Internet
- Do NOT pay anyone for Internet transit
- Exchange traffic with other Tier 1 networks for FREE (peering)
- Own and operate intercontinental infrastructure

#### **Key Characteristics**

**Global Reach**:
- Presence on multiple continents
- Ownership of submarine cables
- Major Internet exchange points (IXPs)

**Settlement-Free Peering**:
- Exchange traffic with other Tier 1 ISPs at no cost
- Based on roughly equal traffic exchange
- No one "owns" the Internet more than others

**Examples of Tier 1 ISPs**:
1. **Level 3 (CenturyLink/Lumen)** - North America, Europe, Asia
2. **AT&T** - Global presence
3. **Verizon Business** - Worldwide
4. **NTT Communications** - Asia-Pacific focus
5. **GTT Communications** - Europe, Americas
6. **Telia Carrier** - Europe, Americas
7. **Telecom Italia Sparkle** - Mediterranean, Europe

#### **How Tier 1 Networks Interconnect**

**Peering Arrangements**:
- Bilateral agreements between Tier 1 providers
- Exchange traffic at Internet Exchange Points (IXPs)
- No money changes hands
- Must be roughly balanced traffic (not 90/10)

**Internet Exchange Points (IXPs)**:
Major peering facilities:
- **DE-CIX (Frankfurt)**: Europe's largest IXP
- **AMS-IX (Amsterdam)**: Major European exchange
- **LINX (London)**: UK's primary exchange
- **Equinix (multiple locations)**: Global exchange operator

```
[AT&T Network] ─────────── [IXP] ─────────── [Level 3 Network]
                              │
                    [NTT Communications]
                    
All exchange traffic freely at IXP
```

---

### **Tier 2 Networks (Regional ISPs)**

#### **What Are Tier 2 ISPs?**

Tier 2 networks are regional or national ISPs that:
- Connect to Tier 1 networks (and pay for transit)
- Peer with other Tier 2 networks
- Serve specific geographic regions
- Sell services to Tier 3 ISPs and large businesses

#### **Key Characteristics**

**Hybrid Model**:
- **Pay** Tier 1 for global reach
- **Peer** with some Tier 2 ISPs (free exchange)
- **Sell** to Tier 3 ISPs and end users

**Regional Focus**:
- Strong presence in specific countries/regions
- Multiple connections to Tier 1 for redundancy
- Often own regional infrastructure

**Examples of Tier 2 ISPs**:
- **Comcast** (US)
- **Cox Communications** (US)
- **BT (British Telecom)** (UK)
- **Vodafone** (Europe)
- **Bharti Airtel** (India)
- **China Telecom** (China)

#### **Why Tier 2 Exists**

**Economics**:
- Building global infrastructure is extremely expensive
- Tier 2 focuses on regional expertise
- Can offer competitive pricing in their region
- Aggregate traffic before sending to Tier 1

**Redundancy**:
- Multiple Tier 1 connections provide backup
- Peering with other Tier 2s offers alternate paths

---

### **Tier 3 Networks (Local Access ISPs)**

#### **What Are Tier 3 ISPs?**

Tier 3 networks are local or retail ISPs that:
- Provide Internet access to end users
- Purchase all transit from Tier 2 or Tier 1 networks
- Do NOT engage in peering
- Focus on "last mile" connectivity

#### **Key Characteristics**

**Customer-Facing**:
- Directly serve homes and small businesses
- Handle customer support
- Manage local infrastructure (DSL, cable, fiber)

**Pay for Everything**:
- Purchase bandwidth from upstream providers
- May buy from multiple Tier 2 ISPs for redundancy

**Examples of Tier 3 ISPs**:
- Local cable companies
- Small regional ISPs
- Municipal broadband providers
- Many smaller ISPs worldwide

#### **"Last Mile" Problem**

The "last mile" (connection from ISP to your home) is:
- Most expensive per byte delivered
- Hardest to maintain
- Often a monopoly (one cable/phone company)
- Technology varies: DSL, Cable, Fiber, Wireless, Satellite

```
Tier 1 Backbone
    ↓
Tier 2 Regional Network
    ↓
Tier 3 Local ISP
    ↓
[Last Mile - Most Expensive]
    ↓
Your Home
```

---

### **Content Delivery Networks (CDNs) - A Special Category**

#### **What Are CDNs?**

CDNs are specialized networks that:
- Cache content closer to end users
- Reduce load on origin servers
- Improve performance and reduce latency
- Sometimes bypass traditional ISP hierarchy

#### **Major CDN Providers**
- **Cloudflare**: Global presence, free tier
- **Akamai**: Largest CDN, enterprise focus
- **Amazon CloudFront**: Integrated with AWS
- **Fastly**: Real-time content delivery
- **Google Cloud CDN**: Part of GCP

#### **How CDNs Fit In**

CDNs often:
- Peer directly with Tier 1, Tier 2, and even Tier 3 ISPs
- Place servers inside ISP networks (caches)
- Bypass traditional hierarchy for efficiency

```
Netflix CDN Example:
Netflix Origin Servers (California)
    ↓
CDN Nodes Worldwide
    ├─ Inside Comcast Network (US)
    ├─ Inside BT Network (UK)
    └─ Inside Bharti Airtel (India)
    
User streams from LOCAL CDN node, not origin
```

---

### **Autonomous Systems (AS) and BGP**

#### **What is an Autonomous System?**

An **Autonomous System (AS)** is a collection of IP networks under control of a single organization that presents a common routing policy.

**AS Numbers (ASN)**:
- Unique identifier for each AS
- 16-bit (1-65535) or 32-bit
- Assigned by regional Internet registries
- Example: AS15169 = Google

#### **Border Gateway Protocol (BGP)**

BGP is the protocol that makes the Internet work by:
- Exchanging routing information between Autonomous Systems
- Determining best paths between networks
- Ensuring data can route anywhere on the Internet

**How BGP Works** (Simplified):
1. Each AS announces its IP prefixes to neighbors
2. Routers exchange routing tables
3. BGP uses path attributes to choose best route
4. Routes propagate across the entire Internet

**Example BGP Path**:
```
Your ISP (AS 1234)
    ↓ BGP says: "To reach 142.250.80.0/24 (Google)..."
Route: AS 1234 → AS 3356 (Level 3) → AS 15169 (Google)
```

**Why BGP Is Critical**:
- Single global routing protocol
- Enables Internet interconnection
- No central control (distributed)

**BGP Vulnerabilities**:
- **Route Hijacking**: Malicious AS announces someone else's IPs
- **Misconfigurations**: Can blackhole traffic
- **No Built-in Security**: Trust-based system (RPKI aims to fix this)

---

### **The Internet's Physical Structure**

#### **Internet Exchange Points (IXPs)**

Physical locations where multiple networks interconnect:

**Major IXPs by Traffic**:
1. **DE-CIX Frankfurt**: 10+ Tbps peak traffic
2. **AMS-IX Amsterdam**: 8+ Tbps
3. **LINX London**: 6+ Tbps
4. **Equinix Ashburn**: Major US exchange

**Benefits of IXPs**:
- Reduced latency (local peering)
- Lower costs (avoid upstream transit)
- Better resilience (more paths)
- Improved performance

#### **Points of Presence (PoPs)**

ISPs don't have direct fiber to every customer. Instead:
- **PoP**: Location where ISP has equipment (routers, switches)
- **Last Mile**: Connection from PoP to customer

Example:
```
Tier 2 ISP Core Network
    ├─ PoP in City A
    │   └─ Serves customers in radius
    ├─ PoP in City B
    └─ PoP in City C
```

---

### **Data Flow Example: Visiting Google.com**

Let's trace the complete path:

```
1. Your Computer (192.168.1.10)
   ↓ DNS query
2. Local Router (192.168.1.1)
   ↓
3. Tier 3 ISP (Your Local ISP)
   ↓ Checks routing table, sends upstream
4. Tier 2 ISP (Regional Provider)
   ↓ BGP routes through peering
5. Tier 1 ISP (Backbone Provider)
   ↓ Peer with Google's AS
6. Google's Edge Network
   ↓ Routes to nearest data center
7. Google's Servers (142.250.80.46)
   ↓ Response travels back via same path (or better path)
8. Back to Your Computer

Total time: 10-100ms depending on distance
```

---

### **Modern Trends Changing the Structure**

#### **1. Content Giants Bypassing ISPs**

**Google, Facebook, Netflix**:
- Build their own backbone networks
- Deploy servers inside ISP networks
- Direct peering with many ISPs
- Become quasi-Tier 1 networks themselves

#### **2. Cloud Providers as Major Networks**

**AWS, Azure, Google Cloud**:
- Massive private networks
- Direct connects to enterprises
- Becoming Internet infrastructure themselves

#### **3. Edge Computing**

Moving computation closer to users:
- 5G edge computing
- CDN computation (Cloudflare Workers)
- IoT edge gateways

#### **4. Starlink and LEO Satellites**

Low Earth Orbit satellite constellations:
- Bypass traditional ISP infrastructure
- Provide last-mile in remote areas
- Lower latency than traditional satellites

---

### **Network Structure Key Takeaways**

1. **Hierarchical Model**: Tier 1 (backbone) → Tier 2 (regional) → Tier 3 (local)
2. **Peering is Key**: Tier 1 networks peer freely; others pay for transit
3. **BGP Glues It Together**: Routing protocol that makes Internet work
4. **No Central Control**: Distributed system of cooperating networks
5. **Physical Reality**: Submarine cables, IXPs, PoPs form physical infrastructure
6. **Changing Landscape**: Tech giants building private networks, edge computing emerging

---

## 12. OSI Model (7 Layers)

### The Conceptual Framework for Network Communication

The **OSI (Open Systems Interconnection) Model** is a conceptual framework that standardizes the functions of a communication system into seven abstraction layers. Though the Internet primarily uses TCP/IP, understanding OSI is crucial for networking professionals.

---

### **Why OSI Model Exists**

**Historical Problem**:
- 1970s: Every vendor had proprietary networking systems
- IBM, DEC, HP systems couldn't communicate
- No standardization

**OSI Solution** (1984):
- ISO (International Organization for Standardization) created OSI model
- Provides vendor-neutral reference
- Separates networking into modular layers
- Each layer has specific responsibilities

**Key Principle**: **Abstraction and Modularity**
- Each layer provides services to layer above
- Each layer uses services from layer below
- Changes in one layer don't affect others
- Clear interfaces between layers

---

### **The 7 Layers (Mnemonic: "Please Do Not Throw Sausage Pizza Away")**

```
Layer 7: Application   ─── "Please"
Layer 6: Presentation  ─── "Do"
Layer 5: Session       ─── "Not"
Layer 4: Transport     ─── "Throw"
Layer 3: Network       ─── "Sausage"
Layer 2: Data Link     ─── "Pizza"
Layer 1: Physical      ─── "Away"
```

---

### **Layer 1: Physical Layer**

#### **Responsibility**: 
Transmitting raw bits over physical medium.

#### **What It Does**:
- Converts data bits (1s and 0s) into electrical, light, or radio signals
- Defines physical characteristics of transmission medium
- Bit synchronization (timing)
- Bit rate control (speed)
- Physical topology

#### **Key Concepts**:

**Transmission Modes**:
- **Simplex**: One direction only (e.g., TV broadcast, keyboard to computer)
- **Half-Duplex**: Both directions, but not simultaneously (e.g., walkie-talkie)
- **Full-Duplex**: Both directions simultaneously (e.g., telephone, Ethernet)

**Transmission Media**:
- **Guided**: Wires, cables (copper, fiber)
  - Twisted Pair (Cat5e, Cat6)
  - Coaxial Cable
  - Fiber Optic
- **Unguided**: Wireless (radio, microwave, infrared)
  - Wi-Fi
  - Bluetooth
  - Cellular (4G/5G)

**Signal Types**:
- **Analog**: Continuous waves (traditional phone, radio)
- **Digital**: Discrete values (modern networks)

#### **Devices Operating at Layer 1**:
- Hubs (broadcast all signals)
- Repeaters (amplify signals)
- Cables and connectors
- Network Interface Cards (physical interface)

#### **Example**:
When you send data:
```
Bits: 1 0 1 1 0 0 1 0
   ↓
Physical Layer converts to:
Electrical voltage: +5V 0V +5V +5V 0V 0V +5V 0V (over copper)
OR
Light pulses: ON OFF ON ON OFF OFF ON OFF (over fiber)
```

---

### **Layer 2: Data Link Layer**

#### **Responsibility**: 
Node-to-node data transfer and error detection/correction.

#### **What It Does**:
- Frames data from network layer
- Adds MAC addresses (source and destination)
- Error detection (CRC - Cyclic Redundancy Check)
- Flow control
- Access control (who can transmit when)

#### **Two Sublayers**:

**1. LLC (Logical Link Control)**:
- Interface to network layer
- Flow control
- Error control

**2. MAC (Media Access Control)**:
- Frame assembly/disassembly
- MAC addressing
- Media access management

#### **Key Concepts**:

**MAC Address** (Media Access Control Address):
- 48-bit (6 bytes) hardware address
- Format: `AA:BB:CC:DD:EE:FF` (hexadecimal)
- First 3 bytes: Manufacturer ID (OUI - Organizationally Unique Identifier)
- Last 3 bytes: Device ID
- Example: `00:1A:2B:3C:4D:5E`
- **Burned into NIC** (Network Interface Card), but can be spoofed in software

**Framing**:
Data is encapsulated into frames:
```
[Preamble | Destination MAC | Source MAC | Type | Payload | CRC]
```

**Error Detection**:
- **CRC (Cyclic Redundancy Check)**: Detects errors in frame
- If error detected → frame discarded (upper layers handle retransmission)

**Flow Control**:
- Prevents sender from overwhelming receiver
- Methods: Stop-and-Wait, Sliding Window

**Access Control Methods**:
- **CSMA/CD** (Carrier Sense Multiple Access with Collision Detection) - Wired Ethernet
- **CSMA/CA** (Collision Avoidance) - Wi-Fi
- **Token Passing** - Token Ring

#### **Devices Operating at Layer 2**:
- **Switches**: Forward frames based on MAC address
- **Bridges**: Connect two LAN segments
- **NICs**: Network Interface Cards
- **Wireless Access Points**: Wi-Fi APs

#### **Example**: Ethernet Frame
```
Sending data from Computer A to Computer B on same network:

Data from Layer 3: IP packet
   ↓
Layer 2 adds:
- Destination MAC: Computer B's MAC
- Source MAC: Computer A's MAC
- CRC: Error checking
   ↓
Frame transmitted over physical medium
```

---

### **Layer 3: Network Layer**

#### **Responsibility**: 
Routing and forwarding of packets across networks.

#### **What It Does**:
- Logical addressing (IP addresses)
- Routing (finding best path)
- Packet forwarding
- Fragmentation and reassembly
- Network-to-network communication

#### **Key Concepts**:

**IP Addressing**:
- IPv4: 32-bit (e.g., 192.168.1.1)
- IPv6: 128-bit (e.g., 2001:0db8::1)
- Hierarchical addressing (network + host)

**Routing**:
- Determining best path from source to destination
- Routing tables and routing protocols
- Protocols: RIP, OSPF, BGP

**Packet Structure**:
```
[IP Header | Payload (from Transport Layer)]
   ↓
IP Header contains:
- Source IP
- Destination IP
- TTL (Time To Live)
- Protocol (TCP, UDP, ICMP)
- Checksum
```

**TTL (Time To Live)**:
- Prevents packets from circulating forever
- Decremented by each router
- When TTL reaches 0 → packet discarded

**Fragmentation**:
- Different networks have different MTU (Maximum Transmission Unit)
- Router may fragment large packets
- Reassembled at destination

#### **Routing Process**:
```
1. Host generates packet with destination IP
2. Checks routing table:
   - Same network? Send directly
   - Different network? Send to default gateway
3. Router receives packet
4. Examines destination IP
5. Consults routing table
6. Forwards to next hop
7. Repeat until reaches destination
```

#### **Devices Operating at Layer 3**:
- **Routers**: Forward packets between networks
- **Layer 3 Switches**: Switches with routing capability
- **Gateways**: Translate between different protocols

#### **Important Protocols**:
- **IP (Internet Protocol)**: Core protocol for packet delivery
- **ICMP (Internet Control Message Protocol)**: Error reporting (ping, traceroute)
- **ARP (Address Resolution Protocol)**: Maps IP to MAC address
- **RARP (Reverse ARP)**: Maps MAC to IP (historical)

#### **Example**: Packet Routing
```
Computer A (192.168.1.10) wants to reach Server (203.0.113.5)

1. A creates packet: [SRC: 192.168.1.10 | DST: 203.0.113.5 | Data]
2. Checks: 203.0.113.5 not on local network
3. Sends to default gateway (router at 192.168.1.1)
4. Router #1 consults routing table → forwards to Router #2
5. Router #2 → Router #3 (across Internet backbone)
6. Router #3 → Server's local network
7. Packet reaches Server
```

---

### **Layer 4: Transport Layer**

#### **Responsibility**: 
End-to-end communication, reliability, flow control.

#### **What It Does**:
- Segmentation and reassembly
- End-to-end error recovery
- Flow control
- Connection management (for connection-oriented protocols)
- Port addressing (multiplexing)

#### **Key Protocols**:

**1. TCP (Transmission Control Protocol)**:
- **Connection-Oriented**: Establishes connection before data transfer
- **Reliable**: Guarantees delivery, order, and error-free data
- **Flow Control**: Prevents overwhelming receiver
- **Congestion Control**: Adjusts to network conditions

**TCP Segment Structure**:
```
[Source Port | Dest Port | Sequence # | Ack # | Flags | Window | Checksum | Data]
```

**TCP Three-Way Handshake** (Connection Establishment):
```
Client                          Server
  |                                |
  |-------SYN (Seq=100)----------->|
  |                                |
  |<------SYN-ACK (Seq=5000)-------|
  |       (Ack=101)                |
  |                                |
  |-------ACK (Ack=5001)---------->|
  |                                |
Connection Established
```

**TCP Connection Termination** (Four-Way Handshake):
```
Client                          Server
  |                                |
  |-------FIN------------------->|
  |<------ACK--------------------|
  |<------FIN--------------------|
  |-------ACK------------------->|
  |                                |
Connection Closed
```

**2. UDP (User Datagram Protocol)**:
- **Connectionless**: No connection setup
- **Unreliable**: No delivery guarantees
- **Fast**: Minimal overhead
- **No Flow/Congestion Control**

**UDP Datagram Structure**:
```
[Source Port | Dest Port | Length | Checksum | Data]
(Much simpler than TCP)
```

#### **TCP vs UDP Comparison**:

| Feature | TCP | UDP |
|---------|-----|-----|
| **Connection** | Connection-oriented | Connectionless |
| **Reliability** | Reliable (ACKs, retransmissions) | Unreliable (best-effort) |
| **Ordering** | Ordered delivery | No ordering |
| **Speed** | Slower (overhead) | Faster |
| **Header Size** | 20+ bytes | 8 bytes |
| **Use Cases** | HTTP, FTP, Email | DNS, Streaming, Gaming, VoIP |
| **Error Checking** | Extensive | Basic checksum |
| **Flow Control** | Yes | No |

#### **Port Multiplexing**:
Transport layer uses ports to multiplex multiple applications:
```
Computer (192.168.1.10)
   ├─ Chrome: Port 54321 → Google: 443
   ├─ Spotify: Port 54322 → Server: 4070
   └─ Zoom: Port 54323 → Server: 8801

All use same IP, different ports distinguish applications
```

#### **Flow Control** (TCP):
- **Sliding Window**: Receiver advertises buffer size
- Sender adjusts transmission rate based on receiver's window
- Prevents buffer overflow at receiver

#### **Congestion Control** (TCP):
- **Slow Start**: Begins with small congestion window, increases exponentially
- **Congestion Avoidance**: Linear increase when threshold reached
- **Fast Retransmit/Recovery**: Quickly retransmit lost packets

#### **Example**: Downloading a Web Page
```
Browser wants to download http://example.com/page.html

Layer 4 (Transport):
1. TCP connection established (three-way handshake)
2. Data segmented:
   - Segment 1: Bytes 1-1460
   - Segment 2: Bytes 1461-2920
   - Segment 3: Bytes 2921-4380
3. Each segment gets sequence number
4. Receiver sends ACKs for received segments
5. If segment lost → retransmitted
6. All segments reassembled in correct order
7. Connection closed (four-way handshake)
```

---

*(Continued in next section...)*

### **Layer 5: Session Layer**

#### **Responsibility**: 
Manages sessions/connections between applications.

#### **What It Does**:
- Session establishment, maintenance, termination
- Synchronization (checkpoints for long transfers)
- Dialog control (half-duplex or full-duplex)
- Token management (in some protocols)

#### **Key Concepts**:

**Session Management**:
- Establishes session between two communicating systems
- Maintains session state
- Gracefully terminates sessions

**Synchronization**:
- Places checkpoints in data stream
- If failure occurs, resume from last checkpoint (not from beginning)
- Important for long file transfers

**Dialog Control**:
- **Half-Duplex**: One direction at a time (e.g., walkie-talkie style)
- **Full-Duplex**: Both directions simultaneously

#### **Example Protocols/APIs**:
- **NetBIOS**: Network Basic Input/Output System (Windows networking)
- **PPTP**: Point-to-Point Tunneling Protocol (VPN)
- **RPC**: Remote Procedure Call
- **SCP**: Session Control Protocol

#### **Real-World Example**:
```
Video Conferencing (Zoom):
1. Session established between participants
2. Maintains session for duration of call
3. Synchronizes audio and video streams
4. Handles reconnection if network drops briefly
5. Gracefully terminates when call ends
```

**Note**: In modern TCP/IP, session management is often handled by application layer protocols rather than a distinct session layer.

---

### **Layer 6: Presentation Layer**

#### **Responsibility**: 
Data translation, encryption, compression.

#### **What It Does**:
- Data format translation
- Encryption/decryption
- Compression/decompression
- Character encoding (ASCII, Unicode)
- Serialization/deserialization

#### **Key Functions**:

**1. Data Translation**:
- Converts data from application format to network format
- Ensures compatibility between different systems
- Example: Big-endian vs Little-endian byte ordering

**2. Encryption**:
- **SSL/TLS**: Secure Socket Layer / Transport Layer Security
- Encrypts data before transmission
- Decrypts received data
- Protects confidentiality and integrity

**3. Compression**:
- Reduces data size for faster transmission
- Algorithms: GZIP, JPEG, MPEG
- Decompression at receiving end

**4. Encoding**:
- Character set conversions (ASCII ↔ Unicode)
- Image formats (BMP, JPEG, PNG)
- Video formats (MP4, AVI, MKV)

#### **Example Protocols**:
- **SSL/TLS**: Secure communication
- **MIME**: Multipurpose Internet Mail Extensions (email attachments)
- **JPEG**: Image compression
- **GIF**: Graphics Interchange Format

#### **Java Connection**:
```java
// Presentation layer concepts in Java

// 1. Serialization (data format conversion)
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
out.writeObject(myObject);  // Converts object to byte stream

// 2. Encryption (SSL/TLS)
SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
SSLSocket sslSocket = (SSLSocket) factory.createSocket("example.com", 443);
// All data automatically encrypted/decrypted

// 3. Compression
GZIPOutputStream gzip = new GZIPOutputStream(fileOut);
gzip.write(data);  // Compresses data

// 4. Character encoding
String text = "Hello";
byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
byte[] asciiBytes = text.getBytes(StandardCharsets.US_ASCII);
```

---

### **Layer 7: Application Layer**

#### **Responsibility**: 
Provides network services to end-user applications.

#### **What It Does**:
- Interface between user applications and network
- Provides network services (file transfer, email, web browsing)
- Application-specific protocols

#### **Key Protocols**:

**1. HTTP/HTTPS (Web)**:
- **HTTP** (Port 80): Hypertext Transfer Protocol
- **HTTPS** (Port 443): HTTP Secure (with SSL/TLS)
- Request-response model
- Stateless (made stateful with cookies/sessions)

**2. FTP (File Transfer)**:
- **Port 21** (control), Port 20 (data)
- Transfer files between systems
- Authentication (username/password)

**3. SMTP (Email Sending)**:
- **Port 25** (or 587 for submission)
- Simple Mail Transfer Protocol
- Push-based (sends emails)

**4. POP3/IMAP (Email Receiving)**:
- **POP3 Port 110**: Post Office Protocol v3
  - Downloads emails and deletes from server
- **IMAP Port 143**: Internet Message Access Protocol
  - Keeps emails on server, syncs across devices

**5. DNS (Domain Name System)**:
- **Port 53**
- Translates domain names to IP addresses
- Hierarchical distributed database

**6. DHCP (Dynamic Host Configuration)**:
- **Ports 67/68**
- Automatically assigns IP addresses
- Also provides subnet mask, gateway, DNS servers

**7. SSH (Secure Shell)**:
- **Port 22**
- Encrypted remote access
- Replaces insecure Telnet

**8. Telnet**:
- **Port 23**
- Unencrypted remote access (deprecated due to security)

**9. SNMP (Simple Network Management Protocol)**:
- **Port 161/162**
- Network management and monitoring

#### **How Application Layer Works**:

**Example: Browsing a Website (HTTP)**
```
User types: www.example.com

1. Application Layer (HTTP):
   - Browser creates HTTP GET request
   GET /index.html HTTP/1.1
   Host: www.example.com
   
2. DNS Resolution (also Application Layer):
   - Queries DNS server for example.com's IP
   - Receives: 93.184.216.34
   
3. HTTP Request Sent:
   - Browser sends GET request to 93.184.216.34:80
   
4. Server Response:
   - HTTP/1.1 200 OK
   - Content-Type: text/html
   - [HTML content]
   
5. Browser renders HTML
```

#### **Java Connection**:
```java
// Application Layer protocols in Java

// 1. HTTP
URL url = new URL("https://www.example.com");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
int responseCode = conn.getResponseCode();  // 200, 404, etc.

// 2. FTP
FTPClient ftpClient = new FTPClient();
ftpClient.connect("ftp.example.com");
ftpClient.login("username", "password");
ftpClient.retrieveFile("file.txt", outputStream);

// 3. SMTP (Email)
Properties props = new Properties();
props.put("mail.smtp.host", "smtp.gmail.com");
Session session = Session.getInstance(props);
MimeMessage message = new MimeMessage(session);
// ... set recipients, subject, body
Transport.send(message);

// 4. DNS
InetAddress address = InetAddress.getByName("www.example.com");
System.out.println("IP: " + address.getHostAddress());
```

---

### **Data Flow Through OSI Layers**

#### **Sending Data (Encapsulation)**:
```
Application Layer (Layer 7):
   Data: "Hello"
   ↓
Presentation Layer (Layer 6):
   Encrypts/Compresses → "aF3xk2"
   ↓
Session Layer (Layer 5):
   Adds session info → [Session:123 | aF3xk2]
   ↓
Transport Layer (Layer 4):
   Adds TCP header → [TCP Header | Session:123 | aF3xk2]
   (Segment)
   ↓
Network Layer (Layer 3):
   Adds IP header → [IP Header | TCP Header | Session:123 | aF3xk2]
   (Packet)
   ↓
Data Link Layer (Layer 2):
   Adds Ethernet frame → [Ethernet Header | IP Header | TCP Header | Session:123 | aF3xk2 | CRC]
   (Frame)
   ↓
Physical Layer (Layer 1):
   Converts to signals → 101010101010...
   (Bits)
```

#### **Receiving Data (Decapsulation)**:
```
Physical Layer receives bits → Converts to frame
   ↓
Data Link Layer removes Ethernet header, checks CRC
   ↓
Network Layer removes IP header, routes packet
   ↓
Transport Layer removes TCP header, reassembles segments
   ↓
Session Layer manages session
   ↓
Presentation Layer decrypts/decompresses
   ↓
Application Layer delivers "Hello" to application
```

---

### **OSI Model Summary Table**

| Layer | Name | Function | Protocols/Devices | Data Unit |
|-------|------|----------|-------------------|-----------|
| **7** | Application | User interface, network services | HTTP, FTP, SMTP, DNS | Data |
| **6** | Presentation | Data translation, encryption, compression | SSL/TLS, JPEG, MPEG | Data |
| **5** | Session | Session management, synchronization | NetBIOS, PPTP, RPC | Data |
| **4** | Transport | End-to-end communication, reliability | TCP, UDP | Segment (TCP) / Datagram (UDP) |
| **3** | Network | Routing, logical addressing | IP, ICMP, Routers | Packet |
| **2** | Data Link | Node-to-node transfer, MAC addressing | Ethernet, Wi-Fi, Switches | Frame |
| **1** | Physical | Bit transmission, physical medium | Cables, Hubs, NICs | Bits |

---

### **Troubleshooting with OSI Model**

The OSI model provides a systematic approach to troubleshooting:

**Start from Layer 1 and work up**:

```
Problem: Can't access a website

Layer 1 (Physical):
- Is cable plugged in?
- Is Wi-Fi enabled?
- Check: Link lights on NIC

Layer 2 (Data Link):
- Can you ping local gateway?
- Check: ARP table, MAC address

Layer 3 (Network):
- Can you ping external IP (e.g., 8.8.8.8)?
- Check: Routing table, IP configuration

Layer 4 (Transport):
- Is service listening on correct port?
- Check: Firewall rules, port availability

Layer 5-6-7 (Upper Layers):
- Is DNS resolving correctly?
- Is application configured properly?
- Check: Application logs, certificate validity
```

---

### **OSI Model Key Takeaways**

1. **Layered Architecture**: Modular design, each layer has specific responsibilities
2. **Abstraction**: Each layer provides services to layer above, uses services from layer below
3. **Encapsulation**: Each layer adds its own header (and sometimes trailer)
4. **Independence**: Changes in one layer don't affect others
5. **Troubleshooting Framework**: Start from Physical layer, work up
6. **Not Always Strictly Followed**: Real protocols don't always fit neatly into OSI layers
7. **Reference Model**: OSI is a conceptual model; TCP/IP is what's actually used

---

## 13. TCP/IP Model (5 Layers)

### The Practical Internet Protocol Suite

While OSI is a conceptual model, the **TCP/IP model** is the actual protocol suite that powers the Internet. It's simpler, with fewer layers, and is what's implemented in real systems.

---

### **TCP/IP vs OSI**

```
OSI Model (7 Layers)         TCP/IP Model (4/5 Layers)
---------------------        --------------------------
Application                  Application
Presentation         }  ───> (Combined into one layer)
Session             }       

Transport            ───────> Transport

Network              ───────> Internet

Data Link           }  ───> Network Access (or split into
Physical            }        Data Link + Physical = 5 layers)
```

**Key Differences**:
- **OSI**: Theoretical model (developed before implementation)
- **TCP/IP**: Practical model (developed alongside actual protocols)
- **OSI**: 7 layers
- **TCP/IP**: 4 or 5 layers (depending on whether you split Network Access)

---

### **The 5-Layer TCP/IP Model**

We'll use the 5-layer version (Physical and Data Link separated) as it's clearer:

```
Layer 5: Application Layer
Layer 4: Transport Layer
Layer 3: Internet Layer
Layer 2: Data Link Layer
Layer 1: Physical Layer
```

---

### **Layer 1: Physical Layer**

(Same as OSI Layer 1 - covered earlier)

**Responsibility**: Transmission of raw bits

**Technologies**:
- Ethernet cables
- Fiber optics
- Wi-Fi radio waves
- Cellular signals

---

### **Layer 2: Data Link Layer**

(Same as OSI Layer 2 - covered earlier)

**Responsibility**: Node-to-node data transfer

**Technologies**:
- Ethernet (802.3)
- Wi-Fi (802.11)
- PPP (Point-to-Point Protocol)

**Key Protocol**: Ethernet
- MAC addressing
- Frame format
- CSMA/CD (wired) / CSMA/CA (wireless)

---

### **Layer 3: Internet Layer**

*Note: Called "Internet" layer in TCP/IP vs "Network" layer in OSI*

#### **Core Protocol: IP (Internet Protocol)**

**Versions**:
- **IPv4**: 32-bit addresses, 4.3 billion addresses
- **IPv6**: 128-bit addresses, 340 undecillion addresses

**Responsibilities**:
- Logical addressing (IP addresses)
- Routing packets across networks
- Fragmentation and reassembly

#### **Important Protocols**:

**1. IP (Internet Protocol)**:
- Core protocol for packet delivery
- Best-effort delivery (unreliable)
- Connectionless

**2. ICMP (Internet Control Message Protocol)**:
- Error reporting and diagnostics
- Used by ping, traceroute
- Messages: Echo Request/Reply, Destination Unreachable, Time Exceeded

**Example**: Ping
```bash
$ ping google.com
PING google.com (142.250.80.46): 56 data bytes
64 bytes from 142.250.80.46: icmp_seq=0 ttl=117 time=10.5 ms
64 bytes from 142.250.80.46: icmp_seq=1 ttl=117 time=10.3 ms
```
- Uses ICMP Echo Request/Reply
- Measures round-trip time
- Checks connectivity

**3. ARP (Address Resolution Protocol)**:
- Maps IP addresses to MAC addresses
- Essential for local network communication

**Example**: ARP Resolution
```
Computer A (192.168.1.10) wants to send to Computer B (192.168.1.20)

1. A checks ARP cache for B's MAC address
2. If not found, broadcasts ARP request:
   "Who has 192.168.1.20? Tell 192.168.1.10"
3. Computer B responds:
   "192.168.1.20 is at MAC AA:BB:CC:DD:EE:FF"
4. A caches this mapping
5. A can now send Ethernet frame to B's MAC address
```

**4. IGMP (Internet Group Management Protocol)**:
- Manages multicast group membership
- Used for streaming to multiple recipients

#### **IP Packet Structure**:
```
IPv4 Header (20+ bytes):
├─ Version (4)
├─ Header Length
├─ Type of Service (QoS)
├─ Total Length
├─ Identification (for fragmentation)
├─ Flags & Fragment Offset
├─ Time to Live (TTL)
├─ Protocol (TCP=6, UDP=17, ICMP=1)
├─ Header Checksum
├─ Source IP Address
├─ Destination IP Address
├─ Options (if any)
└─ Payload (from Transport Layer)
```

**TTL (Time To Live)**:
- Prevents infinite loops
- Decremented by each router
- When 0 → packet discarded, ICMP Time Exceeded sent back
- Used by traceroute to map network path

#### **IP Routing Example**:
```
Computer (192.168.1.10) → Web Server (203.0.113.50)

Routing decision at Computer:
1. Compare destination (203.0.113.50) with own network (192.168.1.0/24)
2. Different network → send to default gateway (192.168.1.1)

Gateway Router:
1. Receives packet
2. Decrements TTL (255 → 254)
3. Checks routing table:
   - Route to 203.0.113.0/24: Next hop = 10.20.30.40
4. Forwards packet to next router

(Repeats through multiple routers until reaching destination network)
```

---

### **Layer 4: Transport Layer**

*(Similar to OSI Layer 4, but let's go deeper)*

#### **Two Main Protocols: TCP and UDP**

---

#### **TCP (Transmission Control Protocol) - Deep Dive**

**Characteristics**:
- Connection-oriented
- Reliable
- Ordered
- Error-checked
- Flow controlled
- Congestion controlled

**TCP Segment Structure**:
```
TCP Header (20+ bytes):
├─ Source Port (16 bits)
├─ Destination Port (16 bits)
├─ Sequence Number (32 bits)
├─ Acknowledgment Number (32 bits)
├─ Data Offset (4 bits)
├─ Reserved (3 bits)
├─ Flags (9 bits):
│  ├─ NS, CWR, ECE
│  ├─ URG (Urgent)
│  ├─ ACK (Acknowledgment)
│  ├─ PSH (Push)
│  ├─ RST (Reset)
│  ├─ SYN (Synchronize)
│  └─ FIN (Finish)
├─ Window Size (16 bits) - Flow control
├─ Checksum (16 bits)
├─ Urgent Pointer (16 bits)
├─ Options (if any)
└─ Data (Payload)
```

**TCP Connection Establishment: Three-Way Handshake (Detailed)**:
```
Client                                    Server
  │                                         │
  │────── SYN ───────────────────────────>│
  │  (Seq=1000, Flags=SYN)                 │
  │  "I want to connect, my seq starts at 1000"
  │                                         │
  │<───── SYN-ACK ─────────────────────────│
  │  (Seq=5000, Ack=1001, Flags=SYN+ACK)   │
  │  "OK, I'll connect. My seq starts at 5000.
  │   I received your 1000, expecting 1001 next"
  │                                         │
  │────── ACK ───────────────────────────>│
  │  (Seq=1001, Ack=5001, Flags=ACK)       │
  │  "Acknowledged. I received your 5000,
  │   expecting 5001 next"
  │                                         │
  │ ════════ Connection Established ═══════ │
```

**Why Three-Way? (Not Two-Way)**:
- Prevents old duplicate connections from causing confusion
- Both sides confirm each other's initial sequence numbers
- Prevents half-open connections

**TCP Data Transfer**:
```
Client sends data:

Segment 1: Seq=1001, 1000 bytes of data
Server ACKs: Ack=2001 (I received up to 2000, want 2001 next)

Segment 2: Seq=2001, 1000 bytes of data
Server ACKs: Ack=3001

Segment 3: Seq=3001, 1000 bytes of data
(Gets lost in network)

Timeout! Client retransmits Segment 3
Server ACKs: Ack=4001

All data received successfully
```

**TCP Flow Control (Sliding Window)**:
```
Receiver's advertised window = 4000 bytes

Sender can send up to 4000 bytes without waiting for ACK:
│◄────────── 4000 bytes ──────────►│
Seq=1001                      Seq=5001

After ACK received for first 2000:
  Window slides forward:
          │◄────────── 4000 bytes ──────────►│
     Seq=3001                          Seq=7001

Prevents sender from overwhelming receiver
```

**TCP Congestion Control**:

1. **Slow Start**:
   - Begin with small congestion window (e.g., 1 MSS)
   - Double window size with each ACK (exponential growth)
   - Continue until threshold reached

2. **Congestion Avoidance**:
   - After threshold, increase linearly
   - Add 1 MSS per RTT

3. **Fast Retransmit**:
   - If 3 duplicate ACKs received → immediately retransmit
   - Don't wait for timeout

4. **Fast Recovery**:
   - After fast retransmit, don't go back to slow start
   - Cut window in half, continue from there

**TCP Connection Termination: Four-Way Handshake**:
```
Client                                    Server
  │                                         │
  │────── FIN ───────────────────────────>│
  │  (Seq=5000, Flags=FIN)                 │
  │  "I'm done sending data"               │
  │                                         │
  │<───── ACK ────────────────────────────│
  │  (Ack=5001)                            │
  │  "OK, I acknowledge your FIN"          │
  │                                         │
  │<───── FIN ────────────────────────────│
  │  (Seq=9000, Flags=FIN)                 │
  │  "I'm also done sending data"          │
  │                                         │
  │────── ACK ───────────────────────────>│
  │  (Ack=9001)                            │
  │  "OK, I acknowledge your FIN"          │
  │                                         │
  │ ═════ Connection Fully Closed ════════ │
```

**Why Four-Way?**:
- Full-duplex connection (two independent directions)
- Each side must close its direction separately
- TIME_WAIT state ensures all packets cleared from network

---

#### **UDP (User Datagram Protocol) - Deep Dive**

**Characteristics**:
- Connectionless
- Unreliable (no guarantees)
- No ordering
- Minimal error checking
- No flow/congestion control
- Fast and lightweight

**UDP Datagram Structure**:
```
UDP Header (8 bytes only!):
├─ Source Port (16 bits)
├─ Destination Port (16 bits)
├─ Length (16 bits)
├─ Checksum (16 bits)
└─ Data (Payload)

Much simpler than TCP's 20+ byte header
```

**When to Use UDP**:

1. **Real-Time Applications**:
   - VoIP (voice calls)
   - Video streaming
   - Online gaming
   - Why: Low latency more important than reliability

2. **Simple Query-Response**:
   - DNS lookups
   - DHCP
   - Why: Overhead of TCP connection not worth it

3. **Broadcast/Multicast**:
   - Network discovery
   - Why: TCP doesn't support broadcast

4. **Custom Reliability**:
   - QUIC protocol (used by HTTP/3)
   - Why: Implement custom reliability on top of UDP

**UDP Example: DNS Lookup**:
```
Client sends UDP datagram to DNS server:
- Source Port: 54321 (ephemeral)
- Dest Port: 53 (DNS)
- Query: "What's the IP for google.com?"

Server responds with UDP datagram:
- Source Port: 53
- Dest Port: 54321
- Response: "142.250.80.46"

If no response → client retries (application-level)
Single datagram exchange for most queries
```

---

#### **Port Numbers (Revisited)**

**Well-Known Ports** (0-1023):
- Standardized services
- Require privileged access to bind

**Registered Ports** (1024-49151):
- Registered applications
- No special privileges needed

**Dynamic/Ephemeral Ports** (49152-65535):
- Temporary ports for client connections
- OS assigns randomly

**Java Example**:
```java
// Server binds to well-known port
ServerSocket server = new ServerSocket(8080);  // Port 8080
Socket client = server.accept();

// Check client's ephemeral port
int clientPort = client.getPort();  // e.g., 54321

// Check local port (should be 8080)
int localPort = client.getLocalPort();  // 8080
```

---

### **Layer 5: Application Layer**

*(Combines OSI Layers 5, 6, 7)*

This layer includes all protocols that applications use:

#### **HTTP (Hypertext Transfer Protocol)**

**Request-Response Protocol**:
```
HTTP Request:
GET /index.html HTTP/1.1
Host: www.example.com
User-Agent: Mozilla/5.0
Accept: text/html

HTTP Response:
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 1234

<!DOCTYPE html>
<html>...
```

**HTTP Methods**:
- **GET**: Retrieve resource
- **POST**: Submit data
- **PUT**: Update resource
- **DELETE**: Remove resource
- **HEAD**: Get headers only
- **OPTIONS**: Query supported methods

**HTTP Status Codes**:
- **1xx**: Informational
- **2xx**: Success (200 OK, 201 Created)
- **3xx**: Redirection (301 Moved Permanently, 302 Found)
- **4xx**: Client Error (400 Bad Request, 404 Not Found, 401 Unauthorized)
- **5xx**: Server Error (500 Internal Server Error, 503 Service Unavailable)

#### **HTTPS (HTTP Secure)**

HTTP over TLS/SSL:
```
1. TCP Three-Way Handshake
2. TLS Handshake:
   - Client Hello (supported cipher suites)
   - Server Hello (chosen cipher, certificate)
   - Client verifies certificate
   - Key exchange (establish session keys)
   - Finished messages
3. Encrypted HTTP communication
4. TLS Close Alert
5. TCP Connection Close
```

**Java Example**:
```java
// HTTP
URL url = new URL("http://example.com");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
int responseCode = conn.getResponseCode();

// HTTPS (automatically handles TLS)
URL secureUrl = new URL("https://example.com");
HttpsURLConnection secureConn = (HttpsURLConnection) secureUrl.openConnection();
// TLS handshake happens automatically
```

---

### **Complete Data Flow Through TCP/IP Stack**

**Sending Data**:
```
Application Layer:
│  HTTP GET request
│  ↓
│  [HTTP Header | Data]
│
Transport Layer:
│  TCP adds segment header
│  ↓
│  [TCP Header | HTTP Header | Data]
│
Internet Layer:
│  IP adds packet header
│  ↓
│  [IP Header | TCP Header | HTTP Header | Data]
│
Data Link Layer:
│  Ethernet adds frame header and trailer
│  ↓
│  [Eth Header | IP Header | TCP Header | HTTP Header | Data | CRC]
│
Physical Layer:
│  Convert to electrical/optical/radio signals
│  ↓
│  Bits transmitted over medium
```

**Receiving Data** (reverse process):
```
Physical Layer: Receive signals → convert to bits
   ↓
Data Link Layer: Remove Ethernet header, verify CRC
   ↓
Internet Layer: Remove IP header, check destination IP
   ↓
Transport Layer: Remove TCP header, reassemble, send to correct port
   ↓
Application Layer: Process HTTP request, generate response
```

---

### **TCP/IP Model Key Takeaways**

1. **Practical Model**: Actually implemented in real systems (unlike theoretical OSI)
2. **Internet Protocol Suite**: Collection of protocols (TCP, IP, HTTP, etc.)
3. **Layered Architecture**: But fewer, more practical layers than OSI
4. **TCP vs UDP**: Choose based on application requirements
5. **Best-Effort Network**: IP provides unreliable delivery; TCP adds reliability on top
6. **Addressing Hierarchy**: MAC (Layer 2), IP (Layer 3), Port (Layer 4)
7. **Encapsulation**: Each layer adds its own header

---

## 14. Client-Server Architecture (Detailed)

### Deep Dive into the Most Common Network Model

We covered basics earlier; now let's explore advanced concepts.

---

### **Client-Server Paradigm**

**Fundamental Asymmetry**:
- **Servers**: Passive entities (wait for requests)
- **Clients**: Active entities (initiate requests)

**Communication Pattern**:
```
Client (Active):
├─ Initiates connection
├─ Sends requests
└─ Waits for responses

Server (Passive):
├─ Listens for connections
├─ Accepts connections
├─ Processes requests
└─ Sends responses
```

---

### **Server Types**

#### **1. Iterative Server**

Handles ONE client at a time:
```
Server:
while (true) {
    Connection conn = accept();
    handleRequest(conn);  // Blocks here until done
    conn.close();
}

Problem: Next client must wait until current client is served
```

**Use Cases**:
- Very simple services
- Fast request processing
- Low traffic

**Limitations**:
- Poor scalability
- Long requests block others

#### **2. Concurrent Server**

Handles MULTIPLE clients simultaneously:

**Approach A: Multi-Threading**
```java
ServerSocket server = new ServerSocket(8080);

while (true) {
    Socket client = server.accept();
    
    // Spawn new thread for each client
    new Thread(() -> {
        handleClient(client);
    }).start();
}
```

**Approach B: Thread Pool**
```java
ExecutorService threadPool = Executors.newFixedThreadPool(100);
ServerSocket server = new ServerSocket(8080);

while (true) {
    Socket client = server.accept();
    
    // Submit to thread pool
    threadPool.submit(() -> {
        handleClient(client);
    });
}
```

**Approach C: Async I/O (Non-Blocking)**
```java
Selector selector = Selector.open();
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false);
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();  // Blocks until events
    Set<SelectionKey> keys = selector.selectedKeys();
    
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
            // Accept new client
        } else if (key.isReadable()) {
            // Read from client
        }
    }
}

// Single thread handles thousands of clients!
```

---

### **Stateless vs Stateful Servers**

#### **Stateless Server**

Server doesn't remember previous requests:
```
HTTP Server (Stateless):

Request 1: GET /page1
Server: [looks up page1, sends it]
Server forgets everything about this client

Request 2: GET /page2
Server: [has no memory of Request 1]
Server: [looks up page2, sends it]
```

**Advantages**:
- Scalability (no state to maintain)
- Load balancing easy (any server can handle any request)
- Fault-tolerant (server crash doesn't lose client state)

**Disadvantages**:
- Must include all context in each request
- Can't remember user preferences
- Inefficient for some operations

**Making HTTP "Stateful"**:
- Cookies
- Session tokens
- JWT (JSON Web Tokens)

#### **Stateful Server**

Server maintains client state across requests:
```
FTP Server (Stateful):

Request 1: USER john
Server: Remembers john is logged in

Request 2: CWD /documents
Server: Changes john's current directory to /documents
Server: Remembers this directory for john

Request 3: LIST
Server: Lists files in john's current directory (/documents)
```

**Advantages**:
- More efficient (don't resend context)
- Richer interactions
- Can maintain complex workflows

**Disadvantages**:
- Harder to scale (state stored on specific server)
- Load balancing complex (must route same client to same server)
- Server crash loses client state

---

### **Server Architectures**

#### **1. Single-Server Architecture**

Simplest form:
```
[Clients] ───────> [Single Server]
```

**Pros**: Simple, cheap
**Cons**: Single point of failure, limited scalability

---

#### **2. Load-Balanced Architecture**

Multiple identical servers behind load balancer:
```
                [Load Balancer]
                /      |      \
            [Server] [Server] [Server]
```

**Load Balancing Algorithms**:
- **Round Robin**: Distribute sequentially
- **Least Connections**: Send to server with fewest active connections
- **Least Response Time**: Send to fastest server
- **IP Hash**: Same client always goes to same server (for stateful apps)

**Java Load Balancer Example** (Simplified):
```java
class LoadBalancer {
    private List<Server> servers;
    private int currentIndex = 0;
    
    public Server getNextServer() {
        // Round-robin
        Server server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }
}

// Client request arrives
Server targetServer = loadBalancer.getNextServer();
targetServer.handleRequest(request);
```

---

#### **3. Multi-Tier Architecture**

Separates concerns into tiers:

**Three-Tier Architecture**:
```
Presentation Tier (Frontend):
    [Web Server / UI]
    ↓
Application Tier (Business Logic):
    [Application Server]
    ↓
Data Tier (Database):
    [Database Server]
```

**Example: E-Commerce Website**
```
User's Browser
    ↓ HTTP
Web Server (Nginx, Apache)
    ↓ HTTP/REST API
Application Server (Java Spring, Node.js)
    ├─ Business logic (pricing, inventory)
    ├─ Authentication
    └─ Payment processing
    ↓ SQL/Database Protocol
Database (PostgreSQL, MySQL)
    └─ User data, products, orders
```

**Advantages**:
- **Separation of Concerns**: Each tier has specific responsibility
- **Scalability**: Scale each tier independently
- **Maintainability**: Easier to update one tier without affecting others
- **Security**: Database not directly exposed to Internet

---

### **Caching in Client-Server**

Caching improves performance at multiple levels:

**1. Browser Cache** (Client-side):
```
First visit to website:
Browser → Server: GET /style.css
Server → Browser: [CSS file + Cache-Control: max-age=86400]

Next visit (within 24 hours):
Browser: Uses cached CSS, doesn't request from server
```

**2. CDN Cache**:
```
User in India requests image:
Client → CDN Node (Mumbai) → Origin Server (US)
CDN caches image

Next user in India:
Client → CDN Node (Mumbai) → [serves from cache]
(No trip to US!)
```

**3. Application Cache**:
```
Database query: "SELECT * FROM products"
Result cached in Redis for 5 minutes

Next request: Served from Redis (milliseconds)
vs Database query (hundreds of milliseconds)
```

**Java Caching Example**:
```java
// Simple in-memory cache
class Cache {
    private Map<String, Object> cache = new ConcurrentHashMap<>();
    private Map<String, Long> timestamps = new ConcurrentHashMap<>();
    private static final long TTL = 60_000; // 1 minute
    
    public Object get(String key) {
        if (timestamps.containsKey(key)) {
            long age = System.currentTimeMillis() - timestamps.get(key);
            if (age < TTL) {
                return cache.get(key);  // Cache hit
            }
        }
        return null;  // Cache miss
    }
    
    public void put(String key, Object value) {
        cache.put(key, value);
        timestamps.put(key, System.currentTimeMillis());
    }
}

// Usage
Object data = cache.get("user:123");
if (data == null) {
    data = database.query("SELECT * FROM users WHERE id=123");
    cache.put("user:123", data);
}
return data;
```

---

### **API Design in Client-Server**

Modern client-server communication often uses APIs:

#### **REST (Representational State Transfer)**

**Principles**:
- Stateless
- Resource-based URIs
- HTTP methods map to CRUD operations
- JSON/XML payloads

**Example REST API**:
```
GET /users           → List all users
GET /users/123       → Get user 123
POST /users          → Create new user
PUT /users/123       → Update user 123
DELETE /users/123    → Delete user 123
```

**Java REST Client**:
```java
// HTTP GET
URL url = new URL("https://api.example.com/users/123");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");

int responseCode = conn.getResponseCode();
if (responseCode == 200) {
    BufferedReader in = new BufferedReader(
        new InputStreamReader(conn.getInputStream()));
    String response = in.readLine();
    // Parse JSON response
}
```

#### **GraphQL**

Alternative to REST:
- Single endpoint
- Client specifies exactly what data it needs
- Reduces over-fetching

```graphql
query {
  user(id: "123") {
    name
    email
    posts {
      title
      date
    }
  }
}
```

#### **gRPC**

Google's RPC framework:
- Uses Protocol Buffers (binary format)
- HTTP/2 based
- Strongly typed
- Faster than REST

---

## 15. Peer-to-Peer Architecture

### Decentralized Network Model

Unlike client-server, P2P networks have **no central authority**. Every node is both client and server.

---

### **P2P Characteristics**

```
Client-Server:          Peer-to-Peer:
    [Server]           [Peer]  [Peer]
    /  |  \             /  \    /  \
[C] [C] [C] [C]      [Peer] [Peer] [Peer]
                       \  /    \  /
                      [Peer]  [Peer]
                      
All equal participants (peers)
```

**Key Features**:
- **Decentralized**: No single point of control/failure
- **Symmetric Roles**: Every peer can be client AND server
- **Self-Organizing**: Network forms dynamically
- **Scalable**: More peers = more resources

---

### **P2P Types**

#### **1. Pure P2P (Unstructured)**

No central coordination:
```
Each peer connects to random other peers
Discovery through flooding/gossip

Example: Early Gnutella
- Search floods through network
- Every peer checks if it has file
- High network traffic
```

**Pros**: Fully decentralized, robust
**Cons**: Inefficient searches, high bandwidth

#### **2. Hybrid P2P (Semi-Centralized)**

Central server for coordination, P2P for data:
```
     [Index Server]
     /    |    \
  [Peer] [Peer] [Peer]
    \      |      /
     (Data transfer P2P)

Example: Original Napster
- Central server: file index/search
- File transfer: directly between peers
```

**Pros**: Efficient search, scalable data transfer
**Cons**: Central server is single point of failure

#### **3. Structured P2P (DHT)**

Distributed Hash Table organizes network:
```
DHT: Maps keys to peers

Key "Movie.mp4" → Hash → Peer #7
Any peer can route to Peer #7 in O(log N) hops

Examples: BitTorrent (Kademlia DHT), Chord
```

**Pros**: Efficient search (O(log N)), no flooding
**Cons**: More complex

---

### **P2P Use Cases**

#### **1. File Sharing**

**BitTorrent**:
- Break file into pieces
- Download different pieces from different peers
- Simultaneously upload to other peers
- Incentivizes sharing (tit-for-tat)

```
File: movie.mp4 (1 GB)
Split into 1000 pieces (1 MB each)

Peer A: Has pieces 1-200
Peer B: Has pieces 201-600
Peer C: Has pieces 401-800
Your computer: Downloads from all simultaneously

As you download pieces, you upload them to others
```

**Advantages**:
- Fast downloads (parallel from multiple sources)
- Scalable (more downloaders = more uploaders)
- No server bandwidth cost

#### **2. Cryptocurrencies**

**Bitcoin**:
- Decentralized ledger (blockchain)
- Every peer has copy of all transactions
- Consensus through mining/proof-of-work

```
[Peer] ← Transaction
  ↓
Broadcasts to network
  ↓
All peers validate transaction
  ↓
Added to blockchain
```

**Advantages**:
- No central authority
- Transparent
- Resistant to censorship

#### **3. Messaging**

**Examples**:
- Tox (encrypted P2P messaging)
- BitMessage

**Advantages**:
- No server can read messages
- No central point to shut down
- Privacy-focused

#### **4. Content Delivery**

**Peerflix, WebTorrent**:
- Video streaming P2P
- Reduces server load
- Viewers help distribute content

#### **5. Distributed Computing**

**SETI@home, Folding@home**:
- Harness idle CPU cycles of millions of computers
- Solve computationally intensive problems

---

### **P2P Challenges**

**1. Discovery**:
- How do peers find each other?
- Solutions: Bootstrap nodes, DHT, tracker servers

**2. NAT Traversal**:
- Most peers behind NAT/firewall
- Can't accept incoming connections directly
- Solutions: STUN, TURN, UPnP, NAT hole punching

**3. Trust**:
- No central authority to verify identities
- Malicious peers can distribute fake/corrupt data
- Solutions: Reputation systems, cryptographic verification

**4. Free Riding**:
- Peers download but don't upload (leech)
- Solutions: Tit-for-tat, reputation, throttling leechers

**5. Performance**:
- Peer availability varies (peers go offline)
- Network paths between peers suboptimal

---

### **P2P vs Client-Server Comparison**

| Aspect | Client-Server | P2P |
|--------|---------------|-----|
| **Control** | Centralized | Decentralized |
| **Scalability** | Server bottleneck | Scales with peers |
| **Cost** | High server costs | Distributed costs |
| **Reliability** | Single point of failure | Robust (no single point) |
| **Performance** | Consistent | Variable (depends on peers) |
| **Management** | Easier | Complex |
| **Security** | Easier to secure | Harder (trust issues) |
| **Latency** | Predictable | Variable |

---

## 16. Networking Devices Deep Dive

### The Hardware that Makes Networks Work

We've mentioned some devices; now let's deep dive into each.

---

### **1. Hub**

**OSI Layer**: Layer 1 (Physical)

**Function**: Broadcast all received data to all ports

```
Port 1: [Computer A]
         ↓
      [Hub]  ← Receives frame from A
         ↓
Broadcasts to: Port 2, Port 3, Port 4, Port 5
```

**Characteristics**:
- **Dumb Device**: No intelligence
- **Half-Duplex**: Only one device can transmit at a time
- **Collision Domain**: All ports share bandwidth
- **Obsolete**: Replaced by switches

**Problems**:
- Security: All devices see all traffic
- Performance: Collisions frequent
- Bandwidth wasting: Sends to all even if not needed

**Historical Importance**: Used in early Ethernet (10BASE-T)

---

### **2. Switch**

**OSI Layer**: Layer 2 (Data Link)

**Function**: Forward frames based on MAC address

```
      [Switch]
       /  |  \
      /   |   \
   [PC] [PC] [PC]

MAC Table:
Port 1 → AA:BB:CC:DD:EE:01
Port 2 → AA:BB:CC:DD:EE:02
Port 3 → AA:BB:CC:DD:EE:03
```

**How It Works**:
1. **Learning**: Switch learns MAC addresses by examining source MAC of incoming frames
2. **Forwarding**: When frame arrives:
   - Look up destination MAC in table
   - Forward ONLY to that port
   - If MAC unknown, flood to all ports (learning opportunity)
3. **Aging**: Old entries removed after timeout (default: 300 seconds)

**Example**:
```
PC A (MAC: AA:AA) sends to PC B (MAC: BB:BB)

Switch receives frame on Port 1:
1. Learns: Port 1 = AA:AA
2. Checks destination MAC (BB:BB)
3. Looks in table: Port 2 = BB:BB
4. Forwards frame ONLY to Port 2

PC B receives frame
PCs C, D, E don't see it (unlike hub)
```

**Advantages over Hub**:
- **Full-Duplex**: Simultaneous send/receive
- **No Collisions**: Each port is separate collision domain
- **Better Performance**: Dedicated bandwidth per port
- **Security**: Devices don't see other devices' traffic

**Types of Switches**:
- **Unmanaged**: Plug-and-play, no configuration
- **Managed**: Configurable (VLANs, QoS, port mirroring)
- **Layer 3 Switch**: Can route between VLANs

---

### **3. Router**

**OSI Layer**: Layer 3 (Network)

**Function**: Forward packets between networks based on IP address

```
Network A (192.168.1.0/24)
         |
      [Router]
         |
Network B (192.168.2.0/24)
```

**Routing Table**:
```
Destination      Gateway         Interface    Metric
192.168.1.0/24   Connected       eth0         0
192.168.2.0/24   Connected       eth1         0
0.0.0.0/0        203.0.113.1     eth2         1  (Default route)
```

**Routing Decision Process**:
```
1. Packet arrives: Destination = 192.168.2.10
2. Router examines destination IP
3. Consults routing table
4. Finds match: 192.168.2.0/24 via eth1
5. Decrements TTL
6. Recalculates IP checksum
7. Forwards packet out eth1
```

**Router Functions**:
1. **Packet Forwarding**: Send packets to next hop
2. **NAT**: Translate private IPs to public IP
3. **DHCP Server**: Assign IP addresses
4. **Firewall**: Filter traffic
5. **QoS**: Prioritize certain traffic

**NAT (Network Address Translation)**:
```
Private Network (192.168.1.0/24):
  ├─ PC A: 192.168.1.10
  ├─ PC B: 192.168.1.11
  └─ PC C: 192.168.1.12

Router (Public IP: 203.0.113.5):
  NAT Table:
  192.168.1.10:54321 ↔ 203.0.113.5:10001
  192.168.1.11:54322 ↔ 203.0.113.5:10002
  192.168.1.12:54323 ↔ 203.0.113.5:10003

Internet sees all traffic as coming from 203.0.113.5
```

**Routing Protocols**:
- **Static Routing**: Manually configured routes
- **Dynamic Routing**: Routes learned automatically
  - **RIP**: Distance-vector (hop count)
  - **OSPF**: Link-state (cost-based)
  - **BGP**: Path-vector (for Internet backbone)

---

### **4. Bridge**

**OSI Layer**: Layer 2 (Data Link)

**Function**: Connect two LAN segments, filter traffic

```
Segment A ───[Bridge]─── Segment B
```

**Usage**: Mostly obsolete (switches do same job better)

---

### **5. Gateway**

**OSI Layer**: Layer 7 (Application) or multiple layers

**Function**: Translate between different protocols/networks

**Example**: Email gateway converting between different email systems

**Default Gateway**: Router that connects local network to outside world

---

### **6. Firewall**

**OSI Layer**: Multiple (typically Layer 3-4, can go to Layer 7)

**Function**: Filter network traffic based on rules

**Types**:

**1. Packet Filter Firewall** (Layer 3-4):
```
Rules:
ALLOW: Source=192.168.1.0/24, Dest=Any, Port=80 (HTTP)
ALLOW: Source=192.168.1.0/24, Dest=Any, Port=443 (HTTPS)
DENY:  Source=Any, Dest=192.168.1.10, Port=22 (SSH)
ALLOW: Source=Any, Dest=192.168.1.100, Port=80 (Web server)
DEFAULT: DENY
```

**2. Stateful Firewall**:
- Tracks connection state
- Allows return traffic for established connections
- Blocks unsolicited incoming connections

```
Outgoing: PC initiates connection to Google:443 → ALLOW
Incoming: Response from Google:443 → ALLOW (part of established connection)
Incoming: Random connection attempt from Internet → DENY (no established connection)
```

**3. Application Layer Firewall** (Layer 7):
- Inspects application data
- Can block specific URLs, file types
- Deep packet inspection

**Example**: Block all .exe downloads, allow .pdf

---

### **7. Access Point (AP)**

**OSI Layer**: Layer 2 (Data Link)

**Function**: Provides wireless connectivity to wired network

```
[Router] ───[Switch]───[Access Point]
                            )))
                         Wireless
                          Clients
```

**Features**:
- **SSID Broadcasting**: Network name
- **Security**: WPA2, WPA3 encryption
- **Multiple SSIDs**: Guest network, corporate network
- **Roaming**: Clients seamlessly switch between APs

---

### **8. Modem**

(Covered earlier in detail)

**Function**: Convert digital ↔ analog signals

---

### **9. Repeater**

**OSI Layer**: Layer 1 (Physical)

**Function**: Amplify and regenerate signals to extend distance

```
[Device A] ───(weak signal)───[Repeater]───(strong signal)───[Device B]
```

**Usage**: Extend cable length, Wi-Fi range

---

### **10. Load Balancer**

**OSI Layer**: Layer 4 or Layer 7

**Function**: Distribute traffic across multiple servers

```
          [Load Balancer]
           /     |     \
       [Web1] [Web2] [Web3]
```

**Algorithms**:
- Round Robin
- Least Connections
- IP Hash
- Weighted Round Robin

**Health Checks**: Regularly ping servers, remove unhealthy ones from pool

---

## 17. Protocols Deep Dive

### The Languages of Computer Networks

We've mentioned protocols throughout; let's compile comprehensive reference.

---

### **Application Layer Protocols**

#### **HTTP/HTTPS (Web)**

**Port**: 80 (HTTP), 443 (HTTPS)

**Request Methods**:
- GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH

**Status Codes**:
- 200 OK, 404 Not Found, 500 Internal Server Error, 301 Moved Permanently

**Headers**:
```
Request Headers:
- Host: www.example.com
- User-Agent: Mozilla/5.0...
- Accept: text/html
- Cookie: session=abc123

Response Headers:
- Content-Type: text/html
- Content-Length: 1234
- Set-Cookie: session=abc123
- Cache-Control: max-age=3600
```

---

#### **FTP (File Transfer)**

**Ports**: 21 (control), 20 (data)

**Modes**:
- **Active**: Server initiates data connection
- **Passive**: Client initiates data connection (better for NAT/firewalls)

**Commands**:
```
USER username
PASS password
CWD /directory
LIST (list files)
RETR filename (download)
STOR filename (upload)
QUIT
```

---

#### **SMTP (Email Sending)**

**Port**: 25 (or 587 for submission)

**Example Session**:
```
Client: HELO mail.example.com
Server: 250 Hello

Client: MAIL FROM:<sender@example.com>
Server: 250 OK

Client: RCPT TO:<recipient@example.com>
Server: 250 OK

Client: DATA
Server: 354 Start mail input

Client: Subject: Test Email
Client: 
Client: This is the email body.
Client: .
Server: 250 OK: Message accepted

Client: QUIT
Server: 221 Bye
```

---

#### **DNS (Domain Name System)**

**Port**: 53 (UDP for queries, TCP for zone transfers)

**Query Process**:
```
1. User types: www.example.com
2. Check local cache
3. If not cached, query DNS resolver
4. Recursive resolution:
   - Query root servers (.com)
   - Query TLD servers (example.com)
   - Query authoritative servers (www.example.com)
5. Return IP: 93.184.216.34
6. Cache result
```

**Record Types**:
- **A**: IPv4 address
- **AAAA**: IPv6 address
- **CNAME**: Alias to another domain
- **MX**: Mail server
- **NS**: Name server
- **TXT**: Text (often for verification)

---

### **Transport Layer Protocols**

**TCP** and **UDP** (covered earlier in depth)

---

### **Network Layer Protocols**

#### **IP (Internet Protocol)**

(Covered earlier)

---

#### **ICMP (Internet Control Message Protocol)**

**Purpose**: Error reporting and diagnostics

**Common Messages**:
- **Echo Request/Reply**: Used by ping
- **Destination Unreachable**: Cannot reach host/port
- **Time Exceeded**: TTL reached 0 (used by traceroute)
- **Redirect**: Better route available

**Ping Example**:
```bash
$ ping 8.8.8.8
PING 8.8.8.8: 56 data bytes
64 bytes from 8.8.8.8: icmp_seq=0 ttl=117 time=10.5 ms
```

Uses ICMP Echo Request/Reply

**Traceroute Example**:
```bash
$ traceroute google.com
1  192.168.1.1 (Router)  1ms
2  10.20.30.40 (ISP)  5ms
3  ...
```

Uses ICMP Time Exceeded messages (by intentionally setting low TTL)

---

#### **ARP (Address Resolution Protocol)**

**Purpose**: Map IP address to MAC address

**Process**:
```
PC needs to send to 192.168.1.20 but only knows IP

1. Check ARP cache
2. If not found, broadcast ARP request:
   "Who has 192.168.1.20? Tell 192.168.1.10"
3. Owner responds:
   "192.168.1.20 is at MAC AA:BB:CC:DD:EE:FF"
4. Cache this mapping
```

**ARP Table**:
```
IP Address       MAC Address           Type
192.168.1.1      00:11:22:33:44:55     dynamic
192.168.1.20     AA:BB:CC:DD:EE:FF     dynamic
```

---

### **Data Link Layer Protocols**

#### **Ethernet (802.3)**

**MAC Frame**:
```
[Preamble | Dest MAC | Source MAC | Type | Payload | CRC]
   7 bytes    6 bytes    6 bytes   2 bytes  46-1500   4 bytes
```

**CSMA/CD**: Carrier Sense Multiple Access with Collision Detection (wired)

---

#### **Wi-Fi (802.11)**

**Standards**:
- 802.11a (5 GHz, 54 Mbps)
- 802.11b (2.4 GHz, 11 Mbps)
- 802.11g (2.4 GHz, 54 Mbps)
- 802.11n (2.4/5 GHz, 600 Mbps)
- 802.11ac (5 GHz, 1+ Gbps)
- 802.11ax (Wi-Fi 6): 9.6 Gbps

**CSMA/CA**: Collision Avoidance (can't detect collisions in wireless)

---

## 18. Sockets Programming

### The API for Network Communication

Sockets are the interface between application and transport layer.

---

### **What is a Socket?**

A **socket** is an endpoint for network communication.

**Socket = IP Address + Port Number + Protocol**

Example:
```
TCP Socket: 192.168.1.10:8080 (TCP)
UDP Socket: 192.168.1.10:9000 (UDP)
```

---

### **Socket Types**

**1. Stream Sockets (TCP)**:
- Connection-oriented
- Reliable, ordered
- `SOCK_STREAM`

**2. Datagram Sockets (UDP)**:
- Connectionless
- Unreliable, unordered
- `SOCK_DGRAM`

**3. Raw Sockets**:
- Direct access to IP layer
- Used for custom protocols, network tools
- Requires root/admin privileges

---

### **Java Socket Programming**

#### **TCP Server**:
```java
public class TCPServer {
    public static void main(String[] args) throws IOException {
        // 1. Create server socket, bind to port
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");
        
        while (true) {
            // 2. Accept client connection (blocks until client connects)
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + 
                clientSocket.getInetAddress());
            
            // 3. Get input/output streams
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(
                clientSocket.getOutputStream(), true);
            
            // 4. Read client message
            String message = in.readLine();
            System.out.println("Received: " + message);
            
            // 5. Send response
            out.println("Echo: " + message);
            
            // 6. Close connection
            clientSocket.close();
        }
    }
}
```

#### **TCP Client**:
```java
public class TCPClient {
    public static void main(String[] args) throws IOException {
        // 1. Create socket, connect to server
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to server");
        
        // 2. Get input/output streams
        PrintWriter out = new PrintWriter(
            socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        
        // 3. Send message
        out.println("Hello, Server!");
        
        // 4. Receive response
        String response = in.readLine();
        System.out.println("Server response: " + response);
        
        // 5. Close connection
        socket.close();
    }
}
```

---

#### **UDP Server**:
```java
public class UDPServer {
    public static void main(String[] args) throws IOException {
        // 1. Create datagram socket, bind to port
        DatagramSocket socket = new DatagramSocket(9000);
        byte[] buffer = new byte[1024];
        
        System.out.println("UDP Server listening on port 9000...");
        
        while (true) {
            // 2. Receive datagram
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);  // Blocks until datagram received
            
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received: " + message);
            System.out.println("From: " + packet.getAddress() + ":" + packet.getPort());
            
            // 3. Send response
            String response = "Echo: " + message;
            byte[] responseData = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                responseData, responseData.length,
                packet.getAddress(), packet.getPort());
            socket.send(responsePacket);
        }
    }
}
```

#### **UDP Client**:
```java
public class UDPClient {
    public static void main(String[] args) throws IOException {
        // 1. Create datagram socket (no connection needed)
        DatagramSocket socket = new DatagramSocket();
        
        // 2. Prepare message
        String message = "Hello, UDP Server!";
        byte[] data = message.getBytes();
        
        // 3. Send datagram
        InetAddress serverAddress = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(
            data, data.length, serverAddress, 9000);
        socket.send(packet);
        
        // 4. Receive response
        byte[] buffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(responsePacket);
        
        String response = new String(
            responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println("Server response: " + response);
        
        // 5. Close socket
        socket.close();
    }
}
```

---

### **Multi-Threaded TCP Server**

Handle multiple clients concurrently:

```java
public class MultiThreadedServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server listening on port 8080...");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            
            // Spawn new thread for each client
            new Thread(() -> {
                try {
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    
    private static void handleClient(Socket socket) throws IOException {
        System.out.println("Handling client: " + socket.getInetAddress());
        
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
            socket.getOutputStream(), true);
        
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println("Received: " + message);
            out.println("Echo: " + message);
            
            if ("bye".equalsIgnoreCase(message)) {
                break;
            }
        }
        
        socket.close();
        System.out.println("Client disconnected: " + socket.getInetAddress());
    }
}
```

---

### **Socket Options**

```java
Socket socket = new Socket();

// Connection timeout
socket.connect(new InetSocketAddress("example.com", 8080), 5000);  // 5 sec

// Read timeout
socket.setSoTimeout(10000);  // 10 seconds

// Keep-alive (TCP)
socket.setKeepAlive(true);

// No delay (disable Nagle's algorithm - send small packets immediately)
socket.setTcpNoDelay(true);

// Reuse address (bind to recently used port)
serverSocket.setReuseAddress(true);
```

---

### **Common Socket Errors**

**1. Connection Refused**:
- Server not running
- Wrong port
- Firewall blocking

**2. Connection Timeout**:
- Server unreachable
- Network issues
- Wrong IP address

**3. Broken Pipe**:
- Writing to closed socket
- Other end disconnected

**4. Address Already in Use**:
- Port already bound by another process
- Solution: Use different port or setReuseAddress(true)

---

**End of Part 2**

---

This completes the comprehensive notes covering all timestamp topics. The notes include:

✅ Network Topologies (Bus, Ring, Star, Tree, Mesh)
✅ Structure of the Network (Tiers, AS, BGP)
✅ OSI Model (7 Layers) - Complete breakdown
✅ TCP/IP Model (5 Layers) - Detailed explanation
✅ Client-Server Architecture - Deep dive
✅ Peer-to-Peer Architecture
✅ Networking Devices - Full catalog
✅ Protocols - Comprehensive reference
✅ Sockets Programming - Practical Java examples

The notes are structured for:
- **Study and Review**: Clear sections, summaries
- **Interview Preparation**: Technical depth with practical examples
- **Java Context**: Real code examples throughout
- **Practical Application**: Real-world scenarios and troubleshooting

Would you like me to:
1. Create a consolidated single-file version?
2. Add more Java-specific networking examples?
3. Create practice questions/exercises?
4. Add more diagrams or visual representations?