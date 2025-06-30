// 사이드바 토글 기능
function setupSidebarToggle() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            const sidebar = document.getElementById('layoutSidenav_nav');
            const content = document.getElementById('layoutSidenav_content');
            
            if (window.innerWidth <= 768) {
                sidebar.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                content.classList.toggle('expanded');
            }
        });
    }
}

// 윈도우 리사이즈 시 클래스 정리
function setupWindowResize() {
    window.addEventListener('resize', function() {
        const sidebar = document.getElementById('layoutSidenav_nav');
        const content = document.getElementById('layoutSidenav_content');
        
        if (window.innerWidth > 768) {
            sidebar.classList.remove('show');
        } else {
            sidebar.classList.remove('collapsed');
            content.classList.remove('expanded');
        }
    });
}

// 답변 폼 제출 처리
function setupAnswerForm() {
    const answerForm = document.getElementById('answerForm');
    if (answerForm) {
        answerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const answerContent = document.getElementById('answerContent');
            if (!answerContent) return;
            
            const content = answerContent.value.trim();
            
            if (!content) {
                alert('답변 내용을 입력해주세요.');
                answerContent.focus();
                return;
            }
            
            if (confirm('답변을 등록하시겠습니까?')) {
                alert('답변이 등록되었습니다.');
                // 실제로는 서버로 데이터 전송
                // 답변 등록 후 목록으로 이동하거나 페이지 새로고침
            }
        });
    }
}

// 답변 취소
function clearAnswer() {
    const answerContent = document.getElementById('answerContent');
    if (!answerContent) return;
    
    const content = answerContent.value.trim();
    
    if (content) {
        if (confirm('작성 중인 답변을 취소하시겠습니까?')) {
            answerContent.value = '';
            answerContent.focus();
        }
    } else {
        answerContent.value = '';
    }
}

// 목록으로 돌아가기
function goBack() {
    alert('문의게시판 목록으로 돌아갑니다.');
    // window.history.back(); 또는 특정 URL로 이동
    // window.location.href = 'inquiry-management.html';
}

// 게시물 상태 전환
function togglePostStatus() {
    const statusBadge = document.querySelector('.status-badge');
    const toggleBtn = document.querySelector('.btn-status-toggle');
    
    if (!statusBadge || !toggleBtn) return;
    
    const isCurrentlyPublic = statusBadge.classList.contains('status-public');
    
    if (isCurrentlyPublic) {
        if (confirm('이 게시물을 비공개로 전환하시겠습니까?\n비공개 전환 시 학생에게 보이지 않습니다.')) {
            // 비공개로 전환
            statusBadge.textContent = '비공개';
            statusBadge.className = 'status-badge status-private';
            toggleBtn.innerHTML = '<i class="fas fa-eye"></i> 공개 전환';
            toggleBtn.className = 'btn-status-toggle public';
            alert('게시물이 비공개로 전환되었습니다.');
        }
    } else {
        if (confirm('이 게시물을 공개로 전환하시겠습니까?')) {
            // 공개로 전환
            statusBadge.textContent = '공개';
            statusBadge.className = 'status-badge status-public';
            toggleBtn.innerHTML = '<i class="fas fa-eye-slash"></i> 비공개 전환';
            toggleBtn.className = 'btn-status-toggle';
            alert('게시물이 공개로 전환되었습니다.');
        }
    }
}

// 텍스트 영역 자동 크기 조정
function setupAutoResize() {
    const textarea = document.getElementById('answerContent');
    if (textarea) {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }
}

// 글자 수 카운터 설정
function setupCharacterCounter() {
    const textarea = document.getElementById('answerContent');
    if (!textarea) return;
    
    // 글자 수 표시 요소 생성
    const counter = document.createElement('div');
    counter.className = 'character-counter';
    counter.style.cssText = `
        text-align: right;
        font-size: 0.85rem;
        color: #6c757d;
        margin-top: 5px;
    `;
    
    textarea.parentNode.appendChild(counter);
    
    // 글자 수 업데이트 함수
    function updateCounter() {
        const currentLength = textarea.value.length;
        const maxLength = 2000; // 최대 글자 수 설정
        counter.textContent = `${currentLength}/${maxLength}`;
        
        if (currentLength > maxLength * 0.9) {
            counter.style.color = '#e74c3c';
        } else if (currentLength > maxLength * 0.7) {
            counter.style.color = '#f39c12';
        } else {
            counter.style.color = '#6c757d';
        }
    }
    
    // 이벤트 리스너 추가
    textarea.addEventListener('input', updateCounter);
    
    // 초기 카운터 설정
    updateCounter();
}

// 답변 임시저장 기능
function setupAutoSave() {
    const textarea = document.getElementById('answerContent');
    if (!textarea) return;
    
    let autoSaveTimer;
    
    textarea.addEventListener('input', function() {
        clearTimeout(autoSaveTimer);
        autoSaveTimer = setTimeout(() => {
            const content = this.value.trim();
            if (content) {
                // 임시저장 (localStorage 사용)
                localStorage.setItem('tempAnswer', content);
                console.log('답변 임시저장됨');
            }
        }, 3000); // 3초 후 자동 저장
    });
    
    // 페이지 로드 시 임시저장된 내용 복원
    const tempContent = localStorage.getItem('tempAnswer');
    if (tempContent && confirm('임시저장된 답변이 있습니다. 복원하시겠습니까?')) {
        textarea.value = tempContent;
        textarea.dispatchEvent(new Event('input')); // 글자 수 카운터 업데이트
    }
}

// 임시저장 내용 삭제
function clearTempAnswer() {
    localStorage.removeItem('tempAnswer');
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 설정
    setupSidebarToggle();
    
    // 윈도우 리사이즈 설정
    setupWindowResize();
    
    // 답변 폼 설정
    setupAnswerForm();
    
    // 텍스트 영역 자동 크기 조정
    setupAutoResize();
    
    // 글자 수 카운터 설정
    setupCharacterCounter();
    
    // 답변 임시저장 설정
    setupAutoSave();
    
    // 게시판 관리 > 문의게시판 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const qnaSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    if (boardManagementMenu) {
        boardManagementMenu.classList.add('active');
    }
    if (qnaSubmenu) {
        qnaSubmenu.style.maxHeight = '200px';
    }
    
    // 페이지 종료 시 임시저장 내용 정리 (답변 등록 성공 시)
    window.addEventListener('beforeunload', function(e) {
        const textarea = document.getElementById('answerContent');
        if (textarea && textarea.value.trim()) {
            e.preventDefault();
            e.returnValue = '작성 중인 답변이 있습니다. 페이지를 떠나시겠습니까?';
        }
    });
    
    console.log('문의게시판 상세 (답변대기) 페이지가 로드되었습니다.');
});