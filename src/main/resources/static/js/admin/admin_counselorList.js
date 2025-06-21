



// 상담분류 필터 기능
document.getElementById('categoryFilter').addEventListener('change', function() {
    const selectedCategory = this.value;
    filterCounselorsByCategory(selectedCategory);
});

// 분야별 필터링 함수
function filterCounselorsByCategory(category) {
    const rows = document.querySelectorAll('.counselor-table tbody tr');
    
    rows.forEach(row => {
        const fieldCell = row.cells[3];
        const fieldText = fieldCell.textContent.trim();
        
        if (category === 'all') {
            row.style.display = '';
        } else {
            let shouldShow = false;
            switch(category) {
                case '심리상담':
                    shouldShow = fieldText === '심리상담';
                    break;
                case '위기상담':
                    shouldShow = fieldText === '위기상담';
                    break;
                case '익명상담':
                    shouldShow = fieldText === '익명상담';
                    break;
                case '진로상담':
                    shouldShow = fieldText === '진로상담';
                    break;
                case '학습컨설팅':
                    shouldShow = fieldText === '학습컨설팅';
                    break;
            }
            
            row.style.display = shouldShow ? '' : 'none';
        }
    });
    
    console.log('상담분류 필터:', category);
}

// 탭 전환 기능
document.querySelectorAll('.tab-item').forEach(tab => {
    tab.addEventListener('click', function(e) {
        e.preventDefault();
        
        document.querySelectorAll('.tab-item').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        
        const status = this.getAttribute('data-status');
        console.log('선택된 상태:', status);
    });
});

// 검색 기능
document.querySelector('.search-btn').addEventListener('click', function() {
    const searchType = document.querySelector('.search-select').value;
    const searchText = document.querySelector('.search-input').value;
    
    console.log('검색 유형:', searchType);
    console.log('검색어:', searchText);
    
    alert(`${searchType}에서 "${searchText}" 검색`);
});

// 등록 버튼 기능
document.querySelector('.register-btn').addEventListener('click', function() {
    alert('상담사 등록 페이지로 이동합니다.');
});

// 상세 페이지로 이동하는 함수
function goToDetail(id) {
    window.open('counselor_detail.html?id=' + id, '_blank');
}

// 엔터키로 검색
document.querySelector('.search-input').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        document.querySelector('.search-btn').click();
    }
});

// 페이지네이션 기능
document.querySelectorAll('.pagination-btn.page-number').forEach(btn => {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.pagination-btn.page-number').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        const pageNumber = this.textContent;
        console.log('페이지 이동:', pageNumber);
    });
});

// 이전/다음 버튼 기능
document.querySelector('.pagination-btn.prev').addEventListener('click', function() {
    if (!this.disabled) {
        console.log('이전 페이지로 이동');
    }
});

document.querySelector('.pagination-btn.next').addEventListener('click', function() {
    if (!this.disabled) {
        console.log('다음 페이지로 이동');
    }
});

