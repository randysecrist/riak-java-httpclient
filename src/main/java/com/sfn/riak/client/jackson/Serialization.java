package com.sfn.riak.client.jackson;

import java.io.InputStream;
import java.io.OutputStream;

import com.sfn.riak.client.Document;

/**
 * Expresses the seralization concern of the Riak Json
 * client library.
 * 
 * Attempts to be as type-explicit as possible to reduce
 * the possibility of {@link RJSerializationErrors}.
 * 
 * @author Randy Secrist
 */
public interface Serialization {
  // writers
  String toJsonString(JsonSerializable object);
  void toOutputStream(JsonSerializable json, OutputStream stream);
    
  // readers
  <T extends Document> T fromDocumentJsonString(String document_as_json, Class<T> type);
  <T extends Document> T fromDocumentInputStream(InputStream document_stream, Class<T> type);
}