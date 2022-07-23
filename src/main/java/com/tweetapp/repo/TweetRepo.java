package com.tweetapp.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.Tweet;


@Repository
public interface TweetRepo extends MongoRepository<Tweet, String>{
    
}
