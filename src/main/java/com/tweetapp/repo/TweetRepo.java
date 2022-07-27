package com.tweetapp.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.Tweet;
import com.tweetapp.model.TweetResponseDto;


@Repository
public interface TweetRepo extends MongoRepository<Tweet, String>{

    List<Tweet> findByLoginId(String userIdFromRequest);

    @Query(value="{loginId: ?0}",sort = "{createdDate: -1}")
    public Page<TweetResponseDto> allTweetsOfuser(String loginId,Pageable pageable);

    @Query(value="{loginId : { $in: ?0 }}",sort = "{createdDate: -1}")
    public Page<TweetResponseDto> tweetsForUserHomePage(Set<String> following,Pageable pageable);
    
    // @Aggregation(pipeline ={
    //       "{$lookup: {from: 'users', localField: 'loginId', foreignField: 'loginId', as: 'user' , pipeline: [{$match: {loginId: ?0}}]}}",
    //       "{$match: { result.0: { $exists: true}}}",
    //       "{$sort : { createdDate : -1 }}",
    //   })
    // public Page<TweetResponseDto> tweetsForUserHomePageAggregation(String principal,Pageable pageable);
}
