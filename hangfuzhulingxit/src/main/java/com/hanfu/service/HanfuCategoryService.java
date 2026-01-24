package com.hanfu.service;

import com.hanfu.entity.HanfuCategory;
import java.util.List;

public interface HanfuCategoryService {
    List<HanfuCategory> getAll();
    void add(HanfuCategory category);
    void update(HanfuCategory category);
    void delete(Integer id);
}
