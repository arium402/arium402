// 샘플 데이터
const counselorData = [
    { id: 1, name: '김상담', empNo: 'EMP001', field: '심리상담', phone: '010-1234-5678', email: 'kim@example.com' },
    { id: 2, name: '이상담', empNo: 'EMP002', field: '진로상담', phone: '010-2345-6789', email: 'lee@example.com' },
    { id: 3, name: '박상담', empNo: 'EMP003', field: '학습컨설팅', phone: '010-3456-7890', email: 'park@example.com' },
    { id: 4, name: '최상담', empNo: 'EMP004', field: '위기상담', phone: '010-4567-8901', email: 'choi@example.com' },
    { id: 5, name: '정상담', empNo: 'EMP005', field: '익명상담', phone: '010-5678-9012', email: 'jung@example.com' },
    { id: 6, name: '강상담', empNo: 'EMP006', field: '심리상담', phone: '010-6789-0123', email: 'kang@example.com' },
    { id: 7, name: '윤상담', empNo: 'EMP007', field: '진로상담', phone: '010-7890-1234', email: 'yoon@example.com' },
    { id: 8, name: '임상담', empNo: 'EMP008', field: '학습컨설팅', phone: '010-8901-2345', email: 'lim@example.com' }
];

let currentPage = 1;
let itemsPerPage = 5;
let filteredData = [...counselorData];

// 테이블 렌더링
function renderTable() {
    const tableBody = document.getElementById('counselorTableBody');
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageData = filteredData.slice(startIndex, endIndex);

    tableBody.innerHTML = '';
    
    pageData.forEach((counselor, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="col-no">${startIndex + index + 1}</td>
            <td class="col-name">${counselor.name}</td>
            <td class="col-empno">${counselor.empNo}</td>
            <td class="col-field">${counselor.field}</td>
        `;
        row.onclick = function() { goToScheduleDetail(counselor.id); };
        tableBody.appendChild(row);
    });

    updatePagination();
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
    window.open('counselor_schedule_detail.html?id=' + id, '_blank');
}

// 검색 기능
function searchCounselors() {
    const searchType = document.getElementById('searchType').value;
    const searchValue = document.getElementById('searchInput').value.trim().toLowerCase();
    const counselingType = document.getElementById('counselingType').value;

    filteredData = counselorData.filter(counselor => {
        // 상담분류 필터
        let typeMatch = true;
        if (counselingType) {
            typeMatch = counselor.field.includes(counselingType);
        }

        // 검색어 필터
        let searchMatch = true;
        if (searchValue) {
            switch (searchType) {
                case 'name':
                    searchMatch = counselor.name.toLowerCase().includes(searchValue);
                    break;
                case 'empno':
                    searchMatch = counselor.empNo.toLowerCase().includes(searchValue);
                    break;
                case 'phone':
                    searchMatch = counselor.phone.includes(searchValue);
                    break;
                case 'email':
                    searchMatch = counselor.email.toLowerCase().includes(searchValue);
                    break;
            }
        }

        return typeMatch && searchMatch;
    });

    currentPage = 1;
    renderTable();
}

// 상담분류 필터 변경 시 자동 검색
document.getElementById('counselingType').addEventListener('change', searchCounselors);

// 검색 입력 시 엔터키로 검색
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchCounselors();
    }
});

// 초기 테이블 렌더링
document.addEventListener('DOMContentLoaded', function() {
    renderTable();
});