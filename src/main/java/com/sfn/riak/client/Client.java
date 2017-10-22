package com.sfn.riak.client;

import com.sfn.riak.client.transports.http.HttpTransport;
import com.sfn.riak.client.transports.http.Protocol;

/**
 * A client for the Riak API.  The client uses a transport
 * which abstracts the actual protocol used to communicate with
 * the Riak API.
 * 
 * @author Randy Secrist
 */
public class Client {
  
  private Transport transport;
  
  public Client(String host, int port) {
    super();
    init_internals(host, port);
  }

  public Client(String host, int port, Protocol protocol) {
    super();
    init_internals(host, port, protocol);
  }
  
  /**
   * Returns the version of the client library.
   * @return The version of the client library.
   */
  public String version() {
    return Version.getVersion();
  }
  
  /**
   * Returns true if the riak ping api returns 200.
   * False otherwise.
   * 
   * @return Boolean based on /ping == 200.
   */
  public boolean ping() {
    return transport.ping();
  }
  
  /**
   * A more detailed check than ping to check if riak_kv
   * is responding normally.
   * 
   * @return True if riak_kv responds normally, false otherwise.
   */
  public boolean pingKV() {
    return transport.pingKV();
  }
  public String put(final Location location, final String value) {
	return transport.put(location, value);
  }
  public String put(final Location location, final Document document) {
    return transport.put(location, document);
  }
  public String put(final Document document) {
	return transport.put(document);
  }
  
  public String find(Location location) {
	return transport.findByLocation(location);
  }
  public <T extends Document> T find(Location location, Class<T> type) {
	return transport.findByLocation(location, type);
  }
  
  public String toString() {
    return transport.toString();
  }
  
  // ---- Internals
  
  private void init_internals(String host, int port) {
    this.transport = new HttpTransport(host, port);
  }
  private void init_internals(String host, int port, Protocol protocol) {
    this.transport = new HttpTransport(host, port, protocol);
  }
 }