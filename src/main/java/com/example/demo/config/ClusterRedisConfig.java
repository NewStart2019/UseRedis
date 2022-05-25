package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 集群版 Redis缓存配置类，如需启用请在配置文件中新增：
 * spring.redis.cluster.nodes={ip1}:{port1},{ip2}:{port2},...
 */
@Configuration
@ConditionalOnClass({JedisCluster.class})
@ConditionalOnProperty(prefix = "spring.redis.cluster", name = "nodes", havingValue = "true")
public class ClusterRedisConfig {

    @Value("${spring.redis.cluster.min-idle}")
    private int minIdle;
    @Value("${spring.redis.cluster.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.cluster.max-active}")
    private int maxActive;
    @Value("${spring.redis.cluster.max-redirects}")
    private int maxRedirects;
    @Value("${spring.redis.cluster.password}")
    private String password;
    @Value("${spring.redis.cluster.nodes}")
    private List<String> nodes;

    /**
     * lettuce 配置
     *
     * @return
     */
    //@Bean
    //public LettuceConnectionFactory redisConnectionFactory() {
    //    RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
    //    redisClusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());
    //    redisClusterConfiguration.setPassword(redisProperties.getPassword());
    //    //支持自适应集群拓扑刷新和静态刷新源
    //    //ClusterTopologyRefreshOptions clusterTopologyRefreshOptions =  ClusterTopologyRefreshOptions.builder()
    //    //        .enablePeriodicRefresh()
    //    //        .enableAllAdaptiveRefreshTriggers()
    //    //        .refreshPeriod(Duration.ofSeconds(redisProperties.getTimeout().getSeconds()))
    //    //        .build();
    //    //ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
    //    //        .topologyRefreshOptions(clusterTopologyRefreshOptions).build();
    //    //从优先，读写分离，读从可能存在不一致，最终一致性CP
    //    //LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
    //    //        //.readFrom(ReadFrom.REPLICA)
    //    //        .clientOptions(clusterClientOptions).build();
    //    return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    //}
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(nodes);
        clusterConfiguration.setMaxRedirects(maxRedirects);
        clusterConfiguration.setPassword(password);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setTestOnBorrow(true);

        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofSeconds(10)).usePooling().poolConfig(poolConfig).build();

        JedisConnectionFactory factory = new JedisConnectionFactory(clusterConfiguration, clientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }



}
