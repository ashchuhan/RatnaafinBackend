package com.ratnaafin.crm.user.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Table(name = "test_blob")
@Entity(name = "Test_blob")
public class testBlob {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "ID")
    private String id;

    @Column(name = "FILENAME")
    private String fileName;

    @Column(name = "FILETYPE")
    private String fileType;

    @Column(name = "REF_INQUIRY_ID")
    private Long ref_inquiry_id;

    @Lob
    @Column(name = "DOC")
    private byte[] data;

    public testBlob() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(Long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
    }
}
