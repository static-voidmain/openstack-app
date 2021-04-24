package org.example.openstack.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.openstack.demo.service.ComputeResourceService;
import org.example.openstack.demo.service.ComputeResourceServiceImpl;
import org.openstack4j.openstack.compute.domain.NovaServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
public class SampleController {

    private final ComputeResourceService computeResourceService;

    @Autowired
    public SampleController(ComputeResourceService computeResourceService) {
        this.computeResourceService = computeResourceService;
    }

    @GetMapping(value = {"", "/", "/login"})
    public String loginPage(){
        return "login";
    }

    @GetMapping(value={"/server/list"})
    public String serverList(Model model){
        List<NovaServer> serverList = (List<NovaServer>) computeResourceService.getAllComputeList(true);
        log.info("VM COUNT : [{}]", serverList.size());
        model.addAttribute("serverList", serverList);
        return  "server/list";
    }
}
