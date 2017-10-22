package com.sfn.riak.client;

/**
 * This transport inteface defines the The minimum set of
 * functionality needed by the client api to communicate
 * with Riak.
 * 
 * Functions are specific to the transport concern, but may
 * support inner level concerns such as serialization and
 * encryption.
 * 
 * @author Randy Secrist
 */
public interface Transport {

  /**
   * Return true if Riak's ping end point is contacted.
   * @return True if alive, false otherwise.
   */
  boolean ping();

  /**
   * Tests if riak_kv is up, deeper than ping.
   * @return True if riak_kv is up, false otherwise.
   */
  boolean pingKV();

  /**
   * Insert a document into Riak.
   * @return The key of the document.
   */
  String put(final Location location, final String string);
  String put(final Location location, final Document document);
  String put(final Document document);

  /**
   * Removes a document from Riak.
   * @param key The collection to remove it from.
   * @return True if the operation is a success, false otherwise.
   */
  boolean delete(final Location location);
  boolean delete(final Document document);

  /**
   * Locates a single document by key.
   * @param key The key of the document to locate.
   * @param type The expected concrete class type of the Document.
   * @return The document (as a java object) if found, null otherwise.
   */
  String findByLocation(final Location location);
  <T extends Document> T findByLocation(final Location location, final Class<T> type);
}