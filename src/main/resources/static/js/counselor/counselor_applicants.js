// 신청자 관리 JavaScript

// 전역 변수
let currentPage = 1;
const itemsPerPage = 10;

// 검색 함수 (구현 예정)
function searchApplicants() {
    const searchType = document.getElementById('searchTypeSelect').value;
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    
    console.log('검색 실행:', { searchType, searchTerm, startDate, endDate });
    // TODO: 실제 검색 로직 구현 필요
}

// 정렬 함수 (구현 예정)
function sortApplicants() {
    const sortBy = document.getElementById('sortSelect').value;
    console.log('정렬 실행:', sortBy);
    // TODO: 실제 정렬 로직 구현 필요
}

// 페이지네이션 함수들 (구현 예정)
function goToPage(page) {
    currentPage = page;
    console.log('페이지 이동:', page);
    // TODO: 실제 페이지네이션 로직 구현 필요
}

function changePage(direction) {
    const newPage = currentPage + direction;
    if (newPage >= 1) {
        currentPage = newPage;
        console.log('페이지 변경:', currentPage);
        // TODO: 실제 페이지 변경 로직 구현 필요
    }
}


// ❌ 사이드바 메뉴 클릭 핸들링 제거 - counselor_sidebar.js에서 처리하도록
// 중복된 이벤트 리스너가 충돌을 일으키고 있었음

document.addEventListener('DOMContentLoaded', function() {
    // 검색어 입력 필드 이벤트 리스너
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        // 엔터키로 검색 실행
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchApplicants();
            }
        });
    }
    
    // 검색 유형 변경 시 placeholder 업데이트
    const searchTypeSelect = document.getElementById('searchTypeSelect');
    if (searchTypeSelect) {
        searchTypeSelect.addEventListener('change', function() {
            const searchInput = document.getElementById('searchInput');
            const searchType = this.value;
            
            switch(searchType) {
                case 'name':
                    searchInput.placeholder = '이름 입력';
                    break;
                case 'department':
                    searchInput.placeholder = '학과명 입력';
                    break;
                case 'studentId':
                    searchInput.placeholder = '학번 입력';
                    break;
                case 'all':
                default:
                    searchInput.placeholder = '검색어 입력';
                    break;
            }
        });
    }
});