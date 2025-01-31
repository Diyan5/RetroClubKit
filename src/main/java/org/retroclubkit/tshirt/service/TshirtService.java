package org.retroclubkit.tshirt.service;

import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TshirtService {

    private final TshirtRepository tshirtRepository;

    @Autowired
    public TshirtService(TshirtRepository tshirtRepository) {
        this.tshirtRepository = tshirtRepository;
    }


    public List<Tshirt> getTshirtsByCategoryAndAvailable(Category category) {
        return tshirtRepository.getTshirtsByCategoryAndIsAvailableTrue(category);
    }
    public List<Tshirt> getAllTshirtsLimit() {
        return tshirtRepository.getAllTshirtsLimit(10);
    }

    public List<Tshirt> getAllTshirts() {
        return tshirtRepository.findAll();
    }

    public Tshirt getById(UUID id) {
        return tshirtRepository.findById(id).orElse(null);
    }
}
