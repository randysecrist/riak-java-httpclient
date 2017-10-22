package com.sfn.riak.client;

import com.sfn.riak.client.jackson.JsonSerializable;

/**
 * 
 * @author Randy Secrist
 */
public interface Document extends JsonSerializable {

  /**
   * Returns the riak_kv location of the Document.
   * @return The location of the document.
   */
  Location getLocation();
  void setLocation(Location location);
}