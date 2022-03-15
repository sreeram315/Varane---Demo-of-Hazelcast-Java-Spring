package com.varane;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.varane.repositories.StudentRepo;
import com.varane.utils.HazelcastInitializer;
import com.varane.utils.PopulateData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring boot application starter
 * @author Sreeram Maram
 */
@SpringBootApplication
public class VaraneApplication extends SpringBootServletInitializer {
    public static void main(String[] args)  {
        SpringApplication.run(VaraneApplication.class, args);
    }

    /**
     * Insert dummy data into table while using non-persistent database
     * @param studentRepo
     * @return
     */
    @Bean
    public CommandLineRunner startup(StudentRepo studentRepo) {
        return args -> PopulateData.init(studentRepo);
    }

    @Bean
    HazelcastInstance hazelcastInstance() {
        return HazelcastInitializer.getHazelcastInstance();
    }
}
