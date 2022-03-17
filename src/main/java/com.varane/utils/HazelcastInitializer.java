package com.varane.utils;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.sql.SqlService;
import com.varane.controllers.student.StudentConstants;

import java.util.ArrayList;
import java.util.List;

public class HazelcastInitializer {

    public static HazelcastInstance getHazelcastInstance(){
        ClientConfig clientConfig = getConfig();
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        createMappings(hazelcastInstance);
        return hazelcastInstance;
    }

    public static ClientConfig getConfig(){
        ClientConfig clientConfig = new ClientConfig();
        ClientUserCodeDeploymentConfig userCodeDeploymentConfig =  clientConfig.getUserCodeDeploymentConfig();
        List<String> classNames  = new ArrayList<>();
        // defining models at server
        classNames.add("com.varane.models.Student");
        userCodeDeploymentConfig.setEnabled( true ).setClassNames(classNames);
        clientConfig.setUserCodeDeploymentConfig(userCodeDeploymentConfig);
        return clientConfig;
    }

    /**
     * To be able to query hazelcast cached objects using SQL, we need to tell hazelcast server how to map
     * the keys to values.
     * @param hazelcastInstance Hazelcast instance for which mappings have to be applied.
     */
    public static void createMappings(HazelcastInstance hazelcastInstance){
        SqlService sqlService = hazelcastInstance.getSql();

        // Mapping for model: com.varane.models.Student for IMAP
        sqlService.execute(String.format("DROP MAPPING IF EXISTS %s", StudentConstants.CACHE_MAP));
        sqlService.execute(
                String.format("CREATE MAPPING %s\n", StudentConstants.CACHE_MAP) +
                "                TYPE IMap\n" +
                "                OPTIONS (\n" +
                "                    'keyFormat' = 'java',\n" +
                "                    'keyJavaClass' = 'java.lang.Integer',\n" +
                "                    'valueFormat' = 'java',\n" +
                "                    'valueJavaClass' = 'com.varane.models.Student'\n" +
                "                )");
    }
}
