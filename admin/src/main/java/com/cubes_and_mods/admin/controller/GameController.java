package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Game;
import com.cubes_and_mods.admin.jpa.repos.GameRepos;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameRepos gameRepository;

    @GetMapping
    public List<Game> getAll() {
        return gameRepository.findAll();
    }

    @PostMapping
    public Game create(@RequestBody Game game) {
        return gameRepository.save(game);
    }

    @PutMapping("/{id}")
    public Game update(@PathVariable Integer id, @RequestBody Game game) {
        game.setId(id);
        return gameRepository.save(game);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        gameRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
