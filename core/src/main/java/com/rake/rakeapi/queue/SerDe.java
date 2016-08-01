package com.rake.rakeapi.queue;

/**
 * Created by jl on 4/23/14.
 */
public interface SerDe<T> {
    T deserialize(byte[] payload);

    byte[] serialize(T payload);

    String toString(byte[] payload);
}