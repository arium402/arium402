// 답변완료 문의 상세보기 페이지 전용 JavaScript

// 목록으로 돌아가기
function goBack() {
    window.location.href = '/counselor/QnA';
}

// 답변 수정 모드 활성화
function enableEdit() {
    document.getElementById('answerView').classList.add('editing');
    document.getElementById('answerEdit').classList.add('active');
    
    // 텍스트영역에 포커스
    const textarea = document.getElementById('answerTextarea');
    if (textarea) {
        textarea.focus();
        // 커서를 텍스트 끝으로 이동
        textarea.setSelectionRange(textarea.value.length, textarea.value.length);
    }
}

// 답변 수정 취소
function cancelEdit() {
    if (confirm('수정을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
        document.getElementById('answerView').classList.remove('editing');
        document.getElementById('answerEdit').classList.remove('active');
        
        // 원본 내용으로 복원
        const originalContent = document.querySelector('.existing-answer-content').textContent;
        document.getElementById('answerTextarea').value = originalContent;
    }
}

// 답변 저장
function saveAnswer() {
    const newAnswer = document.getElementById('answerTextarea').value.trim();
    
    if (!newAnswer) {
        alert('답변 내용을 입력해주세요.');
        document.getElementById('answerTextarea').focus();
        return;
    }
    
    if (confirm('답변을 수정하시겠습니까?')) {
        // 실제로는 서버에 저장하고 화면을 업데이트
        document.querySelector('.existing-answer-content').textContent = newAnswer;
        
        // 수정 일시 업데이트
        const now = new Date();
        const timeString = now.getFullYear() + '-' + 
                          String(now.getMonth() + 1).padStart(2, '0') + '-' + 
                          String(now.getDate()).padStart(2, '0') + ' ' + 
                          String(now.getHours()).padStart(2, '0') + ':' + 
                          String(now.getMinutes()).padStart(2, '0');
        
        const answerInfo = document.querySelector('.existing-answer-info');
        answerInfo.innerHTML = `<strong>김상담 상담사</strong> | ${timeString} <span style="color: #e6a456;">(수정됨)</span>`;
        
        // 수정 모드 종료
        document.getElementById('answerView').classList.remove('editing');
        document.getElementById('answerEdit').classList.remove('active');
        
        alert('답변이 수정되었습니다.');
    }
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 텍스트영역 자동 높이 조절
    const textarea = document.getElementById('answerTextarea');
    if (textarea) {
        // 초기 높이 설정
        textarea.style.height = 'auto';
        textarea.style.height = (textarea.scrollHeight) + 'px';
        
        // 입력 시 높이 자동 조절
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }
    
    // ESC 키로 수정 취소
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const editMode = document.getElementById('answerEdit');
            if (editMode && editMode.classList.contains('active')) {
                cancelEdit();
            }
        }
    });
    
    // Ctrl+S로 저장
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 's') {
            e.preventDefault();
            const editMode = document.getElementById('answerEdit');
            if (editMode && editMode.classList.contains('active')) {
                saveAnswer();
            }
        }
    });
    
    // 글자 수 카운터 추가 (선택사항)
    if (textarea) {
        const maxLength = 2000;
        const counterElement = document.createElement('div');
        counterElement.className = 'char-counter';
        counterElement.style.cssText = 'text-align: right; margin-top: 5px; font-size: 12px; color: #6c757d;';
        
        function updateCounter() {
            const currentLength = textarea.value.length;
            counterElement.textContent = `${currentLength} / ${maxLength}자`;
            
            if (currentLength > maxLength) {
                counterElement.style.color = '#e74c3c';
                textarea.style.borderColor = '#e74c3c';
            } else {
                counterElement.style.color = '#6c757d';
                textarea.style.borderColor = '#ddd';
            }
        }
        
        // 초기 카운터 설정
        updateCounter();
        
        // 텍스트영역 다음에 카운터 추가
        textarea.parentNode.insertBefore(counterElement, textarea.nextSibling);
        
        // 입력 이벤트 리스너
        textarea.addEventListener('input', updateCounter);
    }
});