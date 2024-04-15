package com.noble.tardis.storage;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageRoles;

@Service
public class StorageService {
    private static Storage storage = null;
    private static Bucket bucket = null;

    private String projectId;
    private String defaultBucketName;

    public StorageService(@Value("${storage.project.id}") String projectId,
            @Value("${storage.project.bucket}") String defaultBucketName) {
        this.projectId = projectId;
        this.defaultBucketName = defaultBucketName;
    }

    public Bucket getBucket() {
        if (storage == null) {
            storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            Policy originalPolicy = storage.getIamPolicy(defaultBucketName);
            storage.setIamPolicy(
                    defaultBucketName,
                    originalPolicy
                            .toBuilder()
                            .addIdentity(StorageRoles.objectViewer(), Identity.allUsers()) // All users can view
                            .build());
        }
        if (bucket == null || !bucket.getName().equals(defaultBucketName)) {
            bucket = storage.get(defaultBucketName);
        }
        return bucket;
    }

    public Storage getStorage() {
        return getBucket().getStorage();
    }

    public String uploadTo(MultipartFile image, String path) {
        try {
            var bucket = getBucket();
            var storage = bucket.getStorage();
            String bucketName = bucket.getName();
            String fileName = path.concat(".jpeg");
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            Storage.BlobWriteOption precondition = storage.get(bucketName, fileName) == null
                    ? Storage.BlobWriteOption.doesNotExist()
                    : Storage.BlobWriteOption.generationMatch(
                            storage.get(bucketName, fileName).getGeneration());
            storage.createFrom(blobInfo, image.getInputStream(), precondition);
            String url = new String("https://[bucket].storage.googleapis.com/[file]")
                    .replace("[bucket]", bucketName)
                    .replace("[file]", fileName);
            return url;
        } catch (Exception e) {
            System.err.println("\n\nImageStorageService ERR: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n" + path);
            System.out.println(Instant.now().toString() + "\n\n");
            return null;
        }
    }

    public boolean delete(String objectName) {
        try {
            var bucket = getBucket();
            var storage = bucket.getStorage();
            String bucketName = bucket.getName();
            Blob blob = storage.get(bucketName, objectName.endsWith(".jpeg") ? objectName : objectName.concat(".jpeg"));
            if (blob == null) {
                System.out.println("The object " + objectName + " wasn't found in " + bucketName);
                return false;
            }
            Storage.BlobSourceOption precondition = Storage.BlobSourceOption.generationMatch(blob.getGeneration());
            return storage.delete(blob.getBlobId(), precondition);
            // return storage.delete(bucketName, objectName, precondition);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
