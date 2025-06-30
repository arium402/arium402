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

// 메뉴 토글 함수
function toggleSubMenu(element) {
    // 클릭된 메뉴 아이템의 부모 nav-item 찾기
    const navItem = element.closest('.nav-item');
    
    // 다른 모든 메뉴 닫기
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item !== navItem) {
            item.classList.remove('menu-open');
        }
    });
    
    // 현재 메뉴 토글
    navItem.classList.toggle('menu-open');
    
    // 이벤트 전파 중단 (링크 이동 방지)
    event.preventDefault();
    return false;
}

// 테이블 행 클릭 이벤트 - 상세 통계 모달 열기
document.querySelectorAll('#statisticsTableBody tr').forEach(row => {
    row.addEventListener('click', function() {
        const programId = this.getAttribute('data-program-id');
        const programName = this.cells[1].textContent.trim();
        const department = this.cells[2].textContent.trim();
        const period = this.cells[3].textContent.trim();
        const participants = this.cells[4].textContent.trim();
        const responseRate = this.cells[5].textContent.trim();
        
        // 모달에 데이터 설정
        document.getElementById('modalProgramTitle').textContent = programName;
        document.getElementById('modalDepartment').textContent = department;
        document.getElementById('modalPeriod').textContent = period;
        document.getElementById('modalParticipants').textContent = participants;
        
        // 응답자 수 계산 (예: 15명 × 93.3% = 14명)
        const participantNum = parseInt(participants.replace('명', ''));
        const responsePercent = parseFloat(responseRate.replace('%', ''));
        const responders = Math.round(participantNum * responsePercent / 100);
        document.getElementById('modalResponders').textContent = `${responders}명 (${responseRate})`;
        
        // 모달 표시
        document.getElementById('detailModal').style.display = 'block';
    });
});

// 상세 통계 모달 닫기
window.closeDetailModal = function() {
    document.getElementById('detailModal').style.display = 'none';
}

// 키보드 이벤트 (ESC로 모달 닫기)
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('detailModal');
        if (modal.style.display === 'block') {
            closeDetailModal();
        }
    }
});

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('detailModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// 검색 함수
function searchStatistics() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput').value;
    const periodFilter = document.getElementById('periodFilter').value;
    
    if (searchInput.trim() === '') {
        alert('검색어를 입력해주세요.');
        return;
    }
    
    console.log(`검색 유형: ${searchType}, 검색어: ${searchInput}, 기간: ${periodFilter}`);
    // 실제 검색 로직 구현
    alert(`${searchType}으로 "${searchInput}" 통계 검색 중...`);
}

// 엔터키로 검색
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchStatistics();
    }
});

// 필터 변경 시 자동 검색
document.getElementById('periodFilter').addEventListener('change', function() {
    console.log('기간 필터 변경:', this.value);
    // 자동 필터링 로직 구현
});

// 페이지네이션 클릭 이벤트
document.querySelectorAll('.pagination .page-link').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        
        // 비활성화된 링크는 무시
        if (this.parentElement.classList.contains('disabled')) {
            return;
        }
        
        // 기존 활성 페이지 제거
        document.querySelectorAll('.pagination .page-item').forEach(item => {
            item.classList.remove('active');
        });
        
        // 새로운 활성 페이지 설정 (화살표가 아닌 경우)
        if (!this.classList.contains('page-arrow')) {
            this.parentElement.classList.add('active');
            const pageText = this.textContent.trim();
            console.log(`페이지 ${pageText}로 이동`);
        } else {
            // 화살표 클릭 처리
            const isNext = this.querySelector('.fa-angle-right');
            console.log(isNext ? '다음 페이지' : '이전 페이지');
        }
        
        // 실제 페이지 로딩 로직 구현
        // loadStatisticsPage(pageText);
    });
});