// 현재 사용자 정보
const currentUser = '홍길동';
const isAdmin = false; // 관리자 여부

// 게시글 데이터
const boardData = [
	{
		no: 15,
		title: '비교과 프로그램 관련 문의드립니다',
		content: '안녕하세요. 하계 비교과 프로그램 신청과 관련하여 문의가 있습니다.',
		date: '2025.06.19',
		author: '김영희',
		views: 23,
		isPrivate: false
	},
	{
		no: 14,
		title: '개인적인 상담 일정 변경 요청',
		content: '개인 사정으로 인해 상담 일정을 변경하고 싶습니다.',
		date: '2025.06.18',
		author: '홍길동',
		views: 5,
		isPrivate: true
	},
	{
		no: 13,
		title: '마일리지 적립 관련 오류',
		content: '마일리지가 정상적으로 적립되지 않은 것 같습니다.',
		date: '2025.06.18',
		author: '이철수',
		views: 18,
		isPrivate: false
	},
	{
		no: 12,
		title: '심리상담 예약 문의',
		content: '심리상담 예약 가능한 시간대에 대해 문의드립니다.',
		date: '2025.06.17',
		author: '박민수',
		views: 12,
		isPrivate: true
	},
	{
		no: 11,
		title: '핵심역량 진단 결과가 보이지 않습니다',
		content: '진단을 완료했는데 결과 페이지에서 확인이 되지 않습니다.',
		date: '2025.06.17',
		author: '최유진',
		views: 31,
		isPrivate: false
	},
	{
		no: 10,
		title: '진로상담 추가 세션 요청',
		content: '추가 진로상담이 필요한 상황입니다.',
		date: '2025.06.16',
		author: '홍길동',
		views: 7,
		isPrivate: true
	},
	{
		no: 9,
		title: '학습컨설팅 자료 요청',
		content: '학습컨설팅에서 제공해주신 자료를 다시 받고 싶습니다.',
		date: '2025.06.16',
		author: '정하늘',
		views: 14,
		isPrivate: false
	},
	{
		no: 8,
		title: '개인정보 수정 요청',
		content: '연락처 정보 수정이 필요합니다.',
		date: '2025.06.15',
		author: '송미영',
		views: 8,
		isPrivate: true
	},
	{
		no: 7,
		title: '센터 운영시간 문의',
		content: '방학 중 센터 운영시간이 어떻게 되나요?',
		date: '2025.06.15',
		author: '강동욱',
		views: 25,
		isPrivate: false
	},
	{
		no: 6,
		title: '익명상담 이용 방법',
		content: '익명상담을 이용하고 싶은데 방법을 알려주세요.',
		date: '2025.06.14',
		author: '윤서연',
		views: 19,
		isPrivate: false
	}
];

// 게시글 목록 렌더링
function renderBoardList(data = boardData) {
	const tbody = document.getElementById('boardTableBody');
	tbody.innerHTML = '';

	data.forEach(post => {
		const row = document.createElement('tr');

		// 비공개 글 접근 권한 확인
		const canAccess = !post.isPrivate || post.author === currentUser || isAdmin;

		// 작성자 표시 (비공개 글의 경우 타인 글은 마스킹)
		let displayAuthor = post.author;
		if (post.isPrivate && post.author !== currentUser && !isAdmin) {
			displayAuthor = post.author.charAt(0) + '**';
		}

		// 제목 처리
		let titleHtml;
		if (post.isPrivate) {
			const lockIcon = '<i class="fas fa-lock lock-icon"></i>';
			if (canAccess) {
				titleHtml = `<a href="#" class="post-title" onclick="viewPost(${post.no}); return false;">
								${lockIcon}${post.title}
							</a>`;
			}
			else {
				titleHtml = `<span class="post-title private-blocked">
								${lockIcon}${post.title}
							</span>`;
			}
		}
		else {
			titleHtml = `<a href="#" class="post-title" onclick="viewPost(${post.no}); return false;">
							${post.title}
						</a>`;
		}
	
		row.innerHTML = `
			<td class="col-no">${post.no}</td>
			<td class="col-title">${titleHtml}</td>
			<td class="col-date">${post.date}</td>
			<td class="col-author">${displayAuthor}</td>
			<td class="col-views">${post.views}</td>
		`;
	
		tbody.appendChild(row);
	});
}

// 게시글 보기
function viewPost(postNo) {
	const post = boardData.find(p => p.no === postNo);
	if (!post) return;

	// 비공개 글 접근 권한 확인
	if (post.isPrivate && post.author !== currentUser && !isAdmin) {
		alert('접근 권한이 없습니다.');
		return;
	}

	alert(`게시글 ${postNo}번을 확인합니다.\n제목: ${post.title}\n작성자: ${post.author}`);
	location.href = /*[[@{/student/notice/askcheck}]]*/ '/student/notice/askcheck';
}

// 글 작성 페이지 열기
function openWritePage() {
	alert('글 작성 페이지를 새 창으로 엽니다.');
	location.href = /*[[@{/student/notice/askadd}]]*/ '/student/notice/askadd';
}

// 검색 기능
function searchPosts() {
	const searchType = document.getElementById('searchType').value;
	const searchKeyword = document.getElementById('searchInput').value.trim();

	if (!searchKeyword) {
		renderBoardList();
		return;
	}

	const filteredData = boardData.filter(post => {
		// 비공개 글 접근 권한 확인
		if (post.isPrivate && post.author !== currentUser && !isAdmin) {
			return false;
		}

		switch (searchType) {
			case 'title':
				return post.title.toLowerCase().includes(searchKeyword.toLowerCase());
			case 'content':
				return post.content.toLowerCase().includes(searchKeyword.toLowerCase());
			case 'author':
				return post.author.toLowerCase().includes(searchKeyword.toLowerCase());
			case 'all':
			default:
				return post.title.toLowerCase().includes(searchKeyword.toLowerCase()) ||
				post.content.toLowerCase().includes(searchKeyword.toLowerCase()) ||
				post.author.toLowerCase().includes(searchKeyword.toLowerCase());
		}
	});

	renderBoardList(filteredData);

	if (filteredData.length === 0) {
		const tbody = document.getElementById('boardTableBody');
		tbody.innerHTML = `
			<tr>
				<td colspan="5" style="text-align: center; padding: 50px; color: #666;">
					검색 결과가 없습니다.
				</td>
			</tr>
		`;
	}
}

// 엔터 키로 검색
document.addEventListener('DOMContentLoaded', function() {
	// 게시글 목록 렌더링
	renderBoardList();

	// 엔터 키 이벤트
	document.getElementById('searchInput').addEventListener('keypress', function(e) {
		if (e.key === 'Enter') {
			searchPosts();
		}
	});
});