package sp_test.Service;

import org.springframework.http.ResponseEntity;
import sp_test.Dto.ChatRoomDto;

import java.util.ArrayList;

public interface ChatService {
    ResponseEntity<ArrayList<ChatRoomDto>> loadChatroom(String acid);

    void createChat(ChatRoomDto crdt);
}
