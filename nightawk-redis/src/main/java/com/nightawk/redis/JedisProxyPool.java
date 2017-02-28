package com.nightawk.redis;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

import java.net.URI;

/**
 * @author Xs.
 */
public class JedisProxyPool extends Pool<Jedis> {

    public JedisProxyPool() {
        this(Protocol.DEFAULT_HOST, Protocol.DEFAULT_PORT);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host) {
        this(poolConfig, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, null,
                Protocol.DEFAULT_DATABASE, null);
    }

    public JedisProxyPool(String host, int port) {
        this(new GenericObjectPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, null,
                Protocol.DEFAULT_DATABASE, null);
    }

    public JedisProxyPool(final String host) {
        URI uri = URI.create(host);
        if (JedisURIHelper.isValid(uri)) {
            String h = uri.getHost();
            int port = uri.getPort();
            String password = JedisURIHelper.getPassword(uri);
            int database = JedisURIHelper.getDBIndex(uri);
            this.internalPool = new GenericObjectPool<>(new JedisProxyFactory(h, port,
                    Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, password, database, null),
                    new GenericObjectPoolConfig());
        } else {
            this.internalPool = new GenericObjectPool<>(new JedisProxyFactory(host,
                    Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, Protocol.DEFAULT_TIMEOUT, null,
                    Protocol.DEFAULT_DATABASE, null), new GenericObjectPoolConfig());
        }
    }

    public JedisProxyPool(final URI uri) {
        this(new GenericObjectPoolConfig(), uri, Protocol.DEFAULT_TIMEOUT);
    }

    public JedisProxyPool(final URI uri, final int timeout) {
        this(new GenericObjectPoolConfig(), uri, timeout);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                          int timeout, final String password) {
        this(poolConfig, host, port, timeout, password, Protocol.DEFAULT_DATABASE, null);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, final int port) {
        this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE, null);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, final int port,
                          final int timeout) {
        this(poolConfig, host, port, timeout, null, Protocol.DEFAULT_DATABASE, null);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                          int timeout, final String password, final int database) {
        this(poolConfig, host, port, timeout, password, database, null);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                          int timeout, final String password, final int database, final String clientName) {
        this(poolConfig, host, port, timeout, timeout, password, database, clientName);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
                          final int connectionTimeout, final int soTimeout, final String password, final int database,
                          final String clientName) {
        super(poolConfig, new JedisProxyFactory(host, port, connectionTimeout, soTimeout, password,
                database, clientName));
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final URI uri) {
        this(poolConfig, uri, Protocol.DEFAULT_TIMEOUT);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final URI uri, final int timeout) {
        this(poolConfig, uri, timeout, timeout);
    }

    public JedisProxyPool(final GenericObjectPoolConfig poolConfig, final URI uri,
                          final int connectionTimeout, final int soTimeout) {
        super(poolConfig, new JedisProxyFactory(uri, connectionTimeout, soTimeout, null));
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = super.getResource();
        jedis.setDataSource(this);
        return jedis;
    }
}
