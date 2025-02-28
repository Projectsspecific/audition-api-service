package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuditionControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AuditionService auditionService;

    @InjectMocks
    private AuditionController auditionController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetPosts() throws Exception {
        AuditionPost post1 = new AuditionPost(1, 1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");
        AuditionPost post2 = new AuditionPost(1, 2, "qui est esse", "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla");
        AuditionPost post3 = new AuditionPost(1, 3, "ea molestias quasi exercitationem repellat qui ipsa sit aut", "et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut");

        List<AuditionPost> posts = Arrays.asList(post1, post2, post3);

        // Positive scenario
        when(auditionService.getPosts()).thenReturn(posts);

        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
                .andExpect(jsonPath("$[1].title").value("qui est esse"))
                .andExpect(jsonPath("$[2].title").value("ea molestias quasi exercitationem repellat qui ipsa sit aut"));

   }

    @Test
    public void testGetPostById() throws Exception {
        AuditionPost post = new AuditionPost(1, 1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto");
    
        // Positive scenario
        when(auditionService.getPostById("1")).thenReturn(post);
    
        mockMvc.perform(get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"));
    
        // Negative scenario: Post not found
        when(auditionService.getPostById("999")).thenThrow(new SystemException("Post not found", 404));
    
        mockMvc.perform(get("/posts/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));
    }
    
    @Test
    public void testGetComments() throws Exception {
        Comment comment1 = new Comment(10, 46, "dignissimos et deleniti voluptate et quod", "Jeremy.Harann@waino.me", "exercitationem et id quae cum omnis\nvoluptatibus accusantium et quidem\nut ipsam sint\ndoloremque illo ex atque necessitatibus sed");
        Comment comment2 = new Comment(10, 47, "rerum commodi est non dolor nesciunt ut", "Pearlie.Kling@sandy.com", "occaecati laudantium ratione non cumque\nearum quod non enim soluta nisi velit similique voluptatibus\nesse laudantium consequatur voluptatem rem eaque voluptatem aut ut\net sit quam");

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(auditionService.getCommentsForPostByQueryParam("10")).thenReturn(comments);

        mockMvc.perform(get("/posts/10/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("dignissimos et deleniti voluptate et quod"))
                .andExpect(jsonPath("$[1].name").value("rerum commodi est non dolor nesciunt ut"));

        when(auditionService.getCommentsForPostByQueryParam("999")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/posts/999/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
