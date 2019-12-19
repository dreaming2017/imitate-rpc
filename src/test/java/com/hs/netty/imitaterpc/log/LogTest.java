package com.hs.netty.imitaterpc.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
	private static Logger LOG = LoggerFactory.getLogger(LogTest.class);
	public static void main(String[] args) {
		LOG.info("11111");
		LOG.debug("2222");
		LOG.error("11111");
	}
}
