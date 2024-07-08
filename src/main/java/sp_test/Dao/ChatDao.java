package sp_test.Dao;

import org.springframework.http.ResponseEntity;
import sp_test.Dto.ChatRoomDto;

import java.util.ArrayList;
import java.util.List;

public interface ChatDao {
    List<ChatRoomDto> select(String acid);

    int insert(ChatRoomDto crdt);
}
