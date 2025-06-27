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
});

// 테이블 행 클릭 이벤트 초기화
function initializeTableRowClicks() {
    document.querySelectorAll('#programTableBody tr').forEach(row => {
        row.addEventListener('click', function() {
            const programId = this.getAttribute('data-program-id');
            const programName = this.cells[1].textContent.trim();
            
            // 상세 페이지로 이동 (실제로는 적절한 URL로 수정)
            console.log(`프로그램 ID: ${programId}, 이름: ${programName} 상세 페이지로 이동`);
            
            // 실제 구현시에는 이렇게 사용:
            // window.location.href = `/admin/admin_noncurr_detail?id=${programId}`;
            
            // 데모용으로 알림창 표시
            alert(`"${programName}" 상세 페이지로 이동합니다.\n(실제로는 상세 페이지가 열립니다)`);
        });
    });
}

// 검색 이벤트 초기화
function initializeSearchEvents() {
    // 엔터키로 검색
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
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
        // 자동 필터링 로직 구현
        filterPrograms();
    });
    
    document.getElementById('statusFilter').addEventListener('change', function() {
        console.log('상태 필터 변경:', this.value);
        // 자동 필터링 로직 구현
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
    const searchInput = document.getElementById('searchInput').value;
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    if (searchInput.trim() === '') {
        alert('검색어를 입력해주세요.');
        return;
    }
    
    console.log(`검색 유형: ${searchType}, 검색어: ${searchInput}, 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // 실제 검색 로직 구현
    // 예: Ajax 요청으로 서버에서 데이터 가져오기
    /*
    $.ajax({
        url: '/admin/searchNoncurriculars',
        method: 'GET',
        data: {
            searchType: searchType,
            searchKeyword: searchInput,
            period: periodFilter,
            status: statusFilter
        },
        success: function(data) {
            updateTable(data);
        },
        error: function() {
            alert('검색 중 오류가 발생했습니다.');
        }
    });
    */
    
    alert(`${searchType}으로 "${searchInput}" 검색 중...`);
}

// 필터링 함수
function filterPrograms() {
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    console.log(`필터 적용 - 기간: ${periodFilter}, 상태: ${statusFilter}`);
    
    // 실제 필터링 로직 구현
    // 예: Ajax 요청으로 필터된 데이터 가져오기
    /*
    $.ajax({
        url: '/admin/filterNoncurriculars',
        method: 'GET',
        data: {
            period: periodFilter,
            status: statusFilter
        },
        success: function(data) {
            updateTable(data);
        }
    });
    */
}

// 비교과 등록 함수
function registerProgram() {
    // 실제 등록 페이지로 이동
    window.location.href = '/admin/noncurr_add';  // ← 경로 수정
}

// 페이지 로드 함수
function loadPage(pageNumber) {
    console.log(`페이지 ${pageNumber} 로딩 중...`);
    
    // 실제 페이지 로딩 로직 구현
    /*
    $.ajax({
        url: '/admin/getNoncurriculars',
        method: 'GET',
        data: {
            page: pageNumber,
            size: 10
        },
        success: function(data) {
            updateTable(data.content);
            updatePagination(data.totalPages, pageNumber);
        }
    });
    */
}

// 다음 페이지로 이동
function goToNextPage() {
    const currentPage = document.querySelector('.pagination .page-item.active .page-link');
    if (currentPage) {
        const currentPageNum = parseInt(currentPage.textContent);
        const nextPageNum = currentPageNum + 1;
        
        // 다음 페이지 버튼이 있는지 확인
        const nextPageLink = document.querySelector(`.pagination .page-link[href="#"]:not(.page-arrow)`);
        if (nextPageLink && parseInt(nextPageLink.textContent) === nextPageNum) {
            loadPage(nextPageNum);
        }
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

// 테이블 업데이트 함수
function updateTable(data) {
    const tbody = document.getElementById('programTableBody');
    tbody.innerHTML = '';
    
    if (!data || data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 20px;">검색 결과가 없습니다.</td></tr>';
        return;
    }
    
    data.forEach((program, index) => {
        const row = document.createElement('tr');
        row.setAttribute('data-program-id', program.id);
        
        // 상태에 따른 CSS 클래스 결정
        let statusClass = 'open';
        switch(program.status) {
            case '진행': statusClass = 'pro'; break;
            case '완료': statusClass = 'completed'; break;
            case '인원 마감': statusClass = 'full'; break;
            default: statusClass = 'open';
        }
        
        row.innerHTML = `
            <td>${index + 1}</td>
            <td class="text-left">${program.name}</td>
            <td>${program.department}</td>
            <td>${program.recruitmentPeriod}</td>
            <td>${program.currentCapacity}/${program.maxCapacity}</td>
            <td>${program.operationPeriod}</td>
            <td><span class="status ${statusClass}">${program.status}</span></td>
        `;
        
        tbody.appendChild(row);
    });
    
    // 새로운 행에 클릭 이벤트 다시 바인딩
    initializeTableRowClicks();
}

// 페이지네이션 업데이트 함수
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
    
    // 페이지 정보 업데이트
    const startItem = (currentPage - 1) * 10 + 1;
    const endItem = Math.min(currentPage * 10, totalPages * 10);
    const totalItems = totalPages * 10;
    
    document.getElementById('paginationInfo').textContent = 
        `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}