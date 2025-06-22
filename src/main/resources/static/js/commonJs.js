// 사이드바 토글 기능 (데스크톱/모바일)
document.getElementById('sidebarToggle').addEventListener('click', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    // 모바일에서는 show 클래스 사용
    if (window.innerWidth <= 768) {
        sidebar.classList.toggle('show');
    } else {
        // 데스크톱에서는 collapsed 클래스 사용
        sidebar.classList.toggle('collapsed');
        content.classList.toggle('expanded');
    }
});


// 윈도우 리사이즈 시 클래스 정리
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


// 특정 페이지로 이동
function goToPage(page) {
    currentPage = page;
    updateTable();
}


// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    updateTable();
});