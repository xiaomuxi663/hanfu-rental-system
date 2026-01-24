package com.hanfu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanfu.entity.HanfuSpu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HanfuSpuMapper extends BaseMapper<HanfuSpu> {
    @Select("SELECT COUNT(*) FROM hanfu_sku WHERE spu_id = #{spuId} AND status = 0")
    Integer getAvailableStock(Long spuId);
}
