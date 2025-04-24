package com.cubes_and_mods.order;

public class ProtectedRequest<T> {
    T data;
    String alpha;

    public ProtectedRequest(T data, String token) {
        this.data = data;
        this.alpha = token;
    }
    public ProtectedRequest() {}
}
