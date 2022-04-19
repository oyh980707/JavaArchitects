package com.loveoyh.utils;

import java.util.*;
import java.util.function.Predicate;

public class BatchUtils {

    public static interface Converter<M, N> {
        N convert(M m);
    }

    public static <M, N> List<N> convert(List<M> sources, Converter<M, N> action) {
        if (sources == null || sources.size() == 0) {
            return new ArrayList<N>();
        }

        List<N> resultList = new ArrayList<N>();
        for (M m : sources) {
            resultList.add(action.convert(m));
        }
        return resultList;
    }

    public static <M, N> List<N> convert(List<M> sources, Converter<M, N> action, Predicate<M> predicate) {
        if (sources == null || sources.size() == 0) {
            return new ArrayList<N>();
        }

        List<N> resultList = new ArrayList<N>();
        for (M m : sources) {
            if(predicate.test(m)){
                resultList.add(action.convert(m));
            }
        }
        return resultList;
    }

    public static <M, N> Set<N> convert(Set<M> sources, Converter<M, N> action) {
        if (sources == null || sources.size() == 0) {
            return new HashSet<N>();
        }

        Set<N> resultSet = new HashSet<N>();
        for (M m : sources) {
            resultSet.add(action.convert(m));
        }
        return resultSet;
    }

    public static<V,K> Map<K,V> convertToMap(List<V> values, Converter<V,K> converter){
        if (values == null || values.size() == 0) {
            return new HashMap<K,V>();
        }

        Map<K,V> map = new HashMap<K, V>();
        for(V v:values){
            map.put(converter.convert(v),v);
        }
        return map;
    }

    public static<V,K> Map<K,V> convertToMap(Set<V> values, Converter<V,K> converter){
        if (values == null || values.size() == 0) {
            return new HashMap<K,V>();
        }

        Map<K,V> map = new HashMap<K, V>();
        for(V v:values){
            map.put(converter.convert(v),v);
        }
        return map;
    }
}