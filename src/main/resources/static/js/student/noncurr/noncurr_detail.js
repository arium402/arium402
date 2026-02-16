// ✅ URL에서 프로그램 ID 추출 함수
function getProgramIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const prgId = urlParams.get('prgId');
    console.log('URL에서 추출한 프로그램 ID:', prgId);
    return prgId ? parseInt(prgId) : null;
}

// ✅ 목록으로 돌아가기
function goBack() {
    window.location.href = '/student/noncurr/list';
}

// ✅ 상세 페이지에서 신청하기 함수 (data 속성으로 ID 가져오기)
function applyProgramFromButton(buttonElement) {
    const prgId = buttonElement.getAttribute('data-prg-id');
    if (!prgId) {
        alert('프로그램 ID를 찾을 수 없습니다.');
        return;
    }
    
    applyProgram(parseInt(prgId), buttonElement);
}

// ✅ 프로그램 신청 함수 (통합 버전)
function applyProgram(prgId, buttonElement = null) {
    if (confirm('이 프로그램에 신청하시겠습니까?')) {
        // 로딩 표시
        let button = buttonElement;
        if (!button) {
            button = event ? event.target : null;
        }
        
        const originalText = button ? button.textContent : '';
        if (button) {
            button.disabled = true;
            button.textContent = '처리중...';
        }
        
        fetch('/api/student/noncurr/apply', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `prgId=${prgId}`
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                // ✅ 상세 페이지에서는 즉시 페이지 새로고침 (캐시 무시)
                window.location.reload(true);
            } else {
                // 실패시 버튼 원상복구
                if (button) {
                    button.disabled = false;
                    button.textContent = originalText;
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('신청 처리 중 오류가 발생했습니다.');
            // 오류시 버튼 원상복구
            if (button) {
                button.disabled = false;
                button.textContent = originalText;
            }
        });
    }
}

// ✅ 신청취소 함수 (data 속성으로 ID 가져오기)
function cancelApplicationFromButton(buttonElement) {
    const prgId = buttonElement.getAttribute('data-prg-id');
    if (!prgId) {
        alert('프로그램 ID를 찾을 수 없습니다.');
        return;
    }
    
    cancelApplication(parseInt(prgId));
}

// ✅ 신청취소 함수
function cancelApplication(prgId) {
    if (confirm('정말로 신청을 취소하시겠습니까?\n취소 후 다시 신청할 수 있습니다.')) {
        // 로딩 표시
        const cancelBtn = document.querySelector('.cancel-btn-header');
        if (cancelBtn) {
            cancelBtn.disabled = true;
            cancelBtn.textContent = '처리중...';
        }
        
        fetch(`/api/student/noncurr/cancel-application/${prgId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => {
            alert(data.message);
            if (data.success) {
                // ✅ 즉시 페이지 새로고침 (캐시 무시)
                window.location.reload(true);
            } else {
                // 실패시 버튼 원상복구
                if (cancelBtn) {
                    cancelBtn.disabled = false;
                    cancelBtn.textContent = '신청취소';
                }
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('신청 취소 중 오류가 발생했습니다.');
            // 오류시 버튼 원상복구
            if (cancelBtn) {
                cancelBtn.disabled = false;
                cancelBtn.textContent = '신청취소';
            }
        });
    }
}

// 차트 인스턴스를 전역으로 관리
let programChart, myChart;

// ✅ 핵심역량 데이터 로드 함수
function loadCompetencyData(prgId) {
    console.log(`프로그램 ${prgId}의 핵심역량 데이터 로드 시작`);
    
    fetch(`/api/student/noncurr/competency/${prgId}`)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                console.log('핵심역량 데이터 로드 성공:', data.data);
                
                // 데이터가 비어있지 않은지 확인
                if (data.data.programCompetencies && data.data.studentCompetencies) {
                    createCharts(data.data);
                } else {
                    console.warn('핵심역량 데이터가 비어있습니다.');
                    showErrorCharts('핵심역량 데이터가 없습니다.');
                }
            } else {
                console.error('핵심역량 데이터 로드 실패:', data.message);
                showErrorCharts('핵심역량 데이터를 불러올 수 없습니다.');
            }
        })
        .catch(error => {
            console.error('핵심역량 데이터 로드 에러:', error);
            showErrorCharts('서버 연결 오류가 발생했습니다.');
        });
}

// ✅ 실제 데이터로 차트 생성
function createCharts(competencyData) {
    console.log('받은 핵심역량 데이터:', competencyData);
    
    // 1. 프로그램 핵심역량 차트 (왼쪽)
    const programCtx = document.getElementById('programCompetencyChart').getContext('2d');
    
    const programLabels = competencyData.programCompetencies.map(item => item.competencyName);
    const programScores = competencyData.programCompetencies.map(item => item.score);
    
    // 동적 색상 설정 (점수가 있는 역량은 색상, 없는 역량은 회색)
    const programColors = programScores.map(score => score > 0 ? '#40E0D0' : '#E0E0E0');
    
    // 기존 차트가 있으면 제거
    if (programChart) {
        programChart.destroy();
    }
    
    programChart = new Chart(programCtx, {
        type: 'bar',
        data: {
            labels: programLabels,
            datasets: [{
                label: '프로그램 핵심역량 점수',
                data: programScores,
                backgroundColor: programColors,
                borderWidth: 0,
                barThickness: 45
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                title: {
                    display: true,
                    text: '이 프로그램으로 얻을 수 있는 핵심역량'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: Math.max(...programScores, 10) + 20,
                    ticks: {
                        callback: function(value) {
                            return value + 'P';
                        }
                    }
                },
                x: {
                    ticks: {
                        maxRotation: 45,
                        font: { size: 10 }
                    }
                }
            }
        }
    });

    // 2. 나의 핵심역량 차트 (오른쪽)
    const myCtx = document.getElementById('myCompetencyChart').getContext('2d');
    
    const studentLabels = competencyData.studentCompetencies.map(item => item.competencyName);
    const currentScores = competencyData.studentCompetencies.map(item => item.currentScore);
    const programAddScores = competencyData.studentCompetencies.map(item => item.programScore);
    
    // 기존 차트가 있으면 제거
    if (myChart) {
        myChart.destroy();
    }
    
    myChart = new Chart(myCtx, {
        type: 'bar',
        data: {
            labels: studentLabels,
            datasets: [
                {
                    label: '현재 나의 역량',
                    data: currentScores,
                    backgroundColor: '#87CEEB',
                    borderWidth: 0,
                    barThickness: 45
                },
                {
                    label: '프로그램 이수 시 추가',
                    data: programAddScores,
                    backgroundColor: '#D3D3D3',
                    borderWidth: 0,
                    barThickness: 45
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        font: { size: 11 }
                    }
                },
                title: {
                    display: true,
                    text: '나의 핵심역량 현황'
                }
            },
            scales: {
                x: {
                    stacked: true,
                    ticks: {
                        maxRotation: 45,
                        font: { size: 10 }
                    }
                },
                y: {
                    stacked: true,
                    beginAtZero: true,
                    max: Math.max(...currentScores.map((curr, idx) => curr + programAddScores[idx]), 10) + 20,
                    ticks: {
                        callback: function(value) {
                            return value + 'P';
                        }
                    }
                }
            },
            // 툴팁 커스터마이징
            interaction: {
                mode: 'index',
                intersect: false,
            }
        }
    });
    
    console.log('차트 생성 완료');
    console.log('프로그램 점수:', programScores);
    console.log('학생 현재 점수:', currentScores);
    console.log('프로그램 추가 점수:', programAddScores);
}

// ✅ 에러 상황을 표시하는 차트
function showErrorCharts(errorMessage) {
    console.log('에러 차트 표시:', errorMessage);
    
    // 프로그램 차트 - 에러 메시지 표시
    const programCtx = document.getElementById('programCompetencyChart').getContext('2d');
    
    if (programChart) {
        programChart.destroy();
    }
    
    programChart = new Chart(programCtx, {
        type: 'bar',
        data: {
            labels: ['데이터 로드 실패'],
            datasets: [{
                label: '오류',
                data: [0],
                backgroundColor: '#FF6B6B',
                borderWidth: 0,
                barThickness: 45
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                title: {
                    display: true,
                    text: errorMessage,
                    color: '#FF6B6B'
                }
            },
            scales: {
                y: { beginAtZero: true, max: 10 },
                x: { ticks: { font: { size: 10 } } }
            }
        }
    });

    // 학생 차트 - 에러 메시지 표시
    const myCtx = document.getElementById('myCompetencyChart').getContext('2d');
    
    if (myChart) {
        myChart.destroy();
    }
    
    myChart = new Chart(myCtx, {
        type: 'bar',
        data: {
            labels: ['데이터 로드 실패'],
            datasets: [{
                label: '오류',
                data: [0],
                backgroundColor: '#FF6B6B',
                borderWidth: 0,
                barThickness: 45
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                title: {
                    display: true,
                    text: '핵심역량 데이터를 불러올 수 없습니다',
                    color: '#FF6B6B'
                }
            },
            scales: {
                y: { beginAtZero: true, max: 10 },
                x: { ticks: { font: { size: 10 } } }
            }
        }
    });
}

// ✅ 페이지 로드 시 실행 (URL에서 프로그램 ID 추출)
document.addEventListener('DOMContentLoaded', function() {
    const programId = getProgramIdFromUrl(); // URL에서 추출
    
    // ✅ 비교과 프로그램 사이드바 메뉴 강제 활성화
    const noncurrListLink = document.querySelector('.sidebar a[href="/student/noncurr/list"]');
    if (noncurrListLink) {
        // 링크 활성화
        noncurrListLink.classList.add('active');
        
        // 부모 li 활성화
        const li = noncurrListLink.closest('li');
        if (li) li.classList.add('active');
        
        // 서브메뉴 열기
        const submenu = noncurrListLink.closest('.submenu');
        if (submenu) {
            const parentLi = submenu.closest('li');
            if (parentLi) {
                parentLi.classList.add('active');
                const submenuEl = parentLi.querySelector('.submenu');
                if (submenuEl) submenuEl.style.display = 'block';
            }
        }
    }    
    if (programId) {
        console.log(`프로그램 ID ${programId}의 핵심역량 데이터 로드 시작`);
        loadCompetencyData(programId);
    } else {
        console.error('URL에서 프로그램 ID를 찾을 수 없습니다.');
        showErrorCharts('프로그램 정보를 찾을 수 없습니다.');
    }
});

// ✅ 윈도우 리사이즈 이벤트 처리
window.addEventListener('resize', function() {
    setTimeout(() => {
        if (programChart) { 
            programChart.resize(); 
        }
        if (myChart) { 
            myChart.resize(); 
        }
    }, 100);
});