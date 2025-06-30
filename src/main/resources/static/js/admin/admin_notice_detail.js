// 사이드바 토글 기능
document.addEventListener('DOMContentLoaded', function() {
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
});

// 윈도우 리사이즈 시 클래스 정리
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (sidebar && content) {
        if (window.innerWidth > 768) {
            sidebar.classList.remove('show');
        } else {
            sidebar.classList.remove('collapsed');
            content.classList.remove('expanded');
        }
    }
});

// 공지사항 수정 페이지로 이동
function editNotice() {
    // 수정 페이지로 이동
    window.location.href = '/admin/notice_modify';
}

// 공지사항 목록 페이지로 이동
function goToNoticeList() {
    // 실제 구현 시에는 목록 페이지로 이동
    alert('공지사항 목록 페이지로 이동합니다.');
    // window.location.href = '/admin/notice/list';
}

// 현재 공지사항 ID 가져오기 (실제 구현 시 사용)
function getCurrentNoticeId() {
    // URL에서 공지사항 ID 추출
    const pathArray = window.location.pathname.split('/');
    return pathArray[pathArray.length - 1];
}

// 공지사항 삭제 기능 (필요 시 추가)
function deleteNotice() {
    const confirmMsg = '이 공지사항을 삭제하시겠습니까?\n삭제된 공지사항은 복구할 수 없습니다.';
    
    if (confirm(confirmMsg)) {
        // 실제 구현 시에는 서버에 삭제 요청
        alert('공지사항이 삭제되었습니다.');
        // 삭제 후 목록 페이지로 이동
        goToNoticeList();
    }
}

// 인쇄 기능
function printNotice() {
    window.print();
}

// 페이지 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('공지사항 상세 페이지 로드 완료');
    
    // 추가적인 초기화 로직이 필요한 경우 여기에 작성
    // 예: 조회수 증가, 공지사항 데이터 로드 등
});