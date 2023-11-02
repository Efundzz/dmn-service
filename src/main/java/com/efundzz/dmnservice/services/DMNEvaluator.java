package com.efundzz.dmnservice.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;

import org.camunda.bpm.engine.DecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DMNEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DMNEvaluator.class);

    @Autowired
    private DecisionService decisionService;

    public List<Map<String, Object>> evaluateDecision(String decisionKey, Map<String, Object> variables) {
        DmnDecisionTableResult result = decisionService.evaluateDecisionTableByKey(decisionKey, variables);
        List<Map<String, Object>> outputList = new ArrayList<>();
        for (Map<String, Object> ruleResult : result) {
            String entity = (String) ruleResult.get("entity");
            if (entity != null && !entity.isEmpty()) {
                Map<String, Object> outputMap = new HashMap<>();
                outputMap.put("entity", entity);
                double loanAmount = Double.parseDouble((String) variables.get("loanAmount"));
                String companyCategory = (String) variables.get("companyCategory");
                // Calculating and adding roi, tenure and proposedEmi for Cashe bank
                if ("Cashe".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 24.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 20.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 16.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 16.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For MyShubhLife bank
                if ("MyShubhLife".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 28.0;
                        double tenure = 24.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 30.0;
                        double tenure = 20.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 32.0;
                        double tenure = 16.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 34.0;
                        double tenure = 16.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For IDFC bank
                if ("IDFC".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.49;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Axis Bank
                if ("Axis Bank".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.49;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For HDFC Bank
                if ("HDFC".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.5;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For ICICI Bank
                if ("ICICI".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 11.0;
                        double tenure = 72.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Yes Bank
                if ("Yes Bank".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.75;
                        double tenure = 72.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Axis Finance Bank
                if ("Axis Finance".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 16.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Bajaj Finserv Bank
                if ("Bajaj Finserv".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 14.5;
                        double tenure = 72.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Poonawala Bank
                if ("Poonawala".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.25;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Fullerton bank
                if ("Fullerton".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 14.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Cholamandalam bank
                if ("Cholamandalam".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 14.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For TATA Capital bank
                if ("TATA Capital".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 10.99;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Finnable bank
                if ("Finnable".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 22.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 24.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 24.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Incred bank
                if ("Incred".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Paysense bank
                if ("Paysense".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 17.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 24.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For IndusInd bank
                if ("IndusInd".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 16.5;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For SCB bank
                if ("SCB".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 11.75;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Kotak bank
                if ("Kotak".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 16.5;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Hero Fincorp bank
                if ("Hero Fincorp".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 16.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Muthoot Finance bank
                if ("Muthoot Finance".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.5;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Finzy bank
                if ("Finzy".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 22.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 25.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 28.0;
                        double tenure = 24.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 30.0;
                        double tenure = 24.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Credit Vidya bank
                if ("Credit Vidya".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 24.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }//For ABFL bank
                if ("ABFL".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 72.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }
                //For Piramal bank
                if ("Piramal".equals(entity)) {
                    if ("SA".equalsIgnoreCase(companyCategory) || "A".equalsIgnoreCase(companyCategory)) {
                        double roi = 12.99;
                        double tenure = 60.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("B".equalsIgnoreCase(companyCategory)) {
                        double roi = 15.0;
                        double tenure = 48.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("C".equalsIgnoreCase(companyCategory)) {
                        double roi = 18.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    } else if ("D".equalsIgnoreCase(companyCategory) || "E".equalsIgnoreCase(companyCategory) || "O".equalsIgnoreCase(companyCategory)) {
                        double roi = 20.0;
                        double tenure = 36.0;
                        int proposedEmi = (int) ((loanAmount * (roi / (12 * 100)) * Math.pow((1 + roi / (12 * 100)), tenure)) / (Math.pow((1 + roi / (12 * 100)), tenure) - 1));
                        outputMap.put("roi", roi);
                        outputMap.put("tenure", tenure);
                        outputMap.put("proposedEmi", proposedEmi);
                    }
                }

                // Check if the variables contain "probabilityPercentage" for the bank
                if (variables.containsKey("probabilityPercentage")) {
                    double probabilityPercentage = (double) variables.get("probabilityPercentage");
                    // Add the probability to the result map
                    outputMap.put("probabilityPercentage", probabilityPercentage);
                }
                outputList.add(outputMap);
            }
        }
        logger.debug("Decision evaluation result: {}", outputList);
        return outputList;
    }
}