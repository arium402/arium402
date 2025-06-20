

   // 파일 업로드 핸들링
   document.getElementById('fileInput').addEventListener('change', function(e) {
       const files = e.target.files;
       const currentFilesContainer = document.getElementById('currentFiles');
       
       for (let i = 0; i < files.length; i++) {
           const file = files[i];
           
           // 새 파일 아이템 생성
           const fileItem = document.createElement('div');
           fileItem.className = 'file-item';
           fileItem.innerHTML = `
               <span class="file-name">${file.name}</span>
               <button type="button" class="file-delete" onclick="deleteFile(this)">삭제</button>
           `;
           
           currentFilesContainer.appendChild(fileItem);
       }
       
       // 파일 입력 필드 초기화
       e.target.value = '';
   });

   // 파일 삭제 함수
   function deleteFile(button) {
       if (confirm('이 파일을 삭제하시겠습니까?')) {
           button.parentElement.remove();
       }
   }

   // 폼 제출 처리
   document.getElementById('noticeEditForm').addEventListener('submit', function(e) {
       e.preventDefault();
       
       // 폼 데이터 수집
       const formData = {
           title: document.getElementById('noticeTitle').value,
           category: document.getElementById('noticeCategory').value,
           isPublic: document.getElementById('isPublic').value,
           isFixed: document.getElementById('isFixed').value,
           content: document.getElementById('noticeContent').value
       };
       
       // 첨부파일 정보 수집
       const fileItems = document.querySelectorAll('.file-item .file-name');
       const attachedFiles = Array.from(fileItems).map(item => item.textContent);
       
       console.log('수정할 데이터:', formData);
       console.log('첨부파일 목록:', attachedFiles);
       
       if (confirm('공지사항을 수정하시겠습니까?')) {
           alert('공지사항이 성공적으로 수정되었습니다.');
           // 실제로는 서버로 데이터 전송 후 목록 페이지로 이동
           // window.location.href = 'notice-management.html';
       }
   });

   // 취소 버튼 클릭
   function cancelEdit() {
       if (confirm('수정을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
           alert('목록 페이지로 돌아갑니다.');
           // 실제로는 목록 페이지로 이동
           // window.location.href = 'notice-management.html';
       }
   }

   // 텍스트 에디터 자동 높이 조절
   const contentEditor = document.getElementById('noticeContent');
   contentEditor.addEventListener('input', function() {
       this.style.height = 'auto';
       this.style.height = this.scrollHeight + 'px';
   });

   // 페이지 로드 시 초기 높이 설정
   window.addEventListener('DOMContentLoaded', function() {
       contentEditor.style.height = contentEditor.scrollHeight + 'px';
   });
