package com.ratnaafin.crm.user.model;

import javax.persistence.*;

@Table(name = "crm_los_document_lob_dtl")
@Entity(name = "Crm_los_document_lob_dtl")
public class DocUploadBlobDtl {

    @Id
    @Column(name = "doc_uuid")
    private String uuid;

    @Column(name = "DOC_NAME")
    private String doc_name;

    @Column(name = "DOC_CONTENT_TYPE")
    private String docContentType;

    @Column(name = "DOC_SIZE")
    private Long doc_size;

    @Lob
    @Column(name = "DOC_DATA")
    private byte[] data;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "BANK_LINE_ID")
    private Long bank_line_id;

    @Column(name = "PARAM1")
    private  String param1;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDocContentType() {
        return docContentType;
    }

    public void setDocContentType(String docContentType) {
        this.docContentType = docContentType;
    }

    public Long getDoc_size() {
        return doc_size;
    }

    public void setDoc_size(Long doc_size) {
        this.doc_size = doc_size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public Long getBank_line_id() {
        return bank_line_id;
    }

    public void setBank_line_id(Long bank_line_id) {
        this.bank_line_id = bank_line_id;
    }
}
