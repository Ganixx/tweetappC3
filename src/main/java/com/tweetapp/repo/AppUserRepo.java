package com.tweetapp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.AppUser;
import com.tweetapp.model.AppUserResponseDto;

@Repository
public interface AppUserRepo extends MongoRepository<AppUser, String> {

    @Query(value = "{loginId: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public AppUser customFindByLoginId(String username);

    @Query(value = "{loginId: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public Optional<AppUser> customExistsByLoginId(String loginId);

    @Query(value = "{email: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public Optional<AppUser> customExistsByEmail(String email);

   
    @Query(value = "{loginId: {$regex:  /?0/ ,$options: i}} ")
    public Optional<List<AppUserResponseDto>> searchByLoginId(String search);

}
