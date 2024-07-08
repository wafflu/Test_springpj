package sp_test.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sp_test.Dao.ChatDao;
import sp_test.Dto.ChatRoomDto;

import java.util.ArrayList;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService{
    @Autowired
    ChatDao chatDao;

    @Override
    public ResponseEntity<ArrayList<ChatRoomDto>> loadChatroom(String acid){
        ArrayList<ChatRoomDto> ChatRoomlist = (ArrayList<ChatRoomDto>) chatDao.select(acid);
        return new ResponseEntity<>(ChatRoomlist, HttpStatus.OK);
    }

    @Override
    public void createChat(ChatRoomDto crdt){
        chatDao.insert(crdt);
    }
}
