package com.baofeng.hbase.ads.redis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baofeng.util.ConfigFactory;
import com.baofeng.util.ConfigProperties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisRead {
	static final Log LOG = LogFactory.getLog(RedisRead.class);

	public static final String NAME = "Redis-Bulk-Load";
	public static final String LOCAL_DIR = "/tmp/attribute_ads";

	public static String host;
	public static int port;

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.APP_CONFIG_PATH);
	static List<String> hashFields = new ArrayList<String>();

	static {
		hashFields.add("adidlist");
		hashFields.add("attr_gender");
		hashFields.add("attr_age");

		host = config.getProperty("redis.host");
		port = config.getInt("redis.port", 6379);
	}

	public static void main(String[] args) throws Exception {
		long st = System.currentTimeMillis();
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxActive(1000);
		config.setMaxIdle(20);
		host = "114.112.70.20";
		JedisPool pool = new JedisPool(config, host, port, 20000);
		Jedis jedis = pool.getResource();
		jedis.auth("_houyi630");
		String s = jedis.hget("{00002B4B-57E5-EA95-240D-E640ED575E7B}",
				"adidlist");
		s = jedis.hget("{0026B69E-89DD-BDE9-D5BE-C5F9EA8D1BBB}", "adidlist");
		System.out.println(s);

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("/root/uid_adids_recom.txt")));
		String line = null;
		int count = 0;
		while (null != (line = br.readLine())) {
			++count;
			line = line.trim();
			String[] arr = line.split("\t", -1);
			if (arr.length == 2) {
				s = jedis.hget(arr[0], "adidlist");
				if (null != s && s.length() > 0) {
					System.out.println(arr[0]);
				}
			}

		}
		br.close();

		long en = System.currentTimeMillis();
		System.out.println("time: " + (en - st));

		pool.destroy();
	}
}
