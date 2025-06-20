// 샘플 프로그램 데이터
const programData = [
    { id: 1, name: "창업 아이디어 경진대회", period: "2025.03.15 ~ 2025.06.15", participants: 45, status: "waiting" },
    { id: 2, name: "코딩 부트캠프", period: "2025.02.01 ~ 2025.02.28", participants: 32, status: "completed" },
    { id: 3, name: "AI 활용 워크샵", period: "2025.04.01 ~ 2025.04.30", participants: 28, status: "waiting" },
    { id: 4, name: "글로벌 리더십 프로그램", period: "2025.01.10 ~ 2025.03.10", participants: 20, status: "completed" },
    { id: 5, name: "데이터 분석 캠프", period: "2025.05.01 ~ 2025.05.30", participants: 38, status: "waiting" },
    { id: 6, name: "디지털 마케팅 세미나", period: "2025.03.01 ~ 2025.03.31", participants: 52, status: "waiting" },
    { id: 7, name: "프로젝트 매니지먼트 과정", period: "2025.02.15 ~ 2025.04.15", participants: 25, status: "completed" },
    { id: 8, name: "UX/UI 디자인 워크샵", period: "2025.04.10 ~ 2025.05.10", participants: 30, status: "waiting" },
    { id: 9, name: "블록체인 기초 교육", period: "2025.01.20 ~ 2025.02.20", participants: 18, status: "completed" },
    { id: 10, name: "소셜벤처 창업 캠프", period: "2025.06.01 ~ 2025.06.30", participants: 42, status: "waiting" }
];

let currentTab = 'all';
let currentPage = 1;
const itemsPerPage = 5;

// 사이드바 토글 기능
document.getElementById('sidebarToggle').addEventListener('click', function() {
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');
    
    if (window.innerWidth <= 768) {
        sidebar.classList.toggle('show');
    } else {
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

// 탭 전환 기능
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        // 탭 버튼 활성화 상태 변경
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        // 현재 탭 설정 및 페이지 리셋
        currentTab = this.dataset.tab;
        currentPage = 1;
        
        // 테이블 업데이트
        updateTable();
    });
});

// 테이블 업데이트 함수
function updateTable() {
    const tbody = document.getElementById('programTableBody');
    let filteredData = programData;

    // 탭에 따른 데이터 필터링
    if (currentTab === 'waiting') {
        filteredData = programData.filter(program => program.status === 'waiting');
    } else if (currentTab === 'completed') {
        filteredData = programData.filter(program => program.status === 'completed');
    }

    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, endIndex);

    // 테이블 생성
    tbody.innerHTML = '';
    paginatedData.forEach(program => {
        const row = document.createElement('tr');
        
        const statusClass = program.status === 'waiting' ? 'status-waiting' : 'status-completed';
        const statusText = program.status === 'waiting' ? '대기' : '완료';
        
        row.innerHTML = `
            <td>${program.id}</td>
            <td class="program-name">${program.name}</td>
            <td class="program-period">${program.period}</td>
            <td class="program-participants">${program.participants}명</td>
            <td><span class="status-badge ${statusClass}">${statusText}</span></td>
        `;
        
        tbody.appendChild(row);
    });

    // 페이지네이션 업데이트
    updatePagination(filteredData.length);
}

// 페이지네이션 업데이트 함수
function updatePagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.querySelector('.pagination');
    const startItem = (currentPage - 1) * itemsPerPage + 1;
    const endItem = Math.min(currentPage * itemsPerPage, totalItems);
    
    // 페이지네이션 버튼 재생성
    pagination.innerHTML = `
        <button class="pagination-btn" id="prevBtn" onclick="changePage(-1)" ${currentPage === 1 ? 'disabled' : ''}>‹</button>
    `;

    // 페이지 번호 버튼들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        pagination.innerHTML += `
            <button class="pagination-btn ${i === currentPage ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>
        `;
    }

    // 다음 버튼
    pagination.innerHTML += `
        <button class="pagination-btn" id="nextBtn" onclick="changePage(1)" ${currentPage === totalPages ? 'disabled' : ''}>›</button>
    `;

    // 페이지 정보 업데이트
    document.getElementById('paginationInfo').textContent = 
        `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 변경 함수
function changePage(direction) {
    let filteredData = programData;
    if (currentTab === 'waiting') {
        filteredData = programData.filter(program => program.status === 'waiting');
    } else if (currentTab === 'completed') {
        filteredData = programData.filter(program => program.status === 'completed');
    }

    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        updateTable();
    }
}




