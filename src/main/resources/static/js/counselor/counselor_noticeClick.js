// 공지사항 상세 페이지 JavaScript


// 목록으로 돌아가기
function goToNoticeList() {
    window.location.href = '/counselor/notice';
}

// 다른 공지사항으로 이동
function goToNotice(noticeId) {
    if (noticeId) {
        alert(`공지사항 ${noticeId}번으로 이동합니다.`);
        // window.location.href = `counselor_noticeClick.html?id=${noticeId}`;
    }
}

