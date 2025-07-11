package edu.neu.oaas.config;

import edu.neu.oaas.filter.AuthorizationFilter;
import edu.neu.oaas.utils.CrossConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Security Configuration Tests")
class SecurityConfigTest {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private CrossConfig crossConfig;

    @Autowired
    private CacheConfig cacheConfig;

    @MockBean
    private AuthorizationFilter authorizationFilter;

    private MockMvc mockMvc;

    @Test
    @DisplayName("Should configure CORS correctly")
    void testCorsConfiguration() throws Exception {
        // Setup MockMvc with CORS filter
        CorsFilter corsFilter = crossConfig.corsFilter();
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilter(corsFilter)
                .build();

        // Test CORS preflight request
        mockMvc.perform(options("/api/test")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", "*"))
                .andExpect(header().string("Access-Control-Allow-Headers", "*"));

        // Test actual request
        mockMvc.perform(get("/api/test")
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    @DisplayName("Should configure static resource handlers")
    void testStaticResourceConfiguration() {
        // Verify resource handlers are configured
        assertDoesNotThrow(() -> {
            webConfig.addResourceHandlers(null);
        });
    }

    @Test
    @DisplayName("Should configure multipart resolver")
    void testMultipartConfiguration() {
        // Verify multipart resolver configuration
        assertNotNull(webConfig.multipartResolver());
    }

    @Test
    @DisplayName("Should configure cache manager")
    void testCacheConfiguration() {
        // Verify cache manager configuration
        assertNotNull(cacheConfig.ehCacheManagerFactoryBean());
        assertNotNull(cacheConfig.ehCacheCacheManager());
    }

    // Test controller for CORS testing
    private static class TestController {
        @org.springframework.web.bind.annotation.GetMapping("/api/test")
        public String test() {
            return "test";
        }
    }
}