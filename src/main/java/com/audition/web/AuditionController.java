package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditionPost> getPosts(@RequestParam(required = false) final String filterParam) {
        List<AuditionPost> posts = auditionService.getPosts();
        if (filterParam != null && !filterParam.isEmpty()) {
            if (filterParam.matches(".*\\d.*")) {
                posts = posts.stream()
                             .filter(post -> post.getUserId() == Integer.parseInt(filterParam))
                             .toList();
            } else if (filterParam.length() >= 2) {
                posts = posts.stream()
                             .filter(post -> post.getTitle().contains(filterParam))
                             .toList();
            }
        }
        if (posts == null || posts.isEmpty()) {
              throw new SystemException("No matching posts found for the given filter param: " + filterParam, 200);
        }
        return posts;
    }

    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsById(@PathVariable("id") final String postId) {
        final AuditionPost auditionPosts = auditionService.getPostById(postId);

        if (postId == null || !postId.matches("\\d+")) {
            throw new SystemException("Invalid post ID");
        }

        if (auditionPosts == null) {
            throw new SystemException("Post not found");
        }
        return auditionPosts;
    }

    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Comment> getComments(@PathVariable("id") final String postId) {
        if (postId == null || !postId.matches("\\d+")) {
            throw new SystemException("Invalid post ID : " + postId, 400);
        }
        return auditionService.getCommentsForPostByQueryParam(postId);
    }

    @ExceptionHandler(SystemException.class)
public ResponseEntity<ErrorResponse> handleSystemException(SystemException ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatusCode());
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
}

static class ErrorResponse {
    private String message;
    private int statusCode;

    public ErrorResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

}
