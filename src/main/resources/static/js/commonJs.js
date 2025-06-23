// 페이지 로드 시 모든 이벤트 리스너 등록 및 메뉴 활성화
document.addEventListener('DOMContentLoaded', function() {
    
    // 사이드바 토글 기능 (데스크톱/모바일)
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            const sidebar = document.getElementById('layoutSidenav_nav');
            const content = document.getElementById('layoutSidenav_content');
            
            if (sidebar) {
                // 모바일에서는 show 클래스 사용
                if (window.innerWidth <= 768) {
                    sidebar.classList.toggle('show');
                } else {
                    // 데스크톱에서는 collapsed 클래스 사용
                    sidebar.classList.toggle('collapsed');
                    if (content) {
                        content.classList.toggle('expanded');
                    }
                }
            }
        });
    }

    // 윈도우 리사이즈 시 클래스 정리
    window.addEventListener('resize', function() {
        const sidebar = document.getElementById('layoutSidenav_nav');
        const content = document.getElementById('layoutSidenav_content');
        
        if (sidebar) {
            if (window.innerWidth > 768) {
                sidebar.classList.remove('show');
            } else {
                sidebar.classList.remove('collapsed');
                if (content) {
                    content.classList.remove('expanded');
                }
            }
        }
    });

    // 메뉴 클릭 이벤트 리스너 등록
    setupMenuClickEvents();
    
    // 약간의 지연을 두고 실행 (DOM 완전 로드 후)
    setTimeout(function() {
        activateMenuByCurrentUrl();
    }, 100);
});

// 현재 URL에 따른 메뉴 활성화 함수
function activateMenuByCurrentUrl() {
    // 현재 URL 경로 가져오기
    const currentPath = window.location.pathname;
    console.log('현재 경로:', currentPath);
    
    // 모든 메뉴 비활성화
    document.querySelectorAll('.nav-link.main-menu').forEach(menu => {
        menu.classList.remove('active');
    });
    document.querySelectorAll('.sub-menu .nav-link').forEach(submenu => {
        submenu.classList.remove('active');
    });
    
    // URL에 따라 해당 메뉴 활성화
    if (isCounselorManagementPage(currentPath)) {
        activateCounselorManagement(currentPath);
    } else if (isCounselingManagementPage(currentPath)) {
        activateCounselingManagement(currentPath);
    } else if (isExtracurricularPage(currentPath)) {
        activateExtracurricularManagement(currentPath);
    } else if (isMileagePage(currentPath)) {
        activateMileageManagement(currentPath);
    } else if (isBoardManagementPage(currentPath)) {
        activateBoardManagement(currentPath);
    } else if (isDashboardPage(currentPath)) {
        // 대시보드는 특별한 활성화 없음
        console.log('대시보드 페이지');
    }
}

// 상담사 관리 관련 페이지 확인
function isCounselorManagementPage(path) {
    return path.includes('admin_counselorList') || 
           path.includes('admin_counselor_schedule') || 
           path.includes('admin_counselor_statistics');
}

// 상담 관리 관련 페이지 확인
function isCounselingManagementPage(path) {
    return path.includes('admin_counselor_studentApply') || 
           path.includes('admin_counselor_scheduleDetail') || 
           path.includes('admin_counselingType_stats');
}

// 비교과 관련 페이지 확인 함수
function isExtracurricularPage(path) {
    return path.includes('noncurr_list') || 
           path.includes('noncurr_add') || 
           path.includes('noncurr_detail') || 
           path.includes('noncurr_edit') ||
           path.includes('noncurr_stat') ||
           path.includes('extracurricular');
}

// 비교과 메뉴 활성화 함수
function activateExtracurricularManagement(currentPath) {
    // 비교과 대메뉴 활성화
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(menu => {
        const icon = menu.querySelector('.material-symbols-outlined');
        if (icon && icon.textContent.trim() === 'school') {
            menu.classList.add('active');
            console.log('비교과 메뉴 활성화됨');
        }
    });
    
    // 해당 소메뉴 활성화
    const subMenuLinks = document.querySelectorAll('.sub-menu .nav-link');
    subMenuLinks.forEach(link => {
        if (currentPath.includes('noncurr_list') || 
            currentPath.includes('noncurr_add') || 
            currentPath.includes('noncurr_detail') ||
            currentPath.includes('noncurr_edit')) {
            if (link.textContent.trim() === '비교과 관리') {
                link.classList.add('active');
                console.log('비교과 관리 메뉴 활성화됨');
            }
        } else if (currentPath.includes('noncurr_stat')) {
            if (link.textContent.trim() === '비교과 통계') {
                link.classList.add('active');
                console.log('비교과 통계 메뉴 활성화됨');
            }
        }
    });
}

// 마일리지 관련 페이지 확인
function isMileagePage(path) {
    return path.includes('admin_mileage_payment') || 
           path.includes('admin_mileage_to_money');
}

// 게시판 관리 관련 페이지 확인 (문의게시판 추가)
function isBoardManagementPage(path) {
    return path.includes('notice') ||     
           path.includes('faq') ||        // F&Q 관련 페이지
           path.includes('qna');          // 문의게시판 관련 페이지 추가
}

// 대시보드 페이지 확인
function isDashboardPage(path) {
    return path.includes('admin_dashboard');
}

// 상담사 관리 메뉴 활성화
function activateCounselorManagement(currentPath) {
    // 상담사 관리 대메뉴 활성화
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(menu => {
        const icon = menu.querySelector('.material-symbols-outlined');
        if (icon && icon.textContent.trim() === 'account_circle') {
            menu.classList.add('active');
            console.log('상담사 관리 메뉴 활성화됨');
        }
    });
    
    // 해당 소메뉴 활성화
    const subMenuLinks = document.querySelectorAll('.sub-menu .nav-link');
    subMenuLinks.forEach(link => {
        if (currentPath.includes('admin_counselorList') && !currentPath.includes('_add') && !currentPath.includes('_detail')) {
            if (link.textContent.trim() === '상담사 목록') {
                link.classList.add('active');
                console.log('상담사 목록 메뉴 활성화됨');
            }
        } else if (currentPath.includes('admin_counselor_schedule')) {
            if (link.textContent.trim() === '상담사 일정 관리') {
                link.classList.add('active');
                console.log('상담사 일정 관리 메뉴 활성화됨');
            }
        } else if (currentPath.includes('admin_counselor_statistics')) {
            if (link.textContent.trim() === '상담사 통계') {
                link.classList.add('active');
                console.log('상담사 통계 메뉴 활성화됨');
            }
        }
    });
}

// 상담 관리 메뉴 활성화
function activateCounselingManagement(currentPath) {
    // 상담 관리 대메뉴 활성화
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(menu => {
        const icon = menu.querySelector('.material-symbols-outlined');
        if (icon && icon.textContent.trim() === 'psychology') {
            menu.classList.add('active');
            console.log('상담 관리 메뉴 활성화됨');
        }
    });
    
    // 해당 소메뉴 활성화
    const subMenuLinks = document.querySelectorAll('.sub-menu .nav-link');
    subMenuLinks.forEach(link => {
        if (currentPath.includes('admin_counselor_studentApply')) {
            if (link.textContent.trim() === '상담 신청 내역') {
                link.classList.add('active');
                console.log('상담 신청 내역 메뉴 활성화됨');
            }
        } else if (currentPath.includes('admin_counselingType_stats')) {
            if (link.textContent.trim() === '상담 분야별 통계') {
                link.classList.add('active');
                console.log('상담 분야별 통계 메뉴 활성화됨');
            }
        }
    });
}

// 마일리지 메뉴 활성화
function activateMileageManagement(currentPath) {
    // 마일리지 대메뉴 활성화
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(menu => {
        const icon = menu.querySelector('.material-symbols-outlined');
        if (icon && icon.textContent.trim() === 'stars') {
            menu.classList.add('active');
            console.log('마일리지 메뉴 활성화됨');
        }
    });
    
    // 해당 소메뉴 활성화
    const subMenuLinks = document.querySelectorAll('.sub-menu .nav-link');
    subMenuLinks.forEach(link => {
        if (currentPath.includes('admin_mileage_payment')) {
            if (link.textContent.trim() === '마일리지 지급') {
                link.classList.add('active');
                console.log('마일리지 지급 메뉴 활성화됨');
            }
        } else if (currentPath.includes('admin_mileage_to_money')) {
            if (link.textContent.trim() === '마일리지 전환') {
                link.classList.add('active');
                console.log('마일리지 전환 메뉴 활성화됨');
            }
        }
    });
}

// 게시판 관리 메뉴 활성화 (문의게시판 추가)
function activateBoardManagement(currentPath) {
    // 게시판 관리 대메뉴 활성화
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(menu => {
        const icon = menu.querySelector('.material-symbols-outlined');
        if (icon && icon.textContent.trim() === 'forum') {
            menu.classList.add('active');
            console.log('게시판 관리 메뉴 활성화됨');
        }
    });
    
    // 해당 소메뉴 활성화
    const subMenuLinks = document.querySelectorAll('.sub-menu .nav-link');
    subMenuLinks.forEach(link => {
        if (currentPath.includes('notice') || currentPath.includes('boardWrite')) {
            if (link.textContent.trim() === '공지사항 관리') {
                link.classList.add('active');
                console.log('공지사항 관리 메뉴 활성화됨');
            }
        } else if (currentPath.includes('faq')) {
            if (link.textContent.trim() === 'F&Q 관리') {
                link.classList.add('active');
                console.log('F&Q 관리 메뉴 활성화됨');
            }
        } else if (currentPath.includes('qna')) {  // 문의게시판 조건 추가
            if (link.textContent.trim() === '문의게시판 관리') {
                link.classList.add('active');
                console.log('문의게시판 관리 메뉴 활성화됨');
            }
        }
    });
}

// 메뉴 클릭 이벤트 설정 함수
function setupMenuClickEvents() {
    const mainMenus = document.querySelectorAll('.nav-link.main-menu');
    mainMenus.forEach(mainMenu => {
        mainMenu.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 현재 활성화된 메뉴가 아닌 경우에만 토글
            if (!this.classList.contains('active')) {
                // 다른 모든 메뉴 비활성화
                mainMenus.forEach(menu => {
                    menu.classList.remove('active');
                });
                
                // 현재 클릭한 메뉴 활성화
                this.classList.add('active');
            }
            // 이미 활성화된 메뉴를 클릭해도 비활성화하지 않음 (고정)
        });
    });
}

// 특정 페이지로 이동
function goToPage(page) {
    currentPage = page;
    if (typeof updateTable === 'function') {
        updateTable();
    }
}

// 디버깅용 - 메뉴 상태 확인
setTimeout(function() {
    console.log('=== 최종 메뉴 상태 확인 ===');
    const activeMainMenus = document.querySelectorAll('.nav-link.main-menu.active');
    const activeSubMenus = document.querySelectorAll('.sub-menu .nav-link.active');
    
    console.log('활성화된 대메뉴 개수:', activeMainMenus.length);
    console.log('활성화된 소메뉴 개수:', activeSubMenus.length);
    
    activeMainMenus.forEach(menu => {
        console.log('활성화된 대메뉴:', menu.textContent.trim());
    });
    
    activeSubMenus.forEach(menu => {
        console.log('활성화된 소메뉴:', menu.textContent.trim());
    });
}, 1000);