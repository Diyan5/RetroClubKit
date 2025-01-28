package org.retroclubkit.tshirt.service;

import org.retroclubkit.tshirt.model.Category;
import org.retroclubkit.tshirt.model.Tshirt;
import org.retroclubkit.tshirt.repository.TshirtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TshirtService {

    private final TshirtRepository tshirtRepository;

    @Autowired
    public TshirtService(TshirtRepository tshirtRepository) {
        this.tshirtRepository = tshirtRepository;
    }


    public List<Tshirt> getTshirtsByCategory(Category category) {
        return tshirtRepository.findAllByCategory(category);
    }
}
