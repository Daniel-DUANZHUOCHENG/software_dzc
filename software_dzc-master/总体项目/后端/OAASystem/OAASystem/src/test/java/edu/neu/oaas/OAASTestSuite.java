package edu.neu.oaas;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "edu.neu.oaas.controller",
    "edu.neu.oaas.service",
    "edu.neu.oaas.mapper",
    "edu.neu.oaas.pojo"
})
public class OAASTestSuite {
    // This class serves as a test suite container
} 