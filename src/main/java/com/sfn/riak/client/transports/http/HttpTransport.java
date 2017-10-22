package com.sfn.riak.client.transports.http;

import static com.sfn.riak.client.transports.http.Method.DELETE;
import static com.sfn.riak.client.transports.http.Method.GET;
import static com.sfn.riak.client.transports.http.Method.POST;
import static com.sfn.riak.client.transports.http.Method.PUT;
import static com.sfn.riak.client.transports.http.Protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.sfn.riak.client.Document;
import com.sfn.riak.client.Location;
import com.sfn.riak.client.Transport;
import com.sfn.riak.client.errors.RJTransportError;
import com.sfn.riak.client.jackson.DefaultSerializer;
import com.sfn.riak.client.jackson.JsonSerializable;
import com.sfn.riak.client.jackson.Serialization;

/**
 * An HTTP Thing
 * @author Randy Secrist
 */
public class HttpTransport implements Transport {

  private Protocol protocol;
  private String host;
  private int port;
  private RestClient client;
  private Serialization serializer;

  public HttpTransport(String host, int port) {
    this(host, port, HTTP);
  }

  public HttpTransport(String host, int port, Protocol protocol) {
    super();
    this.protocol = protocol;
    this.host = host;
    this.port = port;
    setRestClient(new ApacheRestClient());
    setSerializer(new DefaultSerializer());
  }

  // Helper Funs
  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }

  public void setRestClient(RestClient client) {
    this.client = client;
  }

  public void setSerializer(Serialization serializer) {
    this.serializer = serializer;
  }

  public String getBaseRiakURL() {
    return protocol + "://" + host + ":" + port;
  }

  public String getRiakQueryString() {
    return "/types/%s/buckets/%s/keys/%s";
  }

  public String toString() {
    return getBaseRiakURL();
  }

  // API
  public boolean ping() {
    URI uri = this.buildURL(getBaseRiakURL(), "/ping");
    return (sendGetRequest(uri).status() == 200) ? true : false;
  }

  public boolean pingKV() {
    URI uri = this.buildURL(getBaseRiakURL(), "/buckets/not_a_bucket/keys/not_a_key");
    return (sendGetRequest(uri).status() == 404) ? true : false;
  }
  
  public String put(Location location, String value) {
	InputStream in = this.fillInputPipe(value);
    return this.put(location, in);
  }
  
  public String put(final Document document) {
    return this.put(document.getLocation(), document);
  }
  
  public String put(final Location location, final Document document) {
    InputStream in = this.fillInputPipe(document);
    String new_key = this.put(location, in);
    document.setLocation(new Location(location.getNamespace(), new_key));
    return new_key;
  }
    
  public boolean delete(final Document document) {
    return this.delete(document.getLocation());
  }

  public boolean delete(final Location location) {
    String type = location.getNamespace().getBucketTypeAsString();
    String bucket = location.getNamespace().getBucketNameAsString();
    String key = location.getKeyAsString();
    URI uri = this.buildURL(getBaseRiakURL(), String.format("/types/%s/buckets/%s/keys/%s", type, bucket, key));
    return (sendGetOrDelete(uri, DELETE).status() == 204) ? true : false;
  }

  public String findByLocation(final Location location) {
    String b_type = location.getNamespace().getBucketTypeAsString();
    String bucket = location.getNamespace().getBucketNameAsString();
    String key = location.getKeyAsString();
    URI uri = this.buildURL(getBaseRiakURL(), String.format("/types/%s/buckets/%s/keys/%s", b_type, bucket, key));
    return this.getBody(sendGetRequest(uri));
  }

  public <T extends Document> T findByLocation(final Location location, final Class<T> type) {
	String body = findByLocation(location);
    return (body != null) ? serializer.fromDocumentJsonString(body, type) : null;
  }
  
  /* PRIVATE FUNS */

  private URI buildURL(String base, String path) {
    try {
      URI uri = new URI(base + path);
      return uri;
    }
    catch (URISyntaxException e) {
      throw new RJTransportError("Invalid Riak URI", e);
    }
  }

  private Response sendGetRequest(URI uri) {
    return this.sendGetOrDelete(uri, GET);
  }

  private Response sendGetOrDelete(URI uri, Method method) {
    return client.sendGetOrDelete(uri, method);
  }

  private Response sendPostOrPut(URI uri, Method method, InputStream input) {
    return client.sendPostOrPut(uri, method, input);
  }
  
  private InputStream fillInputPipe(String rawstring) {
	PipedInputStream in = new PipedInputStream(4096);
	
	try (PipedOutputStream stream = new PipedOutputStream(in)) {
	  stream.write(rawstring.getBytes());
	}
	catch (IOException e) {
	  throw new RJTransportError("Unexpected IOException while setting up response pipe.", e);
	}
	return in;
  }

  private InputStream fillInputPipe(JsonSerializable json) {
    PipedInputStream in = new PipedInputStream(4096);
    
    try (PipedOutputStream stream = new PipedOutputStream(in)) {
      serializer.toOutputStream(json, stream);
    }
    catch (IOException e) {
      throw new RJTransportError("Unexpected IOException while setting up response pipe.", e);
    }
    return in;
  }

  private String getBody(Response response) {
    if (response.status() != 200)
      return null;
    return new String(response.body());
  }
  
  private String put(final Location location, InputStream in) {
    String key = location.getKeyAsString();
    if (key != null) {
    	  URI uri = getURI(location);
      Response response = sendPostOrPut(uri, PUT, in);
      return (response.status() == 204) ? key : new String(response.body());
    }
    else {
    	  URI uri = getURI(location);
      Response response = sendPostOrPut(uri, POST, in);
      String location_header = response.headers().get("Location");
      int key_location = location_header.lastIndexOf('/');
      String new_key = location_header.substring(key_location + 1, location_header.length());
      return (response.status() == 201) ? new_key : new String(response.body());
    }
  }
  
  private URI getURI(Location location) {
    URI uri = null;
    String type = location.getNamespace().getBucketTypeAsString();
    String bucket = location.getNamespace().getBucketNameAsString();
    String key = location.getKeyAsString();
    if (key != null) {
    	  uri = this.buildURL(getBaseRiakURL(), String.format("/types/%s/buckets/%s/keys/%s", type, bucket, key));
    }
    else {
      uri = this.buildURL(getBaseRiakURL(), String.format("/types/%s/buckets/%s/keys/", type, bucket));
    }
    return uri;
  }

}
