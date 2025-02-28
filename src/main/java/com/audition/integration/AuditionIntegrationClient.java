package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class AuditionIntegrationClient {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuditionLogger auditionLogger;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(AuditionIntegrationClient.class);

    String POSTS_JSON = "https://jsonplaceholder.typicode.com/posts";
	String COMMENTS_JSON = "https://jsonplaceholder.typicode.com/comments";

    private HttpEntity<String> handleRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    public List<AuditionPost> getPosts() {
        // TODO make RestTemplate call to get Posts from https://jsonplaceholder.typicode.com/posts
        auditionLogger.info(log, "calling getPosts() external service");
        HttpEntity<String> entity = handleRequest();
        String jsonString = restTemplate.exchange(POSTS_JSON, HttpMethod.GET, entity, String.class).getBody();
        try {
            return Arrays.asList(objectMapper.readValue(jsonString, AuditionPost[].class));
        } catch (JsonProcessingException e) {
            auditionLogger.logErrorWithException(log, "JSON parsing error in getPosts() message: {}", e);
            throw new SystemException("Error while parsing JSON string ", 500, e);
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e, POSTS_JSON);
        }
        return new ArrayList<>();
   }

    public AuditionPost getPostById(final String id) {
        // TODO get post by post ID call from https://jsonplaceholder.typicode.com/posts/
        auditionLogger.info(log, "calling getPostById() external service for id: {}", id);
        HttpEntity<String> entity = handleRequest();
        AuditionPost auditionPost = null;
        try {
            String jsonString = restTemplate.exchange(POSTS_JSON + "/" + id, HttpMethod.GET, entity, String.class).getBody();
            auditionPost = objectMapper.readValue(jsonString, AuditionPost.class);
        }catch (JsonProcessingException e) {
			auditionLogger.logErrorWithException(log, "JSON parsing error in getPostById() message: ", e);
			throw new SystemException("Error while parsing JSON string ", 500, e);
		} catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                    404);
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                throw new SystemException("Error retrieving post", e.getMessage(), e.getStatusCode().value());
            }
        }
        return auditionPost;
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.
    public List<Comment> getCommentsForPost(final String postId) {
        auditionLogger.info(log, "calling getComments() external service for postId: {}", postId);
        HttpEntity<String> entity = handleRequest();
        String jsonString = restTemplate.exchange(POSTS_JSON + "/" + postId + "/comments", HttpMethod.GET, entity, String.class).getBody();
        try {
            return Arrays.asList(objectMapper.readValue(jsonString, Comment[].class));
        } catch (JsonProcessingException e) {
            auditionLogger.logErrorWithException(log, "JSON parsing error in getComments() message: {}", e);
            throw new SystemException("Error while parsing JSON string", 500, e);
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e, POSTS_JSON + "/" + postId + "/comments");
        }
        return new ArrayList<>();
    }

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
    public List<Comment> getCommentsForPostByQueryParam(final String postId) {
        auditionLogger.info(log, "calling getCommentByPostId() external service for postId: {}", postId);
        HttpEntity<String> entity = handleRequest();
        List<Comment> comments = null;
        try {
            String jsonString = restTemplate.exchange(COMMENTS_JSON + "?postId=" + postId, HttpMethod.GET, entity, String.class).getBody();
            comments = Arrays.asList(objectMapper.readValue(jsonString, Comment[].class));
        } catch (JsonProcessingException e) {
            auditionLogger.logErrorWithException(log, "JSON parsing error in getCommentByPostId() message: {}", e);
            throw new SystemException("Error while parsing JSON string", 500, e);
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e, COMMENTS_JSON + "?postId=" + postId);
        }
        return comments;
    }

    
    private void handleHttpClientErrorException(HttpClientErrorException e, String url) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            //auditionLogger.logHttpStatusCodeError(log, "HTTP client error: Not Found (404) while calling URL: {}", url, Integer.valueOf(404));
            throw new SystemException("Resource not found at URL: " + url, e.getMessage(), e.getStatusCode().value());
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatusCode());
            problemDetail.setTitle("HttpClientErrorException");
            problemDetail.setDetail("Error while calling URL: " + url);
            auditionLogger.logStandardProblemDetail(log, problemDetail, e);
            throw new SystemException("HttpClientErrorException occurred while calling URL: " + url, e.getMessage(), e.getStatusCode().value());
        }
    }
}
