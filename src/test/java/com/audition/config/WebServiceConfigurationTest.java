package com.audition.config;

import com.audition.configuration.WebServiceConfiguration;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.audition.configuration.LoggingInterceptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebServiceConfigurationTest {

    private ApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(WebServiceConfiguration.class);
    }

    @Test
    public void testObjectMapper() {
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

        assertNotNull(objectMapper);
        assertFalse(objectMapper.getDeserializationConfig().hasDeserializationFeatures(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask()));
        assertEquals("yyyy-MM-dd", ((java.text.SimpleDateFormat) objectMapper.getDateFormat()).toPattern());
        assertFalse(objectMapper.getSerializationConfig().isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES));
        assertFalse(objectMapper.getSerializationConfig().isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertEquals(com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE, objectMapper.getPropertyNamingStrategy());
    }

    @Test
    public void testRestTemplate() throws Exception {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);

        assertNotNull(restTemplate);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        assertEquals(1, interceptors.size());
        assertTrue(interceptors.get(0) instanceof LoggingInterceptor);

        boolean objectMapperConfigured = restTemplate.getMessageConverters().stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .map(converter -> ((MappingJackson2HttpMessageConverter) converter).getObjectMapper())
                .anyMatch(objectMapper -> objectMapper != null && objectMapper.getPropertyNamingStrategy() == com.fasterxml.jackson.databind.PropertyNamingStrategies.LOWER_CAMEL_CASE);

        assertTrue(objectMapperConfigured);

        // Retrieve ObjectMapper from context
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

        // Test data serialization and deserialization
        String postJson = "[{\"userId\":1,\"id\":1,\"title\":\"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\"body\":\"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"}]";
        AuditionPost[] posts = objectMapper.readValue(postJson, AuditionPost[].class);
        assertNotNull(posts);
        assertEquals(1, posts.length);
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", posts[0].getTitle());

        String commentJson = "[{\"postId\":10,\"id\":46,\"name\":\"dignissimos et deleniti voluptate et quod\",\"email\":\"Jeremy.Harann@waino.me\",\"body\":\"exercitationem et id quae cum omnis\\nvoluptatibus accusantium et quidem\\nut ipsam sint\\ndoloremque illo ex atque necessitatibus sed\"}]";
        Comment[] comments = objectMapper.readValue(commentJson, Comment[].class);
        assertNotNull(comments);
        assertEquals(1, comments.length);
        assertEquals("dignissimos et deleniti voluptate et quod", comments[0].getName());
    }
}
