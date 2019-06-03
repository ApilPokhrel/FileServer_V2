package com.fileserver.app.works.file;

public interface FileInterface {
    FileSchema create(FileSchema fileSchema);

    FileSchema findOne(String field, String value);
}
