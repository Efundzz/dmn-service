package com.efundzz.dmnservice.services;

import org.camunda.bpm.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class DMNDeployment {

    @Autowired
    private RepositoryService repositoryService;

    @Value("classpath*:*.dmn")
    private Resource[] dmnResources;

    @PostConstruct
    public void deployAllDMNs() throws IOException {
        for (Resource dmnResource : dmnResources) {
            repositoryService.createDeployment()
                    .addInputStream(dmnResource.getFilename(), dmnResource.getInputStream())
                    .deploy();
        }
    }
}
