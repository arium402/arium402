// 샘플 통계 데이터
       const statisticsData = {
           2025: {
               1: {
                   psychology: { completed: 28, scheduled: 12, gender: [18, 10], grade: [8, 6, 9, 5] },
                   anonymous: { completed: 15, scheduled: 8, gender: [12, 3], grade: [5, 4, 3, 3] },
                   crisis: { completed: 8, scheduled: 3, gender: [6, 2], grade: [2, 3, 2, 1] },
                   career: { completed: 35, scheduled: 18, gender: [22, 13], grade: [10, 8, 12, 5] },
                   consulting: { completed: 22, scheduled: 11, gender: [14, 8], grade: [6, 7, 5, 4] }
               },
               2: {
                   psychology: { completed: 24, scheduled: 9, gender: [16, 8], grade: [7, 5, 8, 4] },
                   anonymous: { completed: 12, scheduled: 6, gender: [10, 2], grade: [4, 3, 3, 2] },
                   crisis: { completed: 6, scheduled: 2, gender: [5, 1], grade: [1, 2, 2, 1] },
                   career: { completed: 31, scheduled: 15, gender: [19, 12], grade: [9, 7, 10, 5] },
                   consulting: { completed: 19, scheduled: 8, gender: [12, 7], grade: [5, 6, 4, 4] }
               }
           },
           2024: {
               1: {
                   psychology: { completed: 26, scheduled: 10, gender: [17, 9], grade: [7, 6, 8, 5] },
                   anonymous: { completed: 13, scheduled: 7, gender: [11, 2], grade: [4, 4, 3, 2] },
                   crisis: { completed: 7, scheduled: 2, gender: [5, 2], grade: [2, 2, 2, 1] },
                   career: { completed: 33, scheduled: 16, gender: [21, 12], grade: [9, 8, 11, 5] },
                   consulting: { completed: 20, scheduled: 9, gender: [13, 7], grade: [5, 6, 5, 4] }
               },
               2: {
                   psychology: { completed: 22, scheduled: 8, gender: [15, 7], grade: [6, 5, 7, 4] },
                   anonymous: { completed: 11, scheduled: 5, gender: [9, 2], grade: [3, 3, 3, 2] },
                   crisis: { completed: 5, scheduled: 1, gender: [4, 1], grade: [1, 2, 1, 1] },
                   career: { completed: 29, scheduled: 13, gender: [18, 11], grade: [8, 6, 9, 6] },
                   consulting: { completed: 17, scheduled: 7, gender: [11, 6], grade: [4, 5, 4, 3] }
               }
           }
       };

       let charts = {};
       let currentYear = 2025;
       let currentSemester = 1;

       
	   /* ********sidebar.js******** */

       // 차트 생성 함수
       function createChart(canvasId, data, colors, isGender = false) {
           const ctx = document.getElementById(canvasId).getContext('2d');
           
           if (charts[canvasId]) {
               charts[canvasId].destroy();
           }

           const labels = isGender ? ['여자', '남자'] : ['1학년', '2학년', '3학년', '4학년'];
           
           charts[canvasId] = new Chart(ctx, {
               type: 'pie',
               data: {
                   labels: labels,
                   datasets: [{
                       data: data,
                       backgroundColor: colors,
                       borderWidth: 2,
                       borderColor: '#fff'
                   }]
               },
               options: {
                   responsive: false,
                   maintainAspectRatio: false,
                   plugins: {
                       legend: {
                           display: false
                       }
                   }
               }
           });
       }

       // 통계 데이터 업데이트
       function updateStatistics() {
           const yearData = statisticsData[currentYear];
           if (!yearData || !yearData[currentSemester]) return;

           const semesterData = yearData[currentSemester];
           const semesterTitle = `${currentYear}학년도 ${currentSemester}학기 통계`;

           // 각 상담 분야별 데이터 업데이트
           const fields = ['psychology', 'anonymous', 'crisis', 'career', 'consulting'];
           const fieldNames = ['심리', '익명', '위기', '진로', '학습컨설팅'];
           
           fields.forEach((field, index) => {
               const data = semesterData[field];
               const prefix = field === 'career' ? 'career' : 
                             field === 'consulting' ? 'consulting' :
                             field === 'psychology' ? 'psychology' :
                             field === 'anonymous' ? 'anonymous' : 'crisis';

               // 학기 제목 업데이트
               document.getElementById(`${prefix}SemesterTitle`).textContent = semesterTitle;
               
               // 완료/예정 건수 업데이트
               document.getElementById(`${prefix}Completed`).textContent = `${data.completed} 건`;
               document.getElementById(`${prefix}Scheduled`).textContent = `${data.scheduled} 건`;

               // 차트 생성
               createChart(`${prefix}GenderChart`, data.gender, ['#ff6b6b', '#4ecdc4'], true);
               createChart(`${prefix}GradeChart`, data.grade, ['#3498db', '#e74c3c', '#f39c12', '#27ae60']);
           });
       }

       // 년도/학기 변경 이벤트
       document.getElementById('yearSelect').addEventListener('change', function() {
           currentYear = parseInt(this.value);
           updateStatistics();
       });

       document.getElementById('semesterSelect').addEventListener('change', function() {
           currentSemester = parseInt(this.value);
           updateStatistics();
       });

       // 초기 통계 로드
       document.addEventListener('DOMContentLoaded', function() {
           updateStatistics();
       });