<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="loginId" value="${sessionScope.userId}"/>
<html>
<head>
  <title>치즈메이트</title>

  <!-- 구글 폰트 영역 -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />

  <!-- 슬라이드 영역 -->
  <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>
  <!-- 기본 리셋 영역 -->
  <link rel="stylesheet" href="/css/reset.css">
  <!-- 사용자 영역 -->
  <link rel="stylesheet" href="/css/mystyle.css">
  <link rel="stylesheet" href="/css/chatting.css">
  <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
  <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>

<div class="maincontent">
  <ul class="userlist">
  <c:forEach var="user" items="${users}">
    <c:if test="${user.id ne sessionScope.userId}">
      <li><a href="/callchat?id=${user.id}">${user.nick}</a></li>
    </c:if>
  </c:forEach>
  </ul>
  <div id="chatbox">
    <div id = "chatlist-box" class="chatbox-info">
      <div class="chatlist-menu">
        <ul class="chatlist-menu-nav">
          <li class="chatlist-menu-active font14">
            <span>전체 대화</span>
            <span class="chatsel-drop-down"></span>
          </li>
          <li>
            <span>구매 대화</span>
            <div class="chatsel-drop-down"></div>
          </li>
          <li>
            <span>판매 대화</span>
            <div class="chatsel-drop-down"></div>
          </li>
        </ul>
      </div>

      <div id="chatlist">

      </div>
    </div>

    <div id="conversationDiv" class="chatbox-info">
      <input type="hidden" id="nick" placeholder="Choose a nickname" value=<c:out value='${usernick}'/>>
      <button id="disconnect" disabled="disabled" onclick="disconnect();"></button>
      <div class="chat-profile-box">
        <div class="chat-profile-img">

        </div>
        <div class="chat-user-nick">

        </div>
      </div>
      <div id="response" class="response-active"></div>
      <div id="sendbox" disabled="disabled">
        <div class="msg-text">
          <textarea id="message" rows="6" cols="80" name="content" placeholder="메세지를 입력하세요." onkeydown="handleKeyPress(event)"></textarea>
        </div>
        <button id="sendMessage" onclick="sendMessage();"></button>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
  let stompClient = null;
  let roomnum; // 채팅방 번호
  $(document).ready(function (){
    let roomnum = ${roomid};
    if(roomnum !== 0){
      connect(roomnum);
    } else {
      disconnect();
    }
  })
  function setConnected(connected) {
    // document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility
            = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
  }
  //채팅 연결
  function connect(roomid) {
    disconnect();
    roomnum = roomid;
    console.log("roomid : "+roomid)
    let socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
      setConnected(true);
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/messages/'+roomnum, function(messageOutput) {
        showMessageOutput(JSON.parse(messageOutput.body), true);
      });
    });
  }

  //채팅 연결 끊기
  function disconnect() {
    if(stompClient != null) {
      stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
    loadChatRoom();
  }

  // 채팅방 리스트 불러오기
  function loadChatRoom(){
    let chatlist = $("#chatlist");
    let chatlist_box = $("#chatlist-box");
    $.ajax({
      url: '/loadchatroom',
      type : 'GET',
      processData : false,
      contentType : false,
      dataType : 'json',
      success : function(result){
        let str = "";
        chatlist.children().remove();
        str+="<ul class='chatullist'>"
        result.forEach((chat)=>{
          if(chat.id == roomnum){
            str += '<li onclick="connect(\'' + chat.no + '\')" class="chatroomsel active-sel-chatroom"> <span class="r_chatnick font14">' + chat.seller_nk + '</span></li>';
          } else {
            str += '<li onclick="connect(\'' + chat.no + '\')" class="chatroomsel"> <span class="r_chatnick font14">' + chat.seller_nk + '</span></li>';
          }
        })
        str+="</ul>"
        chatlist.append(str);

        if (chatlist.children().length === 0) {
          chatlist_box.addClass("chatlist-box");
        } else {
          $("#chatlist-box").css("background-image", "none");
        }
      },
      error : function(result){
        alert("오류");
      }
    })
  }

  //메세지전송
  function sendMessage() {
    let nick = document.getElementById('nick').value;
    let message = document.getElementById('message').value;
    stompClient.send("/app/chat/"+roomnum, {},
            JSON.stringify({'nick':nick, 'message':message}));
    document.getElementById('message').value = "";
  }
  //Enter
  function handleKeyPress(event){
    if(event.keyCode === 13){
      sendMessage();
      event.preventDefault();
      return false;
    }
  }
  // 메세지 표기
  function showMessageOutput(messageOutput, load) {
    let response = document.getElementById('response');
    let p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    // 현재 사용자의 닉네임 가져오기
    let currentNick = $("#nick").val();
    let date = messageOutput.r_date;
    if(!load){
      let originalDate = messageOutput.r_date; // "2024-05-14 11:23:36"와 같은 형식의 날짜 문자열
      let formattedDate = originalDate.split(" ")[1].substring(0, 5); // "11:23"과 같은 형식의 시간 문자열 추출
      date = formattedDate;
    }
    // 닉네임과 메시지의 닉네임 비교 후 클래스 추가
    if (currentNick === messageOutput.nick) {
      p.classList.add("buyermsg");
    } else {
      p.classList.add("sellermsg");
    }
    p.appendChild(document.createTextNode(messageOutput.nick + ": "
            + messageOutput.message + " (" + date + ")"));
    response.appendChild(p);
  }
</script>

</body>
</html>