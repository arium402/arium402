// ================================
// 메인 페이지 전용 JavaScript
// ================================

// 페이지 이동 함수
function goToPage(url) {
    window.location.href = url;
}

// 공지사항 상세 페이지로 이동
function goToNotice(id) {
    window.location.href = `/notice/detail/${id}`;
}

// 채팅봇 팝업 열기
function openPopup() {
    const width = window.innerWidth;
    const height = window.innerHeight;
    const isMobile = width < 769;
    const popupWidth = isMobile ? width : 400;
    const popupHeight = isMobile ? height : 800;
    
    window.open('/user/chatbot', '', `width=${popupWidth},height=${popupHeight}`);
}

// 키보드 단축키 (메인 페이지 바로가기)
document.addEventListener('keydown', function(e) {
    // Alt + 1~4로 바로가기 메뉴 접근
    if (e.altKey) {
        switch(e.key) {
            case '1':
                e.preventDefault();
                goToPage('/student/counseling');
                break;
            case '2':
                e.preventDefault();
                goToPage('/student/noncurricular');
                break;
            case '3':
                e.preventDefault();
                goToPage('/student/diagnosis');
                break;
            case '4':
                e.preventDefault();
                goToPage('/student/mileage');
                break;
        }
    }
});

// 페이지 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('메인 페이지 로드 완료');
    
    // 히어로 배너 애니메이션 효과 (선택사항)
    const heroTitle = document.querySelector('.hero-title');
    const heroSubtitle = document.querySelector('.hero-subtitle');
    
    if (heroTitle && heroSubtitle) {
        // 페이드인 효과
        setTimeout(() => {
            heroTitle.style.opacity = '1';
            heroTitle.style.transform = 'translateY(0)';
        }, 200);
        
        setTimeout(() => {
            heroSubtitle.style.opacity = '1';
            heroSubtitle.style.transform = 'translateY(0)';
        }, 400);
    }
});

// 스크롤 시 플로팅 버튼 표시/숨김 (선택사항)
window.addEventListener('scroll', function() {
    const floatingBtn = document.querySelector('.floating-btn-chatbot');
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    if (floatingBtn) {
        if (scrollTop > 300) {
            floatingBtn.style.opacity = '1';
            floatingBtn.style.visibility = 'visible';
        } else {
            floatingBtn.style.opacity = '0.7';
        }
    }
});

// 공지사항 아이템 호버 효과 강화 (선택사항)
document.addEventListener('DOMContentLoaded', function() {
    const noticeItems = document.querySelectorAll('.notice-item');
    
    noticeItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(5px)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0)';
        });
    });
});

// 바로가기 카드 클릭 시 효과 (선택사항)
document.addEventListener('DOMContentLoaded', function() {
    const shortcutItems = document.querySelectorAll('.shortcut-item');
    
    shortcutItems.forEach(item => {
        item.addEventListener('click', function(e) {
            // 클릭 효과
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = 'translateY(-5px)';
            }, 100);
        });
    });
});