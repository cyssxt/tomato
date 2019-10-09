package com.cyssxt.tomato.dao;

import com.cyssxt.common.dao.BaseRepository;
import com.cyssxt.tomato.entity.GoalsEntity;

public interface GoalsRepository extends BaseRepository<GoalsEntity> {
    GoalsEntity findFirstByUserIdAndDelFlagFalseOrderByCreateTimeDesc(String userId);
}
