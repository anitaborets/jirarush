package com.javarush.jira.profile.internal;

import com.javarush.jira.common.BaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Component
public interface ProfileRepository extends BaseRepository<Profile> {
    default Profile getOrCreate(long id) {
        return findById(id).orElseGet(() -> new Profile(id));
    }
}
