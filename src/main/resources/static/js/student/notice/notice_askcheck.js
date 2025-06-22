// 현재 사용자 정보
const currentUser = '홍길동';
const isAdmin = false; // 관리자 여부 (실제로는 로그인 정보에서 가져옴)

// 샘플 데이터 (실제로는 URL 파라미터나 서버에서 가져옴)
const inquiryData = {
	no: 14,
	title: '개인적인 상담 일정 변경 요청',
	category: '상담',
	author: '홍길동',
	createDate: '2025.06.18',
	isPrivate: true,
	content: `안녕하세요. 개인 사정으로 인해 다음 주 화요일로 예정된 상담 일정을 변경하고 싶습니다.

		가능하다면 다음 주 목요일 오후 2시경으로 변경이 가능할까요?

		급한 일정 변경 요청드려 죄송합니다. 답변 부탁드립니다.

		감사합니다.`,
	answer: {
		title: '일정 변경 안내',
		author: '상담센터',
		createDate: '2025.06.18',
		content: `안녕하세요, 홍길동님.

			상담 일정 변경 요청을 확인했습니다.

			다음 주 목요일(6월 26일) 오후 2시로 일정 변경이 가능합니다.
			상담실은 기존과 동일하게 201호에서 진행됩니다.

			변경된 일정에 대한 확인 문자를 발송해드렸으니 확인 부탁드립니다.

			기타 문의사항이 있으시면 언제든지 연락주세요.

			감사합니다.`
	}
};

// 목록으로 돌아가기
function goToList() {
	if (window.opener) {
		window.close();
	}
	else {
		alert('문의게시판 목록으로 이동합니다.');
		location.href = /*[[@{/student/notice/asklist}]]*/ '/student/notice/asklist';
	}
}

// 답변 수정
function editAnswer() {
	if (!isAdmin) {
		alert('관리자만 답변을 수정할 수 있습니다.');
		return;
	}
	alert('답변 수정 페이지로 이동합니다.');
	// 실제로는 답변 수정 폼으로 이동
}

// 답변 삭제
function deleteAnswer() {
	if (!isAdmin) {
		alert('관리자만 답변을 삭제할 수 있습니다.');
		return;
	}

	if (confirm('답변을 삭제하시겠습니까?')) {
		alert('답변이 삭제되었습니다.');
		// 실제로는 서버에 삭제 요청
		renderAnswerArea(null);
	}
}

// 문의 내용 렌더링
function renderInquiryContent() {
	// 제목 설정 (비공개 글인 경우 자물쇠 아이콘 추가)
	const titleElement = document.getElementById('questionTitle');
	let titleHtml = `Q. ${inquiryData.title}`;
	if (inquiryData.isPrivate) {
		titleHtml = `<i class="fas fa-lock lock-icon"></i>Q. ${inquiryData.title}`;
	}
	titleElement.innerHTML = titleHtml;

	// 유형 설정
	document.getElementById('categoryBadge').textContent = inquiryData.category;

	// 작성자 설정 (비공개 글의 경우 타인 글은 마스킹)
	let displayAuthor = inquiryData.author;
	if (inquiryData.isPrivate && inquiryData.author !== currentUser && !isAdmin) {
		displayAuthor = inquiryData.author.charAt(0) + '**';
	}
	document.getElementById('authorName').textContent = displayAuthor;

	// 작성일 설정
	document.getElementById('createDate').textContent = inquiryData.createDate;

	// 내용 설정
	document.getElementById('inquiryContent').innerHTML = inquiryData.content.replace(/\n/g, '<br>');
}

// 답변 영역 렌더링
function renderAnswerArea(answerData) {
	const answerArea = document.getElementById('answerArea');

	if (!answerData) {
		// 답변이 없는 경우
		answerArea.innerHTML = `
			<div class="no-answer">
				<i class="fas fa-clock" style="font-size: 2rem; color: #ccc; margin-bottom: 10px;"></i>
				<p>아직 답변이 등록되지 않았습니다.</p>
			</div>
		`;
		return;
	}

	// 답변이 있는 경우
	answerArea.innerHTML = `
		<div class="answer-info">
			<div class="info-item">
				<span class="info-label">작성자</span>
				<span class="info-value">${answerData.author}</span>
			</div>
			<div class="info-item">
				<span class="info-label">작성일</span>
				<span class="info-value">${answerData.createDate}</span>
			</div>
		</div>
		<div class="answer-content">${answerData.content.replace(/\n/g, '<br>')}</div>
		${isAdmin ? `
			<div class="answer-buttons">
				<button class="btn-edit" onclick="editAnswer()">
					<i class="fas fa-edit"></i>
					수정
				</button>
				<button class="btn-delete" onclick="deleteAnswer()">
					<i class="fas fa-trash"></i>
					삭제
				</button>
			</div>
		` : ''}
	`;

	// 답변 제목 설정
	document.getElementById('answerTitleText').textContent = `A. ${answerData.title}`;
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
	// 비공개 글 접근 권한 확인
	if (inquiryData.isPrivate && inquiryData.author !== currentUser && !isAdmin) {
		alert('접근 권한이 없습니다.');
		goToList();
		return;
	}

	// 콘텐츠 렌더링
	renderInquiryContent();
	renderAnswerArea(inquiryData.answer);
});