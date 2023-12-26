package com.techtejendra.controller;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.reflect.TypeToken;
import com.hazelcast.core.HazelcastInstance;
import com.techtejendra.property.GetTimestamp;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

@RestController
public class HelloWorldController {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	private ConcurrentMap<String, String> retrieveMap() {
		return hazelcastInstance.getMap("map");
	}

	 @PostMapping("/put")
    public CommandResponse put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
		System.out.println("putting value "+key+":"+value);
        retrieveMap().put(key, value);
        return new CommandResponse(value);
    }

    @GetMapping("/get")
    public CommandResponse get(@RequestParam(value = "key") String key) {
				System.out.println("retrieving value for "+key);
        String value = retrieveMap().get(key);
        return new CommandResponse(value);
    }

	@RequestMapping("/")
	public String hello() {
		System.out.println("");
		GetTimestamp t = new GetTimestamp();
		String prev_timestamp = t.read();
		System.out.println("prev timestamp-----> " + prev_timestamp);

		// System.out.println("---------------------------------------------REading
		// Again");

		java.util.Date date = new java.util.Date();
		String latest_timestamp = date.toString();
		t.write(latest_timestamp);

		String run_env;
		run_env = System.getenv("RUN_ENV");

		String output;
		output = "<center><H1>Greetings for the day</H1>";
		output = output + "<h2 style='color:green;'>version : 2.0</h2>";
		output = output + "<h2 style='color:red;'> Previous Timestamp :" + prev_timestamp + "</h2>";
		output = output + "<h2 style='color:blue;'> Latest Timestamp :" + latest_timestamp + "</h2>";
		if (run_env != null)
			output = output + "<h2 style='color:#f90e4e;'> Run Env :" + run_env + "</h2>";
		output = output + "";
		output = output + "</center>";

		return output;
	}


	@RequestMapping("/call-nodes")
	public String callNodes() {

	  ApiClient client;
	try {
		client = Config.defaultClient();
		 Configuration.setDefaultApiClient(client);

		 
	
       

        CoreV1Api api = new CoreV1Api();
		
		
        V1PodList list = api.listNamespacedPod("noryak-dev", null, null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getSpec().getHostname());
        }

		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getResponseBody());
		}
    
		
		return "hi";
	}

}
