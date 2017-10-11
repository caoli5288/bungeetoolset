package com.mengcraft.lobbybalancer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by on 10-11.
 */
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class Pair<K, V> {

    private final K key;
    private V value;
}
