package com.cubes_and_mods.res.service_repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes_and_mods.res.db.Version;
import com.cubes_and_mods.res.dto.VersionWithoutArchive;
import com.cubes_and_mods.res.service_repos.repos.ReposVersion;

import java.util.List;

@Service
public class ServiceVersion {
	
    @Autowired
    private ReposVersion versionRepository;
    
    public Version saveVersion(Version v) {
        return versionRepository.save(v); // Сохранение пользователя
    }

	public List<VersionWithoutArchive> findAllVersions() {
	    return versionRepository.findAllVersions();
	}

    public Version findVersionById(Integer id) {
        return versionRepository.findById(id).orElse(null); // Поиск пользователя по ID
    }

    public void deleteVersion(String name) {
        versionRepository.deleteByName(name); // Удаление пользователя
    }
}
