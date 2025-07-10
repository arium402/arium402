// 마일리지 지급 관리 JavaScript - 메인/상세 페이지 통합

// 전역 변수
let currentTab = 'all';
let currentPage = 1;
let currentSize = 5;
let programData = [];
let isLoading = false;

// API 엔드포인트
const API_ENDPOINTS = {
    programs: '/api/admin/mileage_programs',
    participants: '/api/admin/mileage_participants',
    payment: '/api/admin/mileage_payment',
    statistics: '/api/admin/mileage_statistics',
    validate: '/api/admin/mileage_validate'
};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    console.log('페이지 로드:', currentPath);
    
    if (currentPath.includes('mileage_payment_detail')) {
        // 상세 페이지 초기화
        initDetailPage();
    } else if (currentPath.includes('admin_mileage_payment')) {
        // 메인 페이지 초기화
        initMainPage();
    }
});

// ============= 메인 페이지 로직 =============

function initMainPage() {
    console.log('마일리지 지급 메인 페이지 초기화');
    initMainEventListeners();
    loadProgramData();
}

function initMainEventListeners() {
    // 탭 버튼 이벤트
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // 탭 버튼 활성화 상태 변경
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 현재 탭 설정 및 페이지 리셋
            currentTab = this.dataset.tab;
            currentPage = 1;
            
            // API에서 데이터 로드
            loadProgramData();
        });
    });
}

// API에서 프로그램 데이터 로드
async function loadProgramData() {
    if (isLoading) return;
    
    try {
        isLoading = true;
        
        // API 파라미터 구성 (서버는 0부터 시작하므로 -1)
        const params = new URLSearchParams({
            page: (currentPage - 1).toString(),
            size: currentSize.toString(),
            status: currentTab === 'all' ? '' : currentTab
        });
        
        console.log('API 호출:', `${API_ENDPOINTS.programs}?${params}`);
        
        try {
            const response = await fetch(`${API_ENDPOINTS.programs}?${params}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const data = await response.json();
            console.log('API 응답:', data);
            
            if (data.success) {
                // 기존 programData 형식으로 변환
                programData = (data.programs || []).map(program => ({
                    id: program.prgId,
                    name: program.prgNm,
                    period: formatPeriod(program.prgStDt, program.prgEndDt),
                    participants: program.currentCnt || 0,
                    status: mapStatus(program.prgStatNm || program.applicationStatus)
                }));
                
                // 기존 함수들 그대로 사용
                updateTable();
                updatePaginationFromAPI(data.pagination);
            } else {
                throw new Error(data.error || '데이터 로드 실패');
            }
        } catch (apiError) {
            console.warn('API 호출 실패, 테스트 데이터 사용:', apiError.message);
            
            // 서버 오류시 테스트 데이터 사용
            programData = getTestProgramData();
            updateTable();
            updatePagination(programData.length, Math.ceil(programData.length / currentSize), currentPage);
        }
        
    } catch (error) {
        console.error('전체 로직 실패:', error);
        
        // 최종 폴백: 빈 데이터
        programData = [];
        updateTable();
    } finally {
        isLoading = false;
    }
}

// 테스트 데이터 생성 함수
function getTestProgramData() {
    const testData = [
        { id: 1, name: "창업 아이디어 경진대회", period: "2025.03.15 ~ 2025.06.15", participants: 45, status: "waiting" },
        { id: 2, name: "코딩 부트캠프", period: "2025.02.01 ~ 2025.02.28", participants: 32, status: "completed" },
        { id: 3, name: "AI 활용 워크샵", period: "2025.04.01 ~ 2025.04.30", participants: 28, status: "waiting" },
        { id: 4, name: "글로벌 리더십 프로그램", period: "2025.01.10 ~ 2025.03.10", participants: 20, status: "completed" },
        { id: 5, name: "데이터 분석 캠프", period: "2025.05.01 ~ 2025.05.30", participants: 38, status: "waiting" }
    ];
    
    // 탭에 따른 필터링
    if (currentTab === 'waiting') {
        return testData.filter(program => program.status === 'waiting');
    } else if (currentTab === 'completed') {
        return testData.filter(program => program.status === 'completed');
    }
    
    return testData;
}

// API 응답의 상태를 기존 형식으로 변환
function mapStatus(apiStatus) {
    const statusMap = {
        'waiting': 'waiting',
        'completed': 'completed',
        'not_ready': 'waiting',
        'no_participants': 'waiting'
    };
    return statusMap[apiStatus] || 'waiting';
}

// API 응답의 기간 포맷팅
function formatPeriod(startDt, endDt) {
    if (!startDt || !endDt) return '기간 정보 없음';
    
    const start = startDt.length >= 10 ? startDt.substring(0, 10) : startDt;
    const end = endDt.length >= 10 ? endDt.substring(0, 10) : endDt;
    return `${start} ~ ${end}`;
}

// API 페이지네이션 정보로 기존 페이지네이션 업데이트
function updatePaginationFromAPI(pagination) {
    if (!pagination) return;
    
    const totalItems = pagination.totalElements;
    const totalPages = pagination.totalPages;
    const currentPageFromAPI = pagination.currentPage + 1; // API는 0부터, UI는 1부터
    
    // 기존 updatePagination 함수 재사용
    updatePagination(totalItems, totalPages, currentPageFromAPI);
}

// 기존 테이블 업데이트 함수
function updateTable() {
    const tbody = document.getElementById('programTableBody');
    if (!tbody) return;
    
    let filteredData = programData;

    // 탭에 따른 데이터 필터링
    if (currentTab === 'waiting') {
        filteredData = programData.filter(program => program.status === 'waiting');
    } else if (currentTab === 'completed') {
        filteredData = programData.filter(program => program.status === 'completed');
    }

    // 테이블 생성
    tbody.innerHTML = '';
    
    if (filteredData.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-4">
                    <div class="empty-message">
                        <i class="fas fa-inbox fa-2x text-muted mb-2"></i>
                        <p class="text-muted">표시할 프로그램이 없습니다.</p>
                    </div>
                </td>
            </tr>
        `;
        return;
    }
    
    filteredData.forEach((program, index) => {
        const row = document.createElement('tr');
        
        const statusClass = program.status === 'waiting' ? 'status-waiting' : 'status-completed';
        const statusText = program.status === 'waiting' ? '대기' : '완료';
        const rowNumber = (currentPage - 1) * currentSize + index + 1;
        
        row.innerHTML = `
            <td>${rowNumber}</td>
            <td class="program-name">
                <a href="javascript:void(0)" onclick="viewProgramDetail(${program.id})" class="program-link">
                    ${program.name}
                </a>
            </td>
            <td class="program-period">${program.period}</td>
            <td class="program-participants">${program.participants}명</td>
            <td><span class="status-badge ${statusClass}">${statusText}</span></td>
        `;
        
        tbody.appendChild(row);
    });
}

// 기존 페이지네이션 업데이트 함수
function updatePagination(totalItems, totalPages, currentPageNumber) {
    const pagination = document.querySelector('.pagination');
    if (!pagination) return;
    
    const startItem = totalItems > 0 ? (currentPageNumber - 1) * currentSize + 1 : 0;
    const endItem = Math.min(currentPageNumber * currentSize, totalItems);
    
    // 페이지네이션 버튼 재생성
    pagination.innerHTML = `
        <button class="pagination-btn" onclick="changePage(${currentPageNumber - 1})" ${currentPageNumber === 1 ? 'disabled' : ''}>‹</button>
    `;

    // 페이지 번호 버튼들
    const startPage = Math.max(1, currentPageNumber - 2);
    const endPage = Math.min(totalPages, currentPageNumber + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        pagination.innerHTML += `
            <button class="pagination-btn ${i === currentPageNumber ? 'active' : ''}" onclick="goToPage(${i})">${i}</button>
        `;
    }

    // 다음 버튼
    pagination.innerHTML += `
        <button class="pagination-btn" onclick="changePage(${currentPageNumber + 1})" ${currentPageNumber === totalPages ? 'disabled' : ''}>›</button>
    `;

    // 페이지 정보 업데이트
    const paginationInfo = document.getElementById('paginationInfo');
    if (paginationInfo) {
        paginationInfo.textContent = `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
    }
}

// 페이지 변경
function changePage(newPage) {
    if (newPage < 1 || isLoading) return;
    currentPage = newPage;
    loadProgramData();
}

// 특정 페이지로 이동
function goToPage(pageNumber) {
    currentPage = pageNumber;
    loadProgramData();
}

// 프로그램 상세보기
function viewProgramDetail(prgId) {
    if (!prgId) {
        alert('프로그램 ID가 유효하지 않습니다.');
        return;
    }
    
    console.log('프로그램 상세보기:', prgId);
    window.location.href = `/admin/mileage_payment_detail?prgId=${prgId}`;
}

// ============= 상세 페이지 로직 (나중에 구현) =============

function initDetailPage() {
    console.log('마일리지 지급 상세 페이지 초기화');
    
    // URL에서 프로그램 ID 추출
    const urlParams = new URLSearchParams(window.location.search);
    const programId = parseInt(urlParams.get('prgId'));
    
    if (!programId) {
        alert('프로그램 ID가 유효하지 않습니다.');
        window.location.href = '/admin/admin_mileage_payment';
        return;
    }
    
    // 상세 페이지 로직은 나중에 구현
    console.log('상세 페이지 로직 구현 예정. 프로그램 ID:', programId);
}

// 전역 함수로 노출
window.viewProgramDetail = viewProgramDetail;
window.changePage = changePage;
window.goToPage = goToPage;