package com.hse.knopkabackend.services;

import com.hse.knopkabackend.DTO.KnopkaUserResponseDTO;
import com.hse.knopkabackend.models.KnopkaUser;
import com.hse.knopkabackend.models.Profile;
import com.hse.knopkabackend.repositories.KnopkaUserRepository;
import com.hse.knopkabackend.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class KnopkaUserService {

    private final KnopkaUserRepository knopkaUserRepository;
    private final ProfileRepository profileKnopkaUserRepository;

    @Autowired
    public KnopkaUserService(KnopkaUserRepository knopkaUserRepository, ProfileRepository profileKnopkaUserRepository) {
        this.knopkaUserRepository = knopkaUserRepository;
        this.profileKnopkaUserRepository = profileKnopkaUserRepository;
    }

    public List<KnopkaUser> getKnopkaUsers() {
        return knopkaUserRepository.findAll();
    }

    public void addNewKnopkaUser(KnopkaUser knopkaUser, Profile profile) {
        Optional<KnopkaUser> knopkaUserByEmail = knopkaUserRepository.findKnopkaUserByEmail(
                knopkaUser.getEmail()
        );
        if (knopkaUserByEmail.isPresent()) {
            throw new IllegalStateException("Oops! Email '" +
                    knopkaUser.getEmail() + "' is already taken. Try another one."
            );
        }
        knopkaUserRepository.save(knopkaUser);
        profile.setUserId(knopkaUser.getId());
        profileKnopkaUserRepository.save(profile);
        System.out.println("Added user with unique email: " + knopkaUser);
    }


    public void deleteKnopkaUser(Long knopkaUserId, String token) {
        KnopkaUser knopkaUserById = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );
        if (Objects.equals(token, knopkaUserById.getToken())) {
            knopkaUserRepository.deleteById(knopkaUserId);
            System.out.println("Deleted KnopkaUser with id: " + knopkaUserId);
        } else {
            throw new IllegalStateException("Token is not valid");
        }
    }


    @Transactional
    public void updateKnopkaUserFriends(Long knopkaUserId, Long friendId, String token) {
        if (Objects.equals(friendId, knopkaUserId)) {
            throw new IllegalStateException("No");
        }
        KnopkaUser knopkaUser = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );

        KnopkaUser knopkaUserFriend = knopkaUserRepository.findById(friendId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + friendId + " doesn't exist")
        );
        if (Objects.equals(token, knopkaUser.getToken())) {
            knopkaUser.getFriends().add(friendId);
            System.out.println(knopkaUserId + " added " + friendId + " as friend!"
            );
        } else {
            throw new IllegalStateException("Your token is invalid. Please chose another one");
        }
    }

    public Set<Long> getKnopkaUsersFriends(Long knopkaUserId, String token) {
        KnopkaUser knopkaUser = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );
        if (Objects.equals(token, knopkaUser.getToken())) {
            return knopkaUser.getFriends();
        } else {
            throw new IllegalStateException("Your token is invalid. Please chose another one");
        }
    }

    public Set<Long> getKnopkaUsersKnopkaIds(Long knopkaUserId, String token) {
        KnopkaUser knopkaUser = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );
        if (Objects.equals(token, knopkaUser.getToken())) {
            return knopkaUser.getKnopkaIds();
        } else {
            throw new IllegalStateException("Your token is invalid. Please chose another one");
        }
    }

    public void deleteKnopkaUserFriend(Long knopkaUserId, Long friendId, String token) {
        KnopkaUser knopkaUser = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );
        KnopkaUser friend = knopkaUserRepository.findById(friendId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );
        if (Objects.equals(token, knopkaUser.getToken())) {
            if (knopkaUser.getFriends().contains(friendId)) {
                knopkaUser.getFriends().remove(friendId);
            } else {
                throw new IllegalStateException("It is not friend");
            }
        } else {
            throw new IllegalStateException("Your token is invalid. Please chose another one");
        }
    }

    public Set<KnopkaUserResponseDTO> getKnopkaUsersFriendsDTOs(Long knopkaUserId, String token, List<Long> friendsId) {
        KnopkaUser knopkaUser = knopkaUserRepository.findById(knopkaUserId).orElseThrow(
                () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
        );

        if (!Objects.equals(knopkaUser.getToken(), token)) {
            throw new IllegalStateException("Token is not valid");
        }
        Set<KnopkaUserResponseDTO> resSet = new HashSet<>();

        for (var id : friendsId) {
            if (knopkaUser.getFriends().contains(id)) {
                KnopkaUser friend = knopkaUserRepository.findById(id).orElseThrow(
                        () -> new IllegalStateException("KnopkaUser with id: " + knopkaUserId + " doesn't exist")
                );
                resSet.add(new KnopkaUserResponseDTO(friend.getProfile().getNickname(), friend.getProfile().getEncodedPhoto(), id));
            }
        }
        return resSet;
    }

}
