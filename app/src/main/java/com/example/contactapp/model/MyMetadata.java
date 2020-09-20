package com.example.contactapp.model;

import com.dropbox.core.v2.files.FileMetadata;

import java.io.Serializable;
import java.util.Date;

public class MyMetadata extends FileMetadata implements Serializable {

    public MyMetadata(String name, String id, Date clientModified, Date serverModified, String rev, long size) {
        super(name, id, clientModified, serverModified, rev, size);
    }
}
