package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.UserSettingEntity;

public interface UserSettingRepository extends BaseRepository<UserSettingEntity> {

    UserSettingEntity findFirstByUserIdAndDelFlag(String userId,Boolean delFlag);
}
