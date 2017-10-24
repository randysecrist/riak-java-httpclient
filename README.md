# riak-java-httpclient
A simple HTTP client for Riak

## Purpose
Riak already has a fully featured Java client which uses protocol buffers.  Under most
circumstances that client should be used primarily.  However if a backup channel is
required (due to a few possible issues with the PB channel being stuck) this client can
be used to perform similar KV operations over HTTP.

In short, this is a fallback client which can be used when the PB channel doesn't work.

### Setup
```java
Client client = new Client("localhost", 8098)
```

### Getting Data In
```java
Namespace ns = new Namespace("test_bucket");
Location location = new Location(ns, "test_key");
client.put(location, "test_value");
```

### Getting Data Out
```java
client.find(location)
```

### Document Serialization