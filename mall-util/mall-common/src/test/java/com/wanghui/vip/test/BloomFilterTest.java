package com.wanghui.vip.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.ArrayList;
import java.util.List;

public class BloomFilterTest {
    // Collection size
    private static int size = 1000000;

    // Google's Bloom Filter
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.01);

    public static void main(String[] args) {
        // Put one million keys into the Bloom filter
        for (int i = 0; i < size; i++) {
            bloomFilter.put(i);
        }

        List<Integer> list = new ArrayList<Integer>(1000);


        // Take 10000 values not in the filter to check false positives
        for (int i = size + 1; i < size + 20000; i++) {
            // Check if the Bloom filter might contain this data
            if (bloomFilter.mightContain(i)) {
                list.add(i);
            }
        }
        System.out.println("误判的数量：" + list.size());
    }
}
