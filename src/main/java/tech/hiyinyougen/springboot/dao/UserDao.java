package tech.hiyinyougen.springboot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import tech.hiyinyougen.springboot.model.UserModel;

import java.util.List;

/**
 * @Author yinyg
 * @CreateTime 2020/6/29 10:20
 * @Description
 */
@Mapper
public interface UserDao {
    @Select("select * from user limit 0, 1000")
    List<UserModel> selectAll();

    List<UserModel> selectAllXML();

    int save(UserModel userModel);
}
