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
    
	//  메인 페이지만 처리
	if (currentPath.includes('admin_mileage_payment') && !currentPath.includes('admin_mileage_payment_add')) {
	    // 메인 페이지 초기화
	    initMainPage();
	}
});

// ============= 메인 페이지 로직 =============

function initMainPage() {
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
			console.log("programs 길이:", data.programs?.length);
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
    
    let start = startDt.length >= 10 ? startDt.substring(0, 10) : startDt;
    let end = endDt.length >= 10 ? endDt.substring(0, 10) : endDt;
    
    // 하이픈을 점으로 변경: 2025-07-04 → 2025.07.04
    start = start.replace(/-/g, '.');
    end = end.replace(/-/g, '.');
    
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
        
        //  전체 행에 클릭 이벤트 및 스타일 추가
        row.style.cursor = 'pointer';
        row.onclick = function() {
            viewProgramDetail(program.id);
        };
        
        //  호버 효과를 위한 이벤트 추가
        row.onmouseenter = function() {
            this.style.backgroundColor = '#f8f9fa';
        };
        row.onmouseleave = function() {
            this.style.backgroundColor = '';
        };
        
        row.innerHTML = `
            <td>${rowNumber}</td>
            <td class="program-name">
                <span class="program-text" style="color: #333;">
                    ${program.name}
                </span>
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
    // .pagination 클래스를 가진 DOM 요소를 찾음
    const pagination = document.querySelector('.pagination');
    if (!pagination) return; // 없으면 함수 종료 (에러 방지)

    // 기존 버튼들 전부 제거 (이전 페이지 버튼 초기화)
    pagination.innerHTML = '';

    //이전(‹) 버튼 생성
    const prevBtn = document.createElement('button'); // 버튼 생성
    prevBtn.className = 'pagination-btn';             // 공통 클래스 적용
    prevBtn.textContent = '‹';                        // 버튼에 표시될 문자

    // 현재 페이지가 1이면 이전 페이지 없음 → 버튼 비활성화
    if (currentPageNumber === 1) {
        prevBtn.disabled = true;
    } else {
        // 클릭 시 이전 페이지로 이동
        prevBtn.addEventListener('click', function() {
            changePage(currentPageNumber - 1);
        });
    }

    // pagination 영역에 버튼 추가
    pagination.appendChild(prevBtn);

    //숫자 버튼 범위 계산

    // 현재 페이지 기준으로 앞뒤 2개씩 보여줌
    const startPage = Math.max(1, currentPageNumber - 2);
    const endPage = Math.min(totalPages, currentPageNumber + 2);

    //숫자 버튼 생성
    for (let i = startPage; i <= endPage; i++) {

        const pageBtn = document.createElement('button'); // 버튼 생성
        pageBtn.className = 'pagination-btn';

        // 현재 페이지면 active 클래스 추가 (스타일용)
        if (i === currentPageNumber) {
            pageBtn.classList.add('active');
        }

        pageBtn.textContent = i; // 버튼에 페이지 번호 표시

        // 클릭하면 해당 페이지로 이동
        pageBtn.addEventListener('click', function() {
			console.log("숫자 버튼 클릭:", i);
            goToPageAPI(i);
			
			
			
        });

        // pagination 영역에 추가
        pagination.appendChild(pageBtn);
    }

    // 다음(›) 버튼 생성
    const nextBtn = document.createElement('button');
    nextBtn.className = 'pagination-btn';
    nextBtn.textContent = '›';

    // 마지막 페이지면 다음 없음 >> 비활성화
    if (currentPageNumber === totalPages) {
        nextBtn.disabled = true;
    } else {
        // 클릭 시 다음 페이지로 이동
        nextBtn.addEventListener('click', function() {
            changePage(currentPageNumber + 1);
        });
    }

    pagination.appendChild(nextBtn);

    // 하단 페이지 정보 표시
    const startItem = totalItems > 0 
        ? (currentPageNumber - 1) * currentSize + 1 
        : 0;

    const endItem = Math.min(currentPageNumber * currentSize, totalItems);

    const paginationInfo = document.getElementById('paginationInfo');

    if (paginationInfo) {
        paginationInfo.textContent =
            `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
    }
}


// 페이지 변경
function changePage(newPage) {
    if (newPage < 1 || isLoading) return;
    currentPage = newPage;
    loadProgramData();
}

// 특정 페이지로 이동
function goToPageAPI(pageNumber) {
	console.log("goToPageAPI 실행됨");
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
    window.location.href = `/admin/admin_mileage_payment_add?prgId=${prgId}`;
}


// 전역 함수로 노출
window.viewProgramDetail = viewProgramDetail;
window.changePage = changePage;
window.goToPageAPI = goToPageAPI;