package com.ry.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ry.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    @Update(
        "update product " +
        "set stock = stock - #{qty} " +
        "where id = #{id} " +
        "and stock >= #{qty} " +
        "and deleted = 0"
    )
    // 返回值是sql语句执行的行数, 这里为1或0
    int updateStock(@Param("id") Long id, @Param("qty") Integer qty);

    @Update(
        "update product " +
        "set stock = stock + #{qty} " +
        "where id = #{id} " +
        "and deleted = 0"
    )
    int restoreStock(@Param("id") Long id, @Param("qty") Integer qty);
}

/*
默认方法:

查询方法
selectById(id)	根据主键 ID 查一条数据
selectBatchIds(ids)	根据 ID 集合批量查询（相当于 WHERE id IN (...)）
selectOne(wrapper)	根据条件查一条（如果查出多条会报错）
selectList(wrapper)	根据条件查列表（最常用，传 null 查全表）
selectCount(wrapper)	查总条数（相当于 COUNT(*)）
selectPage(page, wrapper)	分页查询（最常用）
selectMaps(wrapper)	查出来的结果直接返回 List<Map>，不用转实体类
exists(wrapper)	判断是否存在符合条件的记录（返回 boolean）

新增方法
insert(entity)	插入一条数据（如果你配置了主键自增，插入后会自动把 ID 回填到实体对象里）

修改方法
updateById(entity)	根据主键 ID 更新非空字段（传了啥更啥，没传的字段不会变成 null）
update(entity, wrapper)	根据条件更新（比如把所有状态为 0 的商品改成 1）

删除方法
deleteById(id)	根据主键 ID 删除
deleteBatchIds(ids)	根据 ID 集合批量删除
delete(wrapper)	根据条件删除（比如删除所有分类 ID 为 5 的商品）
deleteByIds(ids)	同上，批量删除（MP 3.5.7+ 版本新增的命名）
 */