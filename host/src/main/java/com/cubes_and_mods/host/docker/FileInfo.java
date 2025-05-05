package com.cubes_and_mods.host.docker;

import java.util.List;

/**
 * DTO-класс для передачи информации о файлах внутри контейнера, contents - содержимое файла, если это файл,
 * ни в коем случае не определять, если целью запроса не является содержимое файла, а только его путь
 */
public class FileInfo {

    public String path;
    public byte[] contents;
    public boolean isDirectory;
    public List<FileInfo> children;
    public Long size;
}
