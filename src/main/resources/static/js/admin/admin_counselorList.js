// 상담분류 필터 기능
document.getElementById('categoryFilter').addEventListener('change', function() {
    const selectedCategory = this.value;
    filterCounselorsByCategory(selectedCategory);
});

// 분야별 필터링 함수
function filterCounselorsByCategory(category) {
    const rows = document.querySelectorAll('.counselor-table tbody tr');
    
    rows.forEach(row => {
        const fieldCell = row.cells[3];
        const fieldText = fieldCell.textContent.trim();
        
        if (category === 'all') {
            row.style.display = '';
        } else {
            let shouldShow = false;
            switch(category) {
                case '심리상담':
                    shouldShow = fieldText === '심리상담';
                    break;
                case '위기상담':
                    shouldShow = fieldText === '위기상담';
                    break;
                case '익명상담':
                    shouldShow = fieldText === '익명상담';
                    break;
                case '진로상담':
                    shouldShow = fieldText === '진로상담';
                    break;
                case '학습컨설팅':
                    shouldShow = fieldText === '학습컨설팅';
                    break;
            }
            
            row.style.display = shouldShow ? '' : 'none';
        }
    });
    
    console.log('상담분류 필터:', category);
}

// 탭 전환 기능
document.querySelectorAll('.tab-item').forEach(tab => {
    tab.addEventListener('click', function(e) {
        e.preventDefault();
        
        document.querySelectorAll('.tab-item').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        
        const status = this.getAttribute('data-status');
        filterByEmploymentStatus(status);
        console.log('선택된 상태:', status);
    });
});

// 재직 상태별 필터링 함수
function filterByEmploymentStatus(status) {
    const rows = document.querySelectorAll('.counselor-table tbody tr');
    
    rows.forEach(row => {
        const statusCell = row.cells[6]; // 재직현황 컬럼
        const statusText = statusCell.textContent.trim();
        
        if (status === 'all') {
            row.style.display = '';
        } else if (status === 'active') {
            row.style.display = statusText.includes('재직') ? '' : 'none';
        } else if (status === 'inactive') {
            row.style.display = statusText.includes('퇴사') ? '' : 'none';
        }
    });
}

// 검색 기능
document.querySelector('.search-btn').addEventListener('click', function() {
    const searchType = document.querySelector('.search-select').value;
    const searchText = document.querySelector('.search-input').value.trim();
    
    if (searchText === '') {
        // 검색어가 없으면 모든 행 표시
        document.querySelectorAll('.counselor-table tbody tr').forEach(row => {
            row.style.display = '';
        });
        return;
    }
    
    console.log('검색 유형:', searchType);
    console.log('검색어:', searchText);
    
    searchCounselors(searchType, searchText);
});

// 등록 버튼 클릭시 상담사등록페이지로 이동 
document.querySelector('.register-btn').addEventListener('click', function() {
    location.href="./admin_counselorList_add";
});

// 상세 페이지로 이동하는 함수
function goToDetail(id) {
    window.open('counselor_detail.html?id=' + id, '_blank');
}

// 엔터키로 검색
document.querySelector('.search-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        document.querySelector('.search-btn').click();
    }
});

// 페이지네이션 기능
document.querySelectorAll('.pagination-btn.page-number').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.pagination-btn.page-number').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        const pageNumber = this.textContent;
        console.log('페이지 이동:', pageNumber);
    });
});

// 이전/다음 버튼 기능
document.querySelector('.pagination-btn.prev').addEventListener('click', function() {
    if (!this.disabled) {
        console.log('이전 페이지로 이동');
    }
});

document.querySelector('.pagination-btn.next').addEventListener('click', function() {
    if (!this.disabled) {
        console.log('다음 페이지로 이동');
    }
});

// 디버깅용 - 나중에 제거
setTimeout(function() {
    console.log('=== 메뉴 상태 확인 ===');
    const activeMainMenus = document.querySelectorAll('.nav-link.main-menu.active');
    const activeSubMenus = document.querySelectorAll('.sub-menu .nav-link.active');
    
    console.log('활성화된 대메뉴 개수:', activeMainMenus.length);
    console.log('활성화된 소메뉴 개수:', activeSubMenus.length);
    
    activeMainMenus.forEach(menu => {
        console.log('활성화된 대메뉴:', menu.textContent.trim());
    });
    
    activeSubMenus.forEach(menu => {
        console.log('활성화된 소메뉴:', menu.textContent.trim());
    });
}, 500);