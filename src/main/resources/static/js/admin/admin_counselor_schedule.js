let currentPage = 1;
let itemsPerPage = 5;


// 상담분류 필터 기능
document.getElementById('counselingType').addEventListener('change', function() {
    const selectedCategory = this.value;
    filterCounselorsByCategory(selectedCategory);
});

// 분야별 필터링 함수
function filterCounselorsByCategory(category) {
    const rows = document.querySelectorAll('.data-table tbody tr');
    
    rows.forEach(row => {
        const fieldCell = row.cells[3];
        const fieldText = fieldCell.textContent.trim();
        
        if (category === 'all') {
            row.style.display = '';
        } else {
            let shouldShow = false;
            switch(category) {
                case '심리 상담':
                    shouldShow = fieldText === '심리 상담';
                    break;
                case '위기 상황 상담':
                    shouldShow = fieldText === '위기 상황 상담';
                    break;
                case '익명 상담':
                    shouldShow = fieldText === '익명 상담';
                    break;
                case '진로 및 취업 관련 상담':
                    shouldShow = fieldText === '진로 및 취업 관련 상담';
                    break;
                case '학습 방법 관련 상담':
                    shouldShow = fieldText === '학습 방법 관련 상담';
                    break;
            }
            
            row.style.display = shouldShow ? '' : 'none';
        }
    });
    
    console.log('상담분류 필터:', category);
}


// 검색 기능
document.querySelector('.search-btn').addEventListener('click', function() {
    const searchType = document.querySelector('.search-type-select').value;
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

// 검색 함수
function searchCounselors(searchType, searchText) {
    const rows = document.querySelectorAll('.data-table tbody tr');
    
    rows.forEach(row => {
        let cellIndex = 0;
        switch(searchType) {
            case 'name': cellIndex = 1; break;
            case 'empno': cellIndex = 2; break;
        }
        
        const cellText = row.cells[cellIndex].textContent.trim().toLowerCase();
        const searchLower = searchText.toLowerCase();
        
        row.style.display = cellText.includes(searchLower) ? '' : 'none';
    });
}


// 페이지네이션 업데이트
function updatePagination() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginationContainer = document.querySelector('.pagination');
    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, filteredData.length);
    
    // 페이지네이션 버튼 생성
    let paginationHTML = '';
    
    // 이전 버튼
    paginationHTML += `<button class="pagination-btn ${currentPage === 1 ? 'disabled' : ''}" onclick="goToPage(${currentPage - 1})">
        <i class="fas fa-angle-left"></i>
    </button>`;
    
    // 페이지 번호 버튼들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `<button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>`;
    }
    
    // 다음 버튼
    paginationHTML += `<button class="pagination-btn ${currentPage === totalPages ? 'disabled' : ''}" onclick="goToPage(${currentPage + 1})">
        <i class="fas fa-angle-right"></i>
    </button>`;
    
    paginationContainer.innerHTML = paginationHTML;
    
    // 페이지 정보 업데이트
    document.querySelector('.pagination-info').textContent = 
        `총 ${filteredData.length}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 이동
function goToPage(page) {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    if (page >= 1 && page <= totalPages && page !== currentPage) {
        currentPage = page;
        renderTable();
    }
}

// 일정 상세 페이지로 이동
function goToScheduleDetail(id) {
    location.href = './admin_counselor_scheduleDetail?id=' + id;
}


// 상담분류 필터 변경 시 자동 검색
document.getElementById('counselingType').addEventListener('change', searchCounselors);
