// 메시지 전송
function sendMessage() {
    const input = document.getElementById('chatInput');
    const message = input.value.trim();
    
    if (message === '') return;
    
    const chatContent = document.getElementById('chat-content');
    const currentTime = new Date().toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true 
    });
    
    // 사용자 메시지 추가
    const userMessage = `
        <div class="message user">
            <div class="message-avatar">학생</div>
            <div class="message-content">
                <div class="message-bubble">${message}</div>
                <div class="message-time">${currentTime}</div>
            </div>
        </div>
    `;
    
    chatContent.insertAdjacentHTML('beforeend', userMessage);
    
    // 입력창 비우기
    input.value = '';
    
    // 스크롤을 맨 아래로
    chatContent.scrollTop = chatContent.scrollHeight;
    
    // 간단한 자동 응답 (실제로는 서버와 통신)
    setTimeout(() => {
        addCounselorResponse();
    }, 1000);
}

// 상담사 자동 응답 (예시)
function addCounselorResponse() {
    const responses = [
        "말씀해 주신 내용을 잘 들었습니다. 좀 더 자세히 설명해 주실 수 있을까요?",
        "그런 상황이라면 많이 힘드셨을 것 같아요. 어떤 부분이 가장 어려우신가요?",
        "충분히 이해됩니다. 함께 해결방법을 찾아보도록 하겠습니다.",
        "말씀해 주셔서 감사합니다. 다른 관련된 상황도 있으신가요?"
    ];
    
    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
    const chatContent = document.getElementById('chat-content');
    const currentTime = new Date().toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true 
    });
    
    const counselorMessage = `
        <div class="message">
            <div class="message-avatar">상담</div>
            <div class="message-content">
                <div class="message-bubble">${randomResponse}</div>
                <div class="message-time">${currentTime}</div>
            </div>
        </div>
    `;
    
    chatContent.insertAdjacentHTML('beforeend', counselorMessage);
    chatContent.scrollTop = chatContent.scrollHeight;
}

// 엔터키로 메시지 전송
function handleKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

// 상담 요청 수락
function acceptRequest() {
    alert('상담 요청을 수락했습니다.');
    document.getElementById('requestContainer').style.display = 'none';
    document.getElementById('endContainer').style.display = 'block';
}

// 상담 요청 거절
function rejectRequest() {
    if (confirm('상담 요청을 거절하시겠습니까?')) {
        alert('상담 요청을 거절했습니다.');
        // 실제로는 페이지를 닫거나 다른 페이지로 이동
    }
}

// 채팅 종료
function endChat() {
    if (confirm('채팅을 종료하시겠습니까?')) {
        alert('채팅이 종료되었습니다.');
        // 실제로는 채팅 종료 처리 및 페이지 이동
    }
}

// 페이지 로드 시 채팅 영역을 맨 아래로 스크롤
document.addEventListener('DOMContentLoaded', function() {
    const chatContent = document.getElementById('chat-content');
    chatContent.scrollTop = chatContent.scrollHeight;
});