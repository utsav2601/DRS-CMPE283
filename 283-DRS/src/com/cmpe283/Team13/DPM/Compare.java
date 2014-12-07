package com.cmpe283.Team13.DPM;



import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Compare implements Comparator<Object> {

    Map<String, Long> map;

    public Compare(Map<String, Long> map) {
        this.map = map;
    }

    public int compare(Object o1, Object o2) {

        if (map.get(o2) == map.get(o1))
            return 1;
        else
            return ((Long) map.get(o2)).compareTo((Long)map.get(o1));

    }
	
}
