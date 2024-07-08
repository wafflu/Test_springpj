package sp_test.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sp_test.Dao.UserDao;
import sp_test.Dto.ChatMessageDto;
import sp_test.Dto.ChatRoomDto;
import sp_test.Dto.UserDto;
import sp_test.Service.ChatService;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserDao userDao;
    private static final int MAX_MESSAGES = 5;
    private static final long TIME_WINDOW_MS = 5000; // 5초
    private final Queue<Long> timestamps = new LinkedList<>();

    @GetMapping("/chat2")
    public String chat(Model model, HttpSession session, @RequestParam(defaultValue = "0") int no) throws Exception {
        //임시 테스트용
        List users = userDao.getAllUsers();
        if(session.getAttribute("userId") == null){
            return "redirect:/";
        }
        String usernick = (String) session.getAttribute("userNick");
        log.info("no : "+no);
        model.addAttribute("roomid", no);
        model.addAttribute("usernick", usernick);
        model.addAttribute("users", users);
        log.info("채팅방 입장 : "+no);
        return "/chat2";
    }

    @RequestMapping ("/callchat")
    public String callchat(Model model, HttpSession session, String id) {
        //임시 테스트용
        String userid = (String) session.getAttribute("userId");
        String usernick = (String) session.getAttribute("userNick");

        UserDto user = userDao.getUserById(id);

        ChatRoomDto crdt = new ChatRoomDto();
        crdt.setBuyer_id(user.getId());
        crdt.setBuyer_nk(user.getNick());
        //판매자
        user = userDao.getUserById(userid);
        crdt.setSeller_id(user.getId());
        crdt.setSeller_nk(user.getNick());
        crdt.setNo(Long.valueOf(user.getNo()));
        crdt.setFirst_id("admin");
        crdt.setLast_id("admin");
        chatService.createChat(crdt);
        int cr_no = user.getNo();
        log.info("방 넘버 : "+cr_no);
        log.info("채팅생성");
        return "redirect:/chat2?no="+cr_no;
    }

    @RequestMapping(value = "/loadchatroom", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDto>> Chatroomlist(HttpSession session) {
        String userid = (String) session.getAttribute("userId");
        ResponseEntity<ArrayList<ChatRoomDto>> crlist = chatService.loadChatroom(userid);
        log.info(""+crlist);
        return crlist;
    }

    @MessageMapping("/chat/{roomid}")
    @SendTo("/topic/messages/{roomid}")
    public ChatMessageDto send(ChatMessageDto cmdt, @DestinationVariable Long roomid) throws Exception {
        long currentTime = System.currentTimeMillis();
        //동기화 하면서 채팅관련 부분 확인해봐야함 멀티적으로
        synchronized (timestamps) {
            // 오래된 타임스탬프 제거
            while (!timestamps.isEmpty() && currentTime - timestamps.peek() > TIME_WINDOW_MS) {
                timestamps.poll();
            }

            if (timestamps.size() >= MAX_MESSAGES) {
                throw new Exception("Rate limit exceeded. Please try again later.");
            }

            // 새로운 타임스탬프 추가
            timestamps.add(currentTime);
        }

        //메세지 저장
        log.info("채팅방 번호 : "+roomid);

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new ChatMessageDto(cmdt.getNick(), cmdt.getMessage(), time, null);
    }
}
