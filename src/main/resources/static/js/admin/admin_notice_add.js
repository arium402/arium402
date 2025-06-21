

   // 파일 업로드 핸들링
   document.getElementById('fileInput').addEventListener('change', function(e) {
       const files = e.target.files;
       const currentFilesContainer = document.getElementById('currentFiles');
       
       if (files.length > 0) {
           // 첫 번째 파일이 추가될 때 "첨부된 파일이 없습니다" 메시지 제거
           if (currentFilesContainer.textContent.includes('첨부된 파일이 없습니다')) {
               currentFilesContainer.innerHTML = '';
               currentFilesContainer.style.display = 'block';
               currentFilesContainer.style.justifyContent = 'flex-start';
               currentFilesContainer.style.alignItems = 'flex-start';
               currentFilesContainer.style.flexDirection = 'column';
               currentFilesContainer.style.fontStyle = 'normal';
           }
           
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
       }
   });

   // 파일 삭제 함수
   function deleteFile(button) {
       if (confirm('이 파일을 삭제하시겠습니까?')) {
           const fileItem = button.parentElement;
           const currentFilesContainer = document.getElementById('currentFiles');
           
           fileItem.remove();
           
           // 모든 파일이 삭제되면 기본 메시지 표시
           if (currentFilesContainer.children.length === 0) {
               currentFilesContainer.innerHTML = '첨부된 파일이 없습니다.';
               currentFilesContainer.style.display = 'flex';
               currentFilesContainer.style.justifyContent = 'center';
               currentFilesContainer.style.alignItems = 'center';
               currentFilesContainer.style.fontStyle = 'italic';
           }
       }
   }

   // 폼 제출 처리
   document.getElementById('noticeRegisterForm').addEventListener('submit', function(e) {
       e.preventDefault();
       
       // 폼 유효성 검사
       const title = document.getElementById('noticeTitle').value.trim();
       const category = document.getElementById('noticeCategory').value;
       const isPublic = document.getElementById('isPublic').value;
       const isFixed = document.getElementById('isFixed').value;
       const content = document.getElementById('noticeContent').value.trim();
       
       if (!title) {
           alert('제목을 입력하세요.');
           document.getElementById('noticeTitle').focus();
           return;
       }
       
       if (!category) {
           alert('분류를 선택하세요.');
           document.getElementById('noticeCategory').focus();
           return;
       }
       
       if (!isPublic) {
           alert('공개여부를 선택하세요.');
           document.getElementById('isPublic').focus();
           return;
       }
       
       if (!isFixed) {
           alert('고정여부를 선택하세요.');
           document.getElementById('isFixed').focus();
           return;
       }
       
       if (!content) {
           alert('내용을 입력하세요.');
           document.getElementById('noticeContent').focus();
           return;
       }
       
       // 폼 데이터 수집
       const formData = {
           title: title,
           category: category,
           isPublic: isPublic,
           isFixed: isFixed,
           content: content
       };
       
       // 첨부파일 정보 수집
       const fileItems = document.querySelectorAll('.file-item .file-name');
       const attachedFiles = Array.from(fileItems).map(item => item.textContent);
       
       console.log('등록할 데이터:', formData);
       console.log('첨부파일 목록:', attachedFiles);
       
       if (confirm('공지사항을 등록하시겠습니까?')) {
           alert('공지사항이 성공적으로 등록되었습니다.');
           // 실제로는 서버로 데이터 전송 후 목록 페이지로 이동
           // window.location.href = 'notice-management.html';
       }
   });

   // 취소 버튼 클릭
   function cancelRegister() {
       if (confirm('등록을 취소하시겠습니까? 입력한 내용이 삭제됩니다.')) {
           // 폼 초기화
           document.getElementById('noticeRegisterForm').reset();
           
           // 첨부파일 목록 초기화
           const currentFilesContainer = document.getElementById('currentFiles');
           currentFilesContainer.innerHTML = '첨부된 파일이 없습니다.';
           currentFilesContainer.style.display = 'flex';
           currentFilesContainer.style.justifyContent = 'center';
           currentFilesContainer.style.alignItems = 'center';
           currentFilesContainer.style.fontStyle = 'italic';
           
           alert('목록 페이지로 돌아갑니다.');
           // 실제로는 목록 페이지로 이동                                                                                                                                                                                                                                                   / 실제로는 목록 페이지로 이동
           // window.location.href = 'notice-management.html';
       }
   }

   // 텍스트 에디터 자동 높이 조절
   const contentEditor = document.getElementById('noticeContent');
   contentEditor.addEventListener('input', function() {
       this.style.height = 'auto';
       this.style.height = this.scrollHeight + 'px';
   });
