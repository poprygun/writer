package io.pivotal.poc.cs.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import io.pivotal.poc.cs.WriterServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashConfiguration {
    private static final int RECEICED_MESSAGES_TRACK_TTL_SECS = 60 * 60;

    // When Spring Boot find a com.hazelcast.config.Config automatically instantiate a HazelcastInstance
    @Bean
    public Config config() {
        return new Config().addMapConfig(
                // Set up TTL for the Map tracking received Messages IDs
                new MapConfig()
                        .setName(WriterServiceImpl.ACCEPTED_MESSAGES_TRACKING_MAP_NAME)
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setTimeToLiveSeconds(RECEICED_MESSAGES_TRACK_TTL_SECS));

    }

}
