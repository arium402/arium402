// 비교과 목록 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    //  URL 파라미터에서 success 확인 (등록 성공 알림)
    const urlParams = new URLSearchParams(window.location.search);
    const success = urlParams.get('success');

    if (success === 'true') {
        alert('비교과 프로그램이 성공적으로 등록되었습니다.');
        // URL에서 파라미터 제거 (새로고침 시 알림 재출력 방지)
        window.history.replaceState({}, document.title, window.location.pathname);
    }
    
    // 검색 입력 필드 엔터키 이벤트
    initializeSearchEvents();
    
    // 필터 변경 이벤트
    initializeFilterEvents();
});

// 데이터 개수에 따른 테이블 높이 조정 함수
function adjustTableHeightByDataCount() {
    const tableBody = document.getElementById('programTableBody');
    const tableContainer = document.querySelector('.table-container');
    const tableContent = document.querySelector('.table-content');
    
    if (tableBody && tableContainer && tableContent) {
        // 실제 데이터 행 개수 계산 (빈 메시지 행 제외)
        const rows = tableBody.querySelectorAll('tr');
        const dataRows = Array.from(rows).filter(row => 
            !row.textContent.includes('등록된 비교과 프로그램이 없습니다')
        );
        
        // 데이터 개수에 따라 클래스 적용
        if (dataRows.length <= 3) {
            // 3개 이하일 때는 고정 높이 사용
            tableContainer.classList.add('small-data-count');
            tableContent.classList.add('small-data-count');
            tableContainer.classList.remove('normal-data-count');
            tableContent.classList.remove('normal-data-count');
        } else {
            // 4개 이상일 때는 flex 사용
            tableContainer.classList.add('normal-data-count');
            tableContent.classList.add('normal-data-count');
            tableContainer.classList.remove('small-data-count');
            tableContent.classList.remove('small-data-count');
        }
        
        console.log(`데이터 개수: ${dataRows.length}개, 적용된 스타일: ${dataRows.length <= 3 ? 'small-data-count' : 'normal-data-count'}`);
    }
}




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
    const statusFilter = document.getElementById('statusFilter');
    
    if (periodFilter) {
        periodFilter.addEventListener('change', function() {
            console.log('기간 필터 변경:', this.value);
            filterPrograms();
        });
    }
    
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            console.log('상태 필터 변경:', this.value);
            filterPrograms();
        });
    }
}

//  실제 검색 함수
function searchPrograms() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput').value;
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    if (searchInput.trim() === '') {
        alert('검색어를 입력해주세요.');
        return;
    }
    
	//  모든 파라미터 포함하여 URL 생성
	const params = new URLSearchParams();
	params.append('search', searchInput.trim());
	params.append('searchType', searchType);
	if (periodFilter) params.append('period', periodFilter);
	if (statusFilter) params.append('status', statusFilter);
	    
	window.location.href = `/admin/noncurr_list?${params.toString()}`;
}
	
//  실제 필터링 함수
function filterPrograms() {
	const periodFilter = document.getElementById('periodFilter').value;
	const statusFilter = document.getElementById('statusFilter').value;
	const currentSearch = document.getElementById('searchInput').value;
	const searchType = document.getElementById('searchType').value;
    
    //  필터 적용 - URL 파라미터로 이동
	const params = new URLSearchParams();
	if (currentSearch.trim()) {
	    params.append('search', currentSearch.trim());
	    params.append('searchType', searchType);
	}
    if (periodFilter) params.append('period', periodFilter);
    if (statusFilter) params.append('status', statusFilter);
    
    window.location.href = `/admin/noncurr_list?${params.toString()}`;
}

//  상세 페이지 이동 함수 (HTML에서 onclick으로 호출)
function goToDetail(row) {
    const programId = row.getAttribute('data-program-id');
    const programName = row.cells[1].textContent.trim();
    
    console.log(`프로그램 ID: ${programId}, 이름: ${programName} 상세 페이지로 이동`);
    
    //  실제 상세 페이지로 이동
	window.location.href = `/admin/noncurr_detail?id=${programId}`;
}

//  비교과 등록 함수 (기존과 동일)
function registerProgram() {
    window.location.href = '/admin/noncurr_add';
}

//  페이지 이동 함수 (페이징에서 사용 - 필요시)
function goToPage(pageNumber) {
    const currentSearch = document.getElementById('searchInput').value;
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    const params = new URLSearchParams();
    params.append('page', pageNumber - 1); // 0-based 페이징
    if (currentSearch.trim()) params.append('search', currentSearch.trim());
    if (periodFilter) params.append('period', periodFilter);
    if (statusFilter) params.append('status', statusFilter);
    
    window.location.href = `/admin/noncurr_list?${params.toString()}`;
}

//  검색 초기화 함수
function resetSearch() {
    document.getElementById('searchInput').value = '';
    document.getElementById('searchType').value = 'prgNm';
    document.getElementById('periodFilter').value = '';
    document.getElementById('statusFilter').value = '';
    
    window.location.href = '/admin/noncurr_list';
}

//  현재 페이지 파라미터 유지하면서 페이지 크기 변경
function changePageSize(size) {
    const currentSearch = document.getElementById('searchInput').value;
    const periodFilter = document.getElementById('periodFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    const params = new URLSearchParams();
    params.append('page', 0); // 첫 페이지로 이동
    params.append('size', size);
    if (currentSearch.trim()) params.append('search', currentSearch.trim());
    if (periodFilter) params.append('period', periodFilter);
    if (statusFilter) params.append('status', statusFilter);
    
    window.location.href = `/admin/noncurr_list?${params.toString()}`;
}