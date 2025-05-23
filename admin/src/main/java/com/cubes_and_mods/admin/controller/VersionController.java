package com.cubes_and_mods.admin.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cubes_and_mods.admin.jpa.Version;
import com.cubes_and_mods.admin.jpa.repos.VersionRepos;

@RestController
@RequestMapping("/api/versions")
public class VersionController {

    @Autowired
    private VersionRepos versionRepos;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVersion(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("idGame") Integer idGame,
            @RequestParam("archive") MultipartFile file) {
        try {
            Version version = new Version();
            version.setName(name);
            version.setDescription(description);
            version.setIdGame(idGame);
            version.setArchive(file.getBytes());

            versionRepos.save(version);
            return ResponseEntity.ok("Uploaded");
            
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file");
        }
    }

    @GetMapping("/list")
    public List<VersionDto> listVersions(@RequestParam("idGame") Integer idGame,
                                         @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return versionRepos.findByGameIdAndNameContaining(idGame, search);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVersion(@PathVariable("id") Integer id) {
        versionRepos.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Integer id) {
        Version version = versionRepos.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getName() + ".zip\"")
                .body(version.getArchive());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateVersion(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("idGame") Integer idGame,
            @RequestParam(value = "archive", required = false) MultipartFile file) {
        try {
            Version version = versionRepos.findById(id).orElseThrow();
            version.setName(name);
            version.setDescription(description);
            version.setIdGame(idGame);
            if (file != null && !file.isEmpty()) {
                version.setArchive(file.getBytes());
            }
            versionRepos.save(version);
            return ResponseEntity.ok("Updated");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating version");
        }
    }
}
