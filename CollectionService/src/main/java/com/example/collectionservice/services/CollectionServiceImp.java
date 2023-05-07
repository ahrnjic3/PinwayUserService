package com.example.collectionservice.services;

import com.example.collectionservice.exception.PinwayError;
import com.example.collectionservice.models.Collection;
import com.example.collectionservice.models.CollectionVisibilityType;
import com.example.collectionservice.repositories.CollectionRepository;
import com.example.collectionservice.repositories.CollectionVisibilityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CollectionServiceImp implements CollectionService{

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CollectionRepository collectionRepository;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CollectionVisibilityTypeRepository collectionVisibilityTypeRepository;

    @Override
    public Collection Create(Collection collection) {
        Collection newCollection = collectionRepository.save(collection);
        return newCollection;
    }

    @Override
    public Iterable<Collection> List() {
        Iterable<Collection> collectionList = collectionRepository.findAll();
        return collectionList;
    }

    @Override
    public Collection Details(Integer id) {

        Optional<Collection> collection = collectionRepository.findById(id);

        if (collection.isPresent())
            return collection.get();

        throw new PinwayError("Not found Collection with id = " + id);
    }

    @Override
    public Boolean Delete(Integer id) {

        Optional<Collection> collection = collectionRepository.findById(id);

        if (collection.isPresent()) {
            collectionRepository.deleteById(id);
            return true;
        }

        throw new PinwayError("Not found Collection with id = " + id);
    }

    @Override
    public Collection Update(Integer id, Collection c) {

        Optional<Collection> collection = collectionRepository.findById(id);

        if (!collection.isPresent())
            throw new PinwayError("Not found Notification with id = " + id);

        Collection newNotification = collection.get();

        newNotification.setName(c.getName());
        newNotification.setNumOfPosts(c.getNumOfPosts());
        newNotification.setCreatedAt(c.getCreatedAt());
        newNotification.setCollectionVisibilityType(c.getCollectionVisibilityType());

        collectionRepository.save(newNotification);
        return newNotification;
    }

    @Override
    public Iterable<CollectionVisibilityType> ListVisibilityTypes() {

        Iterable<CollectionVisibilityType> collectionVisibilityTypes = collectionVisibilityTypeRepository.findAll();
        return collectionVisibilityTypes;
    }
}
