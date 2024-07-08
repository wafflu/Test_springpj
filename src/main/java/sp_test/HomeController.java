package sp_test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sp_test.Dao.UserDao;
import sp_test.Dto.UserDto;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
public class HomeController {
    @Autowired
    UserDao userDao;
    @GetMapping("/")
    public String home(){
        return "home";
    }

    @RequestMapping("/login")
    public String Login(Model m, HttpSession session, String userid, String nick){
        if(userid.isEmpty() || nick.isEmpty()){
            return "redirect:/";
        }
        UserDto user = new UserDto();
        user.setId(userid);
        user.setNick(nick);
        user.setPw("1234");
        user.setName("테스트");
        if(userDao.getUserById(userid) == null){
            userDao.insert(user);
        }
        session.setAttribute("userId", userid);
        session.setAttribute("userNick", nick);
        return "redirect:/chat2";
    }
}
