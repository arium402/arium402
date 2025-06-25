// 테이블 행 클릭 이벤트 - 상세 페이지로 이동
document.addEventListener('DOMContentLoaded', function() {
    // 테이블 행 클릭 이벤트 설정
    initializeTableRowClicks();
    
    // 검색 입력 필드 엔터키 이벤트
    initializeSearchEvents();
    
    // 필터 변경 이벤트
    initializeFilterEvents();
    
    // 페이지네이션 이벤트
    initializePaginationEvents();
    
    // 실제 데이터 로드
    loadProgramData();
});

// 프로그램 데이터 로드 (API 호출)
function loadProgramData() {
    fetch('/api/admin/noncurr')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.programs) {
                updateTableWithData(data.programs);
                updatePaginationWithData(data);
            }
        })
        .catch(error => {
            console.error('데이터 로딩 실패:', error);
        });
}

// 테이블 데이터 업데이트
function updateTableWithData(programs) {
    const tbody = document.getElementById('programTableBody');
    tbody.innerHTML = '';
    
    programs.forEach((program, index) => {
        const row = document.createElement('tr');
        row.setAttribute('data-program-id', program.prgId);
        row.innerHTML = `
            <td>${index + 1}</td>
            <td class="text-left">${program.prgNm}</td>
            <td>${program.deptNm || '-'}</td>
            <td>${formatDateRange(program.recruitStart, program.recruitEnd)}</td>
            <td>${program.currentCnt || 0}/${program.maxCnt}</td>
            <td>${formatDateRange(program.prgStDt, program.prgEndDt)}</td>
            <td><span class="status ${getStatusClass(program.prgStatNm)}">${program.prgStatNm}</span></td>
        `;
        tbody.appendChild(row);
    });
    
    // 클릭 이벤트 재바인딩
    initializeTableRowClicks();
}

// 날짜 범위 포맷팅
function formatDateRange(startDate, endDate) {
    if (!startDate || !endDate) return '-';
    return `${startDate}~${endDate}`;
}

// 상태 클래스 반환
function getStatusClass(status) {
    switch(status) {
        case '진행': case '진행중': return 'pro';
        case '완료': return 'completed';
        case '인원 마감': case '마감': return 'full';
        default: return 'open';
    }
}

// 테이블 행 클릭 이벤트 초기화
function initializeTableRowClicks() {
    document.querySelectorAll('#programTableBody tr').forEach(row => {
        row.addEventListener('click', function() {
            const programId = this.getAttribute('data-program-id');
            const programName = this.cells[1].textContent.trim();
            
            // 상세 페이지로 이동
            console.log(`프로그램 ID: ${programId}, 이름: ${programName} 상세 페이지로 이동`);
            
            // 실제 구현
            window.location.href = `/admin/noncurr_detail?id=${programId}`;
        });
    });
}

// 검색 이벤트 초기화
function initializeSearchEvents() {
    // 엔터키로 검색
    document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchPrograms();
        }
    });
}

// 필터 이벤트 초기화
function initializeFilterEvents() {
    // 필터 변경 시 자동 검색
    document.getElementById('periodFilter').addEventListener('change', function() {
        console.log('기간 필터 변경:', this.value);
        filterPrograms();
    });
    
    document.getElementById('statusFilter').addEventListener('change', function() {
        console.log('상태 필터 변경:', this.value);
        filterPrograms();
    });
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

// 검색 함수
function searchPrograms() {
    const searchType = document.getElementById('searchType').value;
    const searchKeyword = document.getElementById('searchKeyword').value;
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    if (searchKeyword.trim() === '') {
        alert('검색어를 입력해주세요.');
        return;
    }
    
    console.log(`검색 유형: ${searchType}, 검색어: ${searchKeyword}, 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // API 호출
    const params = new URLSearchParams();
    params.append('searchType', searchType);
    params.append('searchKeyword', searchKeyword);
    if (periodFilter) params.append('periodFilter', periodFilter);
    if (statusFilter) params.append('statusFilter', statusFilter);
    
    fetch(`/api/admin/noncurr?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateTableWithData(data.programs);
                updatePaginationWithData(data);
            } else {
                alert('검색 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('검색 오류:', error);
            alert('검색 중 오류가 발생했습니다.');
        });
}

// 필터링 함수
function filterPrograms() {
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    console.log(`필터 적용 - 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // API 호출
    const params = new URLSearchParams();
    if (periodFilter) params.append('periodFilter', periodFilter);
    if (statusFilter) params.append('statusFilter', statusFilter);
    
    fetch(`/api/admin/noncurr?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateTableWithData(data.programs);
                updatePaginationWithData(data);
            }
        })
        .catch(error => {
            console.error('필터링 오류:', error);
        });
}

// 비교과 등록 함수
function registerProgram() {
    window.location.href = '/admin/noncurr_add';
}

// 페이지 로드 함수
function loadPage(pageNumber) {
    console.log(`페이지 ${pageNumber} 로딩 중...`);
    
    const params = new URLSearchParams();
    params.append('page', pageNumber - 1); // 0부터 시작
    params.append('size', 10);
    
    fetch(`/api/admin/noncurr?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateTableWithData(data.programs);
                updatePaginationWithData(data);
            }
        })
        .catch(error => {
            console.error('페이지 로딩 오류:', error);
        });
}

// 다음 페이지로 이동
function goToNextPage() {
    const currentPage = document.querySelector('.pagination .page-item.active .page-link');
    if (currentPage) {
        const currentPageNum = parseInt(currentPage.textContent);
        const nextPageNum = currentPageNum + 1;
        
        loadPage(nextPageNum);
    }
}

// 이전 페이지로 이동
function goToPrevPage() {
    const currentPage = document.querySelector('.pagination .page-item.active .page-link');
    if (currentPage) {
        const currentPageNum = parseInt(currentPage.textContent);
        const prevPageNum = currentPageNum - 1;
        
        if (prevPageNum > 0) {
            loadPage(prevPageNum);
        }
    }
}

// 페이징 정보 업데이트
function updatePaginationWithData(data) {
    const paginationInfo = document.getElementById('paginationInfo');
    const startItem = data.currentPage * data.size + 1;
    const endItem = Math.min((data.currentPage + 1) * data.size, data.totalElements);
    
    paginationInfo.textContent = 
        `총 ${data.totalElements}건 중 ${startItem}-${endItem}건 표시`;
    
    // 페이지 버튼 업데이트
    updatePaginationButtons(data.currentPage + 1, data.totalPages);
}

// 페이지 버튼 업데이트
function updatePaginationButtons(currentPage, totalPages) {
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
    
    // 이벤트 재바인딩
    initializePaginationEvents();
}