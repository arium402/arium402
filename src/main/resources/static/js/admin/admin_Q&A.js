// 샘플 문의게시판 데이터
const inquiryData = [
    { 
        id: 1, 
        title: "상담 예약 방법이 궁금합니다", 
        category: "counseling", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "N", 
        registerDate: "2025.06.15",
        content: "상담 예약을 어떻게 하는지 궁금합니다..."
    },
    { 
        id: 2, 
        title: "토익 특강 신청 관련 문의", 
        category: "extracurricular", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "Y", 
        registerDate: "2025.06.14",
        content: "토익 특강 신청과 관련하여 문의드립니다..."
    },
    { 
        id: 3, 
        title: "학점 관련 문의사항", 
        category: "other", 
        writer: "202*****",
        isPublic: "private", 
        hasAnswer: "Y", 
        registerDate: "2025.06.13",
        content: "학점 관련하여 문의드립니다..."
    },
    { 
        id: 4, 
        title: "심리상담 시간 변경 요청", 
        category: "counseling", 
        writer: "202*****",
        isPublic: "private", 
        hasAnswer: "N", 
        registerDate: "2025.06.12",
        content: "심리상담 시간 변경과 관련하여 문의드립니다..."
    },
    { 
        id: 5, 
        title: "창업 프로그램 참여 조건", 
        category: "extracurricular", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "Y", 
        registerDate: "2025.06.11",
        content: "창업 프로그램 참여 조건에 대해 문의드립니다..."
    },
    { 
        id: 6, 
        title: "등록금 납부 관련 문의", 
        category: "other", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "Y", 
        registerDate: "2025.06.10",
        content: "등록금 납부와 관련하여 문의드립니다..."
    },
    { 
        id: 7, 
        title: "진로상담 신청 방법", 
        category: "counseling", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "N", 
        registerDate: "2025.06.09",
        content: "진로상담 신청 방법에 대해 문의드립니다..."
    },
    { 
        id: 8, 
        title: "비교과 마일리지 적립 기준", 
        category: "extracurricular", 
        writer: "202*****",
        isPublic: "private", 
        hasAnswer: "Y", 
        registerDate: "2025.06.08",
        content: "비교과 프로그램 마일리지 적립 기준에 대해 문의드립니다..."
    },
    { 
        id: 9, 
        title: "휴학 신청 절차 문의", 
        category: "other", 
        writer: "202*****",
        isPublic: "public", 
        hasAnswer: "N", 
        registerDate: "2025.06.07",
        content: "휴학 신청 절차에 대해 문의드립니다..."
    },
    { 
        id: 10, 
        title: "위기상담 신청 관련", 
        category: "counseling", 
        writer: "202*****",
        isPublic: "private", 
        hasAnswer: "Y", 
        registerDate: "2025.06.06",
        content: "위기상담 신청에 대해 문의드립니다..."
    }
];

let currentCategory = 'all';
let currentVisibility = 'all';
let currentPage = 1;
const itemsPerPage = 8;

// 사이드바 토글 기능
function setupSidebarToggle() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            const sidebar = document.getElementById('layoutSidenav_nav');
            const content = document.getElementById('layoutSidenav_content');
            
            if (window.innerWidth <= 768) {
                sidebar.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
                content.classList.toggle('expanded');
            }
        });
    }
}

// 윈도우 리사이즈 시 클래스 정리
function setupWindowResize() {
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
}

// 카테고리 탭 전환 기능
function setupCategoryTabs() {
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
}

// 공개상태 필터 변경
function setupVisibilityFilter() {
    const visibilityFilter = document.getElementById('visibilityFilter');
    if (visibilityFilter) {
        visibilityFilter.addEventListener('change', function() {
            currentVisibility = this.value;
            currentPage = 1;
            updateTable();
        });
    }
}

// 테이블 업데이트 함수
function updateTable() {
    const tbody = document.getElementById('inquiryTableBody');
    if (!tbody) return;
    
    let filteredData = inquiryData;

    // 카테고리 필터링
    if (currentCategory !== 'all') {
        filteredData = filteredData.filter(inquiry => inquiry.category === currentCategory);
    }

    // 공개상태 필터링
    if (currentVisibility === 'public') {
        filteredData = filteredData.filter(inquiry => inquiry.isPublic === 'public');
    } else if (currentVisibility === 'private') {
        filteredData = filteredData.filter(inquiry => inquiry.isPublic === 'private');
    }

    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, endIndex);

    // 테이블 생성
    tbody.innerHTML = '';
    paginatedData.forEach(inquiry => {
        const row = document.createElement('tr');
        
        const categoryClass = `category-${inquiry.category}`;
        const categoryText = getCategoryText(inquiry.category);
        const publicClass = inquiry.isPublic === 'public' ? 'status-public' : 'status-private';
        const publicText = inquiry.isPublic === 'public' ? '공개' : '비공개';
        const answerClass = inquiry.hasAnswer === 'Y' ? 'status-y' : 'status-n';
        
        row.innerHTML = `
            <td>${inquiry.id}</td>
            <td class="inquiry-title">${inquiry.title}</td>
            <td><span class="category-badge ${categoryClass}">${categoryText}</span></td>
            <td class="writer-info">${inquiry.writer}</td>
            <td><span class="status-badge ${publicClass}">${publicText}</span></td>
            <td><span class="status-badge ${answerClass}">${inquiry.hasAnswer}</span></td>
            <td>${inquiry.registerDate}</td>
        `;
        
        // 행 클릭 이벤트 추가 (상세 페이지로 이동)
        row.addEventListener('click', () => openDetailPage(inquiry));
        
        tbody.appendChild(row);
    });

    // 페이지네이션 업데이트
    updatePagination(filteredData.length);
}

// 카테고리 텍스트 반환
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
    let filteredData = inquiryData;
    
    if (currentCategory !== 'all') {
        filteredData = filteredData.filter(inquiry => inquiry.category === currentCategory);
    }
    
    if (currentVisibility === 'public') {
        filteredData = filteredData.filter(inquiry => inquiry.isPublic === 'public');
    } else if (currentVisibility === 'private') {
        filteredData = filteredData.filter(inquiry => inquiry.isPublic === 'private');
    }

    const totalPages = Math.ceil(filteredData.length / itemsPerPage);
    const newPage = currentPage + direction;

    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        updateTable();
    }
}

// 특정 페이지로 이동
function goToPage(page) {
    currentPage = page;
    updateTable();
}

// 상세 페이지 열기
function openDetailPage(inquiry) {
    // 상세 페이지로 이동 (실제로는 별도 HTML 파일)
    alert(`"${inquiry.title}" 상세 페이지로 이동합니다.`);
    // window.location.href = 'inquiry-detail.html?id=' + inquiry.id;
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 토글 설정
    //setupSidebarToggle();
    
    // 윈도우 리사이즈 설정
    //setupWindowResize();
    
    // 카테고리 탭 설정
    setupCategoryTabs();
    
    // 공개상태 필터 설정
    setupVisibilityFilter();
    
    // 테이블 초기화
    updateTable();
    
    // 게시판 관리 > 문의게시판 관리 메뉴 활성화
    const boardManagementMenu = document.querySelector('.nav-item:last-child .main-menu');
    const qnaSubmenu = document.querySelector('.nav-item:last-child .sub-menu');
    
    if (boardManagementMenu) {
        boardManagementMenu.classList.add('active');
    }
    if (qnaSubmenu) {
        qnaSubmenu.style.maxHeight = '200px';
    }
});