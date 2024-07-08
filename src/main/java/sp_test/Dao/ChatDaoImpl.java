package sp_test.Dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sp_test.Dto.ChatMessageDto;
import sp_test.Dto.ChatRoomDto;

import java.util.List;

@Repository
public class ChatDaoImpl implements ChatDao{
    @Autowired
    private SqlSession session;
    private static String namespace = "sp_test.Dao.ChatDao.";
    @Override
    public List<ChatRoomDto> select(String acid){
        return session.selectList(namespace+"select_chatlist", acid);
    }

    @Override
    public int insert(ChatRoomDto crdt){
        return session.insert(namespace+"insert_chatroom", crdt);
    }


}
