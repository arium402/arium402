// 샘플 공지사항 데이터
const noticeData = [
    { 
        id: 1, 
        title: "2025년 상담 프로그램 운영 안내", 
        category: "counseling", 
        isPublic: "Y", 
        isFixed: "Y", 
        registerDate: "2025.06.15",
        content: "2025년 상담 프로그램 운영에 대해 안내드립니다..."
    },
    { 
        id: 2, 
        title: "토익 특강 신청 안내", 
        category: "extracurricular", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.14",
        content: "토익 특강 신청에 대해 안내드립니다..."
    },
    { 
        id: 3, 
        title: "시스템 점검 안내", 
        category: "other", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.13",
        content: "시스템 점검에 대해 안내드립니다..."
    },
    { 
        id: 4, 
        title: "심리상담 운영시간 변경 안내", 
        category: "counseling", 
        isPublic: "N", 
        isFixed: "N", 
        registerDate: "2025.06.12",
        content: "심리상담 운영시간 변경에 대해 안내드립니다..."
    },
    { 
        id: 5, 
        title: "창업 특강 개최 안내", 
        category: "extracurricular", 
        isPublic: "Y", 
        isFixed: "Y", 
        registerDate: "2025.06.11",
        content: "창업 특강 개최에 대해 안내드립니다..."
    },
    { 
        id: 6, 
        title: "학사 일정 안내", 
        category: "other", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.10",
        content: "학사 일정에 대해 안내드립니다..."
    },
    { 
        id: 7, 
        title: "온라인 상담 시스템 도입 안내", 
        category: "counseling", 
        isPublic: "Y", 
        isFixed: "N", 
        registerDate: "2025.06.09",
        content: "온라인 상담 시스템 도입에 대해 안내드립니다..."
    },
    { 
        id: 8, 
        title: "취업 특강 프로그램 안내", 
        category: "extracurricular", 
        isPublic: "N", 
        isFixed: "N", 
        registerDate: "2025.06.08",
        content: "취업 특강 프로그램에 대해 안내드립니다..."
    }
];

let currentCategory = 'all';
let currentVisibility = 'all';
let currentSearchType = 'title';
let currentSearchKeyword = '';
let currentPage = 1;
const itemsPerPage = 5;




// 카테고리 탭 전환 기능
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // 탭 버튼 활성화 상태 변경
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 현재 카테고리 설정 및 페이지 리셋
            currentCategory = this.dataset.category;
            currentPage = 1;
            
            // 테이블 업데이트
            updateTable();
        });
    });
});

// 공개상태 필터 변경 이벤트
document.addEventListener('DOMContentLoaded', function() {
    const visibilityFilter = document.getElementById('visibilityFilter');
    if (visibilityFilter) {
        visibilityFilter.addEventListener('change', function() {
            currentVisibility = this.value;
            currentPage = 1;
            updateTable();
        });
    }
});

// 전체 선택/해제 함수
function toggleSelectAll() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    
    itemCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
    
    updateDeleteButton();
}

// 개별 체크박스 변경 시 삭제 버튼 상태 업데이트
function updateDeleteButton() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
    const deleteBtn = document.getElementById('deleteBtn');
    const selectAllCheckbox = document.getElementById('selectAll');
    const allItemCheckboxes = document.querySelectorAll('.item-checkbox');
    
    // 삭제 버튼 활성화/비활성화
    if (deleteBtn) {
        deleteBtn.disabled = checkedBoxes.length === 0;
    }
    
    // 전체 선택 체크박스 상태 업데이트
    if (selectAllCheckbox) {
        if (checkedBoxes.length === 0) {
            selectAllCheckbox.indeterminate = false;
            selectAllCheckbox.checked = false;
        } else if (checkedBoxes.length === allItemCheckboxes.length) {
            selectAllCheckbox.indeterminate = false;
            selectAllCheckbox.checked = true;
        } else {
            selectAllCheckbox.indeterminate = true;
            selectAllCheckbox.checked = false;
        }
    }
}

// 선택된 항목 삭제
function deleteSelected() {
    const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
    if (checkedBoxes.length === 0) return;
    
    const selectedIds = Array.from(checkedBoxes).map(cb => parseInt(cb.value));
    const confirmMsg = `선택한 ${checkedBoxes.length}개 항목을 삭제하시겠습니까?`;
    
    if (confirm(confirmMsg)) {
        // 실제로는 서버에 삭제 요청을 보냄
        alert(`${checkedBoxes.length}개 항목이 삭제되었습니다.`);
        
        // 데모용: 선택된 항목을 배열에서 제거
        selectedIds.forEach(id => {
            const index = noticeData.findIndex(item => item.id === id);
            if (index > -1) {
                noticeData.splice(index, 1);
            }
        });
        
        // 페이지 재조정 및 테이블 업데이트
        const filteredData = getFilteredData();
        const maxPage = Math.ceil(filteredData.length / itemsPerPage);
        if (currentPage > maxPage && maxPage > 0) {
            currentPage = maxPage;
        }
        
        updateTable();
    }
}

// 검색 기능
function performSearch() {
    const searchTypeElement = document.getElementById('searchType');
    const searchInputElement = document.getElementById('searchInput');
    
    if (searchTypeElement && searchInputElement) {
        currentSearchType = searchTypeElement.value;
        currentSearchKeyword = searchInputElement.value.trim();
        currentPage = 1;
        updateTable();
    }
}

// 엔터키로 검색
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
});

// 필터링된 데이터 가져오기
function getFilteredData() {
    let filteredData = [...noticeData];

    // 카테고리 필터링
    if (currentCategory !== 'all') {
        filteredData = filteredData.filter(notice => notice.category === currentCategory);
    }

    // 공개상태 필터링
    if (currentVisibility === 'public') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'Y');
    } else if (currentVisibility === 'private') {
        filteredData = filteredData.filter(notice => notice.isPublic === 'N');
    }

    // 검색 필터링
    if (currentSearchKeyword) {
        filteredData = filteredData.filter(notice => {
            if (currentSearchType === 'title') {
                return notice.title.toLowerCase().includes(currentSearchKeyword.toLowerCase());
            } else if (currentSearchType === 'content') {
                return notice.content.toLowerCase().includes(currentSearchKeyword.toLowerCase());
            }
            return false;
        });
    }

    return filteredData;
}

// 테이블 업데이트 함수
function updateTable() {
    const tbody = document.getElementById('noticeTableBody');
    if (!tbody) return;
    
    const filteredData = getFilteredData();

    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, endIndex);

    // 테이블 생성
    tbody.innerHTML = '';
    paginatedData.forEach(notice => {
        const row = document.createElement('tr');
        
        const categoryClass = `category-${notice.category}`;
        const categoryText = getCategoryText(notice.category);
        const publicClass = notice.isPublic === 'Y' ? 'status-y' : 'status-n';
        const fixedClass = notice.isFixed === 'Y' ? 'status-y' : 'status-n';
        
        row.innerHTML = `
            <td class="checkbox-cell">
                <input type="checkbox" class="notice-checkbox item-checkbox" value="${notice.id}" onchange="updateDeleteButton()">
            </td>
            <td>${notice.id}</td>
            <td class="notice-title">${notice.title}</td>
            <td><span class="category-badge ${categoryClass}">${categoryText}</span></td>
            <td><span class="status-badge ${publicClass}">${notice.isPublic}</span></td>
            <td><span class="status-badge ${fixedClass}">${notice.isFixed}</span></td>
            <td>${notice.registerDate}</td>
        `;
        
        // 체크박스가 아닌 다른 영역 클릭 시 상세 페이지로 이동
        row.addEventListener('click', (e) => {
            if (e.target.type !== 'checkbox') {
                openNoticeDetail(notice.id);
            }
        });
        
        tbody.appendChild(row);
    });

    // 전체 선택 체크박스 초기화
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
    }
    
    // 삭제 버튼 비활성화
    const deleteBtn = document.getElementById('deleteBtn');
    if (deleteBtn) {
        deleteBtn.disabled = true;
    }

    // 페이지네이션 업데이트
    updatePagination(filteredData.length);
}

// 카테고리 텍스트 변환
function getCategoryText(category) {
    switch(category) {
        case 'extracurricular': return '비교과';
        case 'counseling': return '상담';
        case 'other': return '기타';
        default: return '기타';
    }
}

// 페이지네이션 업데이트 함수
function updatePagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.querySelector('.pagination');
    const paginationInfo = document.getElementById('paginationInfo');
    
    if (!pagination || !paginationInfo) return;
    
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
    paginationInfo.textContent = `총 ${totalItems}건 중 ${startItem}-${endItem}건 표시`;
}

// 페이지 변경 함수
function changePage(direction) {
    const filteredData = getFilteredData();
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        updateTable();
    }
}

// 특정 페이지로 이동하는 함수
function goToPage(pageNumber) {
    currentPage = pageNumber;
    updateTable();
}

// 등록 페이지로 이동
function openRegisterPage() {
    // 실제로는 등록 페이지로 이동
    alert('공지사항 등록 페이지로 이동합니다.');
    window.location.href = '/admin/notice_add';
}

// 공지사항 상세보기
function openNoticeDetail(noticeId) {
    // 실제로는 상세 페이지로 이동
    const notice = noticeData.find(n => n.id === noticeId);
    if (notice) {
        alert(`"${notice.title}" 상세 페이지로 이동합니다.`);
    }
    // window.location.href = `/admin/notice/detail/${noticeId}`;
}

// 페이지 로드 시 초기 테이블 렌더링
document.addEventListener('DOMContentLoaded', function() {
    updateTable();
});