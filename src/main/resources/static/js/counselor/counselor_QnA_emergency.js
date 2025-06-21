// 긴급문의 상세보기 페이지 전용 JavaScript

// 목록으로 돌아가기
function goBack() {
    if (confirm('작성 중인 답변이 있다면 저장되지 않습니다. 정말 돌아가시겠습니까?')) {
        window.location.href = '/counselor/QnA';
    }
}

// 답변 저장
function saveAnswer() {
    const content = document.getElementById('answerContent').value.trim();
    
    if (!content) {
        alert('답변 내용을 입력해주세요.');
        document.getElementById('answerContent').focus();
        return;
    }
    
    if (confirm('긴급 답변을 등록하시겠습니까?\n학생에게 즉시 알림이 발송됩니다.')) {
        // 실제 저장 로직 구현
        alert('긴급 답변이 성공적으로 등록되었습니다.\n학생에게 알림이 발송되었습니다.');
        window.location.href = 'counselor_QnA.html';
    }
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 긴급 문의 알림음 (선택사항)
    // const audio = new Audio('/sounds/alert.mp3');
    // audio.play().catch(e => console.log('Audio play failed'));
    
    // 긴급 문의 브라우저 알림 (선택사항)
    if (Notification.permission === 'granted') {
        new Notification('긴급 문의 알림', {
            body: '새로운 긴급 문의가 있습니다. 즉시 답변해주세요.',
            icon: '/images/urgent-icon.png'
        });
    } else if (Notification.permission !== 'denied') {
        Notification.requestPermission().then(function(permission) {
            if (permission === 'granted') {
                new Notification('긴급 문의 알림', {
                    body: '새로운 긴급 문의가 있습니다. 즉시 답변해주세요.',
                    icon: '/images/urgent-icon.png'
                });
            }
        });
    }
    
    // 긴급 문의 페이지 자동 새로고침 (5분마다 - 선택사항)
    // setInterval(function() {
    //     if (confirm('긴급 문의 상태를 확인하기 위해 페이지를 새로고침하시겠습니까?')) {
    //         location.reload();
    //     }
    // }, 5 * 60 * 1000); // 5분
});