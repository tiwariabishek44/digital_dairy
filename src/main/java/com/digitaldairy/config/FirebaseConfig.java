//package com.digitaldairy.config;
//
///**
// * FirebaseConfig: Initializes Firebase Admin SDK for FCM push notifications (e.g., OTPs, broadcasts).
// * Loads service account from resources/firebase-adminsdk.json; provides singleton FirebaseMessaging instance.
// */
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//@Configuration
//public class FirebaseConfig {
//
//    @Bean
//    public FirebaseMessaging firebaseMessaging() throws IOException {
//        if (FirebaseApp.getApps().isEmpty()) {
//            InputStream serviceAccount = getClass().getClassLoader()
//                    .getResourceAsStream("firebase-adminsdk.json");
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//            FirebaseApp.initializeApp(options);
//        }
//        return FirebaseMessaging.getInstance();
//    }
//}