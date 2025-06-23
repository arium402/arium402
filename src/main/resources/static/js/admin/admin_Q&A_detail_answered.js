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

// 목록으로 돌아가기
function goBack() {
    alert('문의게시판 목록으로 돌아갑니다.');
    // window.history.back(); 또는 특정 URL로 이동
    // window.location.href = 'inquiry-management.html';
}

// 답변 수정
function editAnswer() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.classList.add('show');
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
}

// 모달 닫기
function closeEditModal() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.classList.remove('show');
        document.body.style.overflow = ''; // 스크롤 복원
    }
}

// 답변 수정 저장
function saveEditAnswer() {
    const editContent = document.getElementById('editAnswerContent');
    if (!editContent) return;
    
    const content = editContent.value.trim();
    
    if (!content) {
        alert('답변 내용을 입력해주세요.');
        return;
    }
    
    if (confirm('답변을 수정하시겠습니까?')) {
        // 기존 답변 내용 업데이트
        const answerContentDiv = document.querySelector('.answer-content');
        if (answerContentDiv) {
            answerContentDiv.innerHTML = content.replace(/\n/g, '<br>');
        }
        
        // 모달 닫기
        closeEditModal();
        alert('답변이 수정되었습니다.');
        
        // 실제로는 서버로 데이터 전송
        // API 호출 등의 로직 추가
    }
}

// 모달 외부 클릭 시 닫기
function setupModalEvents() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeEditModal();
            }
        });
    }
}

// ESC 키로 모달 닫기
function setupKeyboardEvents() {
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const modal = document.getElementById('editModal');
            if (modal && modal.classList.contains('show')) {
                closeEditModal();
            }
        }
    });
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

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 설정
    setupSidebarToggle();
    
    // 윈도우 리사이즈 설정
    setupWindowResize();
    
    // 모달 이벤트 설정
    setupModalEvents();
    
    // 키보드 이벤트 설정
    setupKeyboardEvents();
    
    // 게시판 관리 > 문의게시판 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const qnaSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    if (boardManagementMenu) {
        boardManagementMenu.classList.add('active');
    }
    if (qnaSubmenu) {
        qnaSubmenu.style.maxHeight = '200px';
    }
    
    console.log('문의게시판 상세 (답변완료) 페이지가 로드되었습니다.');
});