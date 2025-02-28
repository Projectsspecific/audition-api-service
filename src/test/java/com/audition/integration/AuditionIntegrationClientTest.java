package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuditionIntegrationClientTest {

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuditionLogger auditionLogger;

    @Mock
    private ObjectMapper objectMapper;

    private static final String POSTS_JSON = "https://jsonplaceholder.typicode.com/posts";
    private static final String COMMENTS_JSON = "https://jsonplaceholder.typicode.com/comments";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPosts() throws Exception {
        String jsonResponse = "[{\"userId\":1,\"id\":1,\"title\":\"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\"body\":\"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"}]";
        when(restTemplate.exchange(eq(POSTS_JSON), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(objectMapper.readValue(jsonResponse, AuditionPost[].class))
                .thenReturn(new AuditionPost[]{
                        new AuditionPost(1, 1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto")
                });

        List<AuditionPost> posts = auditionIntegrationClient.getPosts();

        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", posts.get(0).getTitle());
    }

    @Test
    public void testGetPostById() throws Exception {
        String id = "1";
        String jsonResponse = "{\"userId\":1,\"id\":1,\"title\":\"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\"body\":\"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"}";
        when(restTemplate.exchange(eq(POSTS_JSON + "/" + id), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(objectMapper.readValue(jsonResponse, AuditionPost.class))
                .thenReturn(new AuditionPost(1, 1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"));

        AuditionPost post = auditionIntegrationClient.getPostById(id);

        assertNotNull(post);
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", post.getTitle());
    }

    @Test
    public void testGetCommentsForPost() throws Exception {
        String postId = "1";
        String jsonResponse = "[{\"postId\":1,\"id\":1,\"name\":\"id labore ex et quam laborum\",\"email\":\"Eliseo@gardner.biz\",\"body\":\"laudantium enim quasi est quidem magnam voluptate ipsam eos\\ntempora quo necessitatibus\\ndolor quam autem quasi\\nreiciendis et nam sapiente accusantium\"}]";
        when(restTemplate.exchange(eq(POSTS_JSON + "/" + postId + "/comments"), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(objectMapper.readValue(jsonResponse, Comment[].class))
                .thenReturn(new Comment[]{
                        new Comment(1, 1, "id labore ex et quam laborum", "Eliseo@gardner.biz", "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")
                });

        List<Comment> comments = auditionIntegrationClient.getCommentsForPost(postId);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("id labore ex et quam laborum", comments.get(0).getName());
    }

    @Test
    public void testGetCommentsForPostByQueryParam() throws Exception {
        String postId = "1";
        String jsonResponse = "[{\"postId\":1,\"id\":1,\"name\":\"id labore ex et quam laborum\",\"email\":\"Eliseo@gardner.biz\",\"body\":\"laudantium enim quasi est quidem magnam voluptate ipsam eos\\ntempora quo necessitatibus\\ndolor quam autem quasi\\nreiciendis et nam sapiente accusantium\"}]";
        when(restTemplate.exchange(eq(COMMENTS_JSON + "?postId=" + postId), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        when(objectMapper.readValue(jsonResponse, Comment[].class))
                .thenReturn(new Comment[]{
                        new Comment(1, 1, "id labore ex et quam laborum", "Eliseo@gardner.biz", "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")
                });

        List<Comment> comments = auditionIntegrationClient.getCommentsForPostByQueryParam(postId);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("id labore ex et quam laborum", comments.get(0).getName());
    }
}
