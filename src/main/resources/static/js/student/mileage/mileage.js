// 차트 초기화
let mileageChart;

function initChart() {
	const ctx = document.getElementById('mileageChart').getContext('2d');
  
	// 기존 차트가 있으면 파괴
	if (mileageChart) {
		mileageChart.destroy();
	}

	mileageChart = new Chart(ctx, {
		type: 'bar',
		data: {
			labels: ['내 포인트', '학과평균', '학년평균', '전체평균'],
			datasets: [{
				label: '마일리지',
				data: [1850, 1245, 1180, 965],
				backgroundColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderColor: ['#4a6fa5', '#6c7b95', '#8e9db3', '#b0bdd1'],
				borderWidth: 0
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
					max: 2000,
					ticks: {
						callback: function(value) {
						return value + 'P';
						}
					},
					grid: {
						color: '#e0e0e0'
					}
				},
				x: {
					grid: {
						display: false
					}
				},
			},
			onHover: (event, activeElements) => {
				event.native.target.style.cursor = activeElements.length > 0 ? 'pointer' : 'default';
			},
			plugins: [{
				afterDatasetsDraw: function(chart) {
					const ctx = chart.ctx;
					chart.data.datasets.forEach((dataset, i) => {
						const meta = chart.getDatasetMeta(i);
						meta.data.forEach((bar, index) => {
							const data = dataset.data[index];
							ctx.fillStyle = '#333';
							ctx.font = 'bold 12px Arial';
							ctx.textAlign = 'center';
							ctx.textBaseline = 'bottom';
							ctx.fillText(data + 'P', bar.x, bar.y - 5);
						});
					});
				}
			}]
		}
	});
}

// 윈도우 리사이즈시 차트 다시 그리기
function resizeChart() {
	if (mileageChart) {
		mileageChart.resize();
	}
}

// 페이지 로드 시 차트 초기화
document.addEventListener('DOMContentLoaded', function() {
	initChart();
	setDefaultDateRange();

	// 윈도우 리사이즈 이벤트 리스너 추가
	window.addEventListener('resize', function() {
		setTimeout(resizeChart, 100);
	});
  
	// 마일리지 가이드 콜랩스 이벤트 리스너
	const mileageGuideCollapse = document.getElementById('mileageGuide');
	if (mileageGuideCollapse) {
		mileageGuideCollapse.addEventListener('show.bs.collapse', function () {
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(180deg)';
		});

		mileageGuideCollapse.addEventListener('hide.bs.collapse', function () {
			const arrow = document.getElementById('guideArrow');
			if (arrow) arrow.style.transform = 'rotate(0deg)';
		});
	}
});

// 기본 날짜 범위 설정 (최근 3개월)
function setDefaultDateRange() {
	const today = new Date();
	const threeMonthsAgo = new Date();
	threeMonthsAgo.setMonth(today.getMonth() - 3);

	document.getElementById('endDate').value = today.toISOString().split('T')[0];
	document.getElementById('startDate').value = threeMonthsAgo.toISOString().split('T')[0];
}

// 알림 닫기
function closeNotification(button) {
	const notification = button.closest('.notification-item');
	notification.style.animation = 'slideOut 0.3s ease-in forwards';
	setTimeout(() => {
		notification.remove();
	}, 300);
}

// 날짜 필터 적용
function applyDateFilter() {
	const startDate = document.getElementById('startDate').value;
	const endDate = document.getElementById('endDate').value;

	if (startDate && endDate) {
		if (new Date(startDate) > new Date(endDate)) {
			alert('시작 날짜는 종료 날짜보다 빨라야 합니다.');
			return;
		}
		
		// 실제 구현에서는 서버에 필터 요청을 보내겠지만, 
		// 여기서는 시뮬레이션으로 메시지만 표시
		alert(`${startDate} ~ ${endDate} 기간의 데이터를 조회합니다.`);
		
		// 테이블 데이터 필터링 로직이 여기에 들어갈 것
		console.log('날짜 필터 적용:', startDate, '~', endDate);
	}
	else {
		alert('시작 날짜와 종료 날짜를 모두 선택해주세요.');
	}
}

// 데이터 새로고침
function refreshData() {
	// 로딩 표시
	const refreshBtn = event.target.closest('button');
	const originalText = refreshBtn.innerHTML;
	refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>새로고침';
	refreshBtn.disabled = true;

	// 새로운 알림 추가 시뮬레이션
	setTimeout(() => {
		addNewNotification();
		
		// 버튼 원래 상태로 복구
		refreshBtn.innerHTML = originalText;
		refreshBtn.disabled = false;
		
		alert('데이터가 새로고침되었습니다.');
	}, 1500);
}

// 새로운 알림 추가
function addNewNotification() {
	const notificationArea = document.getElementById('notificationArea');
	const newNotification = `
		<div class="notification-item notification-new">
			<div class="notification-content">
				<i class="fas fa-bell notification-icon"></i>
				<div>
					<p class="notification-text"><strong>새로운 마일리지 적립!</strong> 창의적 사고력 개발 프로그램 참여로 +45P가 적립되었습니다.</p>
					<span class="notification-time">방금 전</span>
				</div>
			</div>
			<button class="notification-close" onclick="closeNotification(this)">
				<i class="fas fa-times"></i>
			</button>
		</div>
	`;

	notificationArea.insertAdjacentHTML('afterbegin', newNotification);
}

function showMileageHistory() {
	console.log('현재 마일리지 내역 페이지입니다.');
}

function showConvertModal() {
	const modal = new bootstrap.Modal(document.getElementById('convertModal'));
	modal.show();
}

function applyConvert() {
	const convertAmount = parseInt(document.getElementById('convertAmount').value) || 0;
	const accountNumber = document.getElementById('accountNumber').value;
	const bankName = document.getElementById('bankName').value;
	const accountHolder = document.getElementById('accountHolder').value;
	const availableAmount = 1850;

	if (convertAmount <= 0) {
		alert('신청할 마일리지를 입력해주세요.');
		return;
	}

	if (convertAmount < 100) {
		alert('최소 전환 단위는 100P입니다.');
		return;
	}
  
	if (convertAmount % 100 !== 0) {
		alert('100P 단위로 입력해주세요.');
		return;
	}

	if (convertAmount > availableAmount) {
		alert('보유 마일리지를 초과했습니다.');
		return;
	}

	if (!accountNumber) {
		alert('계좌번호를 입력해주세요.');
		return;
	}

	if (!bankName) {
		alert('은행을 선택해주세요.');
		return;
	}

	if (!accountHolder) {
		alert('예금주명을 입력해주세요.');
		return;
	}

	// 전환 금액 계산 (1000p = 100,000원)
	const convertedAmount = (convertAmount / 10) * 1000;

	if (confirm(`${convertAmount.toLocaleString()}P를 ${convertedAmount.toLocaleString()}원으로 전환하시겠습니까?\n\n입금 계좌: ${bankName} ${accountNumber}\n예금주: ${accountHolder}\n\n신청 후 3~5일 내 입금됩니다.`)) {
		alert('마일리지 전환 신청이 완료되었습니다.\n\n신청 내역은 마일리지 내역에서 확인하실 수 있습니다.');
		const modal = bootstrap.Modal.getInstance(document.getElementById('convertModal'));
		modal.hide();
		resetConvertForm();
	}
}

function cancelConvert() {
	const modal = bootstrap.Modal.getInstance(document.getElementById('convertModal'));
	modal.hide();
}

function resetConvertForm() {
	document.getElementById('convertAmount').value = '';
}