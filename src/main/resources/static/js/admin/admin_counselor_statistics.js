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

// 통계 데이터 샘플
const statisticsData = {
    1: { // 1학기
        completed: 15,
        scheduled: 8,
        gradeData: [6, 4, 3, 2] // 1,2,3,4학년 순
    },
    2: { // 2학기
        completed: 12,
        scheduled: 5,
        gradeData: [4, 5, 2, 1]
    }
};

let currentPage = 1;
let itemsPerPage = 5;
let filteredData = [...counselorData];
let currentSemester = 1;
let chart = null;

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
        
        // 행 클릭 이벤트 추가
        row.addEventListener('click', () => openModal(counselor));
        
        tableBody.appendChild(row);
    });

    updatePagination();
}

// 페이지네이션 업데이트
function updatePagination() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginationContainer = document.getElementById('pagination');
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
    document.getElementById('paginationInfo').textContent = 
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

// 검색 기능
function searchCounselors() {
    const searchType = document.getElementById('searchType').value;
    const searchValue = document.getElementById('searchInput').value.trim().toLowerCase();
    const counselingType = document.getElementById('counselingType').value;
    const yearFilter = document.getElementById('yearFilter').value;

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

// 모달 열기
function openModal(counselor) {
    document.getElementById('modalCounselorName').textContent = counselor.name;
    document.getElementById('modalCounselorField').textContent = counselor.field;
    
    // 선택된 년도로 제목 설정
    const selectedYear = document.getElementById('yearFilter').value;
    document.getElementById('semesterTitle').textContent = `${selectedYear}학년도 ${currentSemester}학기 통계`;
    
    updateModalStats();
    createChart();
    
    document.getElementById('statisticsModal').classList.add('show');
    document.body.style.overflow = 'hidden';
}

// 모달 닫기
function closeModal() {
    document.getElementById('statisticsModal').classList.remove('show');
    document.body.style.overflow = 'auto';
    
    if (chart) {
        chart.destroy();
        chart = null;
    }
}

// 학기 변경
function changeSemester(semester) {
    currentSemester = semester;
    
    // 버튼 상태 업데이트
    document.querySelectorAll('.semester-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-semester="${semester}"]`).classList.add('active');
    
    // 제목 업데이트 (년도 필터 값 사용)
    const selectedYear = document.getElementById('yearFilter').value;
    document.getElementById('semesterTitle').textContent = `${selectedYear}학년도 ${semester}학기 통계`;
    
    updateModalStats();
    updateChart();
}

// 모달 통계 업데이트
function updateModalStats() {
    const stats = statisticsData[currentSemester];
    document.getElementById('completedCount').textContent = `${stats.completed} 건`;
    document.getElementById('scheduledCount').textContent = `${stats.scheduled} 건`;
}

// 차트 생성
function createChart() {
    const ctx = document.getElementById('gradeChart').getContext('2d');
    const stats = statisticsData[currentSemester];
    
    chart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['1학년', '2학년', '3학년', '4학년'],
            datasets: [{
                data: stats.gradeData,
                backgroundColor: [
                    '#3498db',
                    '#e74c3c',
                    '#f39c12',
                    '#27ae60'
                ],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

// 차트 업데이트
function updateChart() {
    if (chart) {
        const stats = statisticsData[currentSemester];
        chart.data.datasets[0].data = stats.gradeData;
        chart.update();
    }
}

// 모달 외부 클릭 시 닫기
document.getElementById('statisticsModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// 년도 필터 변경 시 자동 검색
document.getElementById('yearFilter').addEventListener('change', searchCounselors);

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
