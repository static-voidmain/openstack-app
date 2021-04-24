package org.example.openstack.demo.service;

import org.openstack4j.model.compute.Server;

import java.util.List;
import java.util.Map;

public interface ComputeResourceService {
    List<? extends Server> getComputeList();

    List<? extends Server> getComputeList(Map<String, String> param);

    List<? extends Server> getComputeDetailList(boolean detail);

    List<? extends Server> getAllComputeList(boolean detail);
}
