package com.cubes_and_mods.web.Clients.model;

import java.util.List;

public class FileInfo {
	
	/*
	public boolean isDirectory;
	public List<FileInfo> files;
	public byte[] contents_bytes;
	public String name;
	*/
	public String path;
    public byte[] contents;
    public boolean isDirectory;
    public List<FileInfo> children;
    public Long size;
}
