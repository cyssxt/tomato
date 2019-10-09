package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.UserInfoEntity;

public interface UserRepository extends BaseRepository<UserInfoEntity> {
    UserInfoEntity findFirstByPhoneNumber(String phoneNumber);
}
