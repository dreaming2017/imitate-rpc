package com.hs.netty.imitate.rpc.servicebean;

import java.util.Random;

public class CalculateImpl implements Calculate {
    //两数相加
    public Integer add(int a, int b) {
    	try {
			Thread.sleep(1000* new Random().nextInt(10));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return a + b;
    }
}
