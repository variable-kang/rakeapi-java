package com.rake.rakeapi.queue;

/**
 * Created by jl on 4/23/14.
 */
public class StringSerDe implements SerDe<String> {
    @Override
    public String deserialize(byte[] payload) {
        return new String(payload);
    }

    @Override
    public byte[] serialize(String content) {
        return content.getBytes();
    }

    @Override
    public String toString(byte[] payload) {
        return new String(payload);
    }
}