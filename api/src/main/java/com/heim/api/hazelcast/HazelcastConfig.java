package com.heim.api.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean(name = "customHazelcastConfig")
    public Config hazelcastConfig() {
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .setName("driver-locations")
                        .setEvictionConfig(new EvictionConfig()
                                .setSize(200)
                                .setMaxSizePolicy(MaxSizePolicy.USED_HEAP_SIZE))
                        .setTimeToLiveSeconds(3600)); // Datos expiran en 1 hora
    }
}
