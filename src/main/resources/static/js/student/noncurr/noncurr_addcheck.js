// ✅ 서버 데이터 사용 (하드코딩 제거)
let programData = [];
let currentFilter = 'all';
let filteredData = [];
let serverCurrentDate = null;  // ✅ 서버 날짜 저장용

// ✅ HTML에서 서버 데이터 읽기 (만족도 조사 상태 포함)
function loadServerData() {
    const dataContainer = document.getElementById('applicationsData');
    
    serverCurrentDate = dataContainer.getAttribute('data-server-date');
    console.log('서버 현재 날짜 (아시아/서울):', serverCurrentDate);
    
    const appDataItems = dataContainer.querySelectorAll('.app-data-item');
    
    const serverApplications = [];
    
    appDataItems.forEach(item => {
        serverApplications.push({
            prgId: parseInt(item.getAttribute('data-app-id')),
            prgNm: item.getAttribute('data-app-name'),
            prgStDt: item.getAttribute('data-start-date'),
            prgEndDt: item.getAttribute('data-end-date'),
            applicationStatus: item.getAttribute('data-application-status'),
            satisfactionStatus: item.getAttribute('data-satisfaction-status') // ✅ 추가
        });
    });
    
    console.log('HTML에서 읽어온 서버 데이터:', serverApplications);
    return serverApplications;
}
// ✅ 서버 데이터를 화면용 형태로 변환
function convertServerDataToDisplayFormat(serverApplications) {
    return serverApplications.map((app, index) => ({
        id: app.prgId,
        name: app.prgNm,
        period: `${app.prgStDt} ~ ${app.prgEndDt}`,
        status: getProgramStatus(app),           // 상태 계산
        satisfaction: app.satisfactionStatus || getSatisfactionStatus(app) // ✅ 서버 데이터 우선 사용
    }));
}

// ✅ 프로그램 상태 계산 (서버 날짜 기준)
function getProgramStatus(app) {
    // ✅ 서버 날짜 사용 (아시아/서울 시간)
    const today = new Date(serverCurrentDate);
    const startDate = new Date(app.prgStDt);
    const endDate = new Date(app.prgEndDt);
    
    console.log(`프로그램 ${app.prgNm} 상태 계산:`);
    console.log(`  - 서버 기준 오늘: ${serverCurrentDate}`);
    console.log(`  - 운영 시작일: ${app.prgStDt}`);
    console.log(`  - 운영 종료일: ${app.prgEndDt}`);
    
    if (today < startDate) {
        console.log(`  → 결과: applied (운영 시작 전)`);
        return 'applied';  // 신청 (운영 시작 전)
    } else if (today >= startDate && today <= endDate) {
        console.log(`  → 결과: ongoing (운영 중)`);
        return 'ongoing';  // 진행 (운영 중)
    } else {
        console.log(`  → 결과: completed (운영 종료)`);
        return 'completed'; // 완료 (운영 종료)
    }
}

// ✅ 만족도 조사 상태 계산
function getSatisfactionStatus(app) {
    const status = getProgramStatus(app);
    
    // 운영기간이 끝났을 때만 만족도 조사 대상
    if (status === 'completed') {
        // 실제로는 서버에서 만족도 조사 완료 여부를 받아와야 함
        // 일단 기본적으로 '실시' 상태로 설정
        return 'pending';  // 항상 '실시' 버튼 표시
    }
    
    return 'none'; // 운영기간이 끝나지 않으면 '-' 표시
}
// 상태별 스타일 반환
function getStatusBadge(status) {
    const statusMap = {
        'applied': { class: 'status-applied', text: '신청' },
        'ongoing': { class: 'status-ongoing', text: '진행' },
        'completed': { class: 'status-completed', text: '완료' }
    };

    const statusInfo = statusMap[status];
    return `<span class="status-badge ${statusInfo.class}">${statusInfo.text}</span>`;
}

// 만족도 조사 상태 반환
function getSatisfactionElement(program) {
    if (program.satisfaction === 'none') {
        return '<span class="satisfaction-none">-</span>';
    }
    else if (program.satisfaction === 'pending') {
        return `<button class="satisfaction-btn" onclick="openSatisfactionSurvey(${program.id})">실시</button>`;
    }
    else if (program.satisfaction === 'completed') {
        return '<span class="satisfaction-completed">완료</span>';
    }
}

// 테이블 렌더링
function renderTable(data) {
    const tbody = document.getElementById('programTableBody');

    if (data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="no-results">검색 결과가 없습니다.</td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = data.map((program, index) => `
        <tr>
            <td>${index + 1}</td>
            <td class="program-name">${program.name}</td>
            <td>${program.period}</td>
            <td>${getStatusBadge(program.status)}</td>
            <td>${getSatisfactionElement(program)}</td>
        </tr>
    `).join('');
}

// 필터 설정
function setFilter(filterType) {
    currentFilter = filterType;

    // 모든 탭에서 active 클래스 제거
    document.querySelectorAll('.filter-tab').forEach(tab => {
        tab.classList.remove('active');
    });

    // 클릭된 탭에 active 클래스 추가
    event.target.classList.add('active');

    // 필터링 적용
    filterPrograms();
}

// 검색 버튼 클릭 처리
function performSearch() {
    filterPrograms();
}

// 프로그램 필터링
function filterPrograms() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase().trim();

    let filtered = [...programData];

    // 상태별 필터링
    if (currentFilter !== 'all') {
        filtered = filtered.filter(program => program.status === currentFilter);
    }

    // 검색어 필터링
    if (searchTerm) {
        filtered = filtered.filter(program => 
            program.name.toLowerCase().includes(searchTerm)
        );
    }

    filteredData = filtered;
    updatePagination(); // ✅ 필터링 후 페이지네이션 업데이트
}

// 만족도 조사 함수
function openSatisfactionSurvey(programId) {
    if (confirm('만족도 조사를 실시하시겠습니까?')) {
        // ✅ 실제 프로그램 ID를 파라미터로 전달
        window.location.href = `/student/noncurr/survey?prgId=${programId}`;
    }
}

// 페이지네이션 함수
let currentPage = 1;
const itemsPerPage = 10; // 페이지당 아이템 수

function changePage(page) {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    
    if (page === 'prev') {
        if (currentPage > 1) {
            currentPage--;
        }
    } 
    else if (page === 'next') {
        if (currentPage < totalPages) {
            currentPage++;
        }
    }
    else {
        currentPage = page;
    }

    updatePaginationButtons();
    renderCurrentPage();
    
    console.log('페이지 변경:', currentPage);
}

// ✅ 현재 페이지 데이터만 렌더링
function renderCurrentPage() {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const currentPageData = filteredData.slice(startIndex, endIndex);
    
    renderTable(currentPageData, startIndex); // startIndex를 넘겨서 번호 계산
}

// ✅ 테이블 렌더링 (번호 계산 수정)
function renderTable(data, startIndex = 0) {
    const tbody = document.getElementById('programTableBody');

    if (data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="no-results">검색 결과가 없습니다.</td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = data.map((program, index) => `
        <tr>
            <td>${startIndex + index + 1}</td>
            <td class="program-name">${program.name}</td>
            <td>${program.period}</td>
            <td>${getStatusBadge(program.status)}</td>
            <td>${getSatisfactionElement(program)}</td>
        </tr>
    `).join('');
}

// ✅ 페이지네이션 버튼 업데이트 (동적 생성) - 수정된 버전
function updatePaginationButtons() {
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const paginationContainer = document.querySelector('.pagination');
    
    // ✅ 데이터가 아예 없을 때만 페이지네이션 숨김
    if (filteredData.length === 0) {
        paginationContainer.style.display = 'none';
        return;
    } else {
        paginationContainer.style.display = 'flex';
    }
    
    let paginationHtml = '';
    
    // 이전 버튼
    paginationHtml += `
        <button onclick="changePage('prev')" ${currentPage === 1 ? 'disabled' : ''}>◀</button>
    `;
    
    // 페이지 번호들 (최대 5개만 표시)
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, startPage + 4);
    
    for (let i = startPage; i <= endPage; i++) {
        paginationHtml += `
            <button onclick="changePage(${i})" ${i === currentPage ? 'class="active"' : ''}>${i}</button>
        `;
    }
    
    // 다음 버튼
    paginationHtml += `
        <button onclick="changePage('next')" ${currentPage === totalPages ? 'disabled' : ''}>▶</button>
    `;
    
    paginationContainer.innerHTML = paginationHtml;
}

function updatePagination() {
    currentPage = 1; // 검색/필터링시 첫 페이지로
    updatePaginationButtons();
    renderCurrentPage();
}

// ✅ 페이지 로드 시 실행 (HTML data 속성에서 데이터 로드)
document.addEventListener('DOMContentLoaded', function() {
    console.log('신청 내역 페이지 로드 시작');
    
    // ✅ HTML의 data 속성에서 서버 데이터 읽기
    const serverApplications = loadServerData();
    
    if (serverApplications && serverApplications.length > 0) {
        console.log('서버 데이터:', serverApplications);
        programData = convertServerDataToDisplayFormat(serverApplications);
        console.log('변환된 데이터:', programData);
    } else {
        console.log('서버 데이터가 없습니다.');
        programData = [];
    }

    // 초기 테이블 렌더링
    filteredData = [...programData];
    updatePagination();
    
    console.log('신청 내역 페이지 로드 완료');
});