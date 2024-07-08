package sp_test.Dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sp_test.Dto.UserDto;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao{
    @Autowired
    private SqlSession sqlSession;

    private String namespace = "sp_test.Dao.UserDao.";
    @Override
    public List<UserDto> getAllUsers() {
        return sqlSession.selectList(namespace + "getAllUsers");
    }

    @Override
    public UserDto getUserById(String id) {
        return sqlSession.selectOne(namespace + "getUserById", id);
    }

    @Override
    public int insert(UserDto userDto) {
        return sqlSession.insert(namespace + "insert", userDto);
    }
}
