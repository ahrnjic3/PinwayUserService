package com.example.notificationservice.services;

import com.example.notificationservice.dto.NotificationOpenOnlyDTO;
import com.example.notificationservice.exception.PinwayError;
import com.example.notificationservice.models.Notification;
import com.example.notificationservice.models.NotificationType;
import com.example.notificationservice.repositories.NotificationRepository;
import com.example.notificationservice.repositories.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class NotificationServiceImp implements NotificationService {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private NotificationRepository notificationRepository;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private NotificationTypeRepository notificationTypeRepository;

    @Override
    public Notification Create(Notification notification) {
        Notification newNotification = notificationRepository.save(notification);
        return newNotification;
    }

    @Override
    public Iterable<Notification> List() {
        Iterable<Notification> notificationList = notificationRepository.findAll();
        return notificationList;
    }

    @Override
    public  Notification Details(Integer id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isPresent())
            return notification.get();

        throw new PinwayError("Not found Notification with id = " + id);
    }

    @Override
    public Boolean Delete(Integer id) {
        Optional<Notification> notification = notificationRepository.findById(id);

        if (notification.isPresent()) {
            notificationRepository.deleteById(id);
            return true;
        }

        throw new PinwayError("Not found Notification with id = " + id);

    }


//    @Override
//    public Notification Update(Integer id, Notification n) {
//        Optional<Notification> notification = notificationRepository.findById(id);
//
//        if (!notification.isPresent())
//            throw new PinwayError("Not found Notification with id = " + id);
//
//        Notification newNotification = notification.get();
//
//        newNotification.setContent(n.getContent());
//        newNotification.setLikedComment(n.getLikedComment());
//        newNotification.setOpen(n.getOpen());
//        newNotification.setPinnedPost(n.getPinnedPost());
//        newNotification.setSharedCollection(n.getSharedCollection());
//        newNotification.setUserId(n.getUserId());
//        newNotification.setActionUserId(n.getActionUserId());
//        newNotification.setNotificationType(n.getNotificationType());
//
//        notificationRepository.save(newNotification);
//        return newNotification;
//    }

    @Override
    public Notification Update(Notification notificationPatched) {
        return notificationRepository.save(notificationPatched);
    }

    @Override
    public Integer partialUpdateOpen(Integer id, Boolean open) {
        return notificationRepository.partialUpdateOpen(id, open);
    }


    @Override
    public Iterable<NotificationType> ListNotificationTypes() {
        Iterable<NotificationType> notificationTypeList = notificationTypeRepository.findAll();
        return notificationTypeList;
    }

    @Override
    public Iterable<Notification> List10NotificationsByOpen() {
        Iterable<Notification> notifications = notificationRepository.get10NotificationsByOpen();
        return notifications;
    }
}
