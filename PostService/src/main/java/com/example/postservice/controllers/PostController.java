package com.example.postservice.controllers;

import com.example.postservice.dto.PostDTO;
import com.example.postservice.dto.PostResponseDTO;
import com.example.postservice.models.Hashtag;
import com.example.postservice.models.Post;
import com.example.postservice.services.HashtagService;
import com.example.postservice.services.PostService;
import com.example.postservice.utils.FileUploadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


@Controller // This means that this class is a Controller
@RequestMapping(path="/api/post") // This means URL's start with /demo (after Application path)
public class PostController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private PostService postService;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private HashtagService hashtagService;

    @PostMapping(path="/add",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE}) // Map ONLY POST Requests
    public @ResponseBody ResponseEntity<Post> addNewPost (@RequestPart("postDTO") String postDTO,
                                                          @RequestPart("image") MultipartFile multipartFile) throws IOException {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        ObjectMapper om = new ObjectMapper();

        PostDTO postDTOObj= om.readValue(postDTO,PostDTO.class);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        Post new_post = new Post();
        new_post.setTitle(postDTOObj.getTitle());
        new_post.setDescription(postDTOObj.getDescription());
        new_post.setImage_path(fileName);
        new_post.setUser_id(Long.valueOf(1));
        new_post.setPin_counter(0);
        new_post.setCreated_at(LocalDateTime.now());

        Iterable<Hashtag> hashtags = hashtagService.CreateIfNotExistsAll(postDTOObj.getHashtagNames());
        
        new_post.setHashtags((Set<Hashtag>) hashtags);

        Post newPost = postService.Create(new_post);

        String uploadDir = "post-photos/" + newPost.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return ResponseEntity.status(201).body(newPost);
    }

    @GetMapping(path="/all")
    public @ResponseBody ResponseEntity<Iterable<PostResponseDTO>> getAllPosts() {
        // This returns a JSON or XML with the users

        Iterable<PostResponseDTO> allPosts =  postService.List();

        return  ResponseEntity.status(200).body(allPosts);
    }
    @GetMapping(path="/findByIds")
    public @ResponseBody ResponseEntity<Iterable<PostResponseDTO>> getPost( @NotNull @DecimalMin("0") @RequestParam Set<Long> Ids) {
        // This returns a JSON or XML with the users
        Iterable<PostResponseDTO> a = postService.FindAllByIds(Ids);
        return  ResponseEntity.status(200).body(a);
    }
    @DeleteMapping("/delete")
    public @ResponseBody ResponseEntity<Boolean> deletePost(@RequestParam Long id){
        Post post = postService.FindById(id);
        hashtagService.FixPostDelete(post.getHashtags());
        postService.Delete(id);
        return ResponseEntity.status(204).body(true);
    }
    @PutMapping("/put")
    public @ResponseBody ResponseEntity<Post> updatePost(@Valid @RequestBody PostDTO postDTO){
        Set<Hashtag> oldHashtags = postService.FindById(postDTO.getId()).getHashtags();
        Set<String> newHashtags = postDTO.getHashtagNames();
        Iterable<Hashtag> hashes = hashtagService.FixPostUpdate(oldHashtags, newHashtags);
        Post post = postService.Update(postDTO, hashes);
        return ResponseEntity.status(200).body(post);
    }
}