// FAQ 데이터
const faqData = {
	'비교과': {
		'신청': [
			{
				question: '비교과 프로그램 신청은 어떻게 하나요?',
				answer: `<h4>신청 방법</h4>
					<p>비교과 프로그램 신청은 다음과 같은 단계로 진행됩니다:</p>
					<ul>
						<li>학생상담센터 홈페이지 접속</li>
						<li>비교과 메뉴 → 비교과 목록/신청 클릭</li>
						<li>원하는 프로그램 선택 후 신청하기 버튼 클릭</li>
						<li>신청서 작성 및 제출</li>
					</ul>
					<p>신청 완료 후 승인 여부는 이메일로 안내드립니다.</p>`
			},
			{
				question: '비교과 프로그램 신청 취소는 가능한가요?',
				answer: `<h4>신청 취소 정책</h4>
					<p>비교과 프로그램 신청 취소는 프로그램 시작일 3일 전까지 가능합니다.</p>
					<ul>
						<li>취소 방법: 비교과 신청내역에서 취소 버튼 클릭</li>
						<li>프로그램 시작 후에는 취소 불가</li>
						<li>불가피한 사유가 있을 경우 담당자에게 문의</li>
					</ul>`
			},
			{
				question: '비교과 프로그램 신청 시 선발 기준이 있나요?',
				answer: `<h4>선발 기준</h4>
					<p>대부분의 비교과 프로그램은 선착순으로 진행되지만, 일부 프로그램은 다음과 같은 기준으로 선발합니다:</p>
					<ul>
						<li>학년별 배정 인원</li>
						<li>전공별 배정 인원</li>
						<li>참여 동기 및 목적</li>
						<li>이전 비교과 프로그램 참여 이력</li>
					</ul>`
			}
		],
		'진행': [
			{
				question: '비교과 프로그램 출석은 어떻게 체크하나요?',
				answer: `<h4>출석 체크 방법</h4>
					<p>비교과 프로그램의 출석 체크는 프로그램 형태에 따라 다릅니다:</p>
					<ul>
						<li><strong>온라인 프로그램:</strong> 접속 로그 및 퀴즈 참여로 확인</li>
						<li><strong>오프라인 프로그램:</strong> 현장에서 QR코드 스캔 또는 서명</li>
						<li><strong>하이브리드 프로그램:</strong> 각 세션별 해당 방식 적용</li>
					</ul>
					<p>출석률 80% 이상 시 수료증이 발급됩니다.</p>`
			},
			{
				question: '비교과 프로그램 진행 중 일정 변경이 있을 때는 어떻게 알려주나요?',
				answer: `<h4>일정 변경 안내</h4>
					<p>프로그램 일정 변경 시 다음과 같은 방법으로 안내해드립니다:</p>
					<ul>
						<li>이메일 개별 발송</li>
						<li>SMS 문자 발송</li>
						<li>홈페이지 공지사항 게시</li>
						<li>프로그램별 단체 채팅방 안내</li>
					</ul>
					<p>중요한 변경사항은 최소 3일 전에 안내드립니다.</p>`
			}
		]
	},
	'상담': {
		'심리': [
			{
				question: '심리상담은 어떻게 신청하나요?',
				answer: `<h4>심리상담 신청 방법</h4>
					<p>심리상담은 다음과 같은 방법으로 신청할 수 있습니다:</p>
					<ul>
						<li>온라인 신청: 홈페이지 상담 메뉴에서 신청</li>
						<li>전화 신청: 02-123-4567</li>
						<li>방문 신청: 학생상담센터 직접 방문</li>
					</ul>
					<p>상담은 개인정보 보호를 위해 완전 비밀보장됩니다.</p>`
			},
			{
				question: '심리상담 비용은 얼마인가요?',
				answer: `<h4>상담 비용</h4>
					<p>재학생 심리상담은 무료로 제공됩니다.</p>
					<ul>
						<li>개인상담: 무료 (연 10회까지)</li>
						<li>집단상담: 무료</li>
						<li>심리검사: 무료 (기본 검사)</li>
						<li>전문 심리검사: 실비 부담</li>
					</ul>`
			}
		],
		'익명': [
			{
				question: '익명상담은 어떻게 이용하나요?',
				answer: `<h4>익명상담 이용 방법</h4>
					<p>익명상담은 개인정보 없이 상담을 받을 수 있는 서비스입니다:</p>
					<ul>
						<li>홈페이지 익명상담 게시판 이용</li>
						<li>실시간 채팅상담 (평일 9-18시)</li>
						<li>익명 전화상담</li>
					</ul>
					<p>모든 상담 내용은 비밀보장되며, 개인 식별이 불가능합니다.</p>`
			}
		],
		'위기': [
			{
				question: '위기상담은 언제 이용할 수 있나요?',
				answer: `<h4>위기상담 운영시간</h4>
					<p>위기상담은 24시간 운영됩니다:</p>
					<ul>
						<li><strong>평일 9-18시:</strong> 전문상담사 직접 상담</li>
						<li><strong>야간/주말:</strong> 위기상담 핫라인 운영</li>
						<li><strong>응급상황:</strong> 즉시 전문기관 연계</li>
					</ul>
					<p>위기상황 시 주저하지 마시고 즉시 연락해주세요.</p>`
			}
		],
		'취업/진로': [
			{
				question: '진로상담은 어떤 내용으로 진행되나요?',
				answer: `<h4>진로상담 내용</h4>
					<p>진로상담에서는 다음과 같은 내용을 다룹니다:</p>
					<ul>
						<li>적성 및 흥미 탐색</li>
						<li>전공 관련 진로 정보 제공</li>
						<li>취업 전략 수립</li>
						<li>이력서 및 자기소개서 작성 지도</li>
						<li>면접 준비 및 모의면접</li>
					</ul>`
			}
		],
		'학습': [
			{
				question: '학습컨설팅은 어떤 도움을 받을 수 있나요?',
				answer: `<h4>학습컨설팅 서비스</h4>
					<p>학습컨설팅에서는 다음과 같은 도움을 받을 수 있습니다:</p>
					<ul>
						<li>개인별 학습 계획 수립</li>
						<li>효과적인 학습 방법 안내</li>
						<li>시간 관리 및 학습 환경 조성</li>
						<li>학습 동기 부여 및 목표 설정</li>
						<li>학습 장애 요인 분석 및 해결</li>
					</ul>`
			}
		]
	},
	'기타': {
		'마일리지': [
			{
				question: '마일리지는 어떻게 적립되나요?',
				answer: `<h4>마일리지 적립 방법</h4>
					<p>마일리지는 다음과 같은 활동으로 적립됩니다:</p>
					<ul>
						<li>비교과 프로그램 참여: 프로그램별 차등 지급</li>
						<li>상담 서비스 이용: 회당 50P</li>
						<li>설문조사 참여: 회당 30P</li>
						<li>행사 참여: 행사별 차등 지급</li>
						<li>게시판 활동: 좋은 글 작성 시 100P</li>
					</ul>`
			},
			{
				question: '마일리지는 어떻게 사용할 수 있나요?',
				answer: `<h4>마일리지 사용 방법</h4>
					<p>적립된 마일리지는 다양한 방법으로 사용할 수 있습니다:</p>
					<ul>
						<li>도서구입비 지원: 1,000P당 1,000원</li>
						<li>교육용품 구매: 마일리지샵 이용</li>
						<li>장학금 신청: 10,000P 이상 시 신청 가능</li>
						<li>특별 프로그램 우선 신청권</li>
					</ul>`
			}
		],
		'핵심역량': [
			{
				question: '핵심역량 진단은 어떻게 받나요?',
				answer: `<h4>핵심역량 진단 방법</h4>
					<p>핵심역량 진단은 다음과 같이 진행됩니다:</p>
					<ul>
						<li>온라인 설문조사 참여 (약 30분 소요)</li>
						<li>6개 영역별 역량 측정</li>
						<li>개인별 맞춤 결과 리포트 제공</li>
						<li>역량 향상 방안 안내</li>
					</ul>
					<p>진단은 학기당 1회 실시하며, 결과는 개인 포트폴리오로 관리됩니다.</p>`
			},
			{
				question: '핵심역량 결과는 어떻게 활용할 수 있나요?',
				answer: `<h4>핵심역량 결과 활용</h4>
					<p>핵심역량 진단 결과는 다음과 같이 활용할 수 있습니다:</p>
					<ul>
						<li>개인별 역량 개발 계획 수립</li>
						<li>맞춤형 비교과 프로그램 추천</li>
						<li>진로 및 취업 상담 시 참고자료</li>
						<li>포트폴리오 작성 시 활용</li>
						<li>장학금 신청 시 가산점 부여</li>
					</ul>`
			}
		]
	}
};

// 현재 선택된 탭 정보
let currentMainTab = '비교과';
let currentSubTab = '';

// 대분류 탭 변경
function showMainTab(tabName) {
	currentMainTab = tabName;

	// 대분류 탭 활성화
	document.querySelectorAll('.main-tab-btn').forEach(btn => {
		btn.classList.remove('active');
		if (btn.textContent === tabName) {
			btn.classList.add('active');
		}
	});

	// 소분류 탭 생성
	createSubTabs(tabName);
}

// 소분류 탭 생성
function createSubTabs(mainTab) {
	const subTabsContainer = document.getElementById('sub-tabs');
	const subCategories = Object.keys(faqData[mainTab]);

	subTabsContainer.innerHTML = '';

	subCategories.forEach((category, index) => {
		const btn = document.createElement('button');
		btn.className = `sub-tab-btn ${index === 0 ? 'active' : ''}`;
		btn.textContent = category;
		btn.onclick = () => showSubTab(mainTab, category);
		subTabsContainer.appendChild(btn);
	});

	// 첫 번째 소분류 탭의 FAQ 표시
	currentSubTab = subCategories[0];
	showFAQList(mainTab, subCategories[0]);
}

// 소분류 탭 변경
function showSubTab(mainTab, subTab) {
	currentSubTab = subTab;

	// 소분류 탭 활성화
	document.querySelectorAll('.sub-tab-btn').forEach(btn => {
		btn.classList.remove('active');
	});
	event.target.classList.add('active');

	// FAQ 목록 표시
	showFAQList(mainTab, subTab);
}

// FAQ 목록 표시
function showFAQList(mainTab, subTab) {
	const faqListContainer = document.getElementById('faq-list');
	const faqs = faqData[mainTab][subTab];

	faqListContainer.innerHTML = '';

	faqs.forEach((faq, index) => {
		const faqItem = document.createElement('div');
		faqItem.className = 'faq-item';
		faqItem.innerHTML = `
			<button class="faq-question" onclick="toggleFAQ(this)">
				<span class="faq-toggle-icon">▷</span>
				<span>Q. ${faq.question}</span>
			</button>
			<div class="faq-answer">
				<div class="faq-answer-content">${faq.answer}</div>
			</div>
		`;
		faqListContainer.appendChild(faqItem);
	});
}

// FAQ 토글
function toggleFAQ(element) {
	const answer = element.nextElementSibling;
	const icon = element.querySelector('.faq-toggle-icon');

	// 다른 FAQ 닫기
	document.querySelectorAll('.faq-question.active').forEach(item => {
		if (item !== element) {
			item.classList.remove('active');
			item.nextElementSibling.classList.remove('active');
			item.querySelector('.faq-toggle-icon').textContent = '▷';
		}
	});

  	// 현재 FAQ 토글
	element.classList.toggle('active');
  	answer.classList.toggle('active');
	icon.textContent = element.classList.contains('active') ? '▽' : '▷';
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {

	// 기본 탭 설정 - 약간의 지연을 두어 DOM이 완전히 로드된 후 실행
	setTimeout(() => {
		showMainTab('비교과');
	}, 100);
});