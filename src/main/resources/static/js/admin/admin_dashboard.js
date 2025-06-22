

// Chart.js 차트 생성
document.addEventListener('DOMContentLoaded', function() {
    // 초기 로드 시 모든 메뉴 비활성화 (대시보드용)
    document.querySelectorAll('.main-menu').forEach(menu => menu.classList.remove('active'));
    document.querySelectorAll('.sub-menu .nav-link').forEach(subLink => subLink.classList.remove('active'));
    
    // 상담 통계 원형 차트
    const counselingCtx = document.getElementById('counselingChart').getContext('2d');
    new Chart(counselingCtx, {
        type: 'doughnut',
        data: {
            labels: ['위기상담', '심리상담', '익명상담', '진로/취업', '학습컨설팅'],
            datasets: [{
                data: [15, 25, 20, 30, 10],
                backgroundColor: [
                    '#e74c3c',
                    '#3498db',
                    '#2ecc71',
                    '#f39c12',
                    '#9b59b6'
                ],
                borderWidth: 3,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });

    // 비교과 통계 막대 차트
    const extracurricularCtx = document.getElementById('extracurricularChart').getContext('2d');
    new Chart(extracurricularCtx, {
        type: 'bar',
        data: {
            labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
            datasets: [{
                label: '참여자 수',
                data: [45, 52, 38, 61, 48, 67],
                backgroundColor: '#3498db',
                borderColor: '#2980b9',
                borderWidth: 2,
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#f1f1f1'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
});

// 윈도우 리사이즈 시 클래스 정리
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

// 메뉴 활성화 상태 관리
document.querySelectorAll('.sub-menu .nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
        // 기존 활성화 상태 제거
        document.querySelectorAll('.main-menu').forEach(menu => menu.classList.remove('active'));
        document.querySelectorAll('.sub-menu .nav-link').forEach(subLink => subLink.classList.remove('active'));
        
        // 클릭된 하위 메뉴 활성화
        this.classList.add('active');
        
        // 상위 메뉴도 활성화
        const parentMenu = this.closest('.nav-item').querySelector('.main-menu');
        if (parentMenu) {
            parentMenu.classList.add('active');
        }
    });
});

// 상위 메뉴 클릭 시 활성화
document.querySelectorAll('.main-menu').forEach(menu => {
    menu.addEventListener('click', function(e) {
        e.preventDefault();
        
        // 모든 메뉴 비활성화
        document.querySelectorAll('.main-menu').forEach(m => m.classList.remove('active'));
        document.querySelectorAll('.sub-menu .nav-link').forEach(subLink => subLink.classList.remove('active'));
        
        // 클릭된 메뉴 활성화
        this.classList.add('active');
    });
});

// 하위 메뉴 호버 효과 (아래로 펼치기)
document.querySelectorAll('.nav-item').forEach(item => {
    const subMenu = item.querySelector('.sub-menu');
    
    if (subMenu) {
        item.addEventListener('mouseenter', function() {
            subMenu.style.maxHeight = '200px';
        });
        
        item.addEventListener('mouseleave', function() {
            subMenu.style.maxHeight = '0';
        });
    }
});