// 답변대기 문의 상세보기 페이지 전용 JavaScript

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
    
    if (confirm('답변을 등록하시겠습니까?')) {
        // 실제 저장 로직 구현
        alert('답변이 성공적으로 등록되었습니다.');
        window.location.href = 'QnA_waiting';
    }
}

// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 답변 내용 텍스트영역 자동 높이 조절
    const textarea = document.getElementById('answerContent');
    if (textarea) {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }
    
    // 답변 내용 글자 수 카운터 (선택사항)
    const maxLength = 1000;
    if (textarea) {
        const counterElement = document.createElement('div');
        counterElement.className = 'char-counter';
        counterElement.style.cssText = 'text-align: right; margin-top: 5px; font-size: 12px; color: #6c757d;';
        counterElement.textContent = `0 / ${maxLength}자`;
        textarea.parentNode.appendChild(counterElement);
        
        textarea.addEventListener('input', function() {
            const currentLength = this.value.length;
            counterElement.textContent = `${currentLength} / ${maxLength}자`;
            
            if (currentLength > maxLength) {
                counterElement.style.color = '#e74c3c';
                this.style.borderColor = '#e74c3c';
            } else {
                counterElement.style.color = '#6c757d';
                this.style.borderColor = '#ddd';
            }
        });
    }
});