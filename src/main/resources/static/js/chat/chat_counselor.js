// 현재 시간 포맷팅 함수
function getCurrentTime() {
    var now = new Date();
    return now.toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true 
    });
}

// 메시지 전송
function sendMessage() {
    var input = document.getElementById('chatInput');
    var message = input.value.trim();
    
    if (message === '') return;
    
    var chatContent = document.getElementById('chat-content');
    var currentTime = getCurrentTime();
    
    // 상담사 메시지 추가
    var counselorMessage = document.createElement('div');
    counselorMessage.className = 'message counselor';
    
    var messageAvatar = document.createElement('div');
    messageAvatar.className = 'message-avatar';
    messageAvatar.textContent = '상담사';
    
    var messageContent = document.createElement('div');
    messageContent.className = 'message-content';
    
    var messageBubble = document.createElement('div');
    messageBubble.className = 'message-bubble';
    messageBubble.textContent = message;
    
    var messageTime = document.createElement('div');
    messageTime.className = 'message-time';
    messageTime.textContent = currentTime;
    
    messageContent.appendChild(messageBubble);
    messageContent.appendChild(messageTime);
    counselorMessage.appendChild(messageAvatar);
    counselorMessage.appendChild(messageContent);
    chatContent.appendChild(counselorMessage);
    
    // 입력창 비우기
    input.value = '';
    autoResize(input);
    
    // 스크롤을 맨 아래로
    chatContent.scrollTop = chatContent.scrollHeight;
    
    // 내담자 응답 시뮬레이션
    setTimeout(function() {
        showTypingIndicator();
        setTimeout(function() {
            hideTypingIndicator();
            addClientResponse();
        }, 2000);
    }, 500);
}

// 내담자 응답 추가 (시뮬레이션)
function addClientResponse() {
    var responses = [
        "네, 감사합니다. 좀 더 구체적으로 설명드리자면...",
        "그런 방법이 있군요. 제가 놓친 부분이 있는 것 같아요.",
        "정말 도움이 되는 조언이네요. 다른 질문이 있는데요...",
        "이해했습니다. 그럼 이 경우에는 어떻게 해야 할까요?",
        "선생님 말씀을 듣고 보니 제가 생각을 좀 더 정리해야겠어요."
    ];
    
    var randomResponse = responses[Math.floor(Math.random() * responses.length)];
    var chatContent = document.getElementById('chat-content');
    var currentTime = getCurrentTime();
    
    var clientMessage = document.createElement('div');
    clientMessage.className = 'message';
    
    var messageAvatar = document.createElement('div');
    messageAvatar.className = 'message-avatar';
    messageAvatar.textContent = '학생';
    
    var messageContent = document.createElement('div');
    messageContent.className = 'message-content';
    
    var messageBubble = document.createElement('div');
    messageBubble.className = 'message-bubble';
    messageBubble.textContent = randomResponse;
    
    var messageTime = document.createElement('div');
    messageTime.className = 'message-time';
    messageTime.textContent = currentTime;
    
    messageContent.appendChild(messageBubble);
    messageContent.appendChild(messageTime);
    clientMessage.appendChild(messageAvatar);
    clientMessage.appendChild(messageContent);
    chatContent.appendChild(clientMessage);
    
    chatContent.scrollTop = chatContent.scrollHeight;
}

// 텍스트 영역 자동 크기 조절
function autoResize(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 80) + 'px';
}

// 엔터키로 메시지 전송 (Shift+Enter는 줄바꿈)
function handleKeyPress(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

// 타이핑 인디케이터 표시
function showTypingIndicator() {
    document.getElementById('typingIndicator').style.display = 'block';
}

// 타이핑 인디케이터 숨김
function hideTypingIndicator() {
    document.getElementById('typingIndicator').style.display = 'none';
}

// 상담 기록 열기
function openNotepad() {
    var noteWindow = window.open('', 'CounselingNote', 'width=600,height=400');
    var noteContent = `
        <html>
        <head>
            <title>상담 기록</title>
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                textarea { width: 100%; height: 300px; border: 1px solid #ddd; padding: 10px; box-sizing: border-box; }
                .header { margin-bottom: 15px; font-weight: bold; }
                .save-btn { background: #3498db; color: white; border: none; padding: 10px 20px; margin-top: 10px; cursor: pointer; border-radius: 4px; }
            </style>
        </head>
        <body>
            <div class="header">상담 기록 - 이철수 학생 (진로/취업 상담)</div>
            <textarea placeholder="상담 내용을 기록하세요..."></textarea>
            <br>
            <button class="save-btn" onclick="alert('상담 기록이 저장되었습니다.'); window.close();">저장</button>
        </body>
        </html>
    `;
    noteWindow.document.write(noteContent);
    noteWindow.document.close();
}

// 상담 종료
function endSession() {
    if (confirm('상담을 종료하시겠습니까? 상담 기록이 자동으로 저장됩니다.')) {
        alert('상담이 종료되었습니다. 상담 기록이 저장되었습니다.');
        // 실제로는 상담 종료 처리 및 페이지 이동
    }
}

// 페이지 로드 시 설정
document.addEventListener('DOMContentLoaded', function() {
    var chatContent = document.getElementById('chat-content');
    chatContent.scrollTop = chatContent.scrollHeight;
    
    // 텍스트 영역 초기 설정
    var chatInput = document.getElementById('chatInput');
    autoResize(chatInput);
});