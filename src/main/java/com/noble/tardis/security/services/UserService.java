package com.noble.tardis.security.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.noble.tardis.security.dto.UserDto;
import com.noble.tardis.security.entities.User;
import com.noble.tardis.security.repositories.UserRepository;
import com.noble.tardis.storage.StorageService;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenService tokenService;
    @Autowired
    StorageService storageService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findDetailsByEmail(username);
    }

    public User findOne(String email) {
        return userRepository.findByEmail(email);
    }

    public User findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User create(String email, String password) {
        var mail = email.split("@")[0];
        var u = new User(email, mail, passwordEncoder.encode(password));
        return userRepository.save(u);
    }

    public String generateAuthToken(User u) {
        return tokenService.token(u);
    }

    public UserDto toDataTransferObject(User user) {
        return new UserDto(user.getEmail(), user.getUsername(), user.getName(), user.getAvatarURL(),
                user.isEmailChecked(), user.getAllUserRoles());
    }

    public boolean passwordMatches(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public UserDto update(User user) {
        var saved = userRepository.save(user);
        return toDataTransferObject(saved);
    }

    public String uploadAvatar(MultipartFile avatar, User user) {
        try {
            var bucket = storageService.getBucket();
            var storage = bucket.getStorage();
            String bucketName = bucket.getName();
            String fileName = "avatar/" + user.getId().toString().concat(".jpeg");
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            Storage.BlobWriteOption precondition = storage.get(bucketName, fileName) == null
                    ? Storage.BlobWriteOption.doesNotExist()
                    : Storage.BlobWriteOption.generationMatch(
                            storage.get(bucketName, fileName).getGeneration());

            storage.createFrom(blobInfo, avatar.getInputStream(), precondition);
            String url = new String("https://[bucket].storage.googleapis.com/[file]")
                    .replace("[bucket]", bucketName)
                    .replace("[file]", fileName);
            user.setAvatarURL(url.concat("?uid=" + Instant.now().toEpochMilli()));
            userRepository.save(user);
            return url;
        } catch (Exception e) {
            System.err.println("\n\nImageStorageService ERR: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n" + user.getId());
            System.out.println(Instant.now().toString() + "\n\n");
            return null;
        }
    }

}
