package com.tweetapp.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.Comment;
import com.tweetapp.model.CommentResDto;
@Repository
public interface CommentRepo extends MongoRepository<Comment, String>{
    
    @Query(value="{ tweetId  :  ?0 }",sort = "{createdDate: -1}")
    public Page<CommentResDto> customFindAll(String tweetId,Pageable pageable);

}
