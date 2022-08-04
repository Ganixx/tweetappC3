package com.tweetapp.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.AppUser;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.SearchAppUserResponseDto;

@Repository
public interface AppUserRepo extends MongoRepository<AppUser, String> {

    @Query(value="{}", fields="{ loginId : 1, firstName : 1 ,lastName : 1 ,image : 1}")
    public Page<AppUserResponseDto> customFindAll(Pageable pageable);

    @Query(value = "{loginId: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public AppUser customFindByLoginId(String username);

    @Query(value = "{loginId: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public Optional<AppUser> customExistsByLoginId(String loginId);

    @Query(value = "{email: ?0}", collation = "{ locale: 'en', strength: 2 }")
    public Optional<AppUser> customExistsByEmail(String email);

   
    @Query(value = "{loginId: {$regex:  /?0/ ,$options: i}} ")
    public Page<SearchAppUserResponseDto> searchByLoginId(String search,Pageable pageable);

}
