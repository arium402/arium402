// 비교과 목록 페이지 JavaScript - API 연동 버전 (DB 컬럼명 통일)

// 전역 변수
let currentPage = 0;
let totalPages = 0;
let currentSearchParams = {
    searchKeyword: '',
    department: '',
    statusCode: null
};

// 페이지 로드시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 초기 데이터 로드
    loadProgramList();
    
    // 테이블 행 클릭 이벤트 설정
    initializeTableRowClicks();
    
    // 검색 입력 필드 엔터키 이벤트
    initializeSearchEvents();
    
    // 필터 변경 이벤트
    initializeFilterEvents();
    
    // 페이지네이션 이벤트
    initializePaginationEvents();
});

// 검색 이벤트 초기화
function initializeSearchEvents() {
    // 엔터키로 검색
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchPrograms();
            }
        });
    }
}

// 필터 이벤트 초기화
function initializeFilterEvents() {
    // 필터 변경 시 자동 검색
    const periodFilter = document.getElementById('periodFilter');
    if (periodFilter) {
        periodFilter.addEventListener('change', function() {
            console.log('기간 필터 변경:', this.value);
            filterPrograms();
        });
    }
    
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            console.log('상태 필터 변경:', this.value);
            filterPrograms();
        });
    }
}

// 페이지네이션 이벤트 초기화
function initializePaginationEvents() {
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
                loadPage(pageText);
            } else {
                // 화살표 클릭 처리
                const isNext = this.querySelector('.fa-angle-right');
                if (isNext) {
                    console.log('다음 페이지');
                    goToNextPage();
                } else {
                    console.log('이전 페이지');
                    goToPrevPage();
                }
            }
        });
    });
}

// 프로그램 목록 로드 (API 호출)
async function loadProgramList(page = 0, size = 10) {
    try {
        showLoading();
        
        const params = new URLSearchParams({
            page: page,
            size: size,
            sortBy: 'regDt',
            sortDir: 'desc'
        });
        
        // 검색 조건 추가
        if (currentSearchParams.searchKeyword) {
            params.append('searchKeyword', currentSearchParams.searchKeyword);
        }
        if (currentSearchParams.department) {
            params.append('department', currentSearchParams.department);
        }
        if (currentSearchParams.statusCode) {
            params.append('statusCode', currentSearchParams.statusCode);
        }
        
        const response = await fetch(`/api/admin/noncurr/list?${params}`);
        const result = await response.json();
        
        if (result.success) {
            currentPage = result.currentPage;
            totalPages = result.totalPages;
            
            // 테이블 업데이트
            updateTable(result.content);
            
            // 페이지네이션 업데이트
            updatePagination(result.totalPages, result.currentPage + 1);
            
            // 페이지 정보 업데이트
            updatePaginationInfo(result);
            
            console.log(`프로그램 목록 로드 완료 - 총 ${result.totalElements}건`);
        } else {
            throw new Error(result.message || '데이터 로드에 실패했습니다.');
        }
        
    } catch (error) {
        console.error('프로그램 목록 로드 실패:', error);
        showError('프로그램 목록을 불러오는데 실패했습니다: ' + error.message);
        
        // 에러 시 빈 테이블 표시
        updateTable([]);
    } finally {
        hideLoading();
    }
}

// 테이블 업데이트 함수 (원본 스타일 유지, DB 컬럼명 수정)
function updateTable(data) {
    const tbody = document.getElementById('programTableBody');
    tbody.innerHTML = '';
    
    if (!data || data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: #6c757d;">
                    <i class="fas fa-search" style="font-size: 48px; margin-bottom: 16px; opacity: 0.3;"></i>
                    <div>검색 결과가 없습니다.</div>
                </td>
            </tr>
        `;
        return;
    }
    
    data.forEach((program, index) => {
        const row = document.createElement('tr');
        row.setAttribute('data-program-id', program.programId);
        row.style.cursor = 'pointer';
        
        // 행 번호 계산 (페이지 기반)
        const rowNumber = (currentPage * 10) + index + 1;
        
        // 상태에 따른 CSS 클래스 결정
        let statusClass = 'open';
        switch(program.statusCode || program.status) {
            case 2: 
            case '진행': statusClass = 'pro'; break;
            case 3:
            case '완료': statusClass = 'completed'; break;
            case 4:
            case '인원 마감': statusClass = 'full'; break;
            default: statusClass = 'open';
        }
        
        // 모집기간과 운영기간 포맷
        const recruitmentPeriod = formatDatePeriod(program.recruitStart, program.recruitEnd);
        const operationPeriod = formatDatePeriod(program.prgStDt || program.operationStart, program.prgEndDt || program.operationEnd);
        const currentCapacity = program.currentCapacity || 0;
        const maxCapacity = program.maxCnt || program.capacity || 0;
        const statusText = getStatusText(program.statusCode || program.status);
        
        row.innerHTML = `
            <td>${rowNumber}</td>
            <td class="text-left">${escapeHtml(program.prgNm || program.programName)}</td>
            <td>${escapeHtml(program.department || '운영부서')}</td>
            <td>${recruitmentPeriod}</td>
            <td>${currentCapacity}/${maxCapacity}</td>
            <td>${operationPeriod}</td>
            <td><span class="status ${statusClass}">${statusText}</span></td>
        `;
        
        tbody.appendChild(row);
    });
    
    // 새로운 행에 클릭 이벤트 다시 바인딩
    initializeTableRowClicks();
}

// 페이지네이션 업데이트 함수 (원본 스타일 유지)
function updatePagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    // 이전 버튼
    const prevButton = document.createElement('li');
    prevButton.className = currentPage === 1 ? 'page-item disabled' : 'page-item';
    prevButton.innerHTML = `
        <a class="page-link page-arrow" href="#" aria-label="Previous">
            <i class="fas fa-angle-left"></i>
        </a>
    `;
    pagination.appendChild(prevButton);
    
    // 페이지 번호들
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = i === currentPage ? 'page-item active' : 'page-item';
        pageItem.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        pagination.appendChild(pageItem);
    }
    
    // 다음 버튼
    const nextButton = document.createElement('li');
    nextButton.className = currentPage === totalPages ? 'page-item disabled' : 'page-item';
    nextButton.innerHTML = `
        <a class="page-link page-arrow" href="#" aria-label="Next">
            <i class="fas fa-angle-right"></i>
        </a>
    `;
    pagination.appendChild(nextButton);
    
    // 페이지네이션 이벤트 다시 바인딩
    initializePaginationEvents();
}

// 테이블 행 클릭 이벤트 초기화
function initializeTableRowClicks() {
    document.querySelectorAll('#programTableBody tr[data-program-id]').forEach(row => {
        row.addEventListener('click', function() {
            const programId = this.getAttribute('data-program-id');
            const programName = this.cells[1].textContent.trim();
            
            console.log(`프로그램 ID: ${programId}, 이름: ${programName} 상세 페이지로 이동`);
            
            // 상세 페이지로 이동
            window.location.href = `/admin/noncurr_detail?id=${programId}`;
        });
        
        // 호버 효과
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f8f9fa';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
}

// 검색 함수
async function searchPrograms() {
    const searchType = document.getElementById('searchType')?.value || 'name';
    const searchInput = document.getElementById('searchInput')?.value?.trim() || '';
    const periodFilter = document.getElementById('periodFilter')?.value || '';
    const statusFilter = document.getElementById('statusFilter')?.value || '';
    
    console.log(`검색 유형: ${searchType}, 검색어: ${searchInput}, 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // 검색 조건 업데이트
    currentSearchParams = {
        searchKeyword: searchInput,
        department: searchType === 'department' ? searchInput : '',
        statusCode: getStatusCodeFromFilter(statusFilter)
    };
    
    // 첫 페이지부터 검색
    await loadProgramList(0);
}

// 필터링 함수
async function filterPrograms() {
    const periodFilter = document.getElementById('periodFilter')?.value || '';
    const statusFilter = document.getElementById('statusFilter')?.value || '';
    
    console.log(`필터 적용 - 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // 검색어는 유지하고 필터만 적용
    currentSearchParams.statusCode = getStatusCodeFromFilter(statusFilter);
    
    // 첫 페이지부터 필터링된 결과 로드
    await loadProgramList(0);
}

// 비교과 등록 함수
function registerProgram() {
    window.location.href = '/admin/noncurr_add';
}

// 페이지 로드 함수
async function loadPage(pageNumber) {
    console.log(`페이지 ${pageNumber} 로딩 중...`);
    
    // 1-based를 0-based로 변환
    const pageIndex = parseInt(pageNumber) - 1;
    await loadProgramList(pageIndex);
}

// 다음 페이지로 이동
function goToNextPage() {
    const currentPageElement = document.querySelector('.pagination .page-item.active .page-link');
    if (currentPageElement) {
        const currentPageNum = parseInt(currentPageElement.textContent);
        const nextPageNum = currentPageNum + 1;
        
        if (nextPageNum <= totalPages) {
            loadPage(nextPageNum);
        }
    }
}

// 이전 페이지로 이동
function goToPrevPage() {
    const currentPageElement = document.querySelector('.pagination .page-item.active .page-link');
    if (currentPageElement) {
        const currentPageNum = parseInt(currentPageElement.textContent);
        const prevPageNum = currentPageNum - 1;
        
        if (prevPageNum > 0) {
            loadPage(prevPageNum);
        }
    }
}

// 페이지네이션 정보 업데이트
function updatePaginationInfo(pageData) {
    const paginationInfoEl = document.getElementById('paginationInfo');
    if (paginationInfoEl) {
        const { currentPage, size, totalElements } = pageData;
        const startItem = (currentPage * size) + 1;
        const endItem = Math.min((currentPage + 1) * size, totalElements);
        paginationInfoEl.textContent = `총 ${totalElements}건 중 ${startItem}-${endItem}건 표시`;
    }
}

// 상태 필터를 상태 코드로 변환
function getStatusCodeFromFilter(statusFilter) {
    const statusMap = {
        'open': 1,
        'pro': 2, 
        'completed': 3,
        'full': 4
    };
    
    return statusMap[statusFilter] || null;
}

// 상태 코드를 CSS 클래스로 변환
function getStatusClass(statusCode) {
    const classMap = {
        1: 'open',
        2: 'pro',
        3: 'completed', 
        4: 'full',
        'OPEN': 'open',
        'PROGRESS': 'pro',
        'COMPLETED': 'completed', 
        'FULL': 'full'
    };
    
    return classMap[statusCode] || 'open';
}

// 상태 코드에 따른 텍스트 반환
function getStatusText(status) {
    switch (status) {
        case 1: 
        case 'OPEN': return '모집중';
        case 2: 
        case 'PROGRESS':
        case '진행': return '운영중';
        case 3: 
        case 'COMPLETED':
        case '완료': return '완료';
        case 4:
        case 'FULL':
        case '인원 마감': return '마감';
        case 0: return '중단';
        default: return '알수없음';
    }
}

// 프로그램 수정
function editProgram(programId) {
    window.location.href = `/admin/noncurr/edit/${programId}`;
}

// 프로그램 삭제
async function deleteProgram(programId, prgNm) {
    if (!confirm(`'${prgNm}' 프로그램을 정말 삭제하시겠습니까?\n\n삭제된 데이터는 복구할 수 없습니다.`)) {
        return;
    }
    
    try {
        showLoading();
        
        const response = await fetch(`/api/admin/noncurr/delete/${programId}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showSuccess('프로그램이 성공적으로 삭제되었습니다.');
            loadProgramList(currentPage);
        } else {
            showError(result.message || '프로그램 삭제에 실패했습니다.');
        }
        
    } catch (error) {
        console.error('프로그램 삭제 실패:', error);
        showError('프로그램 삭제 중 오류가 발생했습니다.');
    } finally {
        hideLoading();
    }
}

// 유틸리티 함수들
function showLoading() {
    const tableBody = document.getElementById('programTableBody');
    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px;">
                    <i class="fas fa-spinner fa-spin" style="font-size: 24px; color: #007bff;"></i>
                    <div style="margin-top: 8px;">데이터를 불러오는 중...</div>
                </td>
            </tr>
        `;
    }
}

function hideLoading() {
    // 로딩 표시는 데이터 로드 완료시 자동으로 사라짐
}

function showError(message) {
    console.error(message);
    alert(message);
}

function showSuccess(message) {
    alert(message); // 실제 프로젝트에서는 토스트 메시지나 모달로 대체
}

function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

function formatDatePeriod(startDate, endDate) {
    if (!startDate || !endDate) {
        // 단일 period 문자열인 경우
        const period = startDate || endDate;
        if (!period) return '';
        
        // "2024-06-01~2024-06-15" 형태를 "2024.06.01~06.15"로 변환
        return period.replace(/(\d{4})-(\d{2})-(\d{2})~(\d{4})-(\d{2})-(\d{2})/, 
                             '$1.$2.$3~$5.$6');
    }
    
    // 시작일과 종료일이 따로 있는 경우
    const formatDate = (dateString) => {
        if (!dateString) return '';
        try {
            const date = new Date(dateString);
            return date.toISOString().split('T')[0].replace(/-/g, '.');
        } catch (error) {
            return dateString;
        }
    };
    
    const formattedStart = formatDate(startDate);
    const formattedEnd = formatDate(endDate);
    
    return `${formattedStart}~${formattedEnd}`;
}

// 검색 초기화
function resetSearch() {
    document.getElementById('searchKeyword').value = '';
    document.getElementById('department').value = '';
    document.getElementById('statusCode').value = '';
    loadProgramList(0, true);
}

// 페이지 크기 변경
function changePageSize() {
    currentSize = parseInt(document.getElementById('pageSize').value);
    loadProgramList(0, true);
}

// 공통 JavaScript 로드 후 실행
if (typeof activateMenuByCurrentUrl === 'function') {
    activateMenuByCurrentUrl();
}