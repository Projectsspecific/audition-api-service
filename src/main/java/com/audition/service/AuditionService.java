package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.audition.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuditionService {
    private static final Logger log = LoggerFactory.getLogger(AuditionService.class);
    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public List<Comment> getPostComments(String postId) {
        return auditionIntegrationClient.getCommentsForPost(postId);
    }

    public List<Comment> getCommentsForPostByQueryParam(final String postId) {
    	if (log.isInfoEnabled()) {
            log.info("service call getCommentsForPostByQueryParam postId={}", postId);
        }
    	return auditionIntegrationClient.getCommentsForPostByQueryParam(postId);
    }


}
