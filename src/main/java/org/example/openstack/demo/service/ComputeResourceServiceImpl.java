package org.example.openstack.demo.service;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ComputeResourceServiceImpl implements ComputeResourceService {

    private final OSClientV3 osClient;

    @Autowired
    public ComputeResourceServiceImpl(OSClientV3 osClient) {
        this.osClient = osClient;
    }

    @Override
    public List<? extends Server> getComputeList(){
        return osClient.compute().servers().list();
    }

    @Override
    public List<? extends Server> getComputeList(Map<String, String> param){
        return osClient.compute().servers().list(param);
    }

    @Override
    public List<? extends Server> getComputeDetailList(boolean detail){
        return osClient.compute().servers().list(detail);
    }

    @Override
    public List<? extends Server> getAllComputeList(boolean detail){
        return osClient.compute().servers().listAll(detail);
    }
}
