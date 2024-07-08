package sp_test.Dao;

import sp_test.Dto.UserDto;

import java.util.List;

public interface UserDao {
    public List<UserDto> getAllUsers();
    public UserDto getUserById(String id);
    public int insert(UserDto userDto);
}
