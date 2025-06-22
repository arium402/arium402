// 즉시 반응하는 사이드바 메뉴
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 사이드바 스크립트 로드됨');
    
    // 즉시 실행 (지연 없음)
    setActiveMenuBasedOnCurrentPage();
    setupMenuClickEvents();
});

// 현재 페이지에 맞는 메뉴를 즉시 활성화
function setActiveMenuBasedOnCurrentPage() {
    const currentPath = window.location.pathname;
    console.log('📍 현재 경로:', currentPath);
    
    // 모든 메뉴 초기화
    resetAllMenus();
    
    let activeMenu = null;
    
    // URL 기반으로 활성 메뉴 찾기
    if (currentPath.includes('/counselor/faq') || currentPath.includes('F&Q')) {
        activeMenu = document.querySelector('a[href*="faq"]');
    } 
    else if (currentPath.includes('/counselor/notice')) {
        activeMenu = document.querySelector('a[href*="notice"]');
    }
    else if (currentPath.includes('/counselor/clients')) {
        activeMenu = document.querySelector('a[href*="clients"]');
    }
    else if (currentPath.includes('/counselor/applicants')) {
        activeMenu = document.querySelector('a[href*="applicants"]');
    }
    else if (currentPath.includes('/counselor/schedule_check')) {
        activeMenu = document.querySelector('a[href*="schedule_check"]');
    }
    else if (currentPath.includes('/counselor/counseling_schedule')) {
        activeMenu = document.querySelector('a[href*="counseling_schedule"]');
    }
    else if (currentPath.includes('/counselor/QnA')) {
        activeMenu = document.querySelector('a[href*="QnA"]');
    }
    
    // 즉시 활성 메뉴 설정
    if (activeMenu) {
        setActiveMenu(activeMenu);
        console.log('✅ 페이지 로드 시 활성 메뉴:', activeMenu.textContent.trim());
    }
}

// 메뉴를 active 상태로 설정하는 함수
function setActiveMenu(menuElement) {
    // 다른 모든 메뉴 비활성화
    resetAllMenus();
    
    // 선택된 메뉴 활성화
    menuElement.classList.add('active');
    menuElement.style.backgroundColor = '#e6a456';
    menuElement.style.color = 'white';
    menuElement.style.fontWeight = 'bold';
}

// 모든 메뉴 초기화
function resetAllMenus() {
    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
        link.classList.remove('active');
        link.style.backgroundColor = '';
        link.style.color = '';
        link.style.fontWeight = '';
    });
}

// 메뉴 클릭 이벤트 설정 - 즉시 반응
function setupMenuClickEvents() {
    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
        link.addEventListener('click', function(e) {
            console.log('🎯 클릭됨:', this.textContent.trim());
            
            // 메인 카테고리면 이동 막기
            if (this.classList.contains('main-category')) {
                e.preventDefault();
                return false;
            }
            
            // 🔥 클릭 즉시 주황색으로 변경
            setActiveMenu(this);
            console.log('⚡ 즉시 주황색 변경 완료');
            
            // 정상적으로 페이지 이동 (새 페이지에서 다시 활성화됨)
        });
    });
}	// 즉시 반응하는 사이드바 메뉴
	document.addEventListener('DOMContentLoaded', function() {
	    console.log('🚀 사이드바 스크립트 로드됨');
	    
	    // 즉시 실행 (지연 없음)
	    setActiveMenuBasedOnCurrentPage();
	    setupMenuClickEvents();
	});

	// 현재 페이지에 맞는 메뉴를 즉시 활성화
	function setActiveMenuBasedOnCurrentPage() {
	    const currentPath = window.location.pathname;
	    console.log('📍 현재 경로:', currentPath);
	    
	    // 모든 메뉴 초기화
	    resetAllMenus();
	    
	    let activeMenu = null;
	    
	    // 🎯 Controller 매핑에 맞춘 정확한 URL 매칭
	    console.log('🔍 메뉴 매칭 시도...');
	    
	    if (currentPath === '/counselor/faq') {
	        activeMenu = findMenuByText('F&Q');
	        console.log('📌 F&Q 매칭 시도');
	    } 
	    else if (currentPath === '/counselor/notice' || currentPath === '/counselor/noticeClick') {
	        activeMenu = findMenuByText('공지사항');
	        console.log('📌 공지사항 매칭 시도 (목록 또는 상세)');
	    }
	    else if (currentPath === '/counselor/clients' || currentPath.startsWith('/counselor/clients/')) {
	        activeMenu = findMenuByText('내담자 관리');
	        console.log('📌 내담자 관리 매칭 시도');
	    }
	    else if (currentPath === '/counselor/applicants') {
	        activeMenu = findMenuByText('신청자 관리');
	        console.log('📌 신청자 관리 매칭 시도');
	    }
	    else if (currentPath === '/counselor/schedule_check') {
	        activeMenu = findMenuByText('상담 일정 확인');
	        console.log('📌 상담 일정 확인 매칭 시도');
	    }
	    else if (currentPath === '/counselor/counseling_schedule' || currentPath === '/counselor/schedule_registration') {
	        activeMenu = findMenuByText('근무 일정 관리');
	        console.log('📌 근무 일정 관리 매칭 시도');
	    }
	    else if (currentPath === '/counselor/QnA' || currentPath.startsWith('/counselor/QnA_')) {
	        activeMenu = findMenuByText('문의 게시판');
	        console.log('📌 문의 게시판 매칭 시도');
	    }
	    
	    // 즉시 활성 메뉴 설정
	    if (activeMenu) {
	        setActiveMenu(activeMenu);
	        console.log('✅ 페이지 로드 시 활성 메뉴:', activeMenu.textContent.trim());
	    } else {
	        console.log('❌ 매칭되는 메뉴를 찾을 수 없음 - 현재 경로:', currentPath);
	    }
	}

	// 텍스트로 메뉴 찾기 (더 안전한 방법)
	function findMenuByText(menuText) {
	    const allLinks = document.querySelectorAll('.sidebar-menu a');
	    for (let link of allLinks) {
	        if (link.textContent.trim() === menuText) {
	            console.log(`🎯 텍스트로 찾음: "${menuText}"`);
	            return link;
	        }
	    }
	    console.log(`❌ 텍스트로 못 찾음: "${menuText}"`);
	    return null;
	}

	// 메뉴를 active 상태로 설정하는 함수
	function setActiveMenu(menuElement) {
	    // 다른 모든 메뉴 비활성화
	    resetAllMenus();
	    
	    // 선택된 메뉴 활성화
	    menuElement.classList.add('active');
	    menuElement.style.backgroundColor = '#e6a456';
	    menuElement.style.color = 'white';
	    menuElement.style.fontWeight = 'bold';
	}

	// 모든 메뉴 초기화
	function resetAllMenus() {
	    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
	        link.classList.remove('active');
	        link.style.backgroundColor = '';
	        link.style.color = '';
	        link.style.fontWeight = '';
	    });
	}

	// 메뉴 클릭 이벤트 설정 - 즉시 반응
	function setupMenuClickEvents() {
	    document.querySelectorAll('.sidebar-menu a').forEach(function(link) {
	        link.addEventListener('click', function(e) {
	            console.log('🎯 클릭됨:', this.textContent.trim());
	            
	            // 메인 카테고리면 이동 막기
	            if (this.classList.contains('main-category')) {
	                e.preventDefault();
	                return false;
	            }
	            
	            // 🔥 클릭 즉시 주황색으로 변경
	            setActiveMenu(this);
	            console.log('⚡ 즉시 주황색 변경 완료');
	            
	            // 정상적으로 페이지 이동 (새 페이지에서 다시 활성화됨)
	        });
	    });
	}