package com.sweetme.back.common.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    private Long id;
    private String filename;
    private String fileUrl;

    public static FileDTO from(FileEntity file) {
        return FileDTO.builder()
                .id(file.getId())
                .filename(file.getFilename())
                .fileUrl(file.getFileUrl())
                .build();
    }

    public FileEntity toEntity() {
        FileEntity entity = new FileEntity();
        entity.setId(this.id);
        entity.setFilename(this.filename);
        entity.setFileUrl(this.fileUrl);
        return entity;
    }
}
