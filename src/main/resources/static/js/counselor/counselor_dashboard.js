// 상담 통계 차트 그리기
function drawStatsChart() {
    const canvas = document.getElementById('statsChart');
    const ctx = canvas.getContext('2d');
    
    // 차트 데이터 (6개월간 상담 건수)
    const months = ['1월', '2월', '3월', '4월', '5월', '6월'];
    const generalData = [45, 52, 38, 61, 48, 55]; // 일반상담
    const academicData = [28, 34, 25, 39, 31, 35]; // 학업상담
    
    const canvasWidth = canvas.width;
    const canvasHeight = canvas.height;
    const chartWidth = canvasWidth - 80;
    const chartHeight = canvasHeight - 80;
    const startX = 50;
    const startY = 40;
    
    // 배경
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, canvasWidth, canvasHeight);
    
    // 축 그리기
    ctx.strokeStyle = '#bdc3c7';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(startX, startY);
    ctx.lineTo(startX, startY + chartHeight);
    ctx.lineTo(startX + chartWidth, startY + chartHeight);
    ctx.stroke();
    
    // 데이터 점과 막대 그리기
    const barWidth = (chartWidth / months.length) * 0.3;
    const maxValue = Math.max(...generalData, ...academicData);
    
    months.forEach((month, index) => {
        const x = startX + (chartWidth / months.length) * index + (chartWidth / months.length) * 0.5;
        
        // 일반상담 막대 (부드러운 주황색)
        const generalHeight = (generalData[index] / maxValue) * (chartHeight - 20);
        const generalY = startY + chartHeight - generalHeight;
        ctx.fillStyle = '#e6a456';
        ctx.fillRect(x - barWidth - 2, generalY, barWidth, generalHeight);
        
        // 학업상담 막대 (파란색)
        const academicHeight = (academicData[index] / maxValue) * (chartHeight - 20);
        const academicY = startY + chartHeight - academicHeight;
        ctx.fillStyle = '#3498db';
        ctx.fillRect(x + 2, academicY, barWidth, academicHeight);
        
        // 값 표시
        ctx.fillStyle = '#2c3e50';
        ctx.font = '10px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(generalData[index], x - barWidth/2 - 2, generalY - 5);
        ctx.fillText(academicData[index], x + barWidth/2 + 2, academicY - 5);
        
        // 월 표시
        ctx.fillText(month, x, startY + chartHeight + 15);
    });
}

// 마이페이지로 이동
function goToMyPage() {
    window.open('/counselor/mypage', '_blank');
}

// 스케줄 관리로 이동
function goToSchedule(type) {
    let message = '';
    let url = '';
    
    switch(type) {
        case 'weekly':
            message = '일반 상담 상세보기로 이동합니다.';
            url = '/counselor/consultation/general';
            break;
        case 'monthly':
            message = '학업 상담 상세보기로 이동합니다.';
            url = '/counselor/consultation/academic';
            break;
        case 'emergency':
            message = '진로/취업 상담 상세보기로 이동합니다.';
            url = '/counselor/consultation/career';
            break;
        case 'total':
            message = '학습 컨설팅 상세보기로 이동합니다.';
            url = '/counselor/consultation/learning';
            break;
    }
    
    if (confirm(message)) {
        window.location.href = url;
    }
}

// 공지사항 상세보기
function openNotice(id) {
    window.location.href = `/counselor/notice/${id}`;
}

// 공지사항 전체보기
function goToNotices() {
    window.location.href = '/counselor/notices';
}

// 로그아웃
function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        window.location.href = '/logout';
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 차트 그리기
    drawStatsChart();
    
    // 사이드바는 counselor_sidebar.js에서 처리하므로 여기서는 제거
    // (사이드바 이벤트 핸들러 코드 삭제됨)
    
    // 로그아웃 버튼 이벤트
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});