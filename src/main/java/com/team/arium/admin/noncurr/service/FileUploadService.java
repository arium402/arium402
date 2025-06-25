package com.team.arium.admin.noncurr.service;

import com.team.arium.domain.Common_File;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    Common_File uploadProgramImage(MultipartFile file);
    Common_File uploadAttachmentFile(MultipartFile file);
}