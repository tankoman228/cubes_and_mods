package com.cubes_and_mods.web.Clients.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileInfo {
	
	public boolean isDirectory;
	public List<FileInfo> files;
	public byte[] contents_bytes;
	public String name;
}
